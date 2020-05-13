package com.hoddmimes.distributor.samples;

import com.hoddmimes.distributor.*;

import java.awt.*;

import javax.imageio.ImageIO;

import javax.swing.*;

import javax.swing.border.EtchedBorder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.border.TitledBorder;


import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;


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
public class GuiPublisher extends JFrame implements DistributorEventCallbackIf {
	static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss.SSS");

	static final String cTestSubject ="/test-subject-name";
	private int mLogFlags = DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS +
			                DistributorApplicationConfiguration.LOG_ERROR_EVENTS;

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

	private JLabel jUpdateToSendLabel = null;

	private JTextField jUpdateToSendTextField = null;

	private JLabel jUpdateMinSizeLabel = null;

	private JTextField jUpdateMinSizeTextField = null;

	private JLabel jUpdateMaxSizeLabel = null;

	private JTextField jUpdateMaxSizeTextField1 = null;

	private JLabel jSegmentSizeLabel = null;

	private JTextField jSegmentSizeTextField = null;

	private JLabel jIpBufferSizeLabel = null;

	private JTextField jIpBufferSizeField = null;

	private JLabel jUpdateRateLabel = null;

	private JTextField jUpdateRateTextField = null;

	private JLabel jOutQueueMaxLengthLabel = null;

	private JLabel jErrorRateLabel = null;

	private JTextField jErrorRateTextField = null;

	private JLabel jUpdatesSentLabel = null;

	private JTextField jUpdatesSentTextField = null;

	private JLabel jBytesSentLabel = null;

	private JTextField jBytesSentTextField = null;

	private JLabel jSentByteRateLabel = null;

	private JTextField jSentBytesRateTextField = null;

	private JLabel jSentUpdateRateLabel = null;

	private JTextField jSentUpdateRateTextField = null;

	private JLabel jOutAvgXtaTime;

	private JTextField jOutXtaAverageTextField = null;

	private JButton jStartSendButton = null;

	private JButton jCancelSendButton = null;

	private JLabel jHoldbackDelayLabel = null;

	private JTextField jHoldbackDelayTextField = null;

	private JLabel jHoldbackThreasholdLabel = null;

	private JTextField jHoldbackThreasholdTextField = null;

	private JButton  jLoggingButton = null;

	Component 										mFameInstance;
	DistributorPublisherIf mPublisher;
	DistributorConnectionIf mConnection;
	
	String												mAddressString = DistributorConnectionConfiguration.DEFAULT_MCA_ADDRESS;
	String											    mPortString = String.valueOf( DistributorConnectionConfiguration.DEFAULT_MCA_PORT );  //  @jve:decl-index=0:
	String												mErrorRateString = "0";
	String												mSegmentSizeString ="8192";
	String										        mIpBufferSizeString = "64000";
	String												mHoldbackDelayString ="10";  //  @jve:decl-index=0:
	String 												mHoldbackThreasholdString= "400";  //  @jve:decl-index=0:
	String												mUpdateCountString = "100000";
	String												mUpdateRateString = "200";
	String 												mUpdateMinSizeString = "100";
	String												mUpdateMaxSizeString = "100";
	int													mTestId = -1;
	StatisticsThread									mStatisticsThread = null;  //  @jve:decl-index=0:
	SenderThread										mSenderThread = null;
	long												mSendSequenceNumber = 0;
	boolean												mMaximize = false;

	boolean 											mUseSimpleAsiiData = false;

	private JLabel jWarmupLabel = null;
	private JLabel jLoggingLabel = null;
	private JLabel jEthDeviceLabel = null;

	private JTextField jWarmupTextField = null;
	private JTextField jEthDeviceTextField;
	private JTextField jOutMaxXtaTextField;
	private JButton jEthDeviceButton;
	private Distributor mDistributor = null;

