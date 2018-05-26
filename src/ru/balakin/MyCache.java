package ru.balakin;


import java.util.HashMap;
import java.util.Map;

public class MyCache<K, V> {
    private Map<K, CacheElement<K, V>> cache =  new HashMap<>();
    private int maxSize;
    private CacheAlgorithm cacheAlgorithm;


    public MyCache(int size, CacheAlgorithm algorithm) {
        this.maxSize = size;
        this.cacheAlgorithm = algorithm;
    }

    // LFU - Least-Frequently Used (возвращаем наименее часто используемый элемент кэша)
    private K getLFU(){
        K key = cache.keySet().iterator().next();
        int accessCounter = cache.get(key).getAccessCounter();
        for (CacheElement<K,V> element : cache.values()){
            if (element.getAccessCounter() < accessCounter){
                key = element.getKey();
                accessCounter = element.getAccessCounter();
            }
        }
       return key;
    }

    // MFU - Most Frequently Used (возвращаем наиболее часто используемый элемент кэша)
    private K getMFU(){
        K key = cache.keySet().iterator().next();
        int accessCounter = cache.get(key).getAccessCounter();
        for (CacheElement<K,V> element : cache.values()){
            if (element.getAccessCounter() > accessCounter){
                key = element.getKey();
                accessCounter = element.getAccessCounter();
            }
        }
        return key;
    }

    //LRU - Least recently used (возвращаем неиспользованный дольше всех элемент кэша)
    private K getLRU(){
        K key = cache.keySet().iterator().next();
        long lastAccessTime = cache.get(key).getLastAccessTime();
        for (CacheElement<K,V> element : cache.values()){
            if (element.getLastAccessTime() < lastAccessTime){
                key = element.getKey();
                lastAccessTime = element.getLastAccessTime();
            }
        }
        return key;
    }

    public void put(K key, V value) {
        // Если размер кэша достиг максимального, то предварительно удаляем старый элемент
        // в соответствии с выбранной стратегией
        if (cache.size() == maxSize){
            switch (cacheAlgorithm){
                case LFU:
                    cache.remove(getLFU());
                    break;
                case MFU:
                    cache.remove(getMFU());
                    break;
                case LRU:
                    cache.remove(getLRU());
                    break;
            }
        }
        cache.put(key, new CacheElement<>(key, value));
    }

    public V get(K key){
        CacheElement<K, V> cacheElement = cache.get(key);
        cacheElement.setLastAccessTime();
        cacheElement.setAccessCounter();
        return cacheElement.getValue();
    }

    public void clear(){
        cache.clear();
    }

}
