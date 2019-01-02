package com.fdz.common.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by wusongsong on 2017/12/15.
 */
public class IOUtils {
    public static boolean close(Closeable closeable) {
        if (closeable == null) {
            return true;
        }
        try {
            closeable.close();
            return true;
        } catch (IOException e) {
        }

        return false;
    }

}