	private int mBdxGwyPort = 0;
	private String mBdxGwyHost = null;
	/**
	 * This is the default constructor
	 */
	public GuiPublisher() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(525, 440);
		this.setResizable(false);
		this.setBackground(new Color(255, 255, 204));
		this.setContentPane(getJContentPane());
		this.setTitle("TestPublisher");
		try {
			{
				BufferedImage icon = ImageIO.read(getClass().getResourceAsStream("arrow_out.gif"));
				setIconImage(icon);
			}
		}
		catch( Exception e) {
			this.setIconImage(Toolkit.getDefaultToolkit().getImage("arrow_out.gif"));
		}

		
		this.getJAddressTextField().setText( DistributorConnectionConfiguration.DEFAULT_MCA_ADDRESS);
		this.getJPortTextField().setText( String.valueOf( DistributorConnectionConfiguration.DEFAULT_MCA_PORT ));
		this.getJUpdateToSendTextField().setText("100000");
		this.getJUpdateMinSizeTextField().setText("100");
		this.getJUpdateMaxSizeTextField().setText("100");
		this.getJUpdateRateTextField().setText("10");
		this.getJErrorRateTextField().setText("0");
		this.getJHoldbackDelayTextField().setText("10");
		this.getJHoldbackThreasholdTextField().setText("400");
		this.getJSegmentSizeTextField().setText("8192");
		this.getJIpBufferSizeField().setText("128000");
		this.getJWarmupTextField().setText("0");
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
			jContentPane.add(getJParameterPanel(), BorderLayout.CENTER);
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
			jPortLabel.setBounds(new Rectangle(201, 32, 63, 21));
			jPortLabel.setText(" Port");
			jAdressLabel = new JLabel();
			jAdressLabel.setBounds(new Rectangle(10, 34, 73, 16));
			jAdressLabel.setText("Address");
			jMulticastGroupLabel = new JLabel();
			jMulticastGroupLabel.setText("Multicast Group");
			jMulticastGroupLabel.setBounds(new Rectangle(14, 6, 151, 16));
	
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
			jWarmupLabel = new JLabel();
			jWarmupLabel.setBounds(new Rectangle(10, 219, 120, 15));
			jWarmupLabel.setText("Warm up time");
			jWarmupLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jHoldbackThreasholdLabel = new JLabel();
			jHoldbackThreasholdLabel.setBounds(new Rectangle(10, 160, 122, 15));
			jHoldbackThreasholdLabel.setText("Holdback threshold");
			jHoldbackThreasholdLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jHoldbackDelayLabel = new JLabel();
			jHoldbackDelayLabel.setBounds(new Rectangle(10, 140, 117, 15));
			jHoldbackDelayLabel.setText("Holdback (ms)");
			jHoldbackDelayLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jErrorRateLabel = new JLabel();
			jErrorRateLabel.setBounds(new Rectangle(10, 120, 102, 15));
			jErrorRateLabel.setText("Error rate %%");
			jErrorRateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jUpdateRateLabel = new JLabel();
			jUpdateRateLabel.setBounds(10, 80, 133, 16);
			jUpdateRateLabel.setText("Updates to send / sec");
			jUpdateRateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jIpBufferSizeLabel = new JLabel();
			jIpBufferSizeLabel.setBounds(new Rectangle(10, 200, 89, 16));
			jIpBufferSizeLabel.setText("IP buffer size");
			jIpBufferSizeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jSegmentSizeLabel = new JLabel();
			jSegmentSizeLabel.setBounds(new Rectangle(10, 180, 91, 16));
			jSegmentSizeLabel.setText("Segment size");
			jSegmentSizeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jUpdateMaxSizeLabel = new JLabel();
			jUpdateMaxSizeLabel.setBounds(10, 60, 122, 16);
			jUpdateMaxSizeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jUpdateMaxSizeLabel.setText("Update max size");
			jUpdateMinSizeLabel = new JLabel();
			jUpdateMinSizeLabel.setBounds(new Rectangle(10, 40, 95, 16));
			jUpdateMinSizeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jUpdateMinSizeLabel.setText("Update min size");
			jUpdateToSendLabel = new JLabel();
			jUpdateToSendLabel.setText("Updates to send");
			jUpdateToSendLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jUpdateToSendLabel.setBounds(new Rectangle(10, 20, 97, 16));
			jLoggingLabel = new JLabel();
			jLoggingLabel.setText("Log Level");
			jLoggingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jLoggingLabel.setBounds(new Rectangle(10, 260, 97, 16));

			jEthDeviceLabel = new JLabel();
			jEthDeviceLabel.setBounds(new Rectangle(10, 240, 102, 15));
			jEthDeviceLabel.setText("Eth Device");
			jEthDeviceLabel.setFont(new Font("Arial", Font.PLAIN, 12));


