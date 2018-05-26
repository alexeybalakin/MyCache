package ru.balakin;


public class CacheElement<K, V> {
    private K key;
    private V value;
    private long lastAccessTime;
    private int accessCounter;


    public CacheElement(K key, V value) {
        this.key = key;
        this.value = value;
        this.lastAccessTime = getCurrentTime();
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
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
