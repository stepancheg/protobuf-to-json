package com.github.stepancheg.protobuftojson.json;

import com.github.stepancheg.protobuftojson.io.IoUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Stepan Koltsov
 */
public class JsonWriter {
    private final Writer underlying;

    public JsonWriter(Writer underlying) {
        this.underlying = underlying;
    }

    private void write(String s) {
        try {
            underlying.write(s);
        } catch (IOException e) {
            throw IoUtils.translate(e);
        }
    }

    private CurrentWriter currentWriter;

    public abstract class CurrentWriter {
        protected final CurrentWriter parent;

        protected CurrentWriter(CurrentWriter parent) {
            this.parent = parent;
        }

        protected void check() {
            if (currentWriter != this) {
                throw new IllegalStateException();
            }
        }
    }

    public class ValueWriter extends CurrentWriter {
        private boolean written = false;

        public ValueWriter(CurrentWriter parent) {
            super(parent);
        }

        private void writeValue(Runnable r) {
            if (written) {
                throw new IllegalStateException();
            }
            r.run();
            written = true;
        }

        public void writeNumber(int value) {
            writeValue(() -> write(Integer.toString(value)));
        }

        public CurrentWriter finish() {
            check();
            if (!written) {
                throw new IllegalStateException();
            }
            currentWriter = parent;
            return currentWriter;
        }
    }

    public class ObjectWriter extends CurrentWriter {

        public ObjectWriter(CurrentWriter parent) {
            super(parent);
        }
    }

    public class ArrayWriter extends CurrentWriter {
        public ArrayWriter(CurrentWriter parent) {
            super(parent);
        }

        ValueWriter writeValue() {
            check();
            return new ValueWriter(this);
        }

        void writeValue(Consumer<ValueWriter> writer) {
            ValueWriter valueWriter = writeValue();
            writer.accept(valueWriter);
            valueWriter.finish();
        }

        public void writeNumber(int value) {
            writeValue(valueWriter -> valueWriter.writeNumber(value));
        }

    }
}
