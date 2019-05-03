package com.example.egercomms.eventObjects;

public class FilePathEventObject {
    private String path;

    public FilePathEventObject(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FilePathEventObject{" +
                "path='" + path + '\'' +
                '}';
    }
}
