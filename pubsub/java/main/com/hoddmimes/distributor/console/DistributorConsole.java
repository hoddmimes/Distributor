package com.hoddmimes.distributor.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.hoddmimes.distributor.Distributor;
import com.hoddmimes.distributor.DistributorApplicationConfiguration;
import com.hoddmimes.distributor.DistributorConnectionConfiguration;
import com.hoddmimes.distributor.DistributorConnectionIf;
import com.hoddmimes.distributor.DistributorErrorEvent;
import com.hoddmimes.distributor.DistributorEvent;
import com.hoddmimes.distributor.DistributorEventCallbackIf;
import com.hoddmimes.distributor.DistributorPublisherIf;
import com.hoddmimes.distributor.DistributorSubscriberIf;
import com.hoddmimes.distributor.DistributorUpdateCallbackIf;
import com.hoddmimes.distributor.auxillaries.UUIDFactory;
import com.hoddmimes.distributor.generated.messages.*;
import com.hoddmimes.distributor.messaging.MessageBinDecoder;
import com.hoddmimes.distributor.messaging.MessageBinEncoder;
import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.messaging.MessageWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DistributorConsole extends JFrame implements
		TreeSelectionListener, DistributorEventCallbackIf,
		DistributorUpdateCallbackIf {

	private static final Logger cLogger = LogManager.getLogger( DistributorConsole.class.getSimpleName());

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel jTopPanel = null;

	static final String cConsoleSubjectControlTopic = "/DistCtlMsg001"; // @jve:decl-index=0:

	private JLabel jAddressLabel = null;

	private JTextField jAddressTextField = null;

	private JLabel jPortLabel = null;

	private JTextField jPortTextField = null;

	private JButton jGetDistributorsButton = null;

	private JButton jConnecButton = null;

	private JScrollPane jTreeScrollPane = null;

	private JTree jDistributorTree = null;

	/**
	 * My Frame variables
	 */
	String mAddressString = DistributorApplicationConfiguration.DEFAULT_CMA_ADDRESS; // @jve:decl-index=0:
	String mPortString = String
			.valueOf(DistributorApplicationConfiguration.DEAFULT_CMA_PORT);
	Component mFameInstance = null;
	DistributorConnectionIf mConnection = null;
	DistributorPublisherIf mPublisher = null;
	DistributorSubscriberIf mSubscriber = null;
	long mCurrentRequestId;
	RootTreeNode mTreeRoot = new RootTreeNode(); // @jve:decl-index=0:

	private JLabel jTransportLabel = null;

	private JScrollPane jTextAreaScrollPane = null;

	private JSplitPane jSplitPane = null;

	private JPanel jTextPanel = null;

	private JTextArea jLeftTextArea = null;

	private JTextArea jRightTextArea = null;

	/**
	 * This method initializes jTopPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJTopPanel() {
		if (jTopPanel == null) {
			jTransportLabel = new JLabel();
			jTransportLabel.setBounds(new Rectangle(4, 10, 168, 18));
			jTransportLabel.setFont(new Font("Arial", Font.BOLD, 11));
			jTransportLabel.setText("Control Multicast Group");
			jTransportLabel.setHorizontalAlignment(SwingConstants.CENTER);
			jTransportLabel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));
			jPortLabel = new JLabel();
			jPortLabel.setBounds(new Rectangle(211, 35, 40, 18));
			jPortLabel.setText("Port");
			jPortLabel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));
			jPortLabel.setHorizontalAlignment(SwingConstants.CENTER);
			jPortLabel.setFont(new Font("Arial", Font.BOLD, 10));
			jAddressLabel = new JLabel();
			jAddressLabel.setBounds(new Rectangle(20, 35, 78, 18));
			jAddressLabel.setText("Address");
			jAddressLabel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));
			jAddressLabel.setHorizontalAlignment(SwingConstants.CENTER);
			jAddressLabel.setFont(new Font("Arial", Font.BOLD, 10));
			jTopPanel = new JPanel();
			jTopPanel.setLayout(null);
			jTopPanel.setPreferredSize(new Dimension(1, 85));
			jTopPanel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));
			jTopPanel.add(jAddressLabel, null);
			jTopPanel.add(getJAddressTextField(), null);
			jTopPanel.add(jPortLabel, null);
			jTopPanel.add(getJPortTextField(), null);
			jTopPanel.add(getJGetDistributorsButton(), null);
			jTopPanel.add(getJConnectButton(), null);
			jTopPanel.add(jTransportLabel, null);
		}
		return jTopPanel;
	}

	/**
	 * This method initializes jAddressTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJAddressTextField() {
		if (jAddressTextField == null) {
			jAddressTextField = new JTextField();
			jAddressTextField.setBounds(new Rectangle(105, 35, 97, 18));
			jAddressTextField.setText("");
			jAddressTextField.setFont(new Font("Arial", Font.PLAIN, 11));
			jAddressTextField.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.LOWERED));
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
			jPortTextField.setBounds(new Rectangle(257, 35, 50, 18));
			jPortTextField.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.LOWERED));
			jPortTextField.setText("");
			jPortTextField.setFont(new Font("Arial", Font.PLAIN, 10));
		}
		return jPortTextField;
	}

	/**
	 * This method initializes jGetDistributorsButton
	 * 
	 * @return javax.swing.JButton
	 */
	@SuppressWarnings("serial")
	private JButton getJGetDistributorsButton() {
		if (jGetDistributorsButton == null) {
			jGetDistributorsButton = new JButton();
			jGetDistributorsButton.setAction(new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					exploreDomain();
				}
			});
			jGetDistributorsButton.setText("Get Distributors");
			jGetDistributorsButton.setEnabled(false);
			jGetDistributorsButton.setFont(new Font("Arial", Font.BOLD, 10));
			jGetDistributorsButton.setBounds(new Rectangle(105, 60, 157, 19));
		}
		return jGetDistributorsButton;
	}

	/**
	 * This method initializes jConnectExitButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJConnectButton() {
		if (jConnecButton == null) {
			jConnecButton = new JButton();
			jConnecButton.setAction(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					try {
						establishConnection();
						jConnecButton.setEnabled(false);
						jGetDistributorsButton.setEnabled(true);
					} catch (Throwable exc) {
						new ErrorMessage(mFameInstance,
								"Failed to establish connection", exc
										.getMessage());
					}
				}
			});
			jConnecButton.setBounds(new Rectangle(319, 35, 120, 18));
			jConnecButton.setActionCommand("Pushed");
			jConnecButton.setFont(new Font("Arial", Font.BOLD, 10));
			jConnecButton.setText("Connect");
		}
		return jConnecButton;
	}

	/**
	 * This method initializes jTreeScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJTreeScrollPane() {
		if (jTreeScrollPane == null) {
			jTreeScrollPane = new JScrollPane();
			jTreeScrollPane.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));
			jTreeScrollPane.setViewportView(getJDistributorTree());
		}
		return jTreeScrollPane;
	}

	/**
	 * This method initializes jDistributorTree
	 * 
	 * @return javax.swing.JTree
	 */
	private JTree getJDistributorTree() {
		if (jDistributorTree == null) {
			jDistributorTree = new JTree();

			jDistributorTree.setFont(new Font("Arial", Font.BOLD, 10));
			jDistributorTree.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
			jDistributorTree.setPreferredSize(new Dimension(350, 0));

			jDistributorTree.addTreeSelectionListener(this);
			jDistributorTree.addMouseListener(new TreeMouseEvents(this));
		}
		return jDistributorTree;
	}

	void parseArguments(String[] args) {
		int i = 0;
		while (i < args.length) {
			if (args[i].compareToIgnoreCase("-udp_address") == 0) {
				mAddressString = args[i + 1];
				i++;
			}
			if (args[i].compareToIgnoreCase("-udp_port") == 0) {
				mPortString = args[i + 1];
				i++;
			}
			i++;
		}
	}

	/**
	 * This method initializes jTextAreaScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJTextAreaScrollPane() {
		if (jTextAreaScrollPane == null) {
			jTextAreaScrollPane = new JScrollPane();
			jTextAreaScrollPane.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));
			jTextAreaScrollPane.setViewportView(getJTextPanel());
		}
		return jTextAreaScrollPane;
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setDividerSize(2);
			jSplitPane.setRightComponent(getJTextAreaScrollPane());
			jSplitPane.setLeftComponent(getJTreeScrollPane());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jTextPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJTextPanel() {
		if (jTextPanel == null) {
			GridBagConstraints gridBagConstraintsRight = new GridBagConstraints();
			gridBagConstraintsRight.fill = GridBagConstraints.BOTH;
			gridBagConstraintsRight.weighty = 1.0;
			gridBagConstraintsRight.weightx = 0.3;
			GridBagConstraints gridBagConstraintsLeft = new GridBagConstraints();
			gridBagConstraintsLeft.fill = GridBagConstraints.BOTH;
			gridBagConstraintsLeft.weighty = 1.0;
			gridBagConstraintsLeft.weightx = 1.0;
			jTextPanel = new JPanel();
			jTextPanel.setLayout(new GridBagLayout());
			jTextPanel.add(getJLeftTextArea(), gridBagConstraintsLeft);
			jTextPanel.add(getJRightTextArea(), gridBagConstraintsRight);
		}
		return jTextPanel;
	}

	/**
	 * This method initializes jLeftTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJLeftTextArea() {
		if (jLeftTextArea == null) {
			jLeftTextArea = new JTextArea();
			jLeftTextArea.setEditable(false);
			jLeftTextArea.setTabSize(2);
			jLeftTextArea.setFont(new Font("Arial", Font.BOLD, 10));
		}
		return jLeftTextArea;
	}

	/**
	 * This method initializes jRightTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJRightTextArea() {
		if (jRightTextArea == null) {
			jRightTextArea = new JTextArea();
			jRightTextArea.setEditable(false);
			jLeftTextArea.setTabSize(2);
			jRightTextArea.setFont(new Font("Arial", Font.BOLD, 10));
		}
		return jRightTextArea;
	}

	/**
	 * @param args, program arguments
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		try {
			javax.swing.UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
		DistributorConsole thisClass = new DistributorConsole();
		thisClass.parseArguments(args);
		thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		thisClass.setVisible(true);
		while (true) {
			try {
				Thread.currentThread().sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * This is the default constructor
	 */
	public DistributorConsole() {
		super();
		initialize();
		jDistributorTree.setModel(new DefaultTreeModel(mTreeRoot));
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(837, 353);
		this.setContentPane(getJContentPane());
		this.setTitle("Distributor Console");
		try {
			BufferedImage icon = ImageIO.read(getClass().getResourceAsStream(
					"anchor.gif"));
			setIconImage(icon);
		} catch (Exception e) {
			this.setIconImage(Toolkit.getDefaultToolkit()
					.getImage("anchor.gif"));
		}

		this.setBackground(new Color(255, 255, 204));
		getJAddressTextField().setText(mAddressString);
		getJPortTextField().setText(mPortString);
		getJDistributorTree().setModel(new DefaultTreeModel(mTreeRoot));
		mFameInstance = this;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(0);
			borderLayout.setVgap(0);
			jContentPane = new JPanel();
			jContentPane.setLayout(borderLayout);
			jContentPane.add(getJTopPanel(), BorderLayout.NORTH);
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	public void valueChanged(TreeSelectionEvent e) {
		// DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)
		// jDistributorTree.getLastSelectedPathComponent();
		// DistNetMsg tRequestMessage = ((DistributorTreeNodeIf)
		// tNode).getRequestMessage();
		// if (tRequestMessage != null) {
		// sendRequest( tRequestMessage );
		// }
	}

	void establishConnection() throws Exception {
		DistributorApplicationConfiguration tApplConfig = new DistributorApplicationConfiguration(
				"DistributorConsole");
		tApplConfig.setMgmtControlEnabled(false);
		tApplConfig.setLocalHostAddress("127.0.0.1");
		Distributor tDistributor = new Distributor(tApplConfig);
		DistributorConnectionConfiguration tConfiguration = new DistributorConnectionConfiguration(
				getJAddressTextField().getText(), 					// CMA IP multicast Address
				Integer.parseInt(getJPortTextField().getText()));	// CMA UDP port
		tConfiguration.setCmaConnection(false);						// Do not start CMS controller
		
		mConnection = tDistributor.createConnection(tConfiguration);
		mPublisher = tDistributor.createPublisher(mConnection, this);
		mSubscriber = tDistributor.createSubscriber(mConnection, null, this);
		mSubscriber.addSubscription(cConsoleSubjectControlTopic, null);
	}

	void sendRequest(DistNetMsg pNetMsg) {
		MessageBinEncoder tEncoder = new MessageBinEncoder();
		mCurrentRequestId = UUIDFactory.getId();
		pNetMsg.setRequestId(mCurrentRequestId);
		pNetMsg.setIsRequestMessage(true);
		try {
			System.out.println("[ Console Send Request] " + pNetMsg.getMessage().getWrappedMessageNameSimpleFormat());
			pNetMsg.encode(tEncoder);
			mPublisher.publish(cConsoleSubjectControlTopic, tEncoder.getBytes());
		} catch (Exception e) {
			new ErrorMessage(mFameInstance,
					"Failed to send request to distributors", e.getMessage());
		}
	}

	void exploreDomain() {
		mTreeRoot = new RootTreeNode();
		getJDistributorTree().setModel(new DefaultTreeModel(mTreeRoot));

		DistNetMsg tNetMsg = new DistNetMsg();
		tNetMsg.setMessage(new MessageWrapper(new DistExploreDomainRqst()));
		sendRequest(tNetMsg);
	}

	TextAreaStringBuilder formatDistributor(DistExploreDistributorRsp pMessage) {
		TextAreaStringBuilder tsb = new TextAreaStringBuilder(1024);
		tsb.add("\n\n\n");
		tsb.add("\t Hostname", pMessage.getDistributor().getHostname());
		tsb.add("\t Hostname Address", pMessage.getDistributor().getHostaddress());
		tsb.add("\t Application", pMessage.getDistributor().getApplicationName());
		tsb.add("\t DistributorId", Long.toHexString(pMessage.getDistributor().getDistributorId()));
		tsb.add("\t Start Time", pMessage.getDistributor().getStartTime());
		tsb.add("\t Connections", pMessage.getDistributor().getConnections());
		tsb.add("\t Active subscriptions", pMessage.getDistributor().getSubscriptions());
		tsb.add("\t Retransmissions Requests [in]", pMessage.getDistributor().getInRetransmissions());
		tsb.add("\t Retransmission Requests [out]", pMessage.getDistributor().getOutRetransmissions());
		tsb.add("\t Max Memory (MB)", (pMessage.getDistributor().getMemMax() / (1024L * 1024L)));
		tsb.add("\t Memory Free (MB)", (pMessage.getDistributor().getMemFree() / (1024L *1024L)));
		tsb.add("\t Memory Used (MB)", (pMessage.getDistributor().getMemUsed() / (1024L *1024L)));
		
		return tsb;
	}

	void formatDataRateItem(TextAreaStringBuilder psb, DataRateItem pItem, String pAttributeString) {
		psb.add(pAttributeString + " current value", pItem.getCurrValue());
		psb.add(pAttributeString + " peak value value", pItem.getPeakValue());
		psb.add(pAttributeString + " peak time", pItem.getPeakTime());
	}

	TextAreaStringBuilder formatConnection(DistExploreConnectionRsp pMessage) {
		TextAreaStringBuilder tsb = new TextAreaStringBuilder(1024);
		tsb.add("\t Multicast Group Address", pMessage.getConnection().getMcaAddress());
		tsb.add("\t Multicast Group Port", pMessage.getConnection().getMcaPort());
		tsb.add("", "");
		tsb.add("\t Out Byte Rate", pMessage.getConnection().getXtaBytes().getCurrValue());
		tsb.add("\t Out Segment Rate", pMessage.getConnection().getXtaSegments().getCurrValue());
		tsb.add("\t Out Update Rate", pMessage.getConnection().getXtaUpdates().getCurrValue());
		tsb.add("\t Total Updates Sent", pMessage.getConnection().getXtaTotalUpdates());
		tsb.add("", "");
		tsb.add("\t In Byte Rate", pMessage.getConnection().getRcvBytes().getCurrValue());
		tsb.add("\t In Segment Rate", pMessage.getConnection().getRcvSegments().getCurrValue());
		tsb.add("\t In Update Rate", pMessage.getConnection().getRcvUpdates().getCurrValue());
		tsb.add("\t Total Updates Received", pMessage.getConnection().getRcvTotalUpdates());
		tsb.add("", "");
		tsb.add("\t Delivery Update Queue Size", pMessage.getConnection().getDeliverUpdateQueue().getSize());
		tsb.add("", "");

		tsb.add("\t Delivery Update Queue Size", pMessage.getConnection().getDeliverUpdateQueue().getSize());
		tsb.add("\t Delivery Update Queue Peak Size", pMessage.getConnection().getDeliverUpdateQueue().getSize());
		tsb.add("\t Delivery Update Queue Peak Time", pMessage.getConnection().getDeliverUpdateQueue().getPeakSize());

		tsb.add("\t Number Of Connected Publishers", pMessage.getConnection().getPublishers());
		tsb.add("\t Number Of Connected Subscribers", pMessage.getConnection().getSubscribers());
		tsb.add("\t Retransmission requests in", pMessage.getConnection().getInRetransmissions());
		tsb.add("\t Retransmission requests out", pMessage.getConnection().getInRetransmissions());

		tsb.add("Receiver Statistics", "");
		formatDataRateItem(tsb, pMessage.getConnection().getRcvSegments(),    "\tReceive segments (1 sek)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getRcvSegments1min(),"\tReceive segments (1 min)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getRcvSegments5min(),"\tReceive segments (5 min)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getRcvBytes(),       "\tReceive bytes       (1 sek)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getRcvBytes1min(),   "\tReceive bytes       (1 min)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getRcvBytes5min(),	  "\tReceive bytes       (5 min)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getRcvUpdates(),     "\tReceive updates   (1 sek)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getRcvUpdates1min(), "\tReceive updates   (1 min)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getRcvUpdates5min(), "\tReceive updates   (5 min)  ");
		tsb.add("Sender Statistics", "");
		formatDataRateItem(tsb, pMessage.getConnection().getXtaSegments(),	  "\tSender segments (1 sek)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getXtaSegments1min(),"\tSender segments (1 min)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getXtaSegments5min(),"\tSender segments (5 min)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getXtaBytes(),		  "\tSender bytes       (1 sek)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getXtaBytes1min(),   "\tSender bytes       (1 min)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getXtaBytes5min(),   "\tSender bytes       (5 min)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getXtaUpdates(),     "\tSender updates   (1 sek)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getXtaUpdates1min(), "\tSender updates   (1 min)  ");
		formatDataRateItem(tsb, pMessage.getConnection().getXtaUpdates5min(), "\tSender updates   (5 min)  ");
		return tsb;
	}

	TextAreaStringBuilder formatRetransmisson(
			DistExploreRetransmissonsRsp pMessage) {
		TextAreaStringBuilder tsb = new TextAreaStringBuilder(1024);
		tsb.add("\n\n\n");
		tsb.add("\t Retransmission Requests [in]", pMessage.getTotalInRqst());
		tsb.add("\t Retransmission Requests [out]", pMessage.getTotalOutRqst());
		tsb.add("\t Retransmission Requests [seen]", pMessage
				.getTotalSeenRqst());
		tsb.add("[Inbound]", "");
		for (int i = 0; i < pMessage.getInHosts().length; i++) {
			tsb.add("\t" + pMessage.getInHosts()[i], " ");
		}
		tsb.add("[Outbound]", "");
		for (int i = 0; i < pMessage.getOutHosts().length; i++) {
			tsb.add("\t" + pMessage.getOutHosts()[i], " ");
		}
		return tsb;
	}

	TextAreaStringBuilder formatSubscriptions(
			DistExploreSubscriptionsRsp pMessage) {
		TextAreaStringBuilder tsb = new TextAreaStringBuilder(1024);
		tsb.add("\n\n\n");
		tsb.add("Active subscriptions", " ");
		if (pMessage.getSubscriptions() == null) {
			tsb.add("\tNo active subscription!", " ");
		} else {
			for (int i = 0; i < pMessage.getSubscriptions().length; i++) {
				tsb.add("\t" + pMessage.getSubscriptions()[i], " ");
			}
		}
		return tsb;
	}

	public void distributorUpdate(String pSubjectName, byte[] pData,
			Object pCallbackParameter, int pQueueLength) {
		DistNetMsg tNetMsg = new DistNetMsg();
		MessageBinDecoder tDecoder = new MessageBinDecoder(pData);
		tNetMsg.decode(tDecoder);

		System.out.println("[ Console Update ] Message: "
				+ tNetMsg.getMessage().getWrappedMessageNameSimpleFormat()
				+ " Response Id: " + tNetMsg.getRequestId() + " Curr Id:  "
				+ mCurrentRequestId);

		MessageInterface tMessage = tNetMsg.getMessage().getWrappedMessage();
		if (tMessage == null) {
			return;
		}

		if (tNetMsg.getRequestId() == mCurrentRequestId) {

			if (tMessage instanceof DistExploreDomainRsp) {
				SwingUtilities.invokeLater(new UpdateDistributorTree(this,
						(DistExploreDomainRsp) tMessage));
			}

			else if (tMessage instanceof DistExploreDistributorRsp) {
				SwingUtilities
						.invokeLater(new UpdateTextAreaTask(
								getJTextAreaScrollPane(),
								getJLeftTextArea(),
								getJRightTextArea(),
								formatDistributor((DistExploreDistributorRsp) tMessage)));
			}

			else if (tMessage instanceof DistExploreConnectionRsp) {
				SwingUtilities.invokeLater(new UpdateTextAreaTask(
						getJTextAreaScrollPane(), getJLeftTextArea(),
						getJRightTextArea(),
						formatConnection((DistExploreConnectionRsp) tMessage)));
			} else if (tMessage instanceof DistExploreRetransmissonsRsp) {
				SwingUtilities
						.invokeLater(new UpdateTextAreaTask(
								getJTextAreaScrollPane(),
								getJLeftTextArea(),
								getJRightTextArea(),
								formatRetransmisson((DistExploreRetransmissonsRsp) tMessage)));
			} else if (tMessage instanceof DistExploreSubscriptionsRsp) {
				SwingUtilities
						.invokeLater(new UpdateTextAreaTask(
								getJTextAreaScrollPane(),
								getJLeftTextArea(),
								getJRightTextArea(),
								formatSubscriptions((DistExploreSubscriptionsRsp) tMessage)));
			}
		}
	}

	public void distributorEventCallback(DistributorEvent pDistributorEvent) {
		if (pDistributorEvent instanceof DistributorErrorEvent) {
			new ErrorMessage(mFameInstance, "Connection error", pDistributorEvent.toString() );
		}
	}

	class UpdateDistributorTree implements Runnable {
		DistributorConsole mConsole;
		DistExploreDomainRsp mDistExploreDomainRsp;

		UpdateDistributorTree(DistributorConsole pConcolse,
				DistExploreDomainRsp pDistExploreDomainRsp) {
			mConsole = pConcolse;
			mDistExploreDomainRsp = pDistExploreDomainRsp;
		}

		public void run() {
			mConsole.mTreeRoot.addDistributor(new DistributorTreeNode(mDistExploreDomainRsp));
			
			JTree tTree = mConsole.getJDistributorTree();
			tTree.invalidate();
			

			//System.out.println("-----------------------------------------");
			//int i = 0;
		    DefaultMutableTreeNode currentNode = mConsole.mTreeRoot.getNextNode();
		    do {
		       //System.out.println("i: " + i + " level: " + currentNode.getLevel() + " node: " + currentNode.toString());
		       if (currentNode.getLevel() == 1) {
		    	   tTree.expandPath(new TreePath(currentNode.getPath()));
		       }
		       currentNode = currentNode.getNextNode();
		    } while (currentNode != null);
//
//	
//
//			
//			
//			
//			
//			tTree.expandPath(tTree.getPathForRow(0));
//			 for( int i = 0; i < tTree.getRowCount(); i++) {
//			 tTree.expandRow(i);
//			 }
//
////			tTree.expandPath(tTree.getPathForRow(0));
////			tTree.makeVisible(mConsole.getJDistributorTree().getClosestPathForLocation(1, 1));
////			tTree.invalidate();
////			tTree.makeVisible(mConsole.getJDistributorTree().getClosestPathForLocation(1, 1));
		    tTree.revalidate();
			tTree.repaint();
			tTree.updateUI();
		}
	}

	class UpdateTextAreaTask implements Runnable {
		JScrollPane mTextScrollPane;
		JTextArea mTextAreaLeft;
		JTextArea mTextAreaRight;
		TextAreaStringBuilder mTAB;

		UpdateTextAreaTask(JScrollPane pTextScrollPane,
				JTextArea pTextAreaLeft, JTextArea pTextAreaRight,
				TextAreaStringBuilder pTAB) {
			mTextScrollPane = pTextScrollPane;
			mTextAreaLeft = pTextAreaLeft;
			mTextAreaRight = pTextAreaRight;
			mTAB = pTAB;
		}

		public void run() {
			mTextAreaLeft.setText(mTAB.getLeftStringArea());
			mTextAreaLeft.repaint();
			mTextAreaRight.setText(mTAB.getRightStringArea());
			mTextAreaRight.repaint();
			mTextAreaRight.setCaretPosition(0);
			mTextAreaLeft.setCaretPosition(0);

			mTextScrollPane.repaint(200);
		}

	}

	class TextAreaStringBuilder {
		StringBuilder mSBR, mSBL;

		TextAreaStringBuilder(int pSize) {
			mSBR = new StringBuilder(pSize);
			mSBL = new StringBuilder(pSize);
		}

		void add(String pString) {
			mSBL.append(pString + "\n");
			mSBR.append(pString + "\n");
		}

		void add(String pLeftString, long pValue) {
			mSBL.append(pLeftString + "\n");
			mSBR.append(String.valueOf(pValue) + "\n");
		}

		void add(String pLeftString, int pValue) {
			mSBL.append(pLeftString + "\n");
			mSBR.append(String.valueOf(pValue) + "\n");
		}

		void add(String pLeftString, String pRightString) {
			mSBL.append(pLeftString + "\n");
			mSBR.append(pRightString + "\n");
		}

		String getRightStringArea() {
			return mSBR.toString();
		}

		String getLeftStringArea() {
			return mSBL.toString();
		}

	}

	public class TreeMouseEvents extends MouseAdapter {
		DistributorConsole mConsole;

		TreeMouseEvents(DistributorConsole pConsole) {
			mConsole = pConsole;
		}

		public void mousePressed(MouseEvent e) {
			int selRow = mConsole.getJDistributorTree().getRowForLocation(
					e.getX(), e.getY());
			TreePath selPath = mConsole.getJDistributorTree()
					.getPathForLocation(e.getX(), e.getY());
			if (selRow != -1) {
				System.out.println("MOUSE-EVENT Row: " + selRow + " Object: "
						+ selPath.getPath()[selPath.getPath().length - 1]);
				DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) selPath
						.getLastPathComponent(); // jDistributorTree.getLastSelectedPathComponent();
				DistNetMsg tRequestMessage = ((DistributorTreeNodeIf) tNode)
						.getRequestMessage();
				if (tRequestMessage != null) {
					sendRequest(tRequestMessage);
				}
			}
		}
	}

	public class ErrorMessage {

		public ErrorMessage(Component parent, String message, String reason) {
			if (reason == null) {
				JOptionPane.showMessageDialog(parent, message, "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(parent, message + "\n"
						+ "reason: " + reason, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
} // @jve:decl-index=0:visual-constraint="12,-9"

