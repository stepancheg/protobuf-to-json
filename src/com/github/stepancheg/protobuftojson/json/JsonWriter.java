package com.github.stepancheg.protobuftojson.json;

import com.github.stepancheg.protobuftojson.io.IoUtils;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.function.Consumer;

/**
 * @author Stepan Koltsov
 *
 * TODO: ident
 */
public class JsonWriter {
    private final Writer underlying;

    public JsonWriter(Writer underlying) {
        this.underlying = underlying;
    }

    private void writeRaw(String s) {
        try {
            underlying.write(s);
        } catch (IOException e) {
            throw IoUtils.translate(e);
        }
    }

    private void writeString(String s) {
        writeRaw(JsonUtils.quote(s));
    }


    public ValueWriter start() {
        return new ValueWriter(currentWriter);
    }

    public void write(Consumer<ValueWriter> consumer) {
        ValueWriter valueWriter = start();
        consumer.accept(valueWriter);
        valueWriter.finish();
    }

    public void writeObject(Consumer<ObjectWriter> consumer) {
        write(w -> w.writeObject(consumer));
    }

    public void writeArray(Consumer<ArrayWriter> consumer) {
        write(w -> w.writeArray(consumer));
    }



    private CurrentWriter currentWriter = new RootWriter();

    public abstract class CurrentWriter {
        protected final CurrentWriter parent;

        protected CurrentWriter(CurrentWriter parent) {
            if (currentWriter != parent) {
                if (!(this instanceof RootWriter)) {
                    throw new IllegalStateException();
                }
            }
            currentWriter = this;
            this.parent = parent;
        }

        protected void check() {
            if (currentWriter != this) {
                throw new IllegalStateException();
            }
        }

        protected void writeBeforeFinish() {}

        protected void childDone() {}

        public final void finish() {
            writeBeforeFinish();
            currentWriter = parent;
            currentWriter.childDone();
        }
    }

    public class ValueWriter extends CurrentWriter {
        private boolean written = false;

        public ValueWriter(CurrentWriter parent) {
            super(parent);
        }

        private void writeValue(Runnable r) {
            check();
            if (written) {
                throw new IllegalStateException();
            }
            r.run();
            written = true;
        }

        @Override
        protected void childDone() {
            if (written) {
                throw new IllegalStateException();
            }
            written = true;
        }

        private void writeValueRaw(String raw) {
            writeValue(() -> writeRaw(raw));
        }

        public void writeNumber(BigDecimal value) {
            writeValueRaw(value.toString());
        }

        public void writeNumber(Number value) {
            writeNumber(JsonUtils.toBigDecimal(value));
        }

        public void writeString(String value) {
            writeValueRaw(JsonUtils.quote(value));
        }

        public void writeNull() {
            writeValueRaw("null");
        }

        public void writeBoolean(boolean value) {
            writeValueRaw(value ? "true" : "false");
        }

        public void writeTrue() {
            writeBoolean(true);
        }

        public void writeFalse() {
            writeBoolean(false);
        }

        protected void writeBeforeFinish() {
            check();
            if (!written) {
                throw new IllegalStateException();
            }
        }

        public ArrayWriter startArray() {
            return new ArrayWriter(this);
        }

        public ObjectWriter startObject() {
            return new ObjectWriter(this);
        }

        public void writeArray(Consumer<ArrayWriter> consumer) {
            ArrayWriter arrayWriter = startArray();
            consumer.accept(arrayWriter);
            arrayWriter.finish();
        }

        public void writeObject(Consumer<ObjectWriter> consumer) {
            ObjectWriter objectWriter = startObject();
            consumer.accept(objectWriter);
            objectWriter.finish();
        }
    }

    protected abstract class ContainerWriter extends CurrentWriter {
        protected boolean first = true;

        protected ContainerWriter(CurrentWriter parent) {
            super(parent);
        }
    }

    public class ObjectWriter extends ContainerWriter {

        public ObjectWriter(CurrentWriter parent) {
            super(parent);
            writeRaw("{");
        }

        @Override
        protected void writeBeforeFinish() {
            writeRaw("}");
        }

        public ValueWriter entryWriter(String key) {
            check();
            if (!first) {
                writeRaw(", ");
            }
            first = false;
            writeString(key);
            writeRaw(": ");
            return new ValueWriter(this);
        }

        public void writeEntry(String key, Consumer<ValueWriter> consumer) {
            ValueWriter valueWriter = entryWriter(key);
            consumer.accept(valueWriter);
            valueWriter.finish();
        }

        public void numberEntry(String key, BigDecimal value) {
            writeEntry(key, w -> w.writeNumber(value));
        }

        public void numberEntry(String key, long value) {
            numberEntry(key, BigDecimal.valueOf(value));
        }

        public void booleanEntry(String key, boolean value) {
            writeEntry(key, w -> w.writeBoolean(value));
        }

    }

    public class ArrayWriter extends ContainerWriter {

        public ArrayWriter(CurrentWriter parent) {
            super(parent);
            writeRaw("[");
        }

        @Override
        protected void writeBeforeFinish() {
            writeRaw("]");
        }

        public ValueWriter elementWriter() {
            check();
            if (!first) {
                writeRaw(", ");
            }
            first = false;
            return new ValueWriter(this);
        }

        public void writeValue(Consumer<ValueWriter> writer) {
            ValueWriter valueWriter = elementWriter();
            writer.accept(valueWriter);
            valueWriter.finish();
        }

        public void writeNumber(Number value) {
            writeValue(valueWriter -> valueWriter.writeNumber(value));
        }

        public void writeBoolean(boolean value) {
            writeValue(valueWriter -> valueWriter.writeBoolean(value));
        }

        public void writeNull() {
            writeValue(ValueWriter::writeNull);
        }

        public void writeArray(Consumer<ArrayWriter> consumer) {
            writeValue(valueWriter -> valueWriter.writeArray(consumer));
        }

        public void writeObject(Consumer<ObjectWriter> consumer) {
            writeValue(valueWriter -> valueWriter.writeObject(consumer));
        }
    }

    private class RootWriter extends CurrentWriter {
        protected RootWriter() {
            super(null);
        }
    }


}
