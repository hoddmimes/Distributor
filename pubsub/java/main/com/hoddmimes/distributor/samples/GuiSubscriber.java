package com.hoddmimes.distributor.samples;

import com.hoddmimes.distributor.*;

import java.awt.BorderLayout;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import java.awt.Font;
import javax.swing.border.TitledBorder;







import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.AbstractAction;
import javax.swing.SwingConstants;




/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class GuiSubscriber extends JFrame implements DistributorEventCallbackIf, DistributorUpdateCallbackIf {
	
	static final String cTestSubject ="/test-subject-name";
	private enum ConnectType {NATIVE,HTTP,TCPIP};
	

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel jTopPanel = null;

	private JPanel jParameterPanel = null;

	private JPanel jStatisticsPanel = null;

	private JPanel jBottomPanel = null;

	private JLabel jMulticastGroupLabel = null;

	private JLabel jAdressLabel = null;

	private JTextField jAddressTextField = null;

	private JLabel jPortLabel = null;

	private JTextField jPortTextField = null;

	private JButton jConnectButton = null;

	private JLabel jSegmentSizeLabel = null;

	private JTextField jSegmentSizeTextField = null;

	private JLabel jIpBufferSizeLabel = null;

	private JTextField jIpBufferSizeField = null;

	private JLabel jErrorRateLabel = null;

	private JTextField jErrorRateTextField = null;

	private JLabel jUpdatesReceivedLabel = null;

	private JTextField jUpdatesReceivedTextField = null;

	private JLabel jReceivedByteRateLabel = null;

	private JTextField jReceivedBytesRateTextField = null;

	private JLabel jReceivedUpdateRateLabel = null;

	private JTextField jReceivedUpdateRateTextField = null;

	private JLabel jDeliveryQueueLengthLabel = null;

	private JTextField jRequestQueueLengthTextField = null;

	private JButton jStartSendButton = null;

	private JButton jCancelSendButton = null;

	private JButton jBrowseDeviceButton = null;

	private JButton jLoggingButton = null;

	private JLabel jStateLabel = null;

	
	int 											mSeqNo;
	Component 										mFameInstance;
	Distributor 									mDistributor;
	DistributorSubscriberIf 						mSubscriber;
	DistributorConnectionIf 						mConnection;
	
	String												mAddressString = DistributorConnectionConfiguration.DEFAULT_MCA_ADDRESS;
	String											    mPortString = String.valueOf( DistributorConnectionConfiguration.DEFAULT_MCA_PORT );
	String												mErrorRateString = "0";
	String												mSegmentSizeString ="8192";
	String										        mIpBufferSizeString = "64000";
	StatisticsThread									mStatisticsThread = null;  //  @jve:decl-index=0:
	ConnectType											mConnectType = ConnectType.NATIVE;  //  @jve:decl-index=0:
	String												mP2PHost;
	int													mP2PPort;

	private int mLogFlags = DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS +
							DistributorApplicationConfiguration.LOG_ERROR_EVENTS;

	private JLabel jBytesReceivedLabel = null;

	private JTextField jBytesReceivedTextField = null;


	private JLabel jCurrentUpdateRateLabel = null;

	private JTextField jCurrentUpdateRateTextField = null;
	private JLabel jCurrentSeqnoSeenLabel;
	private JTextField jLastSeqnoSeenTextField;
	private JLabel jEthDeviceLabel;
	private JLabel jLoggingLabel;
	private JTextField jEthDeviceTextField;

	private int mBdxGwyPort = 0;
	private String mBdxGwyHost = null;

  //  @jve:decl-index=0:
	/**
	 * This is the default constructor
	 */
	public GuiSubscriber() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(530, 321);
		this.setResizable(false);
		this.setBackground(new Color(255, 255, 204));
		this.setContentPane(getJContentPane());
		this.setTitle("TestSubscriber");
		try {
			BufferedImage icon = ImageIO.read(getClass().getResourceAsStream("arrow_in.gif"));
			setIconImage(icon);
		}
		catch( Exception e) {
			this.setIconImage(Toolkit.getDefaultToolkit().getImage("arrow_in.gif"));
		}
		
		this.getJAddressTextField().setText( mAddressString );
		this.getJPortTextField().setText(String.valueOf( mPortString ));
		this.getJErrorRateTextField().setText( mErrorRateString );
		this.getJSegmentSizeTextField().setText( mSegmentSizeString );
		this.getJIpBufferSizeField().setText( mIpBufferSizeString );
		this.mFameInstance = this;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJTopPanel(), BorderLayout.NORTH);
			jContentPane.add(getJParameterPanel(), BorderLayout.WEST);
			jContentPane.add(getJStatisticsPanel(), BorderLayout.CENTER);
			jContentPane.add(getJBottomPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTopPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJTopPanel() {
		if (jTopPanel == null) {
	
			jPortLabel = new JLabel();
			jPortLabel.setBounds(new Rectangle(163, 29, 34, 21));
			jPortLabel.setText("Port");
			jAdressLabel = new JLabel();
			jAdressLabel.setBounds(new Rectangle(16, 34, 44, 16));
			jAdressLabel.setText("Adress");
			jMulticastGroupLabel = new JLabel();
			jMulticastGroupLabel.setText("Multicast Group");
			jMulticastGroupLabel.setBounds(new Rectangle(14, 6, 146, 16));
			jTopPanel = new JPanel();
			jTopPanel.setLayout(null);
			jTopPanel.setPreferredSize(new Dimension(1, 70));
			jTopPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			jTopPanel.add(jMulticastGroupLabel, null);
			jTopPanel.add(jAdressLabel, null);
			jTopPanel.add(getJAddressTextField(), null);
			jTopPanel.add(jPortLabel, null);
			jTopPanel.add(getJPortTextField(), null);
			jTopPanel.add(getJConnectButton(), null);
		}
		return jTopPanel;
	}

	/**
	 * This method initializes jParameterPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJParameterPanel() {
		if (jParameterPanel == null) {
			jErrorRateLabel = new JLabel();
			jErrorRateLabel.setBounds(new Rectangle(10, 20, 102, 16));
			jErrorRateLabel.setText("Error rate %%");
			jErrorRateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jIpBufferSizeLabel = new JLabel();
			jIpBufferSizeLabel.setBounds(new Rectangle(10, 60, 89, 16));
			jIpBufferSizeLabel.setText("IP Buffer size");
			jIpBufferSizeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jSegmentSizeLabel = new JLabel();
			jSegmentSizeLabel.setBounds(new Rectangle(10, 40, 100, 16));
			jSegmentSizeLabel.setText("Segment size");
			jSegmentSizeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jParameterPanel = new JPanel();
			jParameterPanel.setLayout(null);
			jParameterPanel.setPreferredSize(new Dimension(260, 1));
			jParameterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Parameters", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP, new Font("Dialog", Font.ITALIC, 10), Color.blue));
			jParameterPanel.add(jSegmentSizeLabel, null);
			jParameterPanel.add(getJSegmentSizeTextField(), null);
			jParameterPanel.add(jIpBufferSizeLabel, null);
			jParameterPanel.add(getJIpBufferSizeField(), null);
			jParameterPanel.add(jErrorRateLabel, null);
			jParameterPanel.add(getJErrorRateTextField(), null);
			jParameterPanel.add(getJEthDeviceLabel());
			jParameterPanel.add(getJEthDeviceTextField());
			jParameterPanel.add(getBrowseDeviceButtonButton());
			jParameterPanel.add(getLoggingLabel());
			jParameterPanel.add(getLoggingButton());
		}
		return jParameterPanel;
	}

	/**
	 * This method initializes jStatisticsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJStatisticsPanel() {
		if (jStatisticsPanel == null) {
			jCurrentUpdateRateLabel = new JLabel();
			jCurrentUpdateRateLabel.setBounds(new Rectangle(10, 60, 171, 15));
			jCurrentUpdateRateLabel.setText("Current rate (upd/s)");
			jCurrentUpdateRateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jBytesReceivedLabel = new JLabel();
			jBytesReceivedLabel.setBounds(new Rectangle(10, 79, 171, 15));
			jBytesReceivedLabel.setText("Bytes received (Kb)");
			jBytesReceivedLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jDeliveryQueueLengthLabel = new JLabel();
			jDeliveryQueueLengthLabel.setBounds(10, 119, 171, 16);
			jDeliveryQueueLengthLabel.setText("Delivery queue length");
			jDeliveryQueueLengthLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jReceivedUpdateRateLabel = new JLabel();
			jReceivedUpdateRateLabel.setBounds(new Rectangle(10, 40, 171, 15));
			jReceivedUpdateRateLabel.setText("Update rate (upd/s)");
			jReceivedUpdateRateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jReceivedByteRateLabel = new JLabel();
			jReceivedByteRateLabel.setBounds(new Rectangle(10, 20, 171, 15));
			jReceivedByteRateLabel.setText("Byte rate (Kbit/s)");
			jReceivedByteRateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jUpdatesReceivedLabel = new JLabel();
			jUpdatesReceivedLabel.setBounds(new Rectangle(10, 99, 171, 16));
			jUpdatesReceivedLabel.setText("Updates received");
			jUpdatesReceivedLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jStatisticsPanel = new JPanel();
			jStatisticsPanel.setPreferredSize(new java.awt.Dimension(223, 144));
			jStatisticsPanel.setLayout(null);
			jStatisticsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Statistics", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.ITALIC, 10), Color.blue));
			jStatisticsPanel.add(jUpdatesReceivedLabel, null);
			jStatisticsPanel.add(getJUpdatesReceivedTextField(), null);
			jStatisticsPanel.add(jReceivedByteRateLabel, null);
			jStatisticsPanel.add(getJBytesRateTextField(), null);
			jStatisticsPanel.add(jReceivedUpdateRateLabel, null);
			jStatisticsPanel.add(getJReceivedUpdateRateTextField(), null);
			jStatisticsPanel.add(jDeliveryQueueLengthLabel, null);
			jStatisticsPanel.add(getJRequestQueueLengthTextField(), null);
			jStatisticsPanel.add(jBytesReceivedLabel, null);
			jStatisticsPanel.add(getJBytesReceivedTextField(), null);
			jStatisticsPanel.add(jCurrentUpdateRateLabel, null);
			jStatisticsPanel.add(getJCurrentUpdateRateTextField(), null);
			jStatisticsPanel.add(getJCurrentSeqnoSeenLabel());
			jStatisticsPanel.add(getJLastSeqnoSeenTextField());
		}
		return jStatisticsPanel;
	}

	/**
	 * This method initializes jBottomPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJBottomPanel() {
		if (jBottomPanel == null) {
			jBottomPanel = new JPanel();
			jBottomPanel.setLayout(null);
			jBottomPanel.setPreferredSize(new Dimension(1, 55));
			jBottomPanel.add(getJStartReceiveButton(), null);
			jBottomPanel.add(getJCancelReceiveButton(), null);
		}
		return jBottomPanel;
	}

	/**
	 * This method initializes jAddressTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJAddressTextField() {
		if (jAddressTextField == null) {
			jAddressTextField = new JTextField();
			jAddressTextField.setBounds(new Rectangle(64, 31, 96, 20));
		}
		return jAddressTextField;
	}

	/**
	 * This method initializes jPortTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJPortTextField() {
		if (jPortTextField == null) {
			jPortTextField = new JTextField();
			jPortTextField.setBounds(new Rectangle(200, 30, 51, 20));
		}
		return jPortTextField;
	}

	/**
	 * This method initializes jConnectButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	@SuppressWarnings("serial")
	private JButton getJConnectButton() {
		if (jConnectButton == null) {
			jConnectButton = new JButton();
			jConnectButton.setAction(new AbstractAction()
			{
                public void actionPerformed(ActionEvent arg0) {
                	connectToDistributorAction();
                }
            });
			jConnectButton.setBounds(new Rectangle(263, 31, 106, 20));
			jConnectButton.setText("Connect");
		}
		return jConnectButton;
	}

	/**
	 * This method initializes jSegmentSizeTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJSegmentSizeTextField() {
		if (jSegmentSizeTextField == null) {
			jSegmentSizeTextField = new JTextField();
			jSegmentSizeTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jSegmentSizeTextField.setBounds(new Rectangle(125, 40, 60, 16));
			jSegmentSizeTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jSegmentSizeTextField;
	}

	/**
	 * This method initializes jIpBufferSizeField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJIpBufferSizeField() {
		if (jIpBufferSizeField == null) {
			jIpBufferSizeField = new JTextField();
			jIpBufferSizeField.setHorizontalAlignment(SwingConstants.TRAILING);
			jIpBufferSizeField.setBounds(new Rectangle(125, 60, 60, 16));
			jIpBufferSizeField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jIpBufferSizeField;
	}

	/**
	 * This method initializes jErrorRateTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJErrorRateTextField() {
		if (jErrorRateTextField == null) {
			jErrorRateTextField = new JTextField();
			jErrorRateTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jErrorRateTextField.setBounds(new Rectangle(125, 20, 60, 16));
			jErrorRateTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jErrorRateTextField;
	}

	/**
	 * This method initializes jUpdatesSentTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJUpdatesReceivedTextField() {
		if (jUpdatesReceivedTextField == null) {
			jUpdatesReceivedTextField = new JTextField();
			jUpdatesReceivedTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jUpdatesReceivedTextField.setEditable(false);
			jUpdatesReceivedTextField.setBounds(158, 99, 62, 16);
			jUpdatesReceivedTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jUpdatesReceivedTextField;
	}

	/**
	 * This method initializes jBytesRateTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJBytesRateTextField() {
		if (jReceivedBytesRateTextField == null) {
			jReceivedBytesRateTextField = new JTextField();
			jReceivedBytesRateTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jReceivedBytesRateTextField.setBounds(158, 19, 62, 16);
			jReceivedBytesRateTextField.setEditable(false);
			jReceivedBytesRateTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jReceivedBytesRateTextField;
	}

	/**
	 * This method initializes jSentUpdateRateTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJReceivedUpdateRateTextField() {
		if (jReceivedUpdateRateTextField == null) {
			jReceivedUpdateRateTextField = new JTextField();
			jReceivedUpdateRateTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jReceivedUpdateRateTextField.setBounds(158, 39, 62, 16);
			jReceivedUpdateRateTextField.setEditable(false);
			jReceivedUpdateRateTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jReceivedUpdateRateTextField;
	}

	/**
	 * This method initializes jRequestQueueLengthTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJRequestQueueLengthTextField() {
		if (jRequestQueueLengthTextField == null) {
			jRequestQueueLengthTextField = new JTextField();
			jRequestQueueLengthTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jRequestQueueLengthTextField.setBounds(158, 119, 62, 16);
			jRequestQueueLengthTextField.setEditable(false);
			jRequestQueueLengthTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jRequestQueueLengthTextField;
	}

	/**
	 * This method initializes jStartSendButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	@SuppressWarnings("serial")
	private JButton getJStartReceiveButton() {
		if (jStartSendButton == null) {
			jStartSendButton = new JButton();
			jStartSendButton.setAction(new AbstractAction(){
				public void actionPerformed(ActionEvent arg0) {
					startToReceiveAction();
				}
			});
			jStartSendButton.setBounds(new Rectangle(65, 12, 133, 20));
			jStartSendButton.setText("Start Receive");
			jStartSendButton.setEnabled(false);
		}
		return jStartSendButton;
	}

	/**
	 * This method initializes jCancelSendButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	@SuppressWarnings("serial")
	private JButton getJCancelReceiveButton() {
		if (jCancelSendButton == null) {
			jCancelSendButton = new JButton();
			jCancelSendButton.setAction(new AbstractAction(){
				public void actionPerformed(ActionEvent arg0) {
					cancelReceiveActions();
				}
			});
			jCancelSendButton.setBounds(new Rectangle(237, 12, 115, 20));
			jCancelSendButton.setEnabled(false);
			jCancelSendButton.setText("Cancel");
		}
		return jCancelSendButton;
	}

	private JButton getBrowseDeviceButtonButton() {
		if (jBrowseDeviceButton == null) {
			jBrowseDeviceButton = new JButton();
			jBrowseDeviceButton.setAction(new AbstractAction(){
				public void actionPerformed(ActionEvent arg0) {
					listEthDevices();
				}
			});
			jBrowseDeviceButton.setBounds(new Rectangle(192, 80, 37, 16));
			jBrowseDeviceButton.setEnabled(true);
			jBrowseDeviceButton.setText("...");
		}
		return jBrowseDeviceButton;
	}


	private JButton getLoggingButton() {
		if (jLoggingButton == null) {
			jLoggingButton = new JButton();
			jLoggingButton.setAction(new AbstractAction(){
				public void actionPerformed(ActionEvent arg0) {
					setLogLevel();
				}
			});
			jLoggingButton.setBounds(new Rectangle(125, 101, 105, 18));
			jLoggingButton.setEnabled(true);
			jLoggingButton.setFont( new Font("Arrial", Font.PLAIN,10));
			jLoggingButton.setText("Set Log Level");
		}
		return jLoggingButton;
	}
	
    void startToReceiveAction() {
  
    	this.getJStartReceiveButton().setEnabled(false);
    	this.getJCancelReceiveButton().setEnabled(true);
    	this.getJBytesRateTextField().setText("");
    	this.getJUpdatesReceivedTextField().setText("");
    	this.getJRequestQueueLengthTextField().setText("");
    	this.getJBytesReceivedTextField().setText("");
    	this.jLastSeqnoSeenTextField.setText("");
    	
    	this.mStatisticsThread = null;
    	
    	try {
    		mSubscriber = mDistributor.createSubscriber(mConnection, this, this);
    		mSubscriber.addSubscription( cTestSubject , null );
    	}
    	catch( DistributorException e) {
    		e.printStackTrace();
    	}
    	
    }

	public void listEthDevices() {
		ListNetwrkDialog dialog = new ListNetwrkDialog(this, "Network Devices", true);
		dialog.pack();
		dialog.setVisible(true);
		String tDevice = dialog.getSelectedDevice();
		if (tDevice != null) {
			jEthDeviceTextField.setText( tDevice );
		}
	}

	public void setLogLevel() {
		SetLogLevelDialog dialog = new SetLogLevelDialog(this, "Set Log Level", mLogFlags, true);
		dialog.pack();
		dialog.setVisible(true);

		mLogFlags = dialog.getSelectedLogLevel();
		if (mDistributor != null) {
			mDistributor.setLogging( mLogFlags );
		}

	}
	
    void cancelReceiveActions() {
    	mSubscriber.close();
    	mSubscriber = null;
    	if (mStatisticsThread != null) {
    		synchronized( mStatisticsThread ) {
    			mStatisticsThread.mTimeToDie = true;
    			mStatisticsThread.interrupt();
    			mStatisticsThread = null;    			
    		}
    		   	
			 this.getJBytesRateTextField().setText("");
			 this.getJCurrentUpdateRateTextField().setText("");
			 this.getJRequestQueueLengthTextField().setText("");
			 this.getJReceivedUpdateRateTextField().setText("");
			 this.getJUpdatesReceivedTextField().setText("");
			 this.getJBytesReceivedTextField().setText("");
			 this.jLastSeqnoSeenTextField.setText("");
        	
        	this.getJStartReceiveButton().setEnabled(true);
        	this.getJCancelReceiveButton().setEnabled(false);
    	}
    }
    
	void connectToDistributorAction() 
	{
		
		if (connectNative()) {
    		getJConnectButton().setEnabled(false);
    		getJStartReceiveButton().setEnabled(true);
		}
	}
	
	
	
	
	
	
	boolean connectNative()
	{
	
		DistributorApplicationConfiguration tApplConfig = new DistributorApplicationConfiguration("TestSubscriber");
		
	    try {
	    	int tErrorRate = Integer.parseInt( getJErrorRateTextField().getText());
	    	int tRqstQueueSize = Integer.parseInt(getJSegmentSizeTextField().getText());
	    
	    	tApplConfig.setEthDevice( jEthDeviceTextField.getText());
//	    	tApplConfig.setLogFlags( DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS + 
//	    							 DistributorApplicationConfiguration.LOG_RMTDB_EVENTS + 
//	    							 DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS + 
//	    							 DistributorApplicationConfiguration.LOG_SEGMENTS_EVENTS );

			if ((mBdxGwyHost != null) && (mBdxGwyPort != 0)) {
				tApplConfig.setBroadcastGatewayAddress( mBdxGwyHost);
				tApplConfig.setBroadcastGatewayPort( mBdxGwyPort );
			}

	    	mDistributor = new Distributor( tApplConfig );
	    	DistributorConnectionConfiguration tConfig = new DistributorConnectionConfiguration( mAddressString, Integer.parseInt( mPortString ));
	    	if (tErrorRate > 0)
	    	     tConfig.setFakeRcvErrorRate(tErrorRate);

	    	tConfig.setIpBufferSize(Integer.parseInt(mIpBufferSizeString));
	    	tConfig.setSegmentSize(Integer.parseInt(this.getJSegmentSizeTextField().getText()));


	  
	    
	    	mConnection = mDistributor.createConnection(tConfig);
	    	return true;
	    	
	    }
	    catch( DistributorException e) {
	    	 new ErrorMessage( mFameInstance, "Failed to connect", e.getMessage());
	    	 return false;
	    }
	}
	
	void parseArguments( String[] args ) 
	{
		int i = 0;
		while( i < args.length) {
			if (args[i].compareToIgnoreCase("-device") == 0) {
				jEthDeviceTextField.setText(args[i+1]);
				i++;
			}
			if (args[i].compareToIgnoreCase("-gatewayHost") == 0) {
				mBdxGwyHost = args[i+1];
				i++;
			}
			if (args[i].compareToIgnoreCase("-gatewayPort") == 0) {
				mBdxGwyPort = Integer.parseInt(args[i+1]);
				i++;
			}
			i++;
		}
		
		if (mConnectType != ConnectType.NATIVE) {
			this.getJErrorRateTextField().setEnabled(false);		
			this.getJSegmentSizeTextField().setEnabled(false);
			this.getJIpBufferSizeField().setEnabled(false);

		}
		
	}
	
	
	 public void distributorEventCallback( DistributorEvent pDistributorEvent ) {
		 if (pDistributorEvent instanceof DistributorErrorEvent)
			 new ErrorMessage( mFameInstance, "Connection error", pDistributorEvent.toString());
		 else
			 System.out.println("Distributor event: " + pDistributorEvent.toString());
	 }
	 
	 public void distributorUpdate( String pSubjectName, byte[] pData, Object pCallbackParameter, int pQueueLength ) {
		 long tSequenceNumber = buffer2Long(pData, 0);

		 if (mSubscriber != null) {
			 if ((mStatisticsThread == null) && (pData[8] == 1)) {
				 mStatisticsThread = new StatisticsThread();
				 mStatisticsThread.start();
			 }

			 mStatisticsThread.updateStatistics(pData.length, pQueueLength, tSequenceNumber);	 
		 }
	 }
	
	 class StatisticsThread extends Thread {
			volatile boolean			mTimeToDie;
			AtomicLong					mUpdatesReceived;
			AtomicLong				    mBytesReceived;
			AtomicInteger				mQueueLength;
			long						mStartTime;
			AtomicInteger				mCurrentUpdateRate;
			AtomicLong					mLastSequenceNumberSeen;
			boolean						mOutOfSequence;
			long						mMissedSequenceNumber;
			int							mTotalOutOfSequence;
			
			StatisticsThread() {
				mLastSequenceNumberSeen = new AtomicLong(0);
				mUpdatesReceived = new AtomicLong(0);
				mBytesReceived = new AtomicLong(0);
				mQueueLength = new AtomicInteger(0);
				mCurrentUpdateRate = new AtomicInteger(0);
				mTimeToDie = false;
				mStartTime = 0;
				mOutOfSequence = false;
				mMissedSequenceNumber = 0;
				mTotalOutOfSequence = 0;
			}
				
			void updateStatistics( int pUpdateSize, int pQueueLength, long pSequenceNumberSeen ) {
				if (mStartTime  == 0) {
					mStartTime = System.currentTimeMillis();
				}
				
//				 System.out.println(" Seqno: " + pSequenceNumberSeen + " Last Seen: " + mLastSequenceNumberSeen + " Diff: " +
//						 (pSequenceNumberSeen - mLastSequenceNumberSeen.get()));
				
				if (mLastSequenceNumberSeen.get() == 0) {
					mLastSequenceNumberSeen.set(pSequenceNumberSeen);
				} else {
					if ((mLastSequenceNumberSeen.get() + 1) == pSequenceNumberSeen) {
						mLastSequenceNumberSeen.set(pSequenceNumberSeen);
					} else {
						if (!mOutOfSequence) {
						  mOutOfSequence = true;
						  mMissedSequenceNumber = pSequenceNumberSeen;
						}
						mTotalOutOfSequence++;
						mLastSequenceNumberSeen.set(pSequenceNumberSeen);
					}
				}
				
				mQueueLength.set(pQueueLength);
				mBytesReceived.getAndAdd(pUpdateSize);
				mUpdatesReceived.getAndIncrement();
				mCurrentUpdateRate.getAndIncrement();
			}
			
			public void run() {
				mTimeToDie = false;
				long mLastTimeStamp = System.currentTimeMillis();
				
				while(!mTimeToDie) {
					try { sleep( 1000 ); }
					catch( InterruptedException e) {}
					
					if (mTimeToDie) {
						return;
					}
					
					
					if (mStartTime != 0) {
						long tTimeDiff = System.currentTimeMillis() - mStartTime;
						double tByteRate = (double) (mBytesReceived.get() / tTimeDiff);
						int tUpdateRate = (int) ((mUpdatesReceived.get() * 1000) / tTimeDiff);
						
						tTimeDiff = System.currentTimeMillis() - mLastTimeStamp;
						int tCurrUpdateRate = (int) ((mCurrentUpdateRate.getAndSet(0) * 1000) / tTimeDiff);
						mLastTimeStamp = System.currentTimeMillis();
						
						String tOutOfSequenceMessage = null;
						if (mOutOfSequence) {
							tOutOfSequenceMessage = "Out of Sequence, first missed: " + String.valueOf( mMissedSequenceNumber ) +
													" number of out of sequence: " + String.valueOf( mTotalOutOfSequence);
						}
						SwingUtilities.invokeLater(	new UpdateStatisticsStringTask( GuiSubscriber.this, tByteRate, tUpdateRate, 
								mQueueLength.get(), mBytesReceived.get(), mUpdatesReceived.get(), tCurrUpdateRate, 
								mLastSequenceNumberSeen.get(), tOutOfSequenceMessage ));
						mOutOfSequence = false;
						mMissedSequenceNumber = 0;
						mTotalOutOfSequence = 0;
						
						if (mTimeToDie) {
							try { sleep( 2000 ); }
							catch( InterruptedException e) {}
							return;
						}
						
					}
					
				}
			}
			
	 }

	 
	 class UpdateButtonsTask implements Runnable {
		 boolean mStartToReceive;
		 boolean mCancel;
		 GuiSubscriber mFrame;
		 
		 UpdateButtonsTask( GuiSubscriber pFrame, boolean pStartToReceive, boolean pCancel ) {
			mStartToReceive  =  pStartToReceive;
			mCancel = pCancel;
			mFrame = pFrame;
		 }
		 
		 public void run() {
			 mFrame.getJCancelReceiveButton().setEnabled(mCancel);
			 mFrame.getJStartReceiveButton().setEnabled(mStartToReceive);
		 }
		 
	 }
	 class UpdateStatisticsStringTask implements Runnable {
		 double mByteRate;
		 int mQueueLength;
		 int mUpdateRate;
		 long mUpdatesReceived;
		 long mBytesReceived;
		 int	mCurrentUpdateRate;
		 long 	mLastSeqnoSeen;
		 String mOutOfSequenceMessage;
		 
		 GuiSubscriber	mFrame;
		 
		 UpdateStatisticsStringTask( GuiSubscriber pFrame, double pByteRate, int pUpdateRate, int pQueueLength, long pTotalBytes, long pTotalUpdates, int pCurrentUpdateRate, long pLastSeqnoSeen, String pOutSequenceMessage ) {
			 mByteRate = pByteRate;
			 mUpdateRate = pUpdateRate;
			 mQueueLength = pQueueLength;
			 mUpdatesReceived = pTotalUpdates;
			 mBytesReceived = pTotalBytes;
			 mCurrentUpdateRate = pCurrentUpdateRate;
			 mFrame = pFrame;
			 mOutOfSequenceMessage = pOutSequenceMessage;
			 mLastSeqnoSeen = pLastSeqnoSeen;
		 }
		 
		 public void run() {
			 DecimalFormat tDecFmt = new DecimalFormat();
			 tDecFmt.setMaximumFractionDigits(2);
			 tDecFmt.setMaximumFractionDigits(2);
			 
			 if (mFrame.mStatisticsThread != null) {
			 mFrame.getJBytesRateTextField().setText(tDecFmt.format(mByteRate));
			 mFrame.getJCurrentUpdateRateTextField().setText(String.valueOf( mCurrentUpdateRate ));
			 mFrame.getJRequestQueueLengthTextField().setText(String.valueOf(mQueueLength));
			 mFrame.getJReceivedUpdateRateTextField().setText( String.valueOf(mUpdateRate));
			 mFrame.getJUpdatesReceivedTextField().setText(String.valueOf(mUpdatesReceived));
			 mFrame.getJBytesReceivedTextField().setText(String.valueOf((mBytesReceived/1024)));
			 mFrame.jLastSeqnoSeenTextField.setText( String.valueOf(mLastSeqnoSeen));
			 } else {
				 mFrame.getJBytesRateTextField().setText("");
				 mFrame.getJCurrentUpdateRateTextField().setText("");
				 mFrame.getJRequestQueueLengthTextField().setText("");
				 mFrame.getJReceivedUpdateRateTextField().setText("");
				 mFrame.getJUpdatesReceivedTextField().setText("");
				 mFrame.getJBytesReceivedTextField().setText("");
				 mFrame.jLastSeqnoSeenTextField.setText("");		 
			 }
			 if (mOutOfSequenceMessage != null) {
				System.out.println( mOutOfSequenceMessage ); 
			 }
			
		 }
		 
	 }
	 
	 
	
	/**
	 * This method initializes jBytesReceivedTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJBytesReceivedTextField() {
		if (jBytesReceivedTextField == null) {
			jBytesReceivedTextField = new JTextField();
			jBytesReceivedTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jBytesReceivedTextField.setBounds(158, 78, 62, 16);
			jBytesReceivedTextField.setEditable(false);
			jBytesReceivedTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jBytesReceivedTextField;
	}

	/**
	 * This method initializes jCurrentUpdateRateTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJCurrentUpdateRateTextField() {
		if (jCurrentUpdateRateTextField == null) {
			jCurrentUpdateRateTextField = new JTextField();
			jCurrentUpdateRateTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jCurrentUpdateRateTextField.setBounds(158, 59, 62, 16);
			jCurrentUpdateRateTextField.setEditable(false);
			jCurrentUpdateRateTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jCurrentUpdateRateTextField;
	}
	
	static void long2Buffer( long pValue, byte[] pBuffer, int pOffset ) 
	{
		pBuffer[ pOffset + 0] = (byte) (pValue >>> 56);
		pBuffer[ pOffset + 1] = (byte) (pValue >>> 48);
		pBuffer[ pOffset + 2] = (byte) (pValue >>> 40);
		pBuffer[ pOffset + 3] = (byte) (pValue >>> 32);
		pBuffer[ pOffset + 4] = (byte) (pValue >>> 24);
		pBuffer[ pOffset + 5] = (byte) (pValue >>> 16);
		pBuffer[ pOffset + 6] = (byte) (pValue >>> 8);
		pBuffer[ pOffset + 7] = (byte) (pValue >>> 0);
	}
	
	static long buffer2Long(byte[] pBuffer, int pOffset ) 
	{
		long tValue = 0;
		tValue += (long) ((long)(pBuffer[ pOffset + 0] & 0xff) << 56);
		tValue += (long) ((long)(pBuffer[ pOffset + 1] & 0xff) << 48);
		tValue += (long) ((long)(pBuffer[ pOffset + 2] & 0xff) << 40);
		tValue += (long) ((long)(pBuffer[ pOffset + 3] & 0xff) << 32);
		tValue += (long) ((long)(pBuffer[ pOffset + 4] & 0xff) << 24);
		tValue += (long) ((long)(pBuffer[ pOffset + 5] & 0xff) << 16);
		tValue += (long) ((long)(pBuffer[ pOffset + 6] & 0xff) << 8);
		tValue += (long) ((long)(pBuffer[ pOffset + 7] & 0xff) << 0);
		return tValue;
	}

	/**
	 * Programs arguments
	 * @param args, program arguments
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {
			//ex.printStackTrace();
		}
		GuiSubscriber thisClass = new GuiSubscriber();
		thisClass.parseArguments(args);
		thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		thisClass.setVisible(true);
		while( true ) {
			try {Thread.currentThread().sleep( 10000 ); }
			catch( InterruptedException e ) {}
		}
	}
	
	public class ErrorMessage {

	    public ErrorMessage(Component parent, String message, String reason) {
	        if (reason == null)
	            JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
	        else
	            JOptionPane.showMessageDialog(
	                parent,
	                message + "\n" + "reason: " + reason,
	                "Error",
	                JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	
	private JLabel getJCurrentSeqnoSeenLabel() {
		if (jCurrentSeqnoSeenLabel == null) {
			jCurrentSeqnoSeenLabel = new JLabel();
			jCurrentSeqnoSeenLabel.setText("Last seqno seen");
			jCurrentSeqnoSeenLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
			jCurrentSeqnoSeenLabel.setBounds(10, 139, 133, 16);
		}
		return jCurrentSeqnoSeenLabel;
	}
	private JTextField getJLastSeqnoSeenTextField() {
		if (jLastSeqnoSeenTextField == null) {
			jLastSeqnoSeenTextField = new JTextField();
			jLastSeqnoSeenTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jLastSeqnoSeenTextField.setFont(new Font("Dialog", Font.PLAIN, 10));
			jLastSeqnoSeenTextField.setEditable(false);
			jLastSeqnoSeenTextField.setBounds(158, 138, 62, 16);
		}
		return jLastSeqnoSeenTextField;
	}
	private JLabel getJEthDeviceLabel() {
		if (jEthDeviceLabel == null) {
			jEthDeviceLabel = new JLabel();
			jEthDeviceLabel.setText("Ethernet device");
			jEthDeviceLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
			jEthDeviceLabel.setBounds(new Rectangle(10, 60, 89, 16));
			jEthDeviceLabel.setBounds(10, 81, 107, 16);
		}
		return jEthDeviceLabel;
	}

	private JLabel getLoggingLabel() {
		if (jLoggingLabel == null) {
			jLoggingLabel = new JLabel();
			jLoggingLabel.setText("Log Flags");
			jLoggingLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
			jLoggingLabel.setBounds(new Rectangle(10, 60, 89, 16));
			jLoggingLabel.setBounds(10, 101, 107, 16);
		}
		return jLoggingLabel;
	}

	private JTextField getJEthDeviceTextField() {
		if (jEthDeviceTextField == null) {
			jEthDeviceTextField = new JTextField();
			jEthDeviceTextField.setText("eth0");
			jEthDeviceTextField.setHorizontalAlignment(SwingConstants.CENTER);
			jEthDeviceTextField.setFont(new Font("Dialog", Font.PLAIN, 10));
			jEthDeviceTextField.setBounds(new Rectangle(125, 60, 60, 16));
			jEthDeviceTextField.setBounds(125, 80, 60, 16);
		}
		return jEthDeviceTextField;
	}
}  //  @jve:decl-index=0:visual-constraint="272,7"



