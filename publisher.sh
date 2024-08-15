#!/bin/bash
#
java -cp "./libs/*"  com.hoddmimes.distributor.samples.Publisher \
  -rate 10 \
  -displayFactor 10 \
  -minSize 64 \
  -maxSize 256 \
  -fakeErrors 0 \
  -device en0 \
  -segmentSize 8192 \
  -holdback 10 \
  -holdbackThreshold 300


