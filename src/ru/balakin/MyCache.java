package ru.balakin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MyCache<K, V extends Serializable> {
    private Map<K, CacheElement> cache = new HashMap<>();
    private int maxSize;
    private CacheAlgorithm cacheAlgorithm;


    public MyCache(int size, CacheAlgorithm algorithm) {
        this.maxSize = size;
        this.cacheAlgorithm = algorithm;
    }

    // LFU - Least-Frequently Used (возвращаем наименее часто используемый элемент кэша)
    private K getLFU() {
        K key = cache.keySet().iterator().next();
        int accessCounter = cache.get(key).getAccessCounter();
        for (Map.Entry<K, CacheElement> element : cache.entrySet()) {
            if (element.getValue().getAccessCounter() < accessCounter) {
                key = element.getKey();
                accessCounter = element.getValue().getAccessCounter();
            }
        }
        return key;
    }

    // MFU - Most Frequently Used (возвращаем наиболее часто используемый элемент кэша)
    private K getMFU() {
        K key = cache.keySet().iterator().next();
        int accessCounter = cache.get(key).getAccessCounter();
        for (Map.Entry<K, CacheElement> element : cache.entrySet()) {
            if (element.getValue().getAccessCounter() > accessCounter) {
                key = element.getKey();
                accessCounter = element.getValue().getAccessCounter();
            }
        }
        return key;
    }

    //LRU - Least recently used (возвращаем неиспользованный дольше всех элемент кэша)
    private K getLRU() {
        K key = cache.keySet().iterator().next();
        long lastAccessTime = cache.get(key).getLastAccessTime();
        for (Map.Entry<K, CacheElement> element : cache.entrySet()) {
            if (element.getValue().getLastAccessTime() < lastAccessTime) {
                key = element.getKey();
                lastAccessTime = element.getValue().getLastAccessTime();
            }
        }
        return key;
    }

    private String writeToFile(V value) {

        File tmpFile = null;
        try {
            tmpFile = File.createTempFile( "tmp", "");
            tmpFile.deleteOnExit();
        } catch (IOException e) {
            System.out.println("Can't create file " + e.getMessage());
        }
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(tmpFile))) {
            outputStream.writeObject(value);
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("Can't write an object to a file " + tmpFile.getName() + ": " + e.getMessage());
        }
        return tmpFile.getAbsolutePath();
    }

    public void put(K key, V value) {
        // Если размер кэша достиг максимального, то предварительно удаляем старый элемент
        // в соответствии с выбранной стратегией
        if (cache.size() == maxSize) {
            K keyForRemove = null;
            switch (cacheAlgorithm) {
                case LFU:
                    keyForRemove = getLFU();
                    break;
                case MFU:
                    keyForRemove = getMFU();
                    break;
                case LRU:
                    keyForRemove = getLRU();
                    break;
            }
            deleteFile(cache.get(keyForRemove).getFileName());
            cache.remove(keyForRemove);
        }
        cache.put(key, new CacheElement(writeToFile(value)));
    }

    public V get(K key) {
        if (cache.containsKey(key)) {
            CacheElement cacheElement = cache.get(key);
            cacheElement.setLastAccessTime();
            cacheElement.setAccessCounter();
            return readFromFile(cacheElement.getFileName());
        }
        return null;
    }

    private V readFromFile(String fileName) {
        V value = null;
        try (FileInputStream fileInputStream = new FileInputStream(new File(fileName));
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            value = (V) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Can't read a file." + e.getMessage());
        }
        return value;
    }

    private void deleteFile(String fileName) {
        File file = new File(fileName);
        file.delete();
    }

    public void clear() {
        for (CacheElement element : cache.values()) {
            deleteFile(element.getFileName());
        }
        cache.clear();
    }

}
