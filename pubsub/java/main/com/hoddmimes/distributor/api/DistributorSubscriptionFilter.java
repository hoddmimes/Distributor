package com.hoddmimes.distributor.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.hoddmimes.distributor.DistributorException;
import com.hoddmimes.distributor.DistributorUpdateCallbackIf;

public class DistributorSubscriptionFilter {
	public static final char DELIMITER = '/';
	public static final String WILDCARD = "*";
	public static final String WILDREST = "...";

	enum NodeType {
		NORMAL, WILDCARD, WILDREST
	};

	KeyNode mRoot;
	HashMap<Object, KeyNode> mSubscriptionMap;

	public DistributorSubscriptionFilter() {
		mRoot = new KeyNode("ROOT");
		mSubscriptionMap = new HashMap<Object, KeyNode>();
	}

	public int getActiveSubscriptions() {
		return mRoot.countActiveSubscriptions();
	}

	public Object add(String pSubjectName, DistributorUpdateCallbackIf pCallback, Object pCallbackParameter) throws DistributorException {
		SubjectTokenParser tKeys = new SubjectTokenParser(pSubjectName);
		if (!tKeys.hasMore()) {
			throw new DistributorException("Invalid pSubjectName: " + pSubjectName);
		}

		KeyNode tKeyNode = mRoot;
		while (tKeys.hasMore()) {
			tKeyNode = tKeyNode.addChild(tKeys.getNextElement());
		}

		return tKeyNode.addSubscription(pSubjectName, pCallback,
				pCallbackParameter);
	}

	public void match(String pSubjectName, byte[] pData, int pAppId, int pQueueLength) {
		SubjectTokenParser tKeys = new SubjectTokenParser(pSubjectName);
		mRoot.matchRecursive(pSubjectName, tKeys, pData, pAppId, pQueueLength);
	}

	public boolean matchAny(String pSubjectName) {
		SubjectTokenParser tKeys = new SubjectTokenParser(pSubjectName);
		return mRoot.matchAny(tKeys);
	}

	public String[] getActiveSubscriptionsStrings() {
		Vector<String> tVector = new Vector<String>();
		mRoot.getActiveSubscriptionsStrings(tVector, "");
		if (tVector.size() == 0) {
			return null;
		} else {
			return tVector.toArray(new String[0]);
		}
	}

	@Override
	public String toString() {
		StringBuffer tSB = new StringBuffer();
		mRoot.dumpSubscriptions(tSB, "");
		return tSB.toString();
	}

	public void remove(Object pHandle) {
		if (pHandle == null) {
			mRoot.removeAll();
			mSubscriptionMap.clear();
		} else {
			KeyNode tKeyNode = mSubscriptionMap.remove(pHandle);
			if ((tKeyNode != null) && (tKeyNode.mSubscriptions != null)) {
				tKeyNode.mSubscriptions.remove(pHandle);
				
			}
			tKeyNode = null;
		}
	}

	class Subscription {
		String mSubjectName;
		DistributorUpdateCallbackIf mCallback;
		Object mCallbackParameter;

		Subscription(String pSubjectName,
				DistributorUpdateCallbackIf pCallback, Object pCallbackParameter) {
			mSubjectName = pSubjectName;
			mCallback = pCallback;
			mCallbackParameter = pCallbackParameter;
		}
	}
 
