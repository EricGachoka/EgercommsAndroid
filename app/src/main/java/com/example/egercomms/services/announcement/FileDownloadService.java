package com.example.egercomms.services.announcement;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.egercomms.data.DataHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileDownloadService extends IntentService {
    private static final String PATH = "filePath";
    public static final String MY_SERVICE_MESSAGE = "fileDownloadMessage";
    public static final String MY_SERVICE_PAYLOAD = "fileDownloadServicePayload";
    String BASE_URL = "https://gachokaeric.pythonanywhere.com";
    private StringBuilder builder = new StringBuilder();
    public static final String TAG = "FileService";
    private String path;
    private String message;
    private DataHandler dataHandler = DataHandler.getInstance();
    private boolean permissionGranted;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    public FileDownloadService() {
        super("FileDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        permissionGranted = dataHandler.isPermissionsGranted();
        path = intent.getStringExtra(PATH);
        String url = builder.append(BASE_URL).append(path).toString();
        FileDownloadWebService webService = FileDownloadWebService.retrofit.create(FileDownloadWebService.class);
        if (permissionGranted) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW);

                notificationChannel.setDescription("no sound");
                notificationChannel.setSound(null, null);
                notificationChannel.enableLights(false);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationBuilder = new NotificationCompat.Builder(this, "id")
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle("Download")
                    .setContentText("Downloading File")
                    .setDefaults(0)
                    .setAutoCancel(true);
            notificationManager.notify(0, notificationBuilder.build());

            Call<ResponseBody> call = webService.downloadAttachments(url);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "server contacted and has file");

                        boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                        if(writtenToDisk){
                        message = "File Downloaded to Egercomms/downloads";
                        }else{
                            message = "Error downloading file. Check if it exists";
                        }

                        Log.d(TAG, "file download was a success? " + writtenToDisk);

                        Log.e("MESSAGE", "onHandleIntent: " + message);
                        returnResults(message);
                    } else {
                        message = "Problem saving file";
                        Log.d(TAG, "server contact failed");
                        returnResults(message);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "error");
                    message = "File download failed";
                    returnResults(message);
                }
            });
        } else {
            returnResults("You must grant file permissions to download files");
        }
    }

    private void returnResults(String result) {
        Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
        messageIntent.putExtra(MY_SERVICE_PAYLOAD, result);
        LocalBroadcastManager manager =
                LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(messageIntent);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            String folderPath = "Egercomms" + File.separator + "downloads";
            File folder = new File(Environment.getExternalStorageDirectory(), folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(Environment.getExternalStorageDirectory(), folderPath + File.separator + getFileName(path));
            if (!file.exists()) {
                InputStream inputStream = null;
                OutputStream outputStream = null;

                try {
                    byte[] fileReader = new byte[4096];
                    int count;

                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;
                    boolean downloadComplete = false;
                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(file);


                    while ((count = inputStream.read(fileReader)) != -1) {
                        fileSizeDownloaded += count;
                        int progress = (int) ((double) (fileSizeDownloaded * 100) / (double) fileSize);

                        updateNotification(progress);
                        outputStream.write(fileReader, 0, count);
                        downloadComplete = true;
                    }
                    onDownloadComplete(downloadComplete);
                    outputStream.flush();

                    return true;
                } catch (IOException e) {
                    return false;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            }else{                notificationManager.cancel(0);
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    private void updateNotification(int currentProgress) {
        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("Downloaded: " + currentProgress + "%");
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void onDownloadComplete(boolean downloadComplete) {
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("File Download Complete");
        notificationManager.notify(0, notificationBuilder.build());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

    private String getFileName(String path) {
        return path.replaceAll(".*/", "");
    }
}