			jParameterPanel = new JPanel();
			jParameterPanel.setLayout(null);
			jParameterPanel.setPreferredSize(new Dimension(200, 1));
			jParameterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Parameters", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP, new Font("Dialog", Font.ITALIC, 10), Color.blue));
			jParameterPanel.add(jUpdateToSendLabel, null);
			jParameterPanel.add(getJUpdateToSendTextField(), null);
			jParameterPanel.add(jUpdateMinSizeLabel, null);
			jParameterPanel.add(getJUpdateMinSizeTextField(), null);
			jParameterPanel.add(jUpdateMaxSizeLabel, null);
			jParameterPanel.add(getJUpdateMaxSizeTextField(), null);
			jParameterPanel.add(jSegmentSizeLabel, null);
			jParameterPanel.add(getJSegmentSizeTextField(), null);
			jParameterPanel.add(jIpBufferSizeLabel, null);
			jParameterPanel.add(getJIpBufferSizeField(), null);
			jParameterPanel.add(jUpdateRateLabel, null);
			jParameterPanel.add(getJUpdateRateTextField(), null);
			jParameterPanel.add(jErrorRateLabel, null);
			jParameterPanel.add(getJErrorRateTextField(), null);
			jParameterPanel.add(jHoldbackDelayLabel, null);
			jParameterPanel.add(getJHoldbackDelayTextField(), null);
			jParameterPanel.add(jHoldbackThreasholdLabel, null);
			jParameterPanel.add(getJHoldbackThreasholdTextField(), null);
			jParameterPanel.add(getJStatisticsPanel(), null);
			jParameterPanel.add(jWarmupLabel, null);
			jParameterPanel.add(getJWarmupTextField(), null);
			jParameterPanel.add(jEthDeviceLabel, null);
			jParameterPanel.add(getEthDeviceTextField(), null);
			jParameterPanel.add(getJEthDeviceBtn());

			jParameterPanel.add(jLoggingLabel, null);
			jParameterPanel.add(getLoggingButton(), null);




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
			jOutAvgXtaTime = new JLabel();
			jOutAvgXtaTime.setBounds(new Rectangle(10, 100, 159, 16));
			jOutAvgXtaTime.setText("Average XMIT Time (usec)");
			jOutAvgXtaTime.setFont(new Font("Arial", Font.PLAIN, 12));
			jSentUpdateRateLabel = new JLabel();
			jSentUpdateRateLabel.setBounds(new Rectangle(10, 60, 146, 15));
			jSentUpdateRateLabel.setText("Update rate (upd/s)");
			jSentUpdateRateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jSentByteRateLabel = new JLabel();
			jSentByteRateLabel.setBounds(new Rectangle(10, 20, 146, 15));
			jSentByteRateLabel.setText("Byte rate (Kbit/s)");
			jSentByteRateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jBytesSentLabel = new JLabel();
			jBytesSentLabel.setBounds(new Rectangle(10, 40, 146, 16));
			jBytesSentLabel.setText("Bytes sent (Kb)");
			jBytesSentLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jUpdatesSentLabel = new JLabel();
			jUpdatesSentLabel.setBounds(new Rectangle(10, 80, 155, 16));
			jUpdatesSentLabel.setText("Updates sent");
			jUpdatesSentLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			jStatisticsPanel = new JPanel();
			jStatisticsPanel.setLayout(null);
			jStatisticsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Statistics", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.ITALIC, 10), Color.blue));
			jStatisticsPanel.setBounds(261, 0, 256, 315);
			jStatisticsPanel.add(jUpdatesSentLabel, null);
			jStatisticsPanel.add(getJUpdatesSentTextField(), null);
			jStatisticsPanel.add(jBytesSentLabel, null);
			jStatisticsPanel.add(getJBytesSentTextField(), null);
			jStatisticsPanel.add(jSentByteRateLabel, null);
			jStatisticsPanel.add(getJBytesRateTextField(), null);
			jStatisticsPanel.add(jSentUpdateRateLabel, null);
			jStatisticsPanel.add(getJSentUpdateRateTextField(), null);
			jStatisticsPanel.add(jOutAvgXtaTime, null);
			jStatisticsPanel.add(getJAverageXtaTimeTextField(), null);
			
			JLabel lblMaxXmitTime = new JLabel();
			lblMaxXmitTime.setText("Max XMIT Time (usec)");
			lblMaxXmitTime.setFont(new Font("Dialog", Font.PLAIN, 12));
			lblMaxXmitTime.setBounds(new Rectangle(10, 100, 146, 16));
			lblMaxXmitTime.setBounds(10, 121, 146, 16);
			jStatisticsPanel.add(lblMaxXmitTime);
			
			jOutMaxXtaTextField = new JTextField();
			jOutMaxXtaTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jOutMaxXtaTextField.setFont(new Font("Dialog", Font.PLAIN, 10));
			jOutMaxXtaTextField.setEditable(false);
			jOutMaxXtaTextField.setBounds(new Rectangle(174, 100, 50, 16));
			jOutMaxXtaTextField.setBounds(183, 121, 50, 16);
			jStatisticsPanel.add(jOutMaxXtaTextField);
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
			jBottomPanel.setPreferredSize(new Dimension(1, 30));
			jBottomPanel.add(getJStartSendButton(), null);
			jBottomPanel.add(getJCancelSendButton(), null);
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
			jAddressTextField.setBounds(new Rectangle(87, 33, 96, 20));
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
			jPortTextField.setBounds(new Rectangle(249, 33, 51, 20));
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
			jConnectButton.setBackground(Color.LIGHT_GRAY);
			jConnectButton.setAction(new AbstractAction(){

				public void actionPerformed(ActionEvent arg0) {
					connectAction();
				}
			});
			jConnectButton.setBounds(new Rectangle(324, 32, 106, 20));
			jConnectButton.setText("Connect");
		}
		return jConnectButton;
	}

	/**
	 * This method initializes jUpdateToSendTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJUpdateToSendTextField() {
		if (jUpdateToSendTextField == null) {
			jUpdateToSendTextField = new JTextField();
			jUpdateToSendTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			
			jUpdateToSendTextField.setBounds(new Rectangle(155, 20, 65, 16));
			jUpdateToSendTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jUpdateToSendTextField;
	}

	/**
	 * This method initializes jUpdateMinSizeTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJUpdateMinSizeTextField() {
		if (jUpdateMinSizeTextField == null) {
			jUpdateMinSizeTextField = new JTextField();
			jUpdateMinSizeTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jUpdateMinSizeTextField.setBounds(new Rectangle(155, 40, 65, 16));
			jUpdateMinSizeTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jUpdateMinSizeTextField;
	}

	/**
	 * This method initializes jUpdateMaxSizeTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJUpdateMaxSizeTextField() {
		if (jUpdateMaxSizeTextField1 == null) {
			jUpdateMaxSizeTextField1 = new JTextField();
			jUpdateMaxSizeTextField1.setHorizontalAlignment(SwingConstants.TRAILING);
			jUpdateMaxSizeTextField1.setBounds(new Rectangle(155, 60, 65, 16));
			jUpdateMaxSizeTextField1.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jUpdateMaxSizeTextField1;
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
			jSegmentSizeTextField.setBounds(new Rectangle(155, 180, 66, 16));
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
			jIpBufferSizeField.setBounds(new Rectangle(155, 200, 66, 16));
			jIpBufferSizeField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jIpBufferSizeField;
	}

	/**
	 * This method initializes jUpdateRateTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJUpdateRateTextField() {
		if (jUpdateRateTextField == null) {
			jUpdateRateTextField = new JTextField();
			jUpdateRateTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jUpdateRateTextField.setBounds(new Rectangle(155, 80, 65, 16));
			jUpdateRateTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jUpdateRateTextField;
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
			jErrorRateTextField.setBounds(new Rectangle(155, 120, 66, 16));
			jErrorRateTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jErrorRateTextField;
	}

	/**
	 * This method initializes jUpdatesSentTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJUpdatesSentTextField() {
		if (jUpdatesSentTextField == null) {
			jUpdatesSentTextField = new JTextField();
			jUpdatesSentTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jUpdatesSentTextField.setEditable(false);
			jUpdatesSentTextField.setBounds(new Rectangle(183, 81, 50, 16));
			jUpdatesSentTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jUpdatesSentTextField;
	}

	/**
	 * This method initializes jBytesSentTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJBytesSentTextField() {
		if (jBytesSentTextField == null) {
			jBytesSentTextField = new JTextField();
			jBytesSentTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jBytesSentTextField.setBounds(new Rectangle(183, 41, 50, 16));
			jBytesSentTextField.setEditable(false);
			jBytesSentTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jBytesSentTextField;
	}

	/**
	 * This method initializes jBytesRateTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJBytesRateTextField() {
		if (jSentBytesRateTextField == null) {
			jSentBytesRateTextField = new JTextField();
			jSentBytesRateTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jSentBytesRateTextField.setBounds(new Rectangle(183, 21, 50, 16));
			jSentBytesRateTextField.setEditable(false);
			jSentBytesRateTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jSentBytesRateTextField;
	}

	/**
	 * This method initializes jSentUpdateRateTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJSentUpdateRateTextField() {
		if (jSentUpdateRateTextField == null) {
			jSentUpdateRateTextField = new JTextField();
			jSentUpdateRateTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jSentUpdateRateTextField.setBounds(new Rectangle(183, 61, 50, 16));
			jSentUpdateRateTextField.setEditable(false);
			jSentUpdateRateTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jSentUpdateRateTextField;
	}

	/**
	 * This method initializes jRequestQueueLengthTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJAverageXtaTimeTextField() {
		if (jOutXtaAverageTextField == null) {
			jOutXtaAverageTextField = new JTextField();
			jOutXtaAverageTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jOutXtaAverageTextField.setBounds(new Rectangle(183, 101, 50, 16));
			jOutXtaAverageTextField.setEditable(false);
			jOutXtaAverageTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jOutXtaAverageTextField;
	}

	/**
	 * This method initializes jStartSendButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	@SuppressWarnings("serial")
	private JButton getJStartSendButton() {
		if (jStartSendButton == null) {
			jStartSendButton = new JButton();
			jStartSendButton.setAction(new AbstractAction(){
				public void actionPerformed(ActionEvent e) 
				{
					startToSendAction();
				}
			});
			jStartSendButton.setBounds(new Rectangle(114, 5, 115, 20));
			jStartSendButton.setText("Start Send");
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
	private JButton getJCancelSendButton() {
		if (jCancelSendButton == null) {
			jCancelSendButton = new JButton();
			jCancelSendButton.setAction(new AbstractAction()
			{
				public void actionPerformed(ActionEvent arg0) {
					cancelButtonAction();
				}
			});
			jCancelSendButton.setBounds(new Rectangle(256, 5, 115, 20));
			jCancelSendButton.setEnabled(false);
			jCancelSendButton.setText("Cancel");
		}
		return jCancelSendButton;
	}

	private JButton getJEthDeviceBtn() {
		if (jEthDeviceButton == null) {

			jEthDeviceButton = new JButton("");
			jEthDeviceButton.setBackground(Color.LIGHT_GRAY);
			jEthDeviceButton.setBounds(226, 240, 32, 16);

			jEthDeviceButton.setAction(new AbstractAction(){

				public void actionPerformed(ActionEvent arg0) {
					listEthDevices();
				}
			});
			jEthDeviceButton.setText("...");

		}
		return jEthDeviceButton;
	}

	/**
	 * This method initializes jHoldbackDelayTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJHoldbackDelayTextField() {
		if (jHoldbackDelayTextField == null) {
			jHoldbackDelayTextField = new JTextField(mHoldbackDelayString);
			jHoldbackDelayTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jHoldbackDelayTextField.setBounds(new Rectangle(155, 140, 66, 16));
			jHoldbackDelayTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jHoldbackDelayTextField;
	}

	/**
	 * This method initializes jHoldbackThreasholdTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJHoldbackThreasholdTextField() {
		if (jHoldbackThreasholdTextField == null) {
			jHoldbackThreasholdTextField = new JTextField();
			jHoldbackThreasholdTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jHoldbackThreasholdTextField.setBounds(new Rectangle(155, 160, 66, 16));
			jHoldbackThreasholdTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jHoldbackThreasholdTextField;
	}
	

	
	void parseArguments( String[] args ) 
	{
		int i = 0;
		while( i < args.length) {
			if (args[i].compareToIgnoreCase("-device") == 0) {
				this.jEthDeviceTextField.setText(args[i+1]);
				i++;
			}
			if (args[i].compareToIgnoreCase("-rate") == 0) {
				this.jSentUpdateRateTextField.setText(args[i+1]);

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

			if (args[i].compareToIgnoreCase("-ascciData") == 0) {
				mUseSimpleAsiiData = Boolean.parseBoolean(args[i+1]);
				i++;
			}

			if (args[i].compareToIgnoreCase("-maximize") == 0) {
				mMaximize = Boolean.parseBoolean(args[i+1]);
				i++;
			}

			i++;
		}
		
	
		
	}
	
	class StatisticsThread extends Thread {
		AtomicLong mTotalUpdates;
		AtomicLong mBytesSent;
		AtomicLong mUpdatesSent;
		AtomicInteger mTotalXtaTime;
		AtomicInteger mMaxXtaTime;
		volatile boolean mTimeToDie;
		long mStartTime;
		
		StatisticsThread() {
			mBytesSent = new AtomicLong(0);
			mUpdatesSent = new AtomicLong(0);
			mTotalXtaTime = new AtomicInteger(0);
			mMaxXtaTime = new AtomicInteger(0);
			mTotalUpdates = new AtomicLong(0);
			mTimeToDie = false;
			mStartTime = 0;
		}
		
		void update( int pBytesSent, int pXtaTime ) {
			mBytesSent.getAndAdd( pBytesSent );
			mUpdatesSent.getAndIncrement();
			mTotalUpdates.getAndIncrement();
			
			mTotalXtaTime.getAndAdd( pXtaTime );
			if (pXtaTime > mMaxXtaTime.get()) {
				mMaxXtaTime.set(pXtaTime);
			}
			if (mStartTime == 0) 
			  mStartTime = System.currentTimeMillis();
		}
		
		public void run() {
			long tTimeDiff;
			while(!mTimeToDie ) {
				try { sleep(1000); }
				catch( InterruptedException e ) {}
				if (mStartTime != 0) {
					tTimeDiff = System.currentTimeMillis() - mStartTime + 1;
					double tByteRate = (double) (mBytesSent.get() / tTimeDiff);
					int tUpdateRate = (int) ((mUpdatesSent.get() * 1000) / tTimeDiff);
					int tAvgXtaTime = (int) (mTotalXtaTime.get() / mTotalUpdates.get());
				
					
					SwingUtilities.invokeLater(	new UpdateStatisticsStringTask( 
												GuiPublisher.this, 
												tByteRate, 
												tUpdateRate,
												tAvgXtaTime,
												mMaxXtaTime.get(), 
												mBytesSent.get(), 
												mUpdatesSent.get()));
				}
			}
		}
	}
	
	class SenderThread extends Thread {
		int mBatchFactor;
		long mDismiss;
		volatile boolean mTimeToDie;
		GuiPublisher mFrame;
		
		int mMinSize, mMaxSize;
		int mMessagesToSend;
		double mRate;
		Random mRandom;
		
		SenderThread( GuiPublisher pFrame ) {
			mTimeToDie = false;
			mFrame = pFrame;
			mRandom = new Random( System.currentTimeMillis());
		}
		
		void parseSenderData() {
			mMessagesToSend = Integer.parseInt(mFrame.getJUpdateToSendTextField().getText());
			mMinSize = Integer.parseInt( mFrame.getJUpdateMinSizeTextField().getText());
			mMaxSize = Integer.parseInt( mFrame.getJUpdateMaxSizeTextField().getText());
			mRate =  Double.parseDouble( mFrame.getJUpdateRateTextField().getText());

			mSendSequenceNumber = 0;
			
			if (mMaximize) {
				mBatchFactor = 5000;
				mDismiss = (long) 1L;
				return;
			}


			if (mRate == 0)
			{
				mBatchFactor = 5000;
				mDismiss = (long) 1L;
				return;
			}
			
			
			if (mRate < 1.0) {
				mBatchFactor = 1;
				mDismiss = (long) (1 / mRate);
			}
			else if (mRate  <= 100 ) {
				mBatchFactor = 1;
				mDismiss = (long) (1000 / mRate);		
			}
			else if (mRate <= 200) {
				mBatchFactor = 2;
				mDismiss = (long) (2000 / mRate);
			}
			else if (mRate <= 800) {
				mBatchFactor = 5;
				mDismiss = (long) (5000 / mRate);
			}
			else if ( mRate  <= 2000 )
			{
			   mBatchFactor = 20;
			   mDismiss = (long) (20000 / mRate);
			}
			else if (mRate <= 5000) {
				mBatchFactor = 80;
				mDismiss = (long) (80000 / mRate);
			}
			else if (mRate <= 20000) {
				mBatchFactor = 200;
				mDismiss = (long) (200000 / mRate);
			}	
			else if (mRate <= 50000) {
				mBatchFactor = 500;
				mDismiss = (long) (500000 / mRate);
			}	
			else {
				mBatchFactor = 1000;
				mDismiss = 0;
			}		
		}
		
	
		
		@SuppressWarnings("static-access")
		void rampup( long tRampupTime ) {
			long tStartTime = System.currentTimeMillis();
			while( (tStartTime + tRampupTime) > System.currentTimeMillis()) {
				for( int i = 0; i < 10; i++ ) {
				  byte[] tBuffer = getSendData(100);
				  tBuffer[8] = 0;
				  try {mFrame.mPublisher.publish(mFrame.cTestSubject, tBuffer); }
				  catch( DistributorException e) {
					  e.printStackTrace();
				  }
				}
				try { Thread.currentThread().sleep(20); }
				catch( InterruptedException e) {}
			}
		}

		byte[] getSimpleAsiiData() {
			String tMsgStr = "[ subject: " + cTestSubject + " time: " + SDF.format(System.currentTimeMillis()) + " SenderSeqNo: " + String.valueOf((mSendSequenceNumber++));
			return tMsgStr.getBytes();
		}
		
		byte[] getSendData() {
			return getSendData(0);
		}
		
		byte[] getSendData(int pMessageSize) {
			byte[] tBuffer = null;
			int tSendSize;
			
			if (pMessageSize != 0) {
				tSendSize = pMessageSize;
			} else {
				if (mMinSize == mMaxSize) {
					tSendSize = mMaxSize;
				} else {
					tSendSize = mMinSize + Math.abs(this.mRandom.nextInt() % (mMaxSize - mMinSize));
				}
			}
			
			if (tSendSize < 9) {
			  tSendSize = 9;
			}
			
			
			tBuffer = new byte[ tSendSize ];
			long2Buffer(mSendSequenceNumber++, tBuffer, 0);
		    return tBuffer;
		}
		
		
		
		@SuppressWarnings("static-access")
		public void run() {
			int tSendTime = 0;
			long mMessagesSent = 0;
			byte[] tSendBuffer;
			SimpleDateFormat mSDF = new SimpleDateFormat("HH:mm:ss.SSS");
			
			parseSenderData();
	
			int tRampupTime = Integer.parseInt(getJWarmupTextField().getText()) * 1000;
			if (tRampupTime > 0)
			  rampup( tRampupTime );
			
			while ((!mTimeToDie) && ( mMessagesSent <= mMessagesToSend)) {
				for( int i = 0; i < mBatchFactor; i++) {
				
					if ( (!mMaximize) && (mRate > 0) && (mMessagesSent % 1000) == 0) {
						System.out.println("[SENT] " + mSDF.format( System.currentTimeMillis()) + " sent: " + mMessagesSent);
					}
					try {
						if (mUseSimpleAsiiData) {
							tSendBuffer = getSimpleAsiiData();
						} else {
							tSendBuffer = getSendData();
							tSendBuffer[8] = 1;
						}

						tSendTime = mFrame.mPublisher.publish(mFrame.cTestSubject, tSendBuffer); 
						mFrame.mStatisticsThread.update(tSendBuffer.length, tSendTime);
					}
					catch( DistributorException e ) {
						new ErrorMessage( mFrame, "Failed to publish", e.getMessage());
					}
					mMessagesSent++;
					if (mMessagesSent > mMessagesToSend) {
						synchronized( mFrame.mStatisticsThread ) {
							mFrame.mStatisticsThread.mTimeToDie = true;
							mFrame.mStatisticsThread.interrupt();
							mFrame.mStatisticsThread = null;
							mFrame.mSenderThread = null;
						}
						SwingUtilities.invokeLater(	new UpdateButtonsTask( mFrame, true, false, false ));
						try { sleep( 500 ); }
						catch( InterruptedException e) {}
						return;
					}
				}
				if ((!mMaximize) && (mRate > 0)){
				try { sleep( mDismiss ); }
				catch( InterruptedException e) {}
			}
		}
	}
	}
	
	
	
	 class UpdateStatisticsStringTask implements Runnable {
		 double mByteRate;
		 int mXtaAverageTime;
		 int mMaxXtaTime;
		 int mUpdateRate;
		 long mUpdatesReceived;
		 long mBytesReceived;
		 GuiPublisher	mFrame;
		 
		 UpdateStatisticsStringTask( GuiPublisher pFrame, double pByteRate, int pUpdateRate, int pXtaAverageTime, int pMaxXtaTime, long pTotalBytes, long pTotalUpdates ) {
			 mByteRate = pByteRate;
			 mUpdateRate = pUpdateRate;
			 mXtaAverageTime = pXtaAverageTime;
			 mMaxXtaTime = pMaxXtaTime;
			 mUpdatesReceived = pTotalUpdates;
			 mBytesReceived = pTotalBytes;
			 mFrame = pFrame;
		 }
		 
		 public void run() {
			 DecimalFormat tDecFmt = new DecimalFormat();
			 tDecFmt.setMaximumFractionDigits(2);
			 tDecFmt.setMaximumFractionDigits(2);
			
			 
			 
			 
			 mFrame.getJBytesRateTextField().setText( tDecFmt.format(mByteRate));
			 mFrame.getJAverageXtaTimeTextField().setText(String.valueOf(mXtaAverageTime));
			 mFrame.getJSentUpdateRateTextField().setText( String.valueOf(mUpdateRate));
			 mFrame.getJUpdatesSentTextField().setText(String.valueOf(mUpdatesReceived));
			 mFrame.getJBytesSentTextField().setText(String.valueOf((mBytesReceived/1024)));
			 mFrame.jOutXtaAverageTextField.setText( String.valueOf(mXtaAverageTime));
			 mFrame.jOutMaxXtaTextField.setText( String.valueOf(mMaxXtaTime));
		 }		 
	 }
	 
	 class UpdateButtonsTask implements Runnable {
		 boolean mStartToSend;
		 boolean mCancel;
		 boolean mEtDevices;
		 GuiPublisher mFrame;
		 
		 UpdateButtonsTask( GuiPublisher pFrame, boolean pStartToReceive, boolean pCancel, boolean pEthDevices ) {
			 mStartToSend  =  pStartToReceive;
			mCancel = pCancel;
			mFrame = pFrame;
			mEtDevices = pEthDevices;
		 }
		 
		 public void run() {
			 mFrame.getJCancelSendButton().setEnabled(mCancel);
			 mFrame.getJStartSendButton().setEnabled(mStartToSend);
		 	 mFrame.getJEthDeviceBtn().setEnabled( mEtDevices );
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
	
	
	
	void 	cancelButtonAction() {
		if (mSenderThread != null) {
			synchronized( mSenderThread ) {
				mSenderThread.mTimeToDie = true;
				mSenderThread.interrupt();
				mSenderThread = null;
			}
			if ( mStatisticsThread != null) {
				synchronized( mStatisticsThread ) {
					mStatisticsThread.mTimeToDie = true;
					mStatisticsThread.interrupt();
					mStatisticsThread = null;
				}
			}
			getJCancelSendButton().setEnabled(false);
			getJStartSendButton().setEnabled(true);
			getJEthDeviceBtn().setEnabled(false);
		}
	}
	
	void startToSendAction() {
		mStatisticsThread = new StatisticsThread();
		mStatisticsThread.start();
		mSenderThread = new SenderThread(this);
		mSenderThread.start();
		getJBytesSentTextField().setText("0");
		getJSentUpdateRateTextField().setText("0");
		getJAverageXtaTimeTextField().setText("0");
		getJBytesRateTextField().setText("0");
		getJUpdatesSentTextField().setText("0");
		getJCancelSendButton().setEnabled(true);
		getJStartSendButton().setEnabled(false);
		getJEthDeviceBtn().setEnabled(false);
	}
	
	void connectAction() {
		
		if (connectNative()) {
			getJStartSendButton().setEnabled(true);
			getJCancelSendButton().setEnabled(false);
			getJConnectButton().setEnabled(false);
		}
	}
	

	
	
		
	boolean connectNative() {	
		DistributorApplicationConfiguration tApplConfig;
		DistributorConnectionConfiguration tConfig;
		DistributorConnectionIf tConnection;
			
		tApplConfig = new DistributorApplicationConfiguration("TestPublisher");
		if ((mBdxGwyHost != null) && (mBdxGwyPort != 0)) {
			tApplConfig.setBroadcastGatewayAddress( mBdxGwyHost);
			tApplConfig.setBroadcastGatewayPort( mBdxGwyPort );
		}
		tApplConfig.setEthDevice(this.jEthDeviceTextField.getText());
		tApplConfig.setLogFlags( mLogFlags );

		
		try {
			mDistributor = new Distributor( tApplConfig );
			
			tConfig = new DistributorConnectionConfiguration( getJAddressTextField().getText(), Integer.parseInt(getJPortTextField().getText()));
			tConfig.setFakeXtaErrorRate( Integer.parseInt(getJErrorRateTextField().getText()));
			tConfig.setSendHoldbackDelay(Integer.parseInt(getJHoldbackDelayTextField().getText()));
			tConfig.setSendHoldbackThreshold(Integer.parseInt( getJHoldbackThreasholdTextField().getText()));
			tConfig.setIpBufferSize(Integer.parseInt( this.getJIpBufferSizeField().getText()));
			//tConfig.setStatisticsLogInterval(10000);
			
	
			tConnection = mDistributor.createConnection(tConfig);
			mPublisher = mDistributor.createPublisher(tConnection, this);

		}
		catch( DistributorException e) {
			new ErrorMessage( this, "Failed to connect to Distributor", e.getMessage());
			return false;
		}
		return true;
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
	
	
	
	public void distributorEventCallback( DistributorEvent pDistributorEvent ) {
		if (pDistributorEvent instanceof DistributorErrorEvent)
			new ErrorMessage( this, "Distribution Error", pDistributorEvent.toString());
		else
			System.out.println("Distributor Event: " + pDistributorEvent.toString());
	}


	private JButton getLoggingButton() {
		if (jLoggingButton == null) {
			jLoggingButton = new JButton();
			jLoggingButton.setAction(new AbstractAction(){
				public void actionPerformed(ActionEvent e)
				{
					setLogLevel();
				}
			});
			jLoggingButton.setBounds(new Rectangle(154, 260, 104, 18));
			jLoggingButton.setText("Set Log Level");
			jLoggingButton.setEnabled(true);
		}
		return jLoggingButton;
	}

	private JTextField getEthDeviceTextField() {
		if (jEthDeviceTextField == null) {
			jEthDeviceTextField = new JTextField();
			jEthDeviceTextField.setHorizontalAlignment(SwingConstants.CENTER);
			jEthDeviceTextField.setText("eth0");
			jEthDeviceTextField.setFont(new Font("Dialog", Font.PLAIN, 10));
			jEthDeviceTextField.setBounds(new Rectangle(154, 240, 66, 16));
		}
		return jEthDeviceTextField;
	}

	/**
	 * This method initializes jRampupTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJWarmupTextField() {
		if (jWarmupTextField == null) {
			jWarmupTextField = new JTextField();
			jWarmupTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			jWarmupTextField.setBounds(new Rectangle(154, 219, 66, 16));
			jWarmupTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jWarmupTextField;
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
	 * @param args, program arguments
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {
			System.out.println( ex.getMessage());
		}
		GuiPublisher thisClass = new GuiPublisher();
		thisClass.parseArguments(args);
		thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		thisClass.setVisible(true);
		while( true ) {
			try {Thread.currentThread().sleep( 10000 ); }
			catch( InterruptedException e ) {}
		}
	}

	
}  //  @jve:decl-index=0:visual-constraint="272,7"
