# Java Multicast Distributor

The *Distributor* utility is a **pub/sub** messaging component. The utility provides an API allowing application to publish real time data received by one or many aplications on a [LAN](https://www.cisco.com/c/en/us/products/switches/what-is-a-lan-local-area-network.html#~types).

The utility is a true _one-to-many_ transport using IP multicast as transport. A publisher publish an update once and it could be received by many subscribers. 
The IP multicast transport requires a LAN topology and take advantage of  IP multicast , see [RFC 1112](http://www.ietf.org/rfc/rfc1112.txt) and the Wikipedia article about [Multicast](http://en.wikipedia.org/wiki/Multicast) for more information.

The _Distributor_ utility main features are;

-	True _one-to-many_ distributor i.e. uses IP multicast as transport mechanism.
-	Guarantee delivery, implements recovery,  guarantying  that messages are not lost or duplicated (error free transport, not store-and-forward, nor transactional).
-	Highly efficient message distribution with focus on latency and throughput.
-	By using IP multicast groups and subject filtering application can select and subscribe to information just relevant for the application.

What is not covered by the _Distributor_

- _Store-and-forward_ messaging. Messages are not persisted. Messaged are published once i.e. fire and forget. Receiver application that like to receive data must be running and connected when the data are published. 
-  No _transactional_ messaging. Publisher application does not know about receivers nor do they know is messages are delivered to receiver apps. 
   Receiver applications on the other hand knows about publisher and detects if messages are out of sequence or lost. 





## API Usage

Using the Distributor API is trivial.  
  
An application create a Distributor object. The Distributor object is like a handle to the Distributor utility.  
Having a Distributor instance the application can create Distributor Connections, Publishers and Subscriber instances.

-   **_[Distributor](javadoc/com/hoddmimes/boreas/core/distributor/Distributor.html)_** object, handle encapsulating the distributor utility.
-   **_[Distributor Connection](javadoc/com/hoddmimes/boreas/core/distributor/DistributorConnectionIf.html)_**  object, is like a transport channel encapsulating the physical transport i.e. the interface to a IP multicast socket.
-   **_[Publisher](javadoc/com/hoddmimes/boreas/core/distributor/DistributorPublisherIf.html)_**  object, an interface allowing applications to publish data.
-   **_[Subscriber](javadoc/com/hoddmimes/boreas/core/distributor/DistributorSubscriberIf.html)_**  object, an interface allowing applications to enable subscriptions and managing data subscription filters.

## A minimalist Publisher App

A small publisher sample is found [here](pubsub/java/main/com/hoddmimes/distributor/samples/Publisher.java)

<sup>
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "age": 25
}
```
</sup>



import com.hoddmimes.distributor.*;


public class Publisher {

	private static final String EthDevice = "eth0";
	private static final String IpAddress = "224.10.10.44";
	private static final int UdpPort = 5656;
	private static final String Subject = "/subject/test";

	private static final int LogFlags =  DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS +
	                         DistributorApplicationConfiguration.LOG_RMTDB_EVENTS +
					         DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS;
	
	
	
	public static void main(String[] pArgs) {
		try {
			DistributorApplicationConfiguration tApplConfig = new DistributorApplicationConfiguration( "publisher" );
			tApplConfig.setEthDevice( EthDevice );
			tApplConfig.setLogFlags( LogFlags );

			Distributor tDistributor = new Distributor(tApplConfig);

			DistributorConnectionConfiguration tConnConfig = new DistributorConnectionConfiguration( IpAddress, UdpPort );

			DistributorConnectionIf tDistributorConnection = tDistributor.createConnection( tConnConfig );
			DistributorPublisherIf tPublisher = tDistributor.createPublisher( tDistributorConnection, new DistributorEventCallbackHandler("PUBLISHER"));

			tPublisher.publish( Subject, "Hello World".getBytes());
			System.out.println("All done");
			System.exit(0);
		}
		catch( DistributorException e) {
			e.printStackTrace();
		}
	}

	
	static class DistributorEventCallbackHandler implements DistributorEventCallbackIf
	{
		String mType;
		
		DistributorEventCallbackHandler( String pType ) {
			mType = pType;
		}

		@Override
		public void distributorEventCallback(DistributorEvent pDistributorEvent) 
		{
			System.out.println("Distributor Application Event [" + mType + "]\n" + pDistributorEvent.toString() );
		}
	}
}```

