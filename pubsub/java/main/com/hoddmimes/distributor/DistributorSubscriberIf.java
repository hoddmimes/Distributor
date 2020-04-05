package com.hoddmimes.distributor;

/**
 * Interface wrapping an subscriber object. Having an subscriber instance an
 * application will be allowed to setup subscription for information that the
 * application have an interest in.
 * 
 * By default the subscriber does not receive any updates. The application must
 * invoke the method <i>addSubscription</i> in order to register an interest. When
 * setting up a subscription the application specifies a subject/topic the
 * application has an interest in. The distributor will perform the filtering
 * and just deliver updates that match the subjects/topics the subscriber has an
 * interest in. The subject string is a hierarchical name string where levels are
 * divided by the character  &quot;/ &quot; for example  &quot;/foo/bar/fie &quot;. The characters
 * (strings)  &quot;* &quot; and  &quot;... &quot; works as wildcards. <i><b>These strings must not be present in a subject when publishing data</b></i>
 * but are applicable when setting up an subscription. The  &quot;* &quot; match any string at a certain level, for example
 *  &quot;/foo/* /fie &quot; will match any string at the second level. The string  &quot;... &quot;
 * matches all strings at the current level and all sublevels, for example
 *  &quot;/foo/... &quot; will match  &quot;/foo/bar/fie &quot; and  &quot;/foo/allan/gazonk/47111 &quot; etc.
 * 
 * @author POBE
 * 
 */
public interface DistributorSubscriberIf {

	/**
	 * Add an subscription. Any update to any subject/topic matching specified
	 * subject name will unsolicited be passed to the 
	 * <A HREF="DistributorUpdateCallbackIf.html">DistributorUpdateCallbackIf</A>
	 * being specified when the subscriber was created.
	 * 
	 * @param pSubjectName  subject / topic string, may contain wildcards.
	 * @param pCallbackParameter callback parameter being passed to the,
	 *   <A HREF="DistributorUpdateCallbackIf.html">DistributorUpdateCallbackIf</A> 
	 *   when information being updated.
	 * @return Object handle identifying the subscription request
	 * @throws DistributorException, add subscription exception error
	 */
	public Object addSubscription(String pSubjectName, Object pCallbackParameter)
			throws DistributorException;

	/**
	 * Removes an active subscription
	 * 
	 * @param pHandle handle identifying the subscription. Passing "null" as argument will remove all active subscriptions.
	 * @throws DistributorException, remove exception error.
	 */
	public void removeSubscription(Object pHandle) throws DistributorException;

	/**
	 * Close and removes the subscriber. All active subscriptions are implicitly removed.
	 */
	public void close();

	/**
	 * Returns the unique subscriber identifier.
	 * 
	 * @return long subscriber identifier
	 */
	public long getId();

}
