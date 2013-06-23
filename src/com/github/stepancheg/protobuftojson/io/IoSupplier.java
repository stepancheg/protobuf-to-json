package com.github.stepancheg.protobuftojson.io;

/**
 * @author Stepan Koltsov
 */
public interface IoSupplier<R> {
    R get() throws Exception;
}
