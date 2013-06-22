package com.github.stepancheg.protobuftojson.io;

import java.io.*;
import java.nio.charset.Charset;
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

    public static <T> T read(File file, IoFunction<InputStream, T> parser) {
        InputStream is = openInputStream(file);
        try {
            return parser.apply(is);
        } catch (Exception e) {
            throw IoUtils.translate(e);
        } finally {
            IoUtils.closeQuietly(is);
        }
    }

    public static <T> T readWithRead(File file, IoFunction<Reader, T> parser) {
        return read(file, is -> parser.apply(new InputStreamReader(is, Charset.forName("utf-8"))));
    }

}
