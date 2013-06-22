package com.github.stepancheg.protobuftojson;

import com.github.stepancheg.protobuftojson.argv.ArgvReader;
import com.github.stepancheg.protobuftojson.io.FileUtils;
import com.github.stepancheg.protobuftojson.protobuf.ProtobufUtils;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.TextFormat;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Stepan Koltsov
 */
public class ProtobufToJsonMain {

    private static void usage() {
        System.err.println("usage: protobuf-to-json [args]");
        System.err.println("    -in <FILE>         protbuf input");
        System.err.println("    -textin <FILE>     protobuf input in text format");
        System.err.println("    -proto-bin <FILE>  binary representation of .proto");
        System.err.println("    -json-out <FILE    .json output");
        System.err.println("    -type              message type");
        System.err.println();
        System.err.println("binary representation of .proto can be generated with command:");
        System.err.println("    protoc mydata.proto -o mydata.proto.bin");
    }

    private static void errorAndUsage(String error) {
        System.err.println(error);
        usage();
    }

    public static void main(String[] argv) throws Exception {
        Optional<String> in = Optional.empty();
        Optional<String> textIn = Optional.empty();
        Optional<String> protoBin = Optional.empty();
        Optional<String> jsonOut = Optional.empty();
        Optional<String> type = Optional.empty();

        ArgvReader argvReader = new ArgvReader(Arrays.asList(argv));
        while (!argvReader.eof()) {
            if (argvReader.nextIsOptionWithParam("-in")) {
                argvReader.advance();
                in = Optional.of(argvReader.advance());
            } else if (argvReader.nextIsOptionWithParam("-text-in")) {
                argvReader.advance();
                textIn = Optional.of(argvReader.advance());
            } else if (argvReader.nextIsOptionWithParam("-proto-bin")) {
                argvReader.advance();
                protoBin = Optional.of(argvReader.advance());
            } else if (argvReader.nextIsOptionWithParam("-json-out")) {
                argvReader.advance();
                jsonOut = Optional.of(argvReader.advance());
            } else if (argvReader.nextIsOptionWithParam("-type")) {
                argvReader.advance();
                type = Optional.of(argvReader.advance());
            } else {
                usage();
                System.exit(1);
            }
        }

        if (in.isPresent() == textIn.isPresent()) {
            errorAndUsage("either -in or -text-in must be specified");
            System.exit(1);
        }

        if (!protoBin.isPresent()) {
            errorAndUsage("-proto-bin must be specified");
            System.exit(1);
        }

        if (!jsonOut.isPresent()) {
            errorAndUsage("-json-out must be specified");
            System.exit(1);
        }

        if (!type.isPresent()) {
            errorAndUsage("-type must be specified");
            System.exit(1);
        }


        DescriptorProtos.FileDescriptorSet fileDescriptorSet = FileUtils.read(new File(protoBin.get()), DescriptorProtos.FileDescriptorSet::parseFrom);

        if (fileDescriptorSet.getFileCount() != 1) {
            throw new IllegalStateException();
        }

        Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor.buildFrom(fileDescriptorSet.getFile(0), new Descriptors.FileDescriptor[0]);
        Descriptors.Descriptor descriptor = fileDescriptor.findMessageTypeByName(type.get());

        DynamicMessage.Builder dynamicMessage = DynamicMessage.newBuilder(descriptor);

        DynamicMessage message;
        if (in.isPresent()) {
            message = FileUtils.read(new File(in.get()), is -> DynamicMessage.parseFrom(descriptor, is));
        } else {
            message = FileUtils.readWithRead(new File(textIn.get()), is -> {
                DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
                TextFormat.merge(is, builder);
                return builder.build();
            });
        }
    }

}
