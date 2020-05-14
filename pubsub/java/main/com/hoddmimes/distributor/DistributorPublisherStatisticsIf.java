package com.hoddmimes.distributor;

import com.hoddmimes.distributor.api.CounterElement;

public interface DistributorPublisherStatisticsIf
{
    /**
 * Returns the average fill rate of allocated segments when being sent
 * @return, fill rate in percentage
 */
public double getXtaAvgMessageFillRate();

    /**
     * Returns the average number of updates per sent UDP message
     * @return, avg updates / UDP package
     */
    public double getXtaAvgUpdatesPerMessage();

    /**
     * Return the average I/O transmission time when sending user data UPD packages
     * @return the avg transmission time in usec.
     */
    public double getXtaAvgIOXTimeUsec();

    /**
     * Returns the total number of user updates sent
     * @return, tot nomber of updates
     */
    public long  getXtaTotalNumberOfUpdates();


    /**
     * Return total number of UDP messages being sent
     * @return
     */
    public long   getXtaTotalNumberOfMessages();

    /**
     * Returns, total number of bytes published
     * @return, tot number of bytes published
     */
    public long   getXtaTotalNumberOfBytes();

    /**
     * Returns average values / sec for aggregated updates under 1 min
     * @return avg update / sec
     */
    public CounterElement getXta1MinUpdates();

    /**
     * Returns average values / sec for aggregated segment/messages under 1 min
     * @return avg msgs / sec
     */
    public CounterElement getXta1MinMessages();


    /**
     * Returns average values / sec for aggregated bytes under 1 min
     * @return avg msgs / sec
     */
    public CounterElement getXta1MinBytes();

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
