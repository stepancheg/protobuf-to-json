#!/bin/sh -e

R=$(dirname $0)

die() {
    echo "$@" >&2
    exit 1
}

check_java_home() {
    d="$1"
    if [ -z "$JAVA_HOME" ]; then
        if [ -d "$d" ]; then
            JAVA_HOME="$1"
        fi
    fi
}


check_java_home /Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home

test -n "$JAVA_HOME" || die "JAVA_HOME not found"

exec $JAVA_HOME/bin/java -classpath "$R/out/production/protobuf-to-json:$R/lib/*" \
    com.github.stepancheg.protobuftojson.ProtobufToJsonMain \
    "$@"

# vim: set ts=4 sw=4 et:
