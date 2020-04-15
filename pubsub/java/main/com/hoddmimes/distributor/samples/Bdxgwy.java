package com.hoddmimes.distributor.samples;

import com.hoddmimes.distributor.DistributorApplicationConfiguration;
import com.hoddmimes.distributor.DistributorConnectionConfiguration;
import com.hoddmimes.distributor.bdxgwy.BdxGatewayParameterEntry;
import com.hoddmimes.distributor.bdxgwy.BdxGwyAuthEntry;
import com.hoddmimes.distributor.bdxgwy.BdxGwyMulticastGroupParameterEntry;
import com.hoddmimes.distributor.bdxgwy.BroadcastGateway;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class Bdxgwy
{
	final Logger mLogger = LogManager.getLogger( this.getClass().getSimpleName());

	private ArrayList<BdxGwyMulticastGroupParameterEntry> mMultiCastGroups;
	private ArrayList<BdxGatewayParameterEntry> mOutboundBroadcastGateways;
	private ArrayList<BdxGwyAuthEntry> mInboundBroadcastGateways;

	private String mBdxGwyName;
	private String mEthDevice;
	int 	mBdxGwyClientAcceptPort;
	int		mBdxGwyGatewayAcceptPort;

	private BroadcastGateway mBdxGwy;
	private String mConfigurationFile = "bdxgwy.xml";
	
	public Bdxgwy()
	{
	}


	private void parseArguments( String[] args ) {
		int i = 0;
		while( i < args.length ) {
			if (args[i].equals("-config") || args[i].equals("-configuration")) {
			  mConfigurationFile = args[i+1];
			  i++;
			}
			i++;
		}

		// Load configuration file
		try {
			Document tRootDocument = AuxXml.loadXMLFromFile( mConfigurationFile );
			Element tRoot = tRootDocument.getDocumentElement();

			mBdxGwyName = AuxXml.getStringAttribute( tRoot, "name", null);
			mBdxGwyClientAcceptPort = AuxXml.getIntAttribute( tRoot, "clientAccessPort", 0);
			mBdxGwyGatewayAcceptPort = AuxXml.getIntAttribute( tRoot, "gatewayAcceptPort", 0);
			mEthDevice = AuxXml.getStringAttribute( tRoot, "ethDevice", "eth0");

			// Parse Multicast Groups
			mMultiCastGroups = new ArrayList<>();
			Element tMcaGroups = AuxXml.getElement(tRoot,"MulticastGroups");
			NodeList tNodeList = tMcaGroups.getElementsByTagName("MulticastGroup");
			for(  i = 0; i < tNodeList.getLength(); i++ ) {
				if (tNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element tMca = (Element) tNodeList.item(i);
					mMultiCastGroups.add( new BdxGwyMulticastGroupParameterEntry( AuxXml.getStringAttribute(tMca, "address", null),
							                                                      AuxXml.getIntAttribute(tMca, "port", 0)));
				}
			}

			// Parse Outbound Broadcast Gateways
			mOutboundBroadcastGateways = new ArrayList<>();
			Element tOutboundGateways = AuxXml.getElement(tRoot,"OutboundGateways");
			tNodeList = tOutboundGateways.getElementsByTagName("Gateway");
			for(  i = 0; i < tNodeList.getLength(); i++ ) {
				if (tNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element tGwy = (Element) tNodeList.item(i);
					mOutboundBroadcastGateways.add( new BdxGatewayParameterEntry(
							AuxXml.getStringAttribute(tGwy, "name", null),
							AuxXml.getStringAttribute(tGwy, "host", null),
							AuxXml.getIntAttribute(tGwy, "port", 0)));
				}
			}

			// Parse Inbound Broadcast Gateways
			mInboundBroadcastGateways = null;
			Element tInboundGateways = AuxXml.getElement(tRoot,"InboundGateways");
			if (tInboundGateways != null) {
				tNodeList = tInboundGateways.getElementsByTagName("Gateway");
				if (tNodeList.getLength() > 0) {
					mInboundBroadcastGateways = new ArrayList<>();
					for(  i = 0; i < tNodeList.getLength(); i++ ) {
						if (tNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
							Element tGwy = (Element) tNodeList.item(i);
							mInboundBroadcastGateways.add( new BdxGwyAuthEntry( AuxXml.getStringAttribute(tGwy, "name", null),
																			    AuxXml.getStringAttribute(tGwy, "host", null)));
						}
					}
				}
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	private void execute() {
		DistributorApplicationConfiguration tAppConfig = new DistributorApplicationConfiguration( mBdxGwyName );
		tAppConfig.setEthDevice( mEthDevice );
		tAppConfig.setLogFlags( DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS +
						        DistributorApplicationConfiguration.LOG_RMTDB_EVENTS +
								DistributorApplicationConfiguration.LOG_SUBSCRIPTION_EVENTS +
				                DistributorApplicationConfiguration.LOG_ERROR_EVENTS);


		mBdxGwy = new BroadcastGateway( tAppConfig,
										mBdxGwyClientAcceptPort,
								        mBdxGwyGatewayAcceptPort,
				                        mInboundBroadcastGateways,
				                        mOutboundBroadcastGateways,
				                        mMultiCastGroups);
		mLogger.info("Successfully started broadcast gateway");

		while( true ) {
			try{ Thread.currentThread().sleep(10000L); }
			catch( InterruptedException e) {}
		}
	}
	
	public static void main(String[] args) {
		Bdxgwy bdxgwy = new Bdxgwy();
		bdxgwy.parseArguments( args  );
		bdxgwy.execute();

	}

}
