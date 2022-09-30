package com.example.demo.config.utils;

import org.springframework.util.ResourceUtils;

import java.io.*;

/**
 * @author ljr
 * @date 2022-09-14 11:04
 */
public class FileUtls {

    public static String readFileTxt(String filePath){
        try {
            File file = ResourceUtils.getFile(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuffer buffer = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                buffer.append(str);
            }
            reader.close();
            return buffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
