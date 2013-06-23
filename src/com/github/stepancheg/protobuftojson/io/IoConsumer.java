package com.github.stepancheg.protobuftojson.io;

/**
 * @author Stepan Koltsov
 */
public interface IoConsumer<T> {

    void accept(T t) throws Exception;

}
