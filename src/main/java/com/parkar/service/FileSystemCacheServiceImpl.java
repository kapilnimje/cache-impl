package com.parkar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.Scanner;

public class FileSystemCacheServiceImpl implements Service {


    private long timeToLive;
    private String cachePath;
    private String cacheExtension;
    private final static Logger logger = LoggerFactory.getLogger(FileSystemCacheServiceImpl.class);


    private void setCachePath(String path) {
        this.cachePath = path;
        if (!(new File(this.cachePath).isDirectory())) {
            new File(this.cachePath).mkdirs();
        }
    }


    public FileSystemCacheServiceImpl(int timeToLive, String cachePath, String cacheExtension, int timeInterval) {
        this.timeToLive = timeToLive * 1000;
        this.setCachePath(cachePath);
        this.cacheExtension = cacheExtension;

        if (timeToLive >0 && timeInterval > 0) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(timeInterval * 1000);
                        } catch (InterruptedException ex) {
                            logger.error("Interrupted Exception Occurred :", ex);
                        }
                        clearCache();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Check if a file is cached or not.
     * @param label
     * @return
     */
    public boolean isCached(String label) {
        String filename = this.cachePath + this.safeFilename(label) + this.cacheExtension;
        File file = new File(filename);
        long diff = new Date().getTime() - file.lastModified();

        return file.exists() && (!(diff > this.timeToLive * 24 * 60 * 60 * 1000));
    }


    public Object getCache(String key) {
        if (this.isCached(key)) {
            String filename = this.cachePath + this.safeFilename(key) + this.cacheExtension;

            String data;
            try (Scanner reader = new Scanner(new File(filename)).useDelimiter("\\Z")) {
                data = reader.next();
                reader.close();
                return data;
            } catch (FileNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Put data into Cache
     * @param key - Key
     * @param value - Value
     */
    @Override
    public void put(String key, Object value) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(this.cachePath + this.safeFilename(key) + this.cacheExtension),
                "utf-8"))) {
            writer.write(String.valueOf(value));
        } catch (IOException ex) {
            logger.error("Exception occurred when putting objects in to cache : ", ex);
        }
    }

    @Override
    public Object get(String key) {
        Object data = null;

        if (this.getCache(key) != null) {
            data = this.getCache(key);
        }
        this.put(key, data);
        return data;
    }


    /**
     * Remove all Cached files
     */
    public void clearCache() {
        for (File file : new File(this.cachePath).listFiles()) {
            file.delete();
        }
    }


    public void clearCache(String label) throws FileNotFoundException {
        String filename = String.valueOf(this.cachePath)
                + this.safeFilename(label) + this.cacheExtension;
        File file = new File(filename);
        if(file.exists()) {
            file.delete();
        } else {
            throw new FileNotFoundException();
        }
    }


    /**
     * Function the number of cached files currently stored
     * @return total cached files
     */
    public int size() {
        return new File(this.cachePath).listFiles().length;
    }

    /**
     * Helper function to help validate file names
     * @param filename
     * @return
     */
    private String safeFilename(String filename) {
        return filename.replaceAll("/[^0-9a-z\\.\\_\\-]/i", "");
    }

}
