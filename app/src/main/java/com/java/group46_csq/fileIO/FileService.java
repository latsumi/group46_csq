package com.java.group46_csq.fileIO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.java.group46_csq.util.News;
/**
 * Created by csq on 17/9/12.
 */

public class FileService {

    public FileService() {

    }


    public static void saveNews(String filename, News news) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename, true);
        } catch (FileNotFoundException e) {
        }
        ObjectOutputStream oso = new ObjectOutputStream(fos);
        try {
            oso.writeObject(news);
        } catch (IOException e) {
            e.printStackTrace();
        }
        oso.close();
    }

    public static News findIfSaved(String filename, News news) throws Exception {
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream osi = new ObjectInputStream(fis);

        News res = new News();

        try {
            while (true) {
                News news1 = (News) osi.readObject();
                byte[] buf = new byte[4];
                fis.read(buf);
                if (news1.equals(news1, news)) {
                    res = news1;
                    break;
                }

            }
        } catch (EOFException e) {
        } finally {
            osi.close();
            return res;
        }
    }

}