	private void test() {
		int i = 0;
		try {

			String pSubjectname = "/z/...";
			add(pSubjectname, new SubscriberCallback(pSubjectname), null);
			
			String tUpdateSubjectName = "/foo/bar/fie";
			System.out.println("MatchAny: " + this.matchAny(tUpdateSubjectName));
			this.match(tUpdateSubjectName, null, 0, 0);
			
			return;
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DistributorSubscriptionFilter tFilter = new DistributorSubscriptionFilter();
		tFilter.test();
	}

	class SubscriberCallback implements DistributorUpdateCallbackIf {
		String mPattern;

		SubscriberCallback(String pPattern) {
			mPattern = pPattern;
		}

		public void distributorUpdate(String pSubjectName, byte[] pData, Object pCallbackParameter, int pAppId, int pDeliveryQueueLength) {
			 System.out.println("SUBCRIBER Subject: [" + pSubjectName +  "] Matching Subscription: [" + mPattern + "]");
		}
	}

	/**
	 * The key node class hold subscription per level Each KeyNode instance
	 * represent a level in the name hiaracy
	 * 
	 * @author POBE
	 * 
	 */

	class KeyNode {
		String mKey;
		NodeType mType;

		HashMap<String, KeyNode> mChildren; // children keys in the name heiracy
		KeyNode mWildcardChild; // wildcard children
		KeyNode mWildcardRestChild; // wild card rest
		ArrayList<Subscription> mSubscriptions; // subscriptions onthis level

		KeyNode(String pKey) {
			mKey = pKey;
			mChildren = null;
			mWildcardChild = null;
			mWildcardRestChild = null;

			if (mKey.equals(WILDCARD)) {
				mType = NodeType.WILDCARD;
			} else if (mKey.equals(WILDREST)) {
				mType = NodeType.WILDREST;
			} else {
				mType = NodeType.NORMAL;
			}
		}

		KeyNode addChild(String pChildKey) throws DistributorException {
			if (mType.equals(NodeType.WILDREST)) {
				throw new DistributorException( "Can not not add child to a \"WildcardRest\" node");
			}

			KeyNode tKeyNode = null;

			if (pChildKey.equals(WILDCARD)) {
				if (mWildcardChild == null) {
					mWildcardChild = new KeyNode(pChildKey);
				}
				return mWildcardChild;
			} else if (pChildKey.equals(WILDREST)) {
				if (mWildcardRestChild == null) {
					mWildcardRestChild = new KeyNode(pChildKey);
				}
				return mWildcardRestChild;
			} else {
				if (mChildren == null) {
					mChildren = new HashMap<String, KeyNode>();
				}
				tKeyNode = mChildren.get(pChildKey);
				if (tKeyNode == null) {
					tKeyNode = new KeyNode(pChildKey);
					mChildren.put(pChildKey, tKeyNode);
				}
			}
			return tKeyNode;
		}

		Subscription addSubscription(String pSubjectName,
				DistributorUpdateCallbackIf pCallback, Object pCallbackParameter) {
			Subscription tSubscription = new Subscription(pSubjectName,
					pCallback, pCallbackParameter);
			if (mSubscriptions == null) {
				mSubscriptions = new ArrayList<Subscription>();
			}
			mSubscriptions.add(tSubscription);
			mSubscriptionMap.put(tSubscription, this);
			return tSubscription;
		}

		void getActiveSubscriptionsStrings(Vector<String> pVector,
				String pPrefix) {
			if ((mSubscriptions != null) && (mSubscriptions.size() > 0)) {
				pVector.add("References: " + mSubscriptions.size() + " Topic: "
						+ pPrefix + "/" + mKey + "\n");
			}

			Iterator<KeyNode> tItr = null;

			if (mKey.equals("ROOT")) {
				if (mChildren != null) {
					tItr = mChildren.values().iterator();
					while (tItr.hasNext()) {
						tItr.next().getActiveSubscriptionsStrings(pVector, "");
					}
				}
				if (mWildcardChild != null) {
					mWildcardChild.getActiveSubscriptionsStrings(pVector, "");
				}
				if (mWildcardRestChild != null) {
					mWildcardRestChild.getActiveSubscriptionsStrings(pVector,
							"");
				}
			} else {
				if (mChildren != null) {
					tItr = mChildren.values().iterator();
					while (tItr.hasNext()) {
						tItr.next().getActiveSubscriptionsStrings(pVector,
								pPrefix + "/" + mKey);
					}
				}
				if (mWildcardChild != null) {
					mWildcardChild.getActiveSubscriptionsStrings(pVector,
							pPrefix + "/" + mKey);
				}
				if (mWildcardRestChild != null) {
					mWildcardRestChild.getActiveSubscriptionsStrings(pVector,
							pPrefix + "/" + mKey);
				}
			}
		}

		void dumpSubscriptions(StringBuffer pSB, String pPrefix) {
			if ((mSubscriptions != null) && (mSubscriptions.size() > 0)) {
				pSB.append("References: " + mSubscriptions.size() + " Topic: "
						+ pPrefix + "/" + mKey + "\n");
			}

			Iterator<KeyNode> tItr = null;

			if (mKey.equals("ROOT")) {
				if (mChildren != null) {
					tItr = mChildren.values().iterator();
					while (tItr.hasNext()) {
						tItr.next().dumpSubscriptions(pSB, "");
					}
				}
				if (mWildcardChild != null) {
					mWildcardChild.dumpSubscriptions(pSB, "");
				}
				if (mWildcardRestChild != null) {
					mWildcardRestChild.dumpSubscriptions(pSB, "");
				}
			} else {
				if (mChildren != null) {
					tItr = mChildren.values().iterator();
					while (tItr.hasNext()) {
						tItr.next()
								.dumpSubscriptions(pSB, pPrefix + "/" + mKey);
					}
				}
				if (mWildcardChild != null) {
					mWildcardChild.dumpSubscriptions(pSB, pPrefix + "/" + mKey);
				}
				if (mWildcardRestChild != null) {
					mWildcardRestChild.dumpSubscriptions(pSB, pPrefix + "/"
							+ mKey);
				}
			}
		}

		public void removeAll() {
			mKey = null;
			mSubscriptions = null;

			
			if (mWildcardChild != null) {
				mWildcardChild.removeAll();
			}
			if (mWildcardRestChild != null) {
				mWildcardRestChild.removeAll();
			}
			mWildcardChild = null;
			mWildcardRestChild = null;
			
			if (mChildren == null) {
				return;
			}
			
			Iterator<KeyNode> tItr = mChildren.values().iterator();
			while (tItr.hasNext()) {
				tItr.next().removeAll();
			}
			mChildren = null;

		}

		int countActiveSubscriptions() {
			int tCount = 0;
			if (mChildren != null) {
				Iterator<KeyNode> tItr = mChildren.values().iterator();
				while (tItr.hasNext()) {
					tCount += tItr.next().countActiveSubscriptions();
				}
			}
			if (mSubscriptions != null) {
				tCount += mSubscriptions.size();
			}
			if (mWildcardChild != null) {
				tCount += mWildcardChild.countActiveSubscriptions();
			}
			if (mWildcardRestChild != null) {
				tCount += mWildcardRestChild.countActiveSubscriptions();
			}

			return tCount;
		}

		boolean matchAny(SubjectTokenParser pKeys) {

			/**
			 * Traversed the whole key 
			 * look if there are any subscriber at this level
			 * if so return true  
			 */
			if (!pKeys.hasMore()) {
				if ((mSubscriptions != null) && (mSubscriptions.size() > 0)) {
					return true;
				} else {
					return false;
				}
			}

			/**
			 * Examine if there are any wildcard subscribers at this level if so return true
			 */
			if ((mWildcardRestChild != null) && (mWildcardRestChild.mSubscriptions.size() > 0)) {
			   return true;
			} 

			if (mWildcardChild != null) {
				pKeys.getNextElement();
				if (mWildcardChild.matchAny(pKeys)) {
					return true;
				}
			}
			

			if (mChildren != null) {
				KeyNode tKeyNode = mChildren.get(pKeys.getNextElement());
				if (tKeyNode != null) {
					return tKeyNode.matchAny(pKeys);
				}
			}

			return false;
		}

		void matchRecursive(String pSubjectName, SubjectTokenParser pKeys,byte[] pData, int pAppId, int pQueueLength) {
			if (!pKeys.hasMore()) {
				if (mSubscriptions != null) {
					Iterator<Subscription> tItr = mSubscriptions.iterator();
					while (tItr.hasNext()) {
						Subscription tSubscription = tItr.next();
						tSubscription.mCallback.distributorUpdate(pSubjectName,
								pData, tSubscription.mCallbackParameter,
								pAppId,
								pQueueLength);
					}
				}
			} else {
				KeyNode tKeyNode = null;
				if (mChildren != null) {
					tKeyNode = mChildren.get(pKeys.getNextElement());
					if (tKeyNode != null) {
						tKeyNode.matchRecursive(pSubjectName, pKeys,  pData, pAppId,pQueueLength);
					}
				}
				if (mWildcardChild != null) {
					pKeys.getNextElement();
					mWildcardChild.matchRecursive(pSubjectName, pKeys, pData, pAppId, pQueueLength);
				}
			}

			if (mWildcardRestChild != null) {
				Iterator<Subscription> tItr = mWildcardRestChild.mSubscriptions.iterator();
				while (tItr.hasNext()) {
					Subscription tSubscription = tItr.next();
					tSubscription.mCallback.distributorUpdate(pSubjectName,
							pData, tSubscription.mCallbackParameter,
							pAppId, pQueueLength);
				}
			}

		}

	}

}
