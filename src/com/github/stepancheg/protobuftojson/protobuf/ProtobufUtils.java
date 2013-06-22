package com.github.stepancheg.protobuftojson.protobuf;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

/**
 * @author Stepan Koltsov
 */
public class ProtobufUtils {

    public static DescriptorProtos.DescriptorProto getType(DescriptorProtos.FileDescriptorSet fileDescriptorSet, String typeName) {
        for (DescriptorProtos.FileDescriptorProto file : fileDescriptorSet.getFileList()) {
            for (DescriptorProtos.DescriptorProto type : file.getMessageTypeList()) {
                String currentTypeName = file.getPackage() != null ? file.getPackage() + "." + type.getName() : type.getName();
                if (currentTypeName.equals(typeName)) {
                    return type;
                }
            }
        }
        throw new RuntimeException("type not found: " + typeName);
    }

}
