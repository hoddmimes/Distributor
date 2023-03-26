package com.hoddmimes.distributor.console;

import com.hoddmimes.distributor.*;
import com.hoddmimes.distributor.generated.messages.DistDomainConnectionEntry;
import com.hoddmimes.distributor.generated.messages.DistDomainDistributorEntry;
import com.hoddmimes.distributor.samples.table.Table;
import com.hoddmimes.distributor.samples.table.TableAttribute;
import com.hoddmimes.distributor.samples.table.TableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SnoopFrame extends JFrame implements Runnable, DistributorUpdateCallbackIf, DistributorEventCallbackIf
{
    private static SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss.SSS");


    private DistDomainDistributorEntry mDistEntry;
    private DistributorApplicationConfiguration mDistConfig;
    private Distributor mDistributor;
    private List<DistributorSubscriberIf> mSubscribers;
    private SchedulerThread mSchedulareThread;

    private Map<String, SubjectEntry> mSubjects = new HashMap<>();
    private StatTotal mTotalStatCounters = new StatTotal();

    private JCheckBox jTraceMsgsChkBox = new JCheckBox("Trace Messages");
    private JButton jCancelButton = new JButton("Cancel");


    private TableModel<StatTotal> mTableModelTotal;
    private Table mTableTotal;

    private TableModel<SubjectEntry> mTableModelSubjects;
    private Table mTableSubjects;

    private TableModel<MessageEntry> mTableModelMsgs;
    private Table mTableMsgs;


    public SnoopFrame(Distributor pDistributor, DistributorApplicationConfiguration pDistConfig, DistDomainDistributorEntry pDistEntry ) {
        super();
        mDistConfig = pDistConfig;
        mDistributor = pDistributor;
        mDistEntry = pDistEntry;
        setup();
        connectToDistributor();
        mSchedulareThread = new SchedulerThread( 1000L, this);
        mSchedulareThread.start();
    }

    private JPanel createTotalStatististicPanel() {
        JPanel tRootPanel = new JPanel(new BorderLayout());
        JPanel tTablePanel = new JPanel(new BorderLayout());
        JPanel tTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel tTitleLabel = new JLabel("Overall Statistics [ " + mDistEntry.getApplicationName() + " -- " + mDistEntry.getHostaddress() + " ]");
        tTitleLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC + Font.BOLD, 14));
        tTopPanel.add(tTitleLabel);

        mTableModelTotal = new TableModel<>(StatTotal.class);
        mTableTotal = new Table(mTableModelTotal, new Dimension(600, 50), null);
        mTableModelTotal.add( mTotalStatCounters );

        tTablePanel.add(mTableTotal, BorderLayout.CENTER);
        tTablePanel.setBorder(new EmptyBorder(20, 10, 15, 10));

        tRootPanel.add(tTopPanel, BorderLayout.NORTH);
        tRootPanel.add(tTablePanel, BorderLayout.CENTER);

        return tRootPanel;
    }

    private JPanel createSubjectsPanel() {
        JPanel tHeadPanel = new JPanel( new BorderLayout());

        JPanel tTablePanel = new JPanel( new BorderLayout());
        JLabel tTitleLabel = new JLabel("Subject Statistics");
        tTitleLabel.setFont( new Font( Font.SANS_SERIF, Font.ITALIC + Font.BOLD, 14));
        tTablePanel.add(tTitleLabel, BorderLayout.NORTH);

        mTableModelSubjects = new TableModel<>( SubjectEntry.class );
        mTableSubjects = new Table( mTableModelSubjects, new Dimension( 600, 400), null);

        tTablePanel.setBorder(new EmptyBorder( 20,10,15,10 ));
        tTablePanel.add( mTableSubjects, BorderLayout.CENTER );
        tHeadPanel.add( tTablePanel, BorderLayout.CENTER );
        return tHeadPanel;
    }

    private JPanel createMessagePanel() {
        JPanel tHeadPanel = new JPanel( new BorderLayout());
        JPanel tTablePanel = new JPanel( new BorderLayout());


        mTableModelMsgs = new TableModel<>( MessageEntry.class );
        mTableModelMsgs.setMaxItems( 150 );
        mTableMsgs = new Table( mTableModelMsgs, new Dimension( 600, 400), null);

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



    private void setup() {
        this.setTitle("Distributor Snoop [ " + mDistEntry.getApplicationName() + " -- " + mDistEntry.getHostaddress());
        JPanel tRootPanel = new JPanel( new BorderLayout());
        this.setContentPane( tRootPanel );
        tRootPanel.add( createTotalStatististicPanel(), BorderLayout.NORTH);
        tRootPanel.add( createSubjectsPanel(), BorderLayout.CENTER);
        tRootPanel.add( createMessagePanel(), BorderLayout.SOUTH);

        jCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mSchedulareThread.setTimeToStop();
                for(DistributorSubscriberIf subscr : mSubscribers) {
                    subscr.close();
                }
                //mTableModelMsgs.clear();
            }
        });
    }

    private void connectToDistributor() {
        mSubscribers = new ArrayList<>();
       for(DistDomainConnectionEntry tConnEntry  : mDistEntry.getConnections()) {
           if ((!mDistConfig.getCMA().equals(tConnEntry.getMcaAddress())) || (mDistConfig.getCMAPort() != tConnEntry.getMcaPort())) {
               DistributorConnectionConfiguration tConnConfig = new DistributorConnectionConfiguration( tConnEntry.getMcaAddress(), tConnEntry.getMcaPort());
               try {
                   DistributorConnectionIf tConnection = mDistributor.createConnection( tConnConfig );
                   DistributorSubscriberIf tSubscriber = mDistributor.createSubscriber( tConnection, this, this );
                   tSubscriber.addSubscription("/...", mDistEntry.getApplicationId());
                   mSubscribers.add( tSubscriber );
               }
               catch( Exception e ) {
                   e.printStackTrace();
               }
           }
       }
    }

    @Override
    public void run() {
        List<SubjectEntry> tSubjects = new ArrayList<>();

        tSubjects.addAll( mSubjects.values());
        tSubjects.stream().forEach( se -> se.calculate());
        mTotalStatCounters.calculate( tSubjects.size());
        mTableModelSubjects.fireTableDataChanged();
        mTableModelTotal.fireTableDataChanged();
        if (jTraceMsgsChkBox.isSelected()) {
            mTableModelMsgs.fireTableDataChanged();
        }
    }

    @Override
    public void distributorEventCallback(DistributorEvent pDistributorEvent) {

    }

    @Override
    public void distributorUpdate(String pSubjectName, byte[] pPayload, Object pCallbackParameter, int pRemoteAppId, int pDeliveryQueueLength) {
        if (pRemoteAppId == mDistEntry.getApplicationId()) {
           mTotalStatCounters.update( pPayload.length );
           SubjectEntry se = mSubjects.get( pSubjectName );
           if (se == null) {
               se = new SubjectEntry( pSubjectName );
               mTableModelSubjects.addFirst( se );
               mSubjects.put( pSubjectName, se);
           }
           se.update( pPayload.length );

           if (jTraceMsgsChkBox.isSelected()) {
               mTableModelMsgs.addFirst( new MessageEntry( System.currentTimeMillis(), pSubjectName, pPayload.length ));
           }
        }
    }

    public class StatTotal
    {
       @TableAttribute(header = "Subjects", column = 0)
       public long mTotSubjects;

       @TableAttribute(header = "Updates", column = 1)
       public long mTotUpdates;

       @TableAttribute(header = "KBytes", column = 2)
       public long mTotKBytes;
       long mTotBytes;

       @TableAttribute(header = "KBytes/sec", column = 3)
       public double mKBytesSec;

       @TableAttribute(header = "Avg KBytes (min)", column = 4)
       public double mKBytesMin;

       @TableAttribute(header = "Update/sec", column = 5)
       public double mUpdateSec;

       @TableAttribute(header = "Avg Upds (min)", column = 6)
       public double mUpdateMin;

       StatCounter mSec;
       StatCounter mMin;

       StatTotal()
       {
            mSec = new StatCounter( 1000L );
            mMin = new StatCounter( 60000L );
       }

       void update( int pPayloadSize ) {
           mSec.update( pPayloadSize );
           mMin.update( pPayloadSize );
           mTotBytes += pPayloadSize;
           mTotUpdates++;
       }

       void calculate( int pActiveSubjects) {
           mSec.calculate();
           mMin.calculate();

           mTotSubjects = pActiveSubjects;
           mTotKBytes = mTotBytes / 1000L;
           mKBytesMin = mMin.getKBytesRate();
           mKBytesSec = mSec.getKBytesRate();
           mUpdateMin = mMin.getUpdateRate();
           mUpdateSec = mSec.getUpdateRate();
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



    public class SubjectEntry
    {
        @TableAttribute(header = "Subjects", column = 0)
        public String   mSubject;
        StatCounter     mSec;
        StatCounter     mMin;

        @TableAttribute(header = "Updates", column = 1)
        public long mTotUpdates;

         @TableAttribute(header = "KBytes", column = 2)
         public long mTotKbytes;
         private long mTotBytes;

         @TableAttribute(header = "Upds/sec", column = 3)
         public double mUpdsSec;

         @TableAttribute(header = "Upds (min)", column = 4)
         public double mUpdsMin;

         @TableAttribute(header = "KBytes/sec", column = 5)
         public double mKbytesSec;

         @TableAttribute(header = "KBytes (min)", column = 6)
         public double mKbytesMin;

         public SubjectEntry( String pSubject ) {
            mSubject = pSubject;
            mSec = new StatCounter( 1000L );
            mMin = new StatCounter( 60000L );
        }

        synchronized public void calculate()  {
            mSec.calculate();
            mMin.calculate();

            mTotKbytes  = mTotBytes / 1000L;
            mKbytesMin = mMin.getKBytesRate();
            mKbytesSec = mSec.getKBytesRate();
            mUpdsMin = mMin.getUpdateRate();
            mUpdsSec = mSec.getUpdateRate();

        }

        synchronized void update( int pPayloadSize ) {
            mSec.update( pPayloadSize );
            mMin.update( pPayloadSize );
            mTotUpdates++;
            mTotBytes += pPayloadSize;
        }
    }
    class StatCounter
    {
        private AtomicLong mTimestamp;
        private AtomicLong mInterval;
        private AtomicLong mUpdates;
        private AtomicLong mBytes;

        private double mUpdateRate;
        private double mKBytesRate;

        private

        StatCounter( long pInterval ) {
            mInterval = new AtomicLong( pInterval );
            mTimestamp = new AtomicLong( System.currentTimeMillis());
            mUpdates = new AtomicLong(0);
            mBytes = new AtomicLong(0);
            mKBytesRate = mUpdateRate = 0.0d;
        }
        void calculate() {
            long tDeltaTime = System.currentTimeMillis() - mTimestamp.get();
            if (tDeltaTime >= mInterval.get()) {

               synchronized (this) {
                   long x = (mUpdates.get() * 1000L * 100L) / tDeltaTime;
                   mUpdateRate = (double) x / 100.0d;

                   x  = (mBytes.get() * 1000L * 100L) / (tDeltaTime * 1000L);
                   mKBytesRate = (double) x / 100.0d;
               }
               mUpdates.set(0);
               mBytes.set(0);
               mTimestamp.set( System.currentTimeMillis());
            }
        }

        void update( long pPayloadSize ) {
            mBytes.addAndGet( pPayloadSize );
            mUpdates.incrementAndGet();
        }

        double getUpdateRate() {
            synchronized( this ) {
                return mUpdateRate;
            }
        }

        double getKBytesRate() {
            synchronized( this ) {
                return mKBytesRate;
            }
        }
    }
}
