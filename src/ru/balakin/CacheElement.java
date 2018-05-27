package ru.balakin;


public class CacheElement {
    private String fileName;
    private long lastAccessTime;
    private int accessCounter;

    public CacheElement(String fileName) {
        this.fileName = fileName;
        this.lastAccessTime = getCurrentTime();
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public String getFileName() {
        return fileName;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime() {
        lastAccessTime = getCurrentTime();
    }

    public int getAccessCounter() {
        return accessCounter;
    }

    public void setAccessCounter() {
        this.accessCounter++;
    }
}
