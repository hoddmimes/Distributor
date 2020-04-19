# Java Multicast Distributor

The *Distributor* utility is a **pub/sub** messaging component. The utility provides an API allowing application to publish real time data received by one or many aplications on a [LAN](https://www.cisco.com/c/en/us/products/switches/what-is-a-lan-local-area-network.html#~types).
The utility is a true _one-to-many_ transport using IP multicast as transport.


Snipplets for minimalist [_Publisher_](#A-minimalist-Publisher-App) and [_Subscriber_](#A-minimalist-Subscriber-App) are found below.


## API Usage

Using the Distributor API is trivial.  
  
An application create a Distributor object. The Distributor object is like a handle to the Distributor utility.  
Having a Distributor instance the application can create Distributor Connections, Publishers and Subscriber instances.

-   **_[Distributor](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/Distributor.html)_** object, handle encapsulating the distributor utility.
-   **_[Distributor Connection](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorConnectionIf.html)_**  object, is like a transport channel encapsulating the physical transport i.e. the interface to a IP multicast socket.
-   **_[Publisher](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorPublisherIf.html)_**  object, an interface allowing applications to publish data.
-   **_[Subscriber](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorSubscriberIf.html)_**  object, an interface allowing applications to enable subscriptions and managing data subscription filters.



## Overall Design
- The focus has been on delivering real time information, high volume and low latency without any loss of data. 

- The Distributor is a true _one-to-many_ mechanism. When publishing updates it sent once and is received by one or more subscribers that have declared an interest in the information.

- The distributor uses IP [multicast](http://en.wikipedia.org/wiki/Multicast) ([RFC 1112](http://www.ietf.org/rfc/rfc1112.txt)) when publishing update messages. The IP multicast transport is not a guarantee transport, messages can get lost or be delivered out of order. The distributor implements a recovery schema guarantying published messages not being lost or duplicated. 

- Negative acknowledgment is used to signal detection of lost messages. Receivers do not acknowledge received messages as long as messages being received in sequence. In case a receiver discovers a loss of messages a retransmission request is published to the publisher requesting retransmission of missed messages. Retransmission and recover takes place over the IP multicast channel used for publishing. All recovery logic is internal to the distributor package and hidden for the applications using the distributor utility.

- Publishers keep sent messages in a retransmission cache and could serve retransmission requests from receivers as long messages are in the retransmission cache. The retransmission cache is a FIFO cache with a limited size (configurable). 

- If a retransmission request can not be serverved the received is notified via the distributor application callback interface. 

- An application may connect up to one or more physical IP multicast groups.  Publishers and subscribers that would like to share information must be connected to the same IP multicast groups i.e. IP Class D address and IP port. 

- The size of a message is not bound to the size of the UDP buffer size. Larger messages are broken up in segments and sent in multiple UDP packaes and re-assembled by receiving  distribuors and delivered as large messages to the receiver applications.

- The distrbutor does not persist messages and do not provide a store-and-forward pattern. Information published is volatile. 

- Publishing of messages are _not transactional_. Publishers do not know about receivers. Receivers on the other hand knows about publishers and monitor the sequense of published messages.


## Distributor Application Configuration 
When creating a [Distributor](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/tree/master/javadoc/com/hoddmimes/distributor/Distributor.html) instance a 
[DistributorApplicationConfiguration](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/tree/master/javadoc/com/hoddmimes/distributor/DistributorApplicationConfiguration.html) instance is passed as parameter.

Normally the _DistributorApplicationConfiguration_ object are instasiated with default parameters suitable for most application you can however modify the default parameters 
after you have created the _DistributorApplicationConfiguration_ object and before creating the _Distributor_ instance.

## Distributor Connection Configuration
When application exchange information using the _Distributor_ utility it is done over IP multicast. A single [IP class D](https://www.tutorialspoint.com/ipv4/ipv4_address_classes.htm) address translates to an physical 
Ethernet multicast group. There is a one-to-one mapping between a [DistributorConnection](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/tree/master/javadoc/com/hoddmimes/distributor/DistributorConnectionIf.html) and a IP Multicast address.

When instasiating a a _DistributorConnection_ a [DistributorConnectionConfiguration](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/tree/master/javadoc/com/hoddmimes/distributor/DistributorConnectionConfiguration.html)
is passed as parameter. The _DistributorConnectionConfiguration_ defines the behaviour of how the distrbutor disaminates and receivs data for that _channel_
To a large extent it defines settings for how the _Distributor_ application protocol will behave/work.
  


## Subject Names

All information published by publisher application have an associated subject name. 
And in order for subscriber application to receive data they must setup subscriptions on subjects that they have and interest in.

Subject names are hieratical string like _“/foo/bar/fie”_. The “/” characters is used to separate levels. 
A subject name could have an arbitrary number of levels. 

The strings “*” and “…” are used to express wildcard matching 

Publisher must publish data with absolute subject names i.e. must not contain the strings “*” or “…”. 
Subscribers may use absolute subject or wildcard subject when setting up subscriptions.

Subject names are case sensitive.
Some typical matching rules.
- “/foo/bar” will not match “/foo/bar/fie”

- “/foo/*” will  match all subjects with two levels and starting with “/foo”

- “/foo/*/bar/*” will match all subjects with four levels where level one is equal with “/foo” level three
       with “/bar” and level two and four being whatever.
“/foo/bar/…” with match anything with three or more levels starting with “/foo” and “/bar” at level  one and two. 


## Nagging Distributor Connections

The transport protocol when dissaminating information with the Distributor utility is IP Multicast.  IP multicasting cater for layer 1 and 2 in the ISO/OSI model.
This implicates that there is *_no flow control or error detection_* if messages are lost or duplicated, The Distributor application _protocol_, hidden for publisher and subscriber 
application implement error detection, retransmissions and and a sort of flow control. Since the mechanism is a true one-to-many there is not a one-to-one relation betweeen a 
publisher and subscriber.   

Each distributor connection having subscriber monitors itself to examine that it does not generate too many retransmission constantly. This could typically happen 
if the distributor get overloaded (i.e. running out of CPU). Receiver buffers will then be filled up and overwritten    

Three configurable parameters are used to control the nagging monitor behavior:
- naggingWindowInterval
- naggingCheckInterval
- naggingMaxRetransmissions

If the parameter naggingWindowInterval is set to “0” the nagging functionality is disabled.
The nagging monitor will examine if retransmissions have constantly been generated over a time period and if the number of retransmission generated over the period has exceeded a max threshold value. If so a nagging event are generated to the local subscribers and publishers using the connection. Furthermore the connection will stop requesting retransmission and stop processing incoming update messages. This is considered to be a fatal situation and the connection should be closed down by the application. 
More in detail the nagging algorithm works as follows:

<sup>

```

startTime = 0;
intervalRetransmission = 0
totRetransmissions = 0;

// The following code runs periodically with a frequency specified by the parameter, naggingWindowInterval
 If (intervalRetransmissions > 0) {
    totRetransmissions +=  intervalRetransmissions;
 }  else {
      reset startTime, intervalRetransmissions, totRetransmissions;
 }
 If (((currentTime – startTime >= naggingCheckInterval)  && 
     (totRetransmission >= naggingMaxRetransmissions)) {
    generate-nagging-event-to-publishers-and-subscribers();
 } else {
        reset startTime, intervalRetransmissions, totRetransmissions;
 }
```

</sup>


## Retransmission 

The _Distributor_ transmission protocol has  guarateed delivery, to some extent. The ptotocoll has mechanisms for detect duplicates and lost messages and take proper action to recover.
However is messages can not be delivered in order the receiver application is notified about the exception via a callback.
Since the _Distributor_ application does not have a sophisticated flow control the most likely cause for losing messages is data overrun. This typically is caused by the receiver application 
not processing data fast enough and internal kernal buffers are filled up and eventually overwritten. Could happen due insufficient process capacity i.e. lack CPU cycles. 
The only solution for these scenarios are; process data faster. Could be done by using a more powerfull machine or optimizing the subscriber app. And possibly subscribe to less data. 

But it can also happen due to broadcast spikes i.e. the publisher sending a large amount of data in a short time.
That will cause the kernel receiver buffer to be filled up an over written. To make receiver applications more ressilient 
kernal bufferes used for receiving multicast can be enlarged. By the default they may be on the lowerside since normally there is not a large demand for 
handling larger volumes of multicast traffic

You have to have administrator privileges to change the kernal buffer configuration.

**_On Linux_**  

// to read current value 
```    
$ sysctl net.core.rmem_max     // display max size for kernel read buffers (default 131071)
$ sysctl net.core.wmem_max     // display max size for kernel send  buffers (default 131071)
```

// to modify the buffer sizes
```    
$ sysctl  –w net.core.rmem_max = 16777216
$ sysctl  –w net.core.wmem_max  = 16777216
```  
The values could be changed dynamically but will not be persisted. In case you reboot your system the setting will go back to the default values. By adding the values to the file /etc/sysctl.conf values can be set at boot time.
If you would like to get information about if you have any data overruns on the eth interface you can use the ifconfig command.
```   
$ ifconfig <eth-device> 	! Typically eth0, eth1 etc
eth1      Link encap:Ethernet  HWaddr 00:50:04:2F:1E:4
               inet6 addr: fe80::250:4ff:fe2f:1e42/64 Scope:Link
               UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
               RX packets:23099429 errors:0 dropped:0 overruns:0 frame:0
               TX packets:927844 errors:0 dropped:0 overruns:0 carrier:0
               collisions:0 txqueuelen:1000
               RX bytes:2638670187 (2.4 GiB)  TX bytes:108286946 (103.2 MiB)
               Interrupt:18 Base address:0xef80
```  

Better and more comprehensive documentation on subject UDP buffering could be found on informatica web site.
- [UDP Buffering Background](https://www.informatica.com/downloads/1568_high_perf_messaging_wp/Topics-in-High-Performance-Messaging.htm#UDP-BUFFERING-BACKGROUND)
- [UDP Buffer Sizing](https://www.informatica.com/downloads/1568_high_perf_messaging_wp/Topics-in-High-Performance-Messaging.htm#UDP-BUFFER-SIZING) 




## Broadcast Gateways
The multicast distribution is possible on LAN and is normally not working in a routed network. However multicast traffic can be _routed_ 
using a Muticast Routing protocol (PIM, MOSPF etc.) These protocols are normally not enable and require some configuration in routers, if being supported.

The _Distrbutor_ library includes some code allowing multicast to be _routed_ between two LAN using a P-2-P connection.
For more information about the _Distributor_ broadcast gateway see [Distributor Broadcast Gateway] (https://github.com/hoddmimes/Distributor/blob/master/doc/bdxgwy.md) 




## A minimalist Publisher App

A bit more comprehensive publisher sample is found [here](https://github.com/hoddmimes/Distributor/blob/master/pubsub/java/main/com/hoddmimes/distributor/samples/Publisher.java)

<sup>


```
import com.hoddmimes.distributor.*;

public class Publisher {

	private static final String EthDevice = "eth0";
	private static final String McaAddress = "224.10.10.44";
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

			DistributorConnectionConfiguration tConnConfig = new DistributorConnectionConfiguration( McaAddress, UdpPort );

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
		public void distributorEventCallback(DistributorEvent pDistributorEvent) {
			System.out.println("Distributor Application Event [" + mType + "]\n" + pDistributorEvent.toString() );
		}
	}
}

```
</sup>


## A minimalist Subscriber App

A bit more comprehensive subscriber sample is found [here](https://github.com/hoddmimes/Distributor/blob/master/pubsub/java/main/com/hoddmimes/distributor/samples/Subscriber.java)

<sup>


```

import com.hoddmimes.distributor.*;


public class MiniSubscriber implements DistributorEventCallbackIf, DistributorUpdateCallbackIf {

	private static final String EthDevice = "eth0";
	private static final String McaAddress = "224.10.10.44";
	private static final int UdpPort = 5656;
	private static final String Subject = "/subject/test";

	private static final int LogFlags = DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS +
			DistributorApplicationConfiguration.LOG_RMTDB_EVENTS +
			DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS;


	public static void main(String[] pArgs) {
		MiniSubscriber tSubscriber = new MiniSubscriber();
		tSubscriber.execute();
		while (true) {
			try { Thread.sleep(1000L); }
			catch (InterruptedException e) {}
		}
	}

	private void execute() {
		try {
			DistributorApplicationConfiguration tApplConfig = new DistributorApplicationConfiguration("Subscriber");
			tApplConfig.setEthDevice(EthDevice);
			tApplConfig.setLogFlags(LogFlags);

			Distributor tDistributor = new Distributor(tApplConfig);

			DistributorConnectionConfiguration tConnConfig = new DistributorConnectionConfiguration(McaAddress, UdpPort);

			DistributorConnectionIf tDistributorConnection = tDistributor.createConnection(tConnConfig);
			DistributorSubscriberIf tSubscriber = tDistributor.createSubscriber(tDistributorConnection, this, this);

			tSubscriber.addSubscription(Subject, "callback-parameter");

		} catch (DistributorException e) {
			e.printStackTrace();
		}
	}	


	@Override
	public void distributorEventCallback(DistributorEvent pDistributorEvent) {
		System.out.println("Distributor Application Event \n   " + pDistributorEvent.toString());
	}

	@Override
	public void distributorUpdate(String pSubjectName, byte[] pData, Object pCallbackParameter, int pDeliveryQueueLength) {

	}
}

```
</sup>