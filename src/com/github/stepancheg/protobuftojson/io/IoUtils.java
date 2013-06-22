package com.github.stepancheg.protobuftojson.io;

import java.io.Closeable;

/**
 * @author Stepan Koltsov
 */
public class IoUtils {

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable e) {
            }
        }
    }

    public static RuntimeException translate(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

}
