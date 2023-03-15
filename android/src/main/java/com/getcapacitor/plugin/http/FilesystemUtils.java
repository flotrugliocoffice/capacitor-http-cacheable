package com.getcapacitor.plugin.http;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class FilesystemUtils {

  public static final String DIRECTORY_DOCUMENTS = "DOCUMENTS";
  public static final String DIRECTORY_APPLICATION = "APPLICATION";
  public static final String DIRECTORY_DOWNLOADS = "DOWNLOADS";
  public static final String DIRECTORY_DATA = "DATA";
  public static final String DIRECTORY_CACHE = "CACHE";
  public static final String DIRECTORY_EXTERNAL = "EXTERNAL";
  public static final String DIRECTORY_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";

  public static File getFileObject(Context c, String path, String directory) {
    if (directory == null || path.startsWith("file://")) {
      Uri u = Uri.parse(path);
      if (u.getScheme() == null || u.getScheme().equals("file")) {
        return new File(u.getPath());
      }
    }

    File androidDirectory = FilesystemUtils.getDirectory(c, directory);

    if (androidDirectory == null) {
      return null;
    } else {
      if (!androidDirectory.exists()) {
        androidDirectory.mkdir();
      }
    }

    return new File(androidDirectory, path);
  }

  public static File getDirectory(Context c, String directory) {
    switch (directory) {
      case DIRECTORY_APPLICATION:
      case DIRECTORY_DATA:
        return c.getFilesDir();
      case DIRECTORY_DOCUMENTS:
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
      case DIRECTORY_DOWNLOADS:
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
      case DIRECTORY_CACHE:
        return c.getCacheDir();
      case DIRECTORY_EXTERNAL:
        return c.getExternalFilesDir(null);
      case DIRECTORY_EXTERNAL_STORAGE:
        return Environment.getExternalStorageDirectory();
    }
    return null;
  }

  /**
   * True if the given directory string is a public storage directory, which is accessible by the user or other apps.
   *
   * @param directory the directory string.
   */
  public static boolean isPublicDirectory(String directory) {
    return (DIRECTORY_DOCUMENTS.equals(directory) || DIRECTORY_DOWNLOADS.equals(directory) || "EXTERNAL_STORAGE".equals(directory));
  }

  public static File getCacheFile(Context context, String filename) {
    File fileDir = context.getExternalFilesDir("appcache");
    if (fileDir == null) {
      //La directory non esiste
      return null;
    }
    if (!fileDir.exists()) {
      return null;
    }
    File file = new File(fileDir, filename + ".cache");
    if (file == null) {
      return null;
    }
    if (file.exists()) {
      return file;
    }
    return null;
  }

  public static Object getObjectFromFile(Context context, File source) {
    try {
      FileInputStream fos = new FileInputStream(source);
      ObjectInputStream inObject = new ObjectInputStream(fos);
      Object data = inObject.readObject();
      return data;
    } catch (Exception ex) {
      return null;
    }
  }

  public static boolean storeCacheFile(Context context, String filename, Object content) {
    File fileDir = context.getExternalFilesDir("appcache");
    if (fileDir == null) {
      return false;
    }
    if (!fileDir.exists()) {
      fileDir.mkdirs();
    }
    try {
      //Prepare file:
      File outputFile = new File(fileDir, filename + ".cache");
      FileOutputStream fos = new FileOutputStream(outputFile);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(content);
      out.close();
      fos.close();
      return true;
    } catch (Exception ex) {
      return false;
    }

  }


}
