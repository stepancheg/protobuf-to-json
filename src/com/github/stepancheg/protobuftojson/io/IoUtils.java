package com.github.stepancheg.protobuftojson.io;

import java.io.Closeable;
import java.io.Writer;

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

    public static <R> R execute(IoSupplier<R> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw translate(e);
        }
    }

    public static void executeRunnable(IoRunnable runnable) {
        execute(() -> { runnable.run(); return null; });
    }

    public static RuntimeException translate(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

}
