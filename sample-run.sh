#!/bin/sh -e

cd $(dirname $0)

exec ./protobuf-to-json.sh -text-in ./test-data.data -proto-bin test-data.proto.bin -type City

# vim: set ts=4 sw=4 et:
