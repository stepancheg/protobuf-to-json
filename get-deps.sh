#!/bin/sh -ex

mkdir -p lib

cd lib

download() {
    url="$1"
    file="$2"
    curl -s -o $file $url
}

download_from_maven() {
    groupSlashed="$1"
    artifact="$2"
    version="$3"
    for suffix in '' '-sources'; do
        download \
            http://repo1.maven.org/maven2/$groupSlashed/$artifact/$version/$artifact-$version$suffix.jar \
            $artifact-$version$suffix.jar
    done
}

download_from_maven com/google/protobuf protobuf-java 2.5.0
download_from_maven junit junit 4.11
download_from_maven com/intellij annotations 12.0
download_from_maven org/hamcrest hamcrest-all 1.3
download_from_maven com/google/code/gson gson 2.2.4


# vim: set ts=4 sw=4 et:
