#!/bin/bash
#
java -cp ../lib/distributor-$version$.jar:../lib/distributorSample-$version$.jar com.hoddmimes.distributor.snoop.DistributorSnoop -config snoop-configuration.xml
