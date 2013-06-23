#!/bin/sh -ex

protoc ./test-data.proto --java_out src

# vim: set ts=4 sw=4 et:
