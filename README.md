# Java Multicast Distributor

The *Distributor* utility is a **publish/subscribe** messaging component. The utility provides an API allowing application to publish real time data received by one or many applications on a [LAN](https://www.cisco.com/c/en/us/products/switches/what-is-a-lan-local-area-network.html#~types).
The utility is a true _one-to-many_ transport using IP multicast as transport.

## Area of Usage
The distributor framework is primarily designed for applications that need to distribute high volumes of volatile real time information to
many receivers in a _reliable_ and _consistent_ way with low latency. 
 
It is possible to push over a million updates / sec  with a size of 50-60 bytes on a Gigabit LAN from a single publisher.
For performance characteristics see [Publisher Performance Characteristics](#publisher-performance-characteristics) below.


Snipplets for minimalist [_Publisher_](#A-minimalist-Publisher-App) and [_Subscriber_](#A-minimalist-Subscriber-App) are found below.

## Quick Start
The _Distributor_ component is developed with IntelliJ and built with _gradle_.
Before you can try _Distributor_ out you have to built it.

From command line execute the command
```
$ gradle build
```

Within IntelliJ execute the command *__Build Project__* from the *__Build__* menu item.

To try out the gui sample execute scripts [_guiPublisher.sh_](https://github.com/hoddmimes/Distributor/blob/master/guiPublisher.sh) and [_guiSubscriber.sh_](https://github.com/hoddmimes/Distributor/blob/master/guiSubscriber.sh)
alternatively _guiPublisher.bat_ and _guiSubscriber.bat_



## Background
The Distributor utility history derives from the financial industry and dissemination of market data. Some of the earlies ideas goes back as far as
to the mid -80 and the Digital Equipment NWAY protocol.  [Digital Equipment](https://en.wikipedia.org/wiki/Digital_Equipment_Corporation) at that time
sold a product in London to the trading rooms for disseminating market data using a true reliable multicast
transport based on the NWAY protocol. I then had the opertunity and pleasure to envolve the ideas when developing the OMdu component at [OMX/NASDAQ Nordic](https://en.wikipedia.org/wiki/Nasdaq_Nordic). OMdu was (is?) the reliable multicast
distribution mechanism used to distribute real time market data using LAN/Multicast. The first implementation was taken into
production -92 and it was still i production 2015. It was then implemented in C.

## API Usage

Using the Distributor API is trivial.  
  
An application create a _Distributor_ object. The Distributor object is like a handle to the Distributor utility.  
Having a Distributor instance the application can create Distributor Connections (i.e. communication channels) , Publishers and Subscriber instances.

-   **_[Distributor](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/Distributor.html)_** object, handle encapsulating the distributor utility.
-   **_[Distributor Connection](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorConnectionIf.html)_**  object, is like a transport channel encapsulating the physical transport i.e. the interface to a IP multicast socket.
-   **_[Publisher](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorPublisherIf.html)_**  object, an interface allowing applications to publish data.
-   **_[Subscriber](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorSubscriberIf.html)_**  object, an interface allowing applications to enable subscriptions and managing data subscription filters.



## Overall Design
- The focus has been on delivering real time information, high volume and low latency without any loss of data. 

- The Distributor is a true _one-to-many_ mechanism. When publishing updates it sent once and is received by one or more subscribers that have declared an interest in the information.

- The size of a message is not bound to the size of the UDP buffer size. Larger messages are broken up in segments and sent in multiple UDP packages. Distributors API re-assemble segments and delivere the data as large messages to the receiver applications.

- The _Distributor_ implements batching logic using a dynamic holdback timer. This implicates that multiple publiser messages can be sent in one in a physical UDP package.  

- The distributor uses IP [multicast](http://en.wikipedia.org/wiki/Multicast) ([RFC 1112](http://www.ietf.org/rfc/rfc1112.txt)) when publishing update messages. The IP multicast transport is an OSI layer 2 transpor
which do not guarantee delivery. Messages can get lost or be delivered out of order. The distributor implements a recovery schema guarantying published messages not being lost or duplicated. 

- Negative acknowledgment is used to signal detection of lost messages. Receivers do not acknowledge received messages as long as messages being received in sequence. 
In case a receiver discovers a loss of messages a retransmission request is published to the publisher requesting retransmission of missed messages. 
Retransmission and recover takes place over the IP multicast channel used for publishing. All recovery logic is internal to the distributor package and hidden for the applications using the distributor utility.

- Publishers keep sent messages in a retransmission cache and could serve retransmission requests from receivers as long messages are in the retransmission cache. 
  The retransmission cache is a FIFO cache with a limited size (configurable). For _Publishers_, there is one retransmission cache per _Distributor connection_ i.e. IP mulicast channel. 

- One of the more challaging scenarios are _broadcast storms_. These could occur when there are high volumes, many subscribers losing several messages is a short time frame, due to a network glitch. 
This will trigger multiple retransmissions to be sent. Which could trigger spikes and additional messages being lost and causing even more retransmission to be sent and 
there is the _broadcast storm_. In order to mitigate these scenarios the _Distributor_ implement the following logic
    - Retransmissions are broadcasted with a very short (configurable) random delay.
    - This implicates that other subscribers can detect if a retransmission that they are missing is being requested by some other subscriber. If so they will dismiss their request.
    - Publishers receiving retransmission request will delay their retransmission for a short time. If other retransmission requests for the same message are received while waiting, 
    these requests are dismissed.   

- If a retransmission request can not be serverved the received is notified via the distributor application callback interface. 

- An application may connect up to one or more physical IP multicast groups.  Publishers and subscribers that would like to share information must be connected to the same IP multicast groups i.e. IP Class D address and IP port. 

- The distributor does not persist messages and do not provide a store-and-forward pattern. Information published is volatile. 

- Publishing of messages are _not transactional_. Publishers do not know about receivers. Receivers on the other hand knows about publishers and monitor the sequense of published messages.


## Distributor Application Configuration 
When creating a [Distributor](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/Distributor.html) instance a 
[DistributorApplicationConfiguration](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorApplicationConfiguration.html) instance is passed as parameter.

Normally the _DistributorApplicationConfiguration_ object are instasiated with default parameters suitable for most application you can however modify the default parameters 
after you have created the _DistributorApplicationConfiguration_ object and before creating the _Distributor_ instance.

## Distributor Connection Configuration
When application exchange information using the _Distributor_ utility it is done over IP multicast. A single [IP class D](https://www.tutorialspoint.com/ipv4/ipv4_address_classes.htm) address translates to an physical 
Ethernet multicast group. There is a one-to-one mapping between a [DistributorConnection](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorConnectionIf.html) and a IP Multicast address.

When instantiating a _DistributorConnection_ a [DistributorConnectionConfiguration](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorConnectionConfiguration.html)
is passed as parameter. The _DistributorConnectionConfiguration_ defines the behaviour for how the distributor disseminates and receives data over that _channel_
To a large extent it defines settings for how the _Distributor_ application protocol will behave/work.
  


## Subject Names

All information published by publisher application have an associated subject name. 
And in order for subscriber application to receive data they must setup a subscriptions on the subjects that they have and interest in.

Subject names are hieratical string like _“/foo/bar/fie”_. The “/” characters is used to separate levels. 
A subject name could have an arbitrary number of levels. 

The strings “*” and “…” are used to express wildcard matching 

Publisher must publish data with absolute subject names i.e. must not contain the strings “*” or “…”. 
Subscribers may use absolute subject or wildcard subject when setting up subscriptions.

Subject names are case sensitive.
Some typical matching rules.
- “/foo/bar” will not match “/foo/bar/fie”

- “/foo/\*” will  match all subjects with two levels and starting with “/foo”

- “/foo/\*/bar/\*” will match all subjects with four levels, where level one is equal with “/foo” level three
       with “/bar” and level two and four being whatever.
“/foo/bar/…” with match anything with three or more levels, starting with “/foo” and “/bar” at level one and two. 


## Nagging Distributor Connections

The transport protocol when disseminating information with the Distributor utility is IP Multicast.  IP multicasting cater for layer 1 and 2 in the ISO/OSI model.
This implicates that there is *_no flow control or error detection_* if messages are lost or duplicated, The Distributor application _protocol_, hidden for publisher and subscriber 
application implement error detection, retransmissions and a sort of flow control. Since the mechanism is a true one-to-many there is not a one-to-one relation between a 
publisher and subscriber.   

Each distributor connection having subscribers monitors itself to examine that it does not generate too many retransmissions, constantly. This could typically happen 
if the distributor gets overloaded (i.e. running out of CPU). Receiver buffers will then be filled up and overwritten    

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

The _Distributor_ transmission protocol has virtually guaranteed delivery. The protocoll has mechanisms for detect duplicates and lost messages and take proper action to recover.
However, if messages can not be delivered in order the receiver application is notified about the exception via a callback.
Since the _Distributor_ application does not have a sophisticated flow control the most likely cause for losing messages is data overrun. This typically is caused by the receiver application 
not processing data fast enough and internal kernal buffers are filled up and eventually overwritten. Could happen due insufficient process capacity i.e. lack CPU cycles. 
The only solution for these scenarios are; process data faster. Could be done by using a more powerful machine or optimizing the subscriber app. And / or possibly subscribe to a reduced dataflow (i.e. less _subjects_). 

But it can also happen due to broadcast spikes i.e. the publisher sending a large amount of data in a short time.
That will cause the kernel receiver buffer to be filled up and overwritten. To make receiver applications more resillent 
kernal bufferes used for receiving multicast can be enlarged. By the default they may be on the lower side since normally there is not a large demand for 
handling larger volumes of multicast traffic.





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

Better and more comprehensive documentation on the subject UDP buffering could be found on the _Informatica_ website.
- [UDP Buffering Background](https://www.informatica.com/downloads/1568_high_perf_messaging_wp/Topics-in-High-Performance-Messaging.htm#UDP-BUFFERING-BACKGROUND)
- [UDP Buffer Sizing](https://www.informatica.com/downloads/1568_high_perf_messaging_wp/Topics-in-High-Performance-Messaging.htm#UDP-BUFFER-SIZING) 


#### Limiting the Transmission Rate 
Normally there is no restriction on how fast and how much data a _publisher_ can publish data. The limitation is normally on the controller side i.e. how 
much data the ethernet controller can handle. In some situations is could be necessary to pace the transmission in order not to flood
the network, during spikes. Especially at startup when an application might want to publish a snapshot of its data.

The _Distributor_ API has a primitive mechanism for controlling  how much data that could be published on a connection (i.e multicastgroup) per second. This is configured when a distributor connection is created.

When declaring a distributor connection [createConnection](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/Distributor.html#createConnection(com.hoddmimes.distributor.DistributorConnectionConfiguration))
a [connection configuration object](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorConnectionConfiguration.html) is passed as argument.

The max bandwith can be configured via the method [setMaxBandwith( int pMaxBandwidth )](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorConnectionConfiguration.html#setMaxBandwidth(double))
The value express is Mbit per second. _Note_ that the lowest value will always be 0.25 Mbit /sec.

It also possible to configure how frequently the bandwith utilization should be monitored using the method 
[setFlowRateRecalculateInterval​(long pInterval)](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorConnectionConfiguration.html#setFlowRateRecalculateInterval(long))
The default interval for checking the publishing  rate is 250 ms.


#### Ethernet Flow Control 
Even if IP Multicast is Link Level 2 protocol without flow control the Ethernet standard 802.3x specify an option for devices to send [MAC Pause messages](https://en.wikipedia.org/wiki/Ethernet_flow_control). 
This will typically happen when massive data is sent out. When a device is overwhelmed with data it can publish a MAC pause Ethenet message asking sending device to 
_slow down_ (i.e take a pause). If you are trying to push high numbers you might expiriance that you can not reach the full potential of the CPU and or network. 
There is then a good chance that someone has sent MAC pause messages. Normally this is what you want in order to avoid massive data overrun and broadcast storms.

You can however configure Ethernet controllers to ignore MAC pause request i.e. disable flow control. On windows you can do this by going into the Contral Panel, Networking, Adapters and change adapter properties. 
On linux the NIC properties are configure with the [ethtool](https://www.thegeekstuff.com/2010/10/ethtool-command/)

However, disabling the flow control increases the risk for data overrun quitesignificantly. The problem is not on the sender side to push out data.
The challange is to receive and process data, this normally requires a much bigger effort.  




#### Distributor Retransmission Cache 
The _Distributor_ keep sent messages in a memory cache. In case a subscriber has missed a message and request a 
retransmission, the publisher will lookup the the missed message in the cache and retransmit the message.
The size of the retransmission cache are configured on a multicast group level 
[setRetransmissionMaxCacheSize​(int pValue)](https://htmlpreview.github.io/?https://github.com/hoddmimes/Distributor/blob/master/javadoc/com/hoddmimes/distributor/DistributorConnectionConfiguration.html#setRetransmissionMaxCacheSize(int))
                                                                            
## Distributor Console
The _Distributor Console_ is a management gui application. The app communicates with other Distrbutor applications using _Distributor broadcasts_
The application will discover  other active Distributor application on the LAN. With the console application detailed statistics can be 
retrieved for active publishers and subscribers. It is also possible to start retrieving live broadcasts from a specific publisher 
to see message rates statistics, subjects published and message live streams.

The distributor console can be started with the script files _console.sh_ or _console.bat_
In the app the configuration multicast address and port used by application should be configured to match the (configuration multicast) address and port used by other 
distributor subscribers and publishers.



## HTTP Monitor Interface
A Distributor instance can be configured to be started with a management controller. The management controller 
may be started and exposing a WEB interface. By the fault the Management controller and HTTP service are enabled.
The HTTP port used by default is 8888. Whatever to enable/disable the management controller, the HTTP service and HTTP port interface 
is configuration parameters being passed when creating a _Distributor_ instance.

## Broadcast Gateways

_**Currently being excluded from the project**_
## WEB Socket Gateway
_**Currently being excluded from the project**_
## Publisher Performance Characteristics 

Below you can find performance characteristics for a publisher application.

**Configuration:**
- Intel i7-3930K 3.20GHz
- Manufacture ASUS [P9X79 PRO](https://www.asus.com/Motherboards/P9X79_PRO/specifications/)
- NIC [Intel 82579V](https://ark.intel.com/content/www/us/en/ark/products/52963/intel-82579v-gigabit-ethernet-phy.html)
- OS Windows 7 Professional (64 bit)

**Application**
- Java publisher app found in the sample directory, [see source here](https://github.com/hoddmimes/Distributor/blob/master/pubsub/java/main/com/hoddmimes/distributor/samples/Publisher.java)
- started with the following VM parameters _"-Xms800m -Xmx1200m"_
- started with the following program parameters _"-maximize true -holdback 100 -holdbackTreshold 0 -ipBuffer 265000 -segment 62000 -displayFactor 100000 -minSize 50 -maxSize 50 -device eth10 -rate 0"_

_With the configuration above the application is pushing  updates with a size of 50 bytes as fast as possible._ 

**Performance Characteristics**
- Updates published per second > 1.000.000 updates / sec
- Average number of published UDP message ~1260 msgs/sec.
- Average I/O transmission time (blocking I/O) ~632 usec.
- CPU utilization < 10%
- Network utilization (1 GBit) ~65%.

The bottleneck is NIC transmission, 87% of the walltime is spent in the I/O send method waiting for the I/O to complete.

**_For solutions distributing high volume data with low latency in a reliable and consistent way, the hard part is not publishing the data.
The challenge is for receiver applications to receive and processes data and not falling behind._**  
 



## A minimalist Publisher App

A bit more comprehensive publisher sample is found [here](https://github.com/hoddmimes/Distributor/blob/master/pubsub/src/main/java/com/hoddmimes/distributor/samples/Publisher.java)

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

A bit more comprehensive subscriber sample is found [here](https://github.com/hoddmimes/Distributor/blob/master/pubsub/src/main/java/com/hoddmimes/distributor/samples/Subscriber.java)

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
	public void distributorUpdate(String pSubjectName, byte[] pData, Object pCallbackParameter, int pRemoteAppId, int pDeliveryQueueLength) {

	}
}

```
</sup>