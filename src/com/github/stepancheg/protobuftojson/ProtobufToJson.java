package com.github.stepancheg.protobuftojson;

import com.github.stepancheg.protobuftojson.json.JsonWriter;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * @author Stepan Koltsov
 */
public class ProtobufToJson {

    public static String protobufToJsonString(Message message) {
        StringWriter stringWriter = new StringWriter();
        protobufToJson(message, stringWriter);
        return stringWriter.toString();
    }

    public static void protobufToJson(Message message, Writer writer) {
        protobufToJson(message, new JsonWriter(writer));
    }

    public static void protobufToJson(Message message, JsonWriter jsonWriter) {
        jsonWriter.writeObject(ow -> protobufToJson(message, ow));
    }

    private static void protobufToJson(Message message, JsonWriter.ObjectWriter objectWriter) {
        message.getAllFields().forEach((field, value) -> {
            objectWriter.writeEntry(field.getName(), w -> {
                if (field.isRepeated()) {
                    List<?> list = (List<?>) value;
                    w.writeArray(aw -> {
                        for (Object item : list) {
                            aw.writeValue(itemWriter -> writeValue(item, field, itemWriter));
                        }
                    });
                } else {
                    writeValue(value, field, w);
                }
            });
        });
    }

    private static void writeValue(Object value, Descriptors.FieldDescriptor field,
            JsonWriter.ValueWriter valueWriter)
    {
        switch (field.getJavaType()) {
        case BOOLEAN:
            valueWriter.writeBoolean((Boolean) value);
            break;
        case FLOAT:
        case DOUBLE:
        case INT:
        case LONG:
            valueWriter.writeNumber((Number) value);
            break;
        case STRING:
            valueWriter.writeString((String) value);
            break;
        case MESSAGE:
            valueWriter.writeObject(ow -> protobufToJson((Message) value, ow));
            break;
        default:
            throw new RuntimeException("unsupported type: " + field.getJavaType());
        }
    }

}
