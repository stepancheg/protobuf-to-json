package com.github.stepancheg.protobuftojson.io;

import java.io.*;
import java.nio.charset.Charset;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Stepan Koltsov
 */
public class FileUtils {


    public static FileInputStream openInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            throw IoUtils.translate(e);
        }
    }

    public static FileOutputStream openOutputStream(File file) {
        try {
            return new FileOutputStream(file);
        } catch (IOException e) {
            throw IoUtils.translate(e);
        }
    }

    public static <T> T read(File file, IoFunction<InputStream, T> parser) {
        return IoUtils.execute(() -> {
            InputStream is = openInputStream(file);
            try {
                return parser.apply(is);
            } finally {
                IoUtils.closeQuietly(is);
            }
        });
    }

    public static <T> T readWithRead(File file, IoFunction<Reader, T> parser) {
        return read(file, is -> parser.apply(new InputStreamReader(is, Charset.forName("utf-8"))));
    }

    public static void write(File file, IoConsumer<OutputStream> serializer) {
        IoUtils.executeRunnable(() -> {
            OutputStream os = openOutputStream(file);
            try {
                serializer.accept(os);
                os.flush();
            } finally {
                IoUtils.closeQuietly(os);
            }
        });
    }

    public static void writeWithWriter(File file, IoConsumer<Writer> serializer) {
        write(file, os -> {
            OutputStreamWriter writer = new OutputStreamWriter(os);
            serializer.accept(writer);
            writer.flush();
        });
    }

    public static void write(File file, String content) {
        writeWithWriter(file, w -> w.write(content));
    }

}
