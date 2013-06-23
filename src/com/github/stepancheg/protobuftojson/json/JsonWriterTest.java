package com.github.stepancheg.protobuftojson.json;

import org.junit.Assert;
import org.junit.Test;

import java.io.StringWriter;
import java.util.function.Consumer;

/**
 * @author Stepan Koltsov
 */
public class JsonWriterTest {

    private void testWriter(String expected, Consumer<JsonWriter.ValueWriter> generator) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        jsonWriter.write(generator);

        Assert.assertEquals(expected, stringWriter.toString());
    }

    private void testArray(String expected, Consumer<JsonWriter.ArrayWriter> generator) {
        testWriter(expected, w -> {
            JsonWriter.ArrayWriter aw = w.startArray();
            generator.accept(aw);
            aw.finish();
        });
    }

    private void testObject(String expected, Consumer<JsonWriter.ObjectWriter> generator) {
        testWriter(expected, w -> {
            JsonWriter.ObjectWriter ow = w.startObject();
            generator.accept(ow);
            ow.finish();
        });
    }

    @Test
    public void integer() {
        testWriter("1", w -> w.writeNumber(1));
    }

    @Test
    public void nullValue() {
        testWriter("null", JsonWriter.ValueWriter::writeNull);
    }

    @Test
    public void booleanValue() {
        testWriter("true", JsonWriter.ValueWriter::writeTrue);
        testWriter("false", JsonWriter.ValueWriter::writeFalse);
    }

    @Test
    public void array() {
        testArray("[]", aw -> {
        });
        testArray("[17]", aw -> {
            aw.writeNumber(17);
        });
        testArray("[17, 19]", aw -> {
            aw.writeNumber(17);
            aw.writeNumber(19);
        });
    }

    @Test
    public void object() {
        testObject("{}", ow -> {
        });
        testObject("{\"size\": 23}", ow -> {
            ow.numberEntry("size", 23);
        });
        testObject("{\"size\": 23, \"colored\": true}", ow -> {
            ow.numberEntry("size", 23);
            ow.booleanEntry("colored", true);
        });
    }

    @Test
    public void complex() {
        testWriter("[{\"aa\": [1, {}]}]", w -> {
            w.writeArray(aw -> {
                aw.writeObject(ow -> {
                    JsonWriter.ValueWriter valueWriter = ow.entryWriter("aa");
                    JsonWriter.ArrayWriter arrayWriter = valueWriter.startArray();
                    arrayWriter.writeNumber(1);
                    arrayWriter.writeObject(objectWriter -> {});
                    arrayWriter.finish();
                    valueWriter.finish();
                });
            });
        });
    }

}
