#!/bin/bash
#
java -cp "./libs/*" com.hoddmimes.distributor.samples.Subscriber \
  -device en0 \
  -displayFactor 10 \
  -segmentSize 8192 \
  -fakeErrors 0
