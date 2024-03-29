package com.hoddmimes.distributor;

/**
 * This interface wrappes a publisher allowing an application to publish data
 * over a connection to subscribers being connected to the "connection".
 * 
 * @author POBE
 * 
 */

public interface DistributorPublisherIf {
	/**
	 * Publish an update on the connection. When publishing data a
	 * <i>subject/topic</i> is associated with the update. Subscribers can when
	 * setting up subscriptions specify what subjects/topics they have an
	 * interest in. The distributor will perform the filtering and just deliver
	 * updates that match the subjects/topics the subscriber has an interest in.
	 * The subject string is a Hierarchical   name string where levels are divided
	 * by the character &quot;/&quot; for example  &quot;/foo/bar/fie &quot;. The characters (strings)
	 *  &quot;* &quot; and  &quot;... &quot; works as wildcards.
	 *  <i><b>These wildcard strings must not be present in a subject when publishing data</i></b> but are applicable when setting up an
	 * subscription. The  &quot;* &quot; match any string at a certain level, for example
	 *  &quot;/foo/* /fie&quot; will match any string at the second level. The string  &quot;... &quot;
	 * matches all strings at the current level and all sublevels, for example
	 *  &quot;/foo/... &quot; will match  &quot;/foo/bar/fie &quot; and  &quot;/foo/allan/gazonk/47111 &quot; etc.
	 * 
	 * @param pSubjectname subject name
	 * @param pData data to be published
	 * @return the time in usec the IP transmission took i.e. the time spent in the <i>sendto</i>
	 * @throws DistributorException, send error exception in case of error
	 */
	public int publish(String pSubjectname, byte[] pData) throws DistributorException;

	/**
	 * Close and removes the publisher
	 * @throws DistributorException, close exception error.
	 */
	public void close() throws DistributorException;

	/**
	 * Returns the volatile id that uniquely identifies the publisher on the
	 * application/ connection / host
	 * 
	 * @return long publisher identifier
	 */
	public long getId();


	/**
	 * Returns publisher statistic counters
	 * @return publisher statistic interface DistributorPublisherStatisticsIf
	 * @throws DistributorException, in case the publisher has been closed
	 */
	public DistributorPublisherStatisticsIf getStatistics() throws DistributorException;
}
