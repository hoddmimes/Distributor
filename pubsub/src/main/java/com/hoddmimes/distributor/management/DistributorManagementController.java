package com.hoddmimes.distributor.management;

import com.hoddmimes.distributor.*;
import com.hoddmimes.distributor.generated.messages.*;
import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.tcpip.TcpIpConnection;
import com.hoddmimes.distributor.tcpip.TcpIpServer;
import com.hoddmimes.distributor.tcpip.TcpIpServerCallbackInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
	This class implements a monitor interface to the Distributor component.
	If configured and started, the Distributor component will on the CMA (configuration multicast address) publish
	it configuration and status. It will also listen for other Distributor instances. The information is exposed as
	HTTP service.

	This service is NOT using the reliable Distribution pub/sub mechanism to broadcast Distributor instances state and status
	is will just use bare-bones IP UDP multicasts

	A Distributor HTTP service may dispatch browse requests to other Distributor instances to retreive detailed info.
	When dispatching requests tcp/ip P-2-P is used
 */

public class DistributorManagementController extends Thread implements MgmtServiceInterface,
																		TpsIpmc.TpsIpmcCallback,
																		TcpIpServerCallbackInterface {
	static final Logger cLogger = LogManager.getLogger( DistributorManagementController.class.getSimpleName());
	Distributor mDistributor; // Parent distributor application
	DistributorApplicationConfiguration mApplicationConfiguration;

	final Map<Long, MgmtDistributorBdx> mMgmtDistributorMap = new HashMap<>();

	TpsIpmc tpsIpmc;

	TcpIpServer mTcpIpServer;
	DistributorHttpService mHttpService;

	volatile boolean mTimeToExit;

	public DistributorManagementController(Distributor pDistributor, DistributorApplicationConfiguration pApplicationConfiguration) throws DistributorException {
		mDistributor = pDistributor;
		mApplicationConfiguration = pApplicationConfiguration;

		tpsIpmc = new TpsIpmc(mApplicationConfiguration.getMgmtAddress(),
				mApplicationConfiguration.getMgmtPort(),
				mApplicationConfiguration.getMgmtInterface(),
				this);


		// Declare Distributor Management HTTP interface

		// Declare Distributor P2P server interface
		try {
			mTcpIpServer = new TcpIpServer(0, this);
		} catch (Exception e) {
			cLogger.fatal(e);
			System.exit(-1);
		}

		if (mApplicationConfiguration.isMgmtHttpEnabled()) {
			mHttpService = new DistributorHttpService(mApplicationConfiguration.getMgmtHttpPort(), this);
		} else {
			mHttpService = null;
		}

		// Start the publishing thread
		mTimeToExit = false;
		this.start(); // Start publishing thread

	}

	public MgmtDistributorBdx getDistributorBdx( long pDistributorId ) {
		return this.mMgmtDistributorMap.get( pDistributorId);
	}

	public int getMgmtPort() {
		return mTcpIpServer.getServerPort();
	}

	@Override
	public void tcpipMessageRead(TcpIpConnection pConnection, byte[] pBuffer)
	{
		DistributorMessagesFactory tFactory = new DistributorMessagesFactory();
		try {
			MessageInterface tRqstMsg = tFactory.createMessage( pBuffer );
			if (tRqstMsg instanceof MgmtDistributorRqst) {
				MgmtDistributorView tDistView = mDistributor.getMgmtDistributorDetails();
				MgmtDistributorRsp tRspMsg = new MgmtDistributorRsp();
				tRspMsg.setDistributor(tDistView);
				pConnection.write(tRspMsg.messageToBytes());
			} else if (tRqstMsg instanceof MgmtConnectionRqst) {
				MgmtConnectionView tConnView = mDistributor.getMgmtConnectionDetails(((MgmtConnectionRqst) tRqstMsg).getConnectionId());
				MgmtConnectionRsp tRspMsg = new MgmtConnectionRsp();
				tRspMsg.setConnection(tConnView);
				pConnection.write(tRspMsg.messageToBytes());
			}
			else{
				cLogger.warn("Invalid Mgmt request message, closing connection");
				pConnection.close();
			}
		}
		catch( IOException e) {
			cLogger.warn("Mgmt connection error, " + pConnection.toString(), e);
			pConnection.close();
		}
	}

	@Override
	public void tcpipClientError(TcpIpConnection pConnection, IOException e) {
			//cLogger.error("Mgmt client disconnected" + pConnection.toString(), e );
			pConnection.close();
	}

	@Override
	public void tcpipInboundConnection(TcpIpConnection pConnection) {
		cLogger.info("Mgmt client connection, " + pConnection.toString());
	}

	public void close() {
		mTimeToExit = true;
		this.interrupt();
	}

	@Override
	public void run() {
		while( !mTimeToExit ) {
			try {Thread.sleep(10000L);}
			catch( InterruptedException e) {}
			if (mTimeToExit) {
				return;
			}
			MgmtDistributorBdx bdx = mDistributor.getMgmtDistributorBdx();
			try {
				tpsIpmc.publish(bdx.messageToBytes());
			}
			catch( Exception e) {
				cLogger.fatal("Failed to broadcast MgmtBdx", e);
				System.exit(-1);
			}
		}
	}

	@Override
	public void ipmgReadComplete(byte[] data, SocketAddress pSourceAddress)
	{
		System.out.println("ipmgReadComplete length: " + data.length);
		MgmtDistributorBdx bdx = new MgmtDistributorBdx( data );
		synchronized( mMgmtDistributorMap ) {
			mMgmtDistributorMap.put( bdx.getDistributorId(), bdx);
		}
	}

	@Override
	public void ipmgError(Exception e) {
		cLogger.fatal("Management distributor transport failure", e);
	}

	@Override
	public List<MgmtDistributorBdx> getMgmtDistributorBdx() {
		synchronized (this.mMgmtDistributorMap) {
			return new ArrayList<>(this.mMgmtDistributorMap.values());
		}
	}
}
