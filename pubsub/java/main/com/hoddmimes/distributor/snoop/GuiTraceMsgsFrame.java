package com.hoddmimes.distributor.snoop;

import com.hoddmimes.distributor.DistributorUpdateCallbackIf;
import com.hoddmimes.distributor.samples.table.Table;
import com.hoddmimes.distributor.samples.table.TableAttribute;
import com.hoddmimes.distributor.samples.table.TableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class GuiTraceMsgsFrame extends JFrame implements DistributorUpdateCallbackIf, Runnable
{
    private static SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss.SSS");

    private SnoopUpdateMsgListner mRegisterUpdateListenerIf;
    private Publisher mPublisher;

    private TableModel<MessageEntry> mTableModelMsgs;
    private Table mTableMsgs;


    private TableModel<SubjectEntry> mTableModelSubjects;
    private Table mTableSubjects;

    private JTextField jStatSubjects = new JTextField();
    private JTextField jStatUpdatesSec = new JTextField();
    private JTextField jStatKBytesSec = new JTextField();
    private JTextField jStatUpdatesMin = new JTextField();
    private JTextField jStatKBytesMin = new JTextField();

    private JCheckBox jTraceMsgsChkBox = new JCheckBox("Trace Messages");
    private JButton jCancelButton = new JButton("Cancel");
    RepaintThread mPaintThread;

    public GuiTraceMsgsFrame( Publisher pPublisher, SnoopUpdateMsgListner pRegisterUpdateListnerIf ) {
        super();
        mPublisher = pPublisher;
        mRegisterUpdateListenerIf = pRegisterUpdateListnerIf;
        String tAppName = (pPublisher == null) ? "Test" : pPublisher.mAppName;
        String tIpAddrStr = (pPublisher == null) ? "192.182.42.100" : pPublisher.mRemoteIpAddress;
        this.setTitle( tAppName+ "   " + tIpAddrStr);

        mTableModelMsgs = new TableModel<>(GuiTraceMsgsFrame.MessageEntry.class);
        mTableModelMsgs.setMaxItems( 100 );
        mTableMsgs = new Table(mTableModelMsgs, new Dimension(400, 140), null);


        mTableModelSubjects = new TableModel<>(GuiTraceMsgsFrame.SubjectEntry.class);
        mTableSubjects = new Table(mTableModelSubjects, new Dimension(250, 140), null);

        JPanel tRootPanel = new JPanel( new BorderLayout());
        tRootPanel.add( createStatPanel(tAppName, tIpAddrStr), BorderLayout.NORTH);
        tRootPanel.add( createSubjectPanel(), BorderLayout.CENTER);
        tRootPanel.add( createMessagePanel(), BorderLayout.SOUTH);

        mPaintThread = new RepaintThread(1000, this );
        if (pPublisher != null) {
            mPaintThread.start();
        }

        jTraceMsgsChkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mRegisterUpdateListenerIf != null) {
                  if (jTraceMsgsChkBox.isSelected()) {
                     mRegisterUpdateListenerIf.addUpdateListener( GuiTraceMsgsFrame.this );
                  } else {
                      mRegisterUpdateListenerIf.removeUpdateListener( GuiTraceMsgsFrame.this );
                  }
                }
            }
        });

        jCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mPaintThread.setTimeToStop();
                GuiTraceMsgsFrame.super.dispose();
            }
        });
        this.setContentPane( tRootPanel );
    }

    public void addMessage( String pSubject, int pSize ) {
        mTableModelMsgs.addFirst( new MessageEntry( System.currentTimeMillis(), pSubject, pSize ));
        mTableModelMsgs.fireTableDataChanged();
    }

    private JPanel createSubjectPanel() {
        JPanel tHeadPanel = new JPanel( new BorderLayout());

        JPanel tTablePanel = new JPanel( new BorderLayout());
        JLabel tTitleLabel = new JLabel("Subject Statistics");
        tTitleLabel.setFont( new Font( Font.SANS_SERIF, Font.ITALIC + Font.BOLD, 14));
        tTablePanel.add(tTitleLabel, BorderLayout.NORTH);

        tTablePanel.setBorder(new EmptyBorder( 20,10,15,10 ));
        tTablePanel.add( mTableSubjects, BorderLayout.CENTER );
        tHeadPanel.add( tTablePanel, BorderLayout.CENTER );




        return tHeadPanel;


    }
    private JPanel createMessagePanel() {
        JPanel tHeadPanel = new JPanel( new BorderLayout());

        JPanel tTablePanel = new JPanel( new BorderLayout());

        JPanel tTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel tTitleLabel = new JLabel("Message Trace");
        tTitleLabel.setFont( new Font( Font.SANS_SERIF, Font.ITALIC + Font.BOLD, 14));
        tTopPanel.add(tTitleLabel);
        tTopPanel.add( jTraceMsgsChkBox );

        tTablePanel.add( tTopPanel, BorderLayout.NORTH);

        tTablePanel.setBorder(new EmptyBorder( 20,10,15,10 ));
        tTablePanel.add( mTableMsgs, BorderLayout.CENTER );

        JPanel tOptionPanel = new JPanel();
        tOptionPanel.add( jCancelButton );
        tHeadPanel.add( tOptionPanel, BorderLayout.SOUTH );

        tHeadPanel.add( tTablePanel, BorderLayout.CENTER );
        return tHeadPanel;
    }


    private JPanel createStatPanel(String pAppName, String pIpAddr ) {
        JPanel tHeadPanel = new JPanel( new BorderLayout());
        JPanel tTitlePanel = new JPanel( new FlowLayout());

        // Add header text
        JLabel tAppLabel = new JLabel("Publisher");
        tAppLabel.setFont( new Font( Font.SANS_SERIF, Font.ITALIC, 14));
        tTitlePanel.add(tAppLabel);

        JLabel tAppNameLabel = new JLabel(pAppName);
        tAppNameLabel.setFont( new Font( Font.SANS_SERIF, Font.ITALIC + Font.BOLD, 14));
        tTitlePanel.add(tAppNameLabel);

        JLabel tAddrLabel = new JLabel("Ip Address");
        tAddrLabel.setFont( new Font( Font.SANS_SERIF, Font.ITALIC, 14));
        tTitlePanel.add(tAddrLabel);

        JLabel tIpAddrLabel = new JLabel(pIpAddr);
        tIpAddrLabel.setFont( new Font( Font.SANS_SERIF, Font.ITALIC + Font.BOLD, 14));
        tTitlePanel.add(tIpAddrLabel);
        tHeadPanel.add( tTitlePanel, BorderLayout.NORTH);

        // Add Stat Table
        JPanel tStatPanel = new JPanel(new BorderLayout());
        tStatPanel.setBorder(new EmptyBorder( 20,10,15,10 ));
        tHeadPanel.add( tStatPanel, BorderLayout.CENTER );

        JLabel tTitleLabel = new JLabel("Publisher Statistics");
        tTitleLabel.setFont( new Font( Font.SANS_SERIF, Font.ITALIC + Font.BOLD, 14));
        tStatPanel.add(tTitleLabel, BorderLayout.NORTH);

        Font tTxtFieldFont = new Font( Font.SANS_SERIF, Font.PLAIN, 12);
        JPanel tCounterPanel = new JPanel(new GridLayout( 5, 1,0,0));
        tCounterPanel.setBorder(new EmptyBorder( 20,30,0, 0 ));
        tStatPanel.add( tCounterPanel, BorderLayout.WEST);

        tCounterPanel.add( createCounterElement("Subjects", jStatSubjects));
        tCounterPanel.add( createCounterElement("Update/sec", jStatUpdatesSec));
        tCounterPanel.add( createCounterElement("KBytes/sec", jStatKBytesSec));

        tCounterPanel.add( createCounterElement("Update/min (avg)", jStatUpdatesMin));
        tCounterPanel.add( createCounterElement("KBytes/min (avg)", jStatKBytesMin));

        return tHeadPanel;
    }

    private JPanel createCounterElement( String pLabelText, JTextField pTxtField ) {
        JPanel tContent = new JPanel();
        JLabel tLabel = new JLabel( pLabelText);
        tLabel.setFont( new Font( "Calabri", Font.BOLD, 12));
        tLabel.setPreferredSize( new Dimension( 100, 18));
        tContent.add( tLabel );
        pTxtField.setText("0");
        pTxtField.setFont(new Font( "Calabri", Font.PLAIN, 10));
        pTxtField.setPreferredSize( new Dimension(50,18));
        pTxtField.setHorizontalAlignment( JTextField.RIGHT);
        tContent.add( pTxtField );
        return  tContent;
    }

    @Override
    public void distributorUpdate(String pSubjectName, byte[] pPayload, Object pCallbackParameter, int pRemoteAppId, int pDeliveryQueueLength) {
        if (mPublisher.mAppId == pRemoteAppId) {
            MessageEntry tMsg = new MessageEntry( System.currentTimeMillis(), pSubjectName, pPayload.length );
            mTableModelMsgs.addFirst( tMsg );
        }
    }

    @Override
    public void run() {
        jStatKBytesSec.setText(String.valueOf(mPublisher.getKBytesSec()));
        jStatKBytesMin.setText(String.valueOf(mPublisher.getKBytesMin()));
        jStatUpdatesSec.setText(String.valueOf(mPublisher.getUpdatesSec()));
        jStatUpdatesMin.setText(String.valueOf(mPublisher.getUpdatesMin()));
        jStatSubjects.setText(String.valueOf(mPublisher.mSubjects.size()));

        // Update Subject Statistics
        updateSubjectStatistics();
    }

    private SubjectEntry findSubjectTabObj(String pSubject, List<SubjectEntry> pSubjectTabObjects) {
        for (SubjectEntry se : pSubjectTabObjects) {
            if (se.mSubject.equals(pSubject)) {
                return se;
            }
        }
        return null;
    }

    private void updateSubjectStatistics() {
        List<Publisher.Statistic> tSubjectStatisticEntries = new ArrayList<>(mPublisher.mSubjects.values());
        List<SubjectEntry> tSubjectTabObjects = new ArrayList(mTableModelSubjects.getObjects());
        for (Publisher.Statistic tSubjStat : tSubjectStatisticEntries) {
            SubjectEntry tSubjTabObj = findSubjectTabObj(tSubjStat.mSubject, tSubjectTabObjects);
            if (tSubjTabObj == null) {
                tSubjTabObj = new SubjectEntry(tSubjStat.mSubject);
                mTableModelSubjects.addFirst(tSubjTabObj);
            }

            tSubjTabObj.mBytes = tSubjStat.mBytes.get();
            tSubjTabObj.mKBytes = tSubjStat.mBytes.get() / 1000L;
            tSubjTabObj.mUpdates = tSubjStat.mUpdates.get();
        }
        mTableModelSubjects.fireTableDataChanged();
    }


    class StatEntry {
        @TableAttribute(header = "Subjects", column = 0)
        public int mSubjects;
        @TableAttribute(header = "Updates / sec", column = 1)
        public String mUpdSec;
        @TableAttribute(header = "Bytes (kb) / sec", column = 2)
        public String mKBytes;

        long   mLastCalcTime;
        double mUpdates;
        double mBytes;

        public StatEntry(String pSubject ) {
            reset();
        }

        private void reset() {
            mLastCalcTime = System.currentTimeMillis();
            mUpdates = 0;
            mBytes = 0;
        }

        void calculate(int pSubjects) {
            NumberFormat nbf = NumberFormat.getNumberInstance();
            nbf.setMaximumFractionDigits(2);
            nbf.setMinimumIntegerDigits(2);

            long tDeltaTime = System.currentTimeMillis() - mLastCalcTime;
            mSubjects = pSubjects;
            mUpdSec = nbf.format( (mUpdates * 1000.0d) / (double) tDeltaTime);
            mKBytes = nbf.format( (mBytes / (double) tDeltaTime));
            reset();
        }

        void update( int pPayloadSize ) {
            mUpdates++;
            mBytes += pPayloadSize;
        }
    }

    public class SubjectEntry {
        @TableAttribute(header = "Subject", column = 0)
        public String mSubject;
        @TableAttribute(header = "Updates", column = 1)
        public long mUpdates;
        @TableAttribute(header = "Bytes (kb)", column = 2)
        public long mKBytes;

        long        mBytes;

        public SubjectEntry(String pSubject ) {
            mBytes = 0L;
            mSubject = pSubject;
            mUpdates = 0L;
        }

        public void update( int pPayloadSize ) {
            mUpdates++;
            mBytes += pPayloadSize;
            mKBytes = (mBytes / 1000L);
        }
    }


    public class MessageEntry {
        @TableAttribute(header = "Time", column = 0)
        public String mTime;
        @TableAttribute(header = "Subject", column = 1)
        public String mSubject;
        @TableAttribute(header = "Size", column = 2)
        public long mSize;

        public MessageEntry(long pTime, String pSubject, int pSize ) {
            mTime = SDF.format( pTime );
            mSubject = pSubject;
            mSize = pSize;
        }
    }


    public static void main( String args[] ) {
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            System.out.println( ex.getMessage());
        }

        GuiTraceMsgsFrame tFrame = new GuiTraceMsgsFrame(null, null );
        tFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tFrame.pack();
        tFrame.setVisible(true);
        while( true ) {
            try {Thread.currentThread().sleep( 10000 ); }
            catch( InterruptedException e ) {}
        }
    }
}
