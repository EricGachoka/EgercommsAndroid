package com.example.egercomms.services.announcement;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
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
            Call<ResponseBody> call = webService.downloadAttachments(url);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "server contacted and has file");

                        boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                        message = "File Downloaded to Egercomms/downloads";

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
        }else{
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
            String folderPath = "Egercomms"+File.separator+"downloads";
            File folder = new File(Environment.getExternalStorageDirectory(), folderPath);
            if(!folder.exists()){
                folder.mkdirs();
            }
            File file = new File(Environment.getExternalStorageDirectory(), folderPath+File.separator + getFileName(path));

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

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
        } catch (IOException e) {
            return false;
        }
    }

    private String getFileName(String path) {
        return path.replaceAll(".*/", "");
    }
}
