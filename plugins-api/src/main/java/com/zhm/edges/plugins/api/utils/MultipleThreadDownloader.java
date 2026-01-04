package com.zhm.edges.plugins.api.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MultipleThreadDownloader {

  private static final int THREAD_COUNT = 4; // You can adjust this

  public static void downloadFile(String fileURL, String outputFile) throws Exception {
    downloadFile(fileURL, outputFile, THREAD_COUNT, (downloaded, total) -> {});
  }

  public static void downloadFile(
      String fileURL, String outputFile, int threadCount, ProgressCallback callback)
      throws Exception {
    URL url = new URL(fileURL);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestProperty("Range", "bytes=0-0");
    conn.connect();

    int responseCode = conn.getResponseCode();
    boolean supportRange = responseCode == 206;
    int contentLength = conn.getContentLength();

    if (contentLength < 0) {
      // Try to get Content-Length from HEAD
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("HEAD");
      conn.connect();
      contentLength = conn.getContentLength();
    }
    if (supportRange && contentLength > threadCount * 10) {
      // Pre-allocate file
      try (RandomAccessFile raf = new RandomAccessFile(outputFile, "rw")) {
        raf.setLength(contentLength);
      }

      final AtomicLong downloaded = new AtomicLong(0);
      final List<Thread> threads = new ArrayList<>();
      final int partSize = contentLength / threadCount;

      final int _contentLength = contentLength;

      for (int i = 0; i < threadCount; i++) {
        int start = i * partSize;
        int end = (i == threadCount - 1) ? contentLength - 1 : (start + partSize - 1);
        Thread t =
            new Thread(
                () -> {
                  try {
                    downloadPart(
                        fileURL, outputFile, start, end, downloaded, _contentLength, callback);
                  } catch (Exception e) {
                    throw new IllegalStateException(
                        "fail download thread - " + Thread.currentThread().getName(), e);
                  }
                });
        threads.add(t);
        t.start();
      }
      for (Thread t : threads) t.join();

      callback.onProgress(contentLength, contentLength); // Ensure 100% at end
    } else {
      conn = (HttpURLConnection) url.openConnection();
      contentLength = conn.getContentLength();
      // Single-threaded download
      try (InputStream in = url.openStream();
          BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
        byte[] buffer = new byte[8192];
        int len;
        long downloaded = 0;
        while ((len = in.read(buffer)) != -1) {
          out.write(buffer, 0, len);
          downloaded += len;
          callback.onProgress(downloaded, contentLength);
        }
      }
      callback.onProgress(contentLength, contentLength); // Ensure 100% at end
    }
  }

  private static void downloadPart(
      String fileURL,
      String outputFile,
      int start,
      int end,
      AtomicLong downloaded,
      long total,
      ProgressCallback callback)
      throws Exception {
    HttpURLConnection conn = (HttpURLConnection) new URL(fileURL).openConnection();
    conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
    conn.connect();
    try (InputStream in = conn.getInputStream();
        RandomAccessFile raf = new RandomAccessFile(outputFile, "rw")) {
      raf.seek(start);
      byte[] buffer = new byte[8192];
      int len;
      while ((len = in.read(buffer)) != -1) {
        raf.write(buffer, 0, len);
        long current = downloaded.addAndGet(len);
        callback.onProgress(current, total);
      }
    }
  }

  public interface ProgressCallback {
    void onProgress(long downloaded, long total);
  }
}
