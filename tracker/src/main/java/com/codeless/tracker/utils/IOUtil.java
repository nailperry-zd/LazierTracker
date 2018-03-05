package com.codeless.tracker.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by zhangdan on 2018/3/5.
 */

public class IOUtil {
    public static String CHARSET = "UTF-8";

    public static String readInputStream(InputStream inputStream) {
        return readInputStream(inputStream, CHARSET);
    }

    public static String readInputStream(InputStream inputStream, String charset) {
        if (inputStream == null) {
            return null;
        }
        try {
            StringBuilder content = new StringBuilder();
            InputStreamReader streamReader = null;
            streamReader = new InputStreamReader(inputStream, charset);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            String line;
            //分行读取
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
            inputStream.close();
            return new String(content.toString().getBytes(), charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void closeIO(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
