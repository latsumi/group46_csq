package com.java.group46_csq.fileIO;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import android.content.Context;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import com.java.group46_csq.util.News;


/**
 * Created by csq on 17/9/12.
 */

public class FileService {
    public FileService() {
    }

    //edited by zhy
    public static void saveNews(FileOutputStream fos, News news) throws Exception {
        ObjectOutputStream oso = new ObjectOutputStream(fos);
        try {
            oso.writeObject(news);
        } catch (IOException e) {
            Log.d("---Exception---", "Exception in saveNews");
        }
        finally {
            oso.close();
        }
    }

    //creatd by zhy
    public static void saveNewsSet (FileOutputStream fos, TreeSet<News> set) throws Exception {
        ObjectOutputStream oso = new ObjectOutputStream(fos);
        try {
            oso.writeObject(set);
        }
        catch (IOException e) {
            Log.d("---Exception---", "Exception in saveNewsSet");
        }
        finally {
            oso.close();
        }
    }

    //edited by zhy
    public static News findIfSaved(FileInputStream fis, News news) throws Exception {
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
            Log.d("---Exception---", "Exception in findIfSaved");
        } finally {
            osi.close();
            return res;
        }
    }

    //edit by zhy
    public static int[] readIntFile(FileInputStream fis) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            ArrayList<Integer> arr = new ArrayList<Integer>();
            String line;
            while ((line = br.readLine()) != null) {
                arr.add(new Integer(Integer.parseInt(line)));
                Log.d("---Print Values---", line);
            }
            int[] res = new int[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                res[i] = arr.get(i).intValue();
            }

            return res;
        }
        catch (Exception e) {
            Log.d("---Exception---", "Exception happens while reading the integers");
            return new int[0];
        }
    }

    //edit by zhy
    public static void writeIntFile(FileOutputStream fos, int[] arr) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));
            for (int i = 0; i < arr.length; i++) {
                pw.println(arr[i]);
            }
            pw.close();
        }
        catch (Exception e) {
            Log.d("---Exception---", "Exception happens while writing the integers");
        }
    }

    public static String readFile(Context context,String fileName) throws IOException {
        String res = "";
        FileInputStream fis = context.openFileInput(fileName);
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        res = new String(buffer, "UTF-8");
        fis.close();
        return res;
    }

    public static void writeFile(Context context, String fileName, String content){
            try {

                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.write(content.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


}