package com.github.stepancheg.protobuftojson.io;

/**
 * @author Stepan Koltsov
 */
public interface IoFunction<A, R> {
    R apply(A a) throws Exception;
}
