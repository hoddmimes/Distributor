

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
}
