package com.wingsmight.bibleloop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileManager
{
    public static byte[] FileToBytes(String filePath, String fileName, String extensionWithDot)
    {
        File file = new File(filePath, fileName + extensionWithDot);
        boolean isExists = file.exists();
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try
        {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return bFile;
    }

    public static void BytesToFile(byte fileContent[], String filePath, String fileName, String extensionWithDot)
    {
        FileOutputStream fileOuputStream = null;

        try {
            fileOuputStream = new FileOutputStream(filePath + "/" + fileName + extensionWithDot);
            fileOuputStream.write(fileContent);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOuputStream != null) {
                try {
                    fileOuputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void CopyFile(File src, File dst) throws IOException
    {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static void DeleteFile(File fdelete)
    {
        if (fdelete.exists()) {
            fdelete.delete();
        }
    }

    public static String PoemTitleToFileName(String poemTitle)
    {
        poemTitle = poemTitle.replace(":", "_");

        if(poemTitle.contains("Лук"))
        {
            poemTitle = (Character.isDigit(poemTitle.charAt(0)) ? poemTitle.charAt(0) + " " : "") + "Лука" + poemTitle.substring(poemTitle.lastIndexOf(' '));
        }
        else if(poemTitle.contains("Матф"))
        {
            poemTitle = (Character.isDigit(poemTitle.charAt(0)) ? poemTitle.charAt(0) + " " : "") + "Матфей" + poemTitle.substring(poemTitle.lastIndexOf(' '));
        }
        else if(poemTitle.contains("Марк"))
        {
            poemTitle = (Character.isDigit(poemTitle.charAt(0)) ? poemTitle.charAt(0) + " " : "") + "Марк" + poemTitle.substring(poemTitle.lastIndexOf(' '));
        }
        else if(poemTitle.contains("Иоанн"))
        {
            poemTitle = (Character.isDigit(poemTitle.charAt(0)) ? poemTitle.charAt(0) + " " : "") + "Иоанн" + poemTitle.substring(poemTitle.lastIndexOf(' '));
        }
        else
        {

        }

        return poemTitle;
    }
}

