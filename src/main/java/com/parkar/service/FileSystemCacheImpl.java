package com.parkar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileSystemCacheImpl<K extends Serializable, V extends Serializable> implements CacheService<K, V> {

    private final static Logger logger = LoggerFactory.getLogger(FileSystemCacheImpl.class);

    private final Map<K, String> storageMap;
    private final Path tempDir;
    private int capacity;

    public FileSystemCacheImpl() throws IOException {
        this.tempDir = Files.createTempDirectory("cache");
        this.tempDir.toFile().deleteOnExit();
        this.storageMap = new ConcurrentHashMap<>();
    }

    public FileSystemCacheImpl(int capacity) throws IOException {
        this.tempDir = Files.createTempDirectory("cache");
        this.tempDir.toFile().deleteOnExit();
        this.capacity = capacity;
        this.storageMap = new ConcurrentHashMap<>(capacity);
    }

    @Override
    public synchronized V get(K key) {
        if (isObjectExists(key)) {
            String fileName = storageMap.get(key);

            try (FileInputStream fis = new FileInputStream(new File(tempDir + File.separator + fileName));
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                return (V) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                logger.error(String.format("Can't read a file. %s: %s", fileName, e.getMessage()));
            }
        }
        logger.debug(String.format("Object with key '%s' does not exists", key));
        return null;
    }

    @Override
    public synchronized void put(K key, V value) {
        File tempFile = null;
        try {
            tempFile = Files.createTempFile(tempDir,  "", "").toFile();
            ObjectOutputStream  objectOutputStream = new ObjectOutputStream(new FileOutputStream(tempFile));
            objectOutputStream.writeObject(value);
            objectOutputStream.flush();
            storageMap.put(key, tempFile.getName());

        } catch (IOException e) {
            logger.error("Can't write object to a file {} : {}", tempFile.getName(), e.getMessage());
        }

    }

    @Override
    public synchronized void remove(K key) {
        String fileName = storageMap.get(key);
        File deletedFile = new File(tempDir + File.separator + fileName);
        if (deletedFile.delete()) {
            logger.debug(String.format("Cache file '%s' has been deleted", fileName));
        } else {
            logger.debug(String.format("Can't delete a file %s", fileName));
        }
        storageMap.remove(key);
    }

    @Override
    public int getSize() {
        return storageMap.size();
    }

    @Override
    public boolean isObjectExists(K key) {
        return storageMap.containsKey(key);
    }

    @Override
    public boolean hasEmpty() {
        return getSize() < this.capacity;
    }

    @Override
    public void clear() {
        try {
            Files.walk(tempDir)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(file -> {
                        if ( file.delete()) {
                            logger.debug(String.format("Cache file '%s' has been deleted ", file));
                        } else {
                            logger.error(String.format("Can't delete a file %s", file));
                        }
                    });
            storageMap.clear();
        } catch (IOException e) {
            logger.error("Error Occurred when deleting cache ", e);
        }
    }
}
