package com.hoddmimes.distributor.snoop;

import com.hoddmimes.distributor.*;
import com.hoddmimes.distributor.samples.AuxXml;
import org.w3c.dom.Element;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * This app act as subscriber and discovers publishers and
 * and track topics, traffic, retransmissions etc.
 */


public class DistributorSnoop extends JFrame implements DistributorUpdateCallbackIf, DistributorEventCallbackIf, SnoopUpdateMsgListner {



    private static final String APPLICATION_NAME = "SNOOP";

    private String mConfigurationFile = "snoop-configuration.xml";

    private String mCmaAddress;
    private int mCmaPort;
    private String mEthDevice;

    private int mIpBufferSize = 128000;
    private int mReadThreads = 1;


    private Distributor mDistributor;

    private Map<Integer, McaEntry> mMcaEntries;
    private List<DistributorSubscriberIf> mSubscribers;
    private List<DistributorUpdateCallbackIf> mUpdateListners;

    // Swing components
    GuiConnections mGuiConnections;
    GuiPublishers mGuiPublishers;


    public DistributorSnoop() {
        super();
    }

    private void initializeGui()
    {
       this.setTitle("Distributor Snoop");
        mUpdateListners = new LinkedList<>();


       JPanel tRootPanel = new JPanel( new GridLayout( 2,0, 20, 20 ));

       mGuiConnections = new GuiConnections( mMcaEntries.values() );
       tRootPanel.add( mGuiConnections );

       mGuiPublishers = new GuiPublishers( this );
       tRootPanel.add( mGuiPublishers );

       this.setContentPane( tRootPanel );
    }


    public static void main( String[] args ) {
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            System.out.println( ex.getMessage());
        }

        DistributorSnoop tSnoop = new DistributorSnoop();
        tSnoop.parseProgramArguments( args );
        tSnoop.loadAndParseConfiguration();
        tSnoop.initializeDistributor();
        tSnoop.initializeMulticastGroupSubscribers();
        tSnoop.initializeGui();
        tSnoop.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tSnoop.pack();
        tSnoop.setVisible(true);
        while( true ) {
            try {Thread.currentThread().sleep( 10000 ); }
            catch( InterruptedException e ) {}
        }
    }

    private void initializeDistributor() {
        DistributorApplicationConfiguration tDistConfig = new DistributorApplicationConfiguration(APPLICATION_NAME);
        tDistConfig.setEthDevice( mEthDevice );
        tDistConfig.setCMA( mCmaAddress );
        tDistConfig.setCMAPort( mCmaPort );

        try {
            mDistributor = new Distributor( tDistConfig );
        }
        catch( Exception e) {
            log("Failed to initialize Distributor", e);
            System.exit(0);
        }

    }


    private void initializeMulticastGroupSubscribers() {
        mSubscribers = new ArrayList<>();
        for( McaEntry tMca : mMcaEntries.values()) {
            DistributorConnectionConfiguration tConfig = new DistributorConnectionConfiguration( tMca.getMcaAddress(), tMca.getMcaPort());
            tConfig.setIpBufferSize( mIpBufferSize );
            tConfig.setReadThreads( mReadThreads );
            try {
                DistributorConnectionIf tMcaConnection = mDistributor.createConnection( tConfig );
                DistributorSubscriberIf tSubscriber = mDistributor.createSubscriber( tMcaConnection, this, this );
                tSubscriber.addSubscription("/...", tMca.getId());
                mSubscribers.add( tSubscriber );
            }
            catch( Exception e) {
                log("Failed to initialize Subscriber", e );
            }

        }
    }




    private void loadAndParseConfiguration() {
        try {
            Element tRootElement = AuxXml.loadXMLFromFile( mConfigurationFile ).getDocumentElement();
            // Parse CMA address/port
            mCmaAddress = AuxXml.getStringAttribute( tRootElement, "cmaAddress", null);
            mCmaPort = AuxXml.getIntAttribute( tRootElement, "cmaPort", 0);
            mEthDevice = AuxXml.getStringAttribute( tRootElement, "ethDevice", "eth0");

            // Parse MCA Entries
            mMcaEntries = new HashMap<>();
           List<Element> tMcaGroupElements = AuxXml.getChildrenElement( tRootElement,"MulticastGroups");
           for( Element tMcaElement : tMcaGroupElements ) {
               McaEntry tMca = new McaEntry( AuxXml.getStringAttribute(tMcaElement,"address", null),
                                             AuxXml.getIntAttribute(tMcaElement,"port", 0));
               mMcaEntries.put( tMca.getId(), tMca );
           }
        }
        catch( Exception e ) {
            log("Failed to load snoop configuration", e );
            System.exit(0);
        }
    }

    private void parseProgramArguments( String[] args ) {
        int i = 0;
        while( i < args.length) {
            if (args[i].startsWith("-config")) {
                mConfigurationFile = args[i+1];
                i++;
            }
            i++;
        }
    }

    private void log( String pMsg ) {
        log( pMsg, null);
    }

    private void log( String pMsg, Exception  pException ) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        if (pException != null) {
            System.err.println( sdf.format(System.currentTimeMillis()) + "  " + pMsg + ", reason: " + pException.getMessage());
            pException.printStackTrace();
            System.err.flush();
            System.out.flush();
        } else {
            System.out.println( sdf.format(System.currentTimeMillis()) + "  " + pMsg );
            System.out.flush();
        }
    }

    @Override
    public void distributorEventCallback(DistributorEvent pDistributorEvent)
    {
        if (pDistributorEvent instanceof  DistributorNewRemoteConnectionEvent) {
            mGuiPublishers.addPublisher((DistributorNewRemoteConnectionEvent) pDistributorEvent );
        } else if (pDistributorEvent instanceof  DistributorRemoveRemoteConnectionEvent) {
            mGuiPublishers.removePublisher((DistributorRemoveRemoteConnectionEvent) pDistributorEvent );
        }
    }

    @Override
    public void distributorUpdate(String pSubjectName, byte[] pPayload, Object pCallbackParameter, int pRemoteAppId, int pDeliveryQueueLength) {
        int mMcaId = (int) pCallbackParameter;
        mGuiConnections.distributorUpdate( mMcaId, pPayload );
        mGuiPublishers.distributorUpdate( pRemoteAppId, pSubjectName, pPayload );
        synchronized ( mUpdateListners ) {
            for( DistributorUpdateCallbackIf tListener : mUpdateListners ) {
                tListener.distributorUpdate( pSubjectName, pPayload, pCallbackParameter, pRemoteAppId, pDeliveryQueueLength);
            }
        }
    }

    @Override
    public void addUpdateListener(DistributorUpdateCallbackIf pCallbackListner) {
       synchronized (mUpdateListners) {
           mUpdateListners.add( pCallbackListner );
       }
    }

    @Override
    public void removeUpdateListener(DistributorUpdateCallbackIf pCallbackListner) {
        synchronized (mUpdateListners) {
            mUpdateListners.remove(pCallbackListner);
        }
    }
}
