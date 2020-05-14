package com.hoddmimes.distributor;

import com.hoddmimes.distributor.api.CounterElement;

public interface DistributorSubscriberStatisticsIf
{
    /**
     * Returns the total number of user updates received
     * @return, tot number of updates
     */
    public long  getRcvTotalNumberOfUpdates();

    /**
     * Return total number of UDP messages being received
     * @return
     */
    public long   getRcvTotalNumberOfMessages();

    /**
     * Returns, total number of bytes received
     * @return, tot number of bytes received
     */
    public long   getRcvTotalNumberOfBytes();

    /**
     * Returns average values / sec for aggregated updates under 1 min
     * @return avg update / sec
     */
    public CounterElement getRcv1MinUpdates();

    /**
     * Returns average values / sec for aggregated segment/messages under 1 min
     * @return avg msgs / sec
     */
    public CounterElement getRcv1MinMessages();


    /**
     * Returns average values / sec for aggregated bytes under 1 min
     * @return avg msgs / sec
     */
    public CounterElement getRcv1MinBytes();


    /**
     * Return the time string "yyyy-MM-dd HH:mm:ss.SSS" when the collection of statistics started
     * @return, time string
     */
    public String getInitTime();

    /**
     * Return the number of seconds since the collection of statistics started
     * @return, number of seconds
     */
    public int getSecondsSinceInit();
}
