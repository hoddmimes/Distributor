package com.hoddmimes.distributor.samples;

import com.hoddmimes.distributor.samples.table.Table;
import com.hoddmimes.distributor.samples.table.TableAttribute;
import com.hoddmimes.distributor.samples.table.TableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.hoddmimes.distributor.DistributorApplicationConfiguration.*;

public class SetLogLevelDialog extends JDialog {
    int mSelectedLogLevel = 0;
    TableModel<LogLevel> mTableModel;


    public int getSelectedLogLevel() {
        return mSelectedLogLevel;
    }

    public SetLogLevelDialog(Frame pComponent, String pTitle, int pLogLevel, boolean pModale ) {
        super( pComponent, pTitle, pModale );
        mSelectedLogLevel = pLogLevel;
        initial();
    }

    private void initial() {
        JPanel tContentsPanel = new JPanel();
        mTableModel = new TableModel( LogLevel.class );
        JPanel tButtonPanel = new JPanel();

        /*
        public static int LOG_ERROR_EVENTS = 1;
        public static int LOG_CONNECTION_EVENTS = 2;
        public static int LOG_RMTDB_EVENTS = 4;
        public static int LOG_RETRANSMISSION_EVENTS = 8;
        public static int LOG_SUBSCRIPTION_EVENTS = 16;
        public static int LOG_STATISTIC_EVENTS = 32;
        public static int LOG_SEGMENTS_EVENTS = 64;
        public static int LOG_DATA_PROTOCOL_RCV = 128;
        public static int LOG_DATA_PROTOCOL_XTA = 256;
        public static int LOG_RETRANSMISSION_CACHE = 512;
        */

        // Init data model
        mTableModel.add( new LogLevel(  ((LOG_ERROR_EVENTS & mSelectedLogLevel) != 0),  LOG_ERROR_EVENTS, "Error Events" ));
        mTableModel.add( new LogLevel(  (((LOG_CONNECTION_EVENTS&mSelectedLogLevel) != 0) ? true : false),  LOG_CONNECTION_EVENTS, "Subscriber / Publisher connect/disconnect events" ));
        mTableModel.add( new LogLevel(  (((LOG_RMTDB_EVENTS&mSelectedLogLevel) != 0) ? true : false),  LOG_RMTDB_EVENTS, "Remote Distributor host connect / disconnect" ));
        mTableModel.add( new LogLevel(  (((LOG_RETRANSMISSION_EVENTS&mSelectedLogLevel) != 0) ? true : false),  LOG_RETRANSMISSION_EVENTS, "Retransmission events" ));
        mTableModel.add( new LogLevel(  (((LOG_SUBSCRIPTION_EVENTS&mSelectedLogLevel) != 0) ? true : false),  LOG_SUBSCRIPTION_EVENTS, "Add / Remove subscription events" ));
        mTableModel.add( new LogLevel(  (((LOG_STATISTIC_EVENTS&mSelectedLogLevel) != 0) ? true : false),  LOG_STATISTIC_EVENTS, "Logging of periodic distributor statistic" ));
        mTableModel.add( new LogLevel(  (((LOG_DATA_PROTOCOL_RCV&mSelectedLogLevel) != 0) ? true : false),  LOG_SEGMENTS_EVENTS, "Logging of segment xta / rcv event" ));
        mTableModel.add( new LogLevel(  (((LOG_SEGMENTS_EVENTS&mSelectedLogLevel) != 0) ? true : false),  LOG_DATA_PROTOCOL_RCV, "Logging rcv data package headers" ));
        mTableModel.add( new LogLevel(  (((LOG_RETRANSMISSION_CACHE&mSelectedLogLevel) != 0) ? true : false),  LOG_RETRANSMISSION_CACHE, "Logging retransmission cache data" ));


        tContentsPanel.setLayout( new BorderLayout());
        Table tTable = new Table( mTableModel, new Dimension( 360, 160), null);
        tContentsPanel.add( tTable, BorderLayout.CENTER );

        tButtonPanel = new JPanel();
        tButtonPanel.setLayout( new FlowLayout());
        JButton jCancelButton = new JButton();
        jCancelButton.setAction(new AbstractAction()
        {
            public void actionPerformed(ActionEvent arg0) {
                SetLogLevelDialog.this.dispose();
            }
        });
        jCancelButton.setBounds(new Rectangle(256, 5, 115, 20));
        jCancelButton.setText("Cancel");

        JButton jOkButton = new JButton();
        jOkButton.setAction(new AbstractAction()
        {
            public void actionPerformed(ActionEvent arg0) {
                mSelectedLogLevel = mTableModel.getObjects().stream().mapToInt( le -> le.getLogMask()).sum();
                SetLogLevelDialog.this.dispose();
            }
        });
        jOkButton.setBounds(new Rectangle(256, 5, 65, 20));
        jOkButton.setText("Set");

        tButtonPanel = new JPanel();
        tButtonPanel.setLayout( new FlowLayout());
        tButtonPanel.add(jOkButton);
        tButtonPanel.add(jCancelButton);
        tContentsPanel.add( tButtonPanel, BorderLayout.SOUTH );
        this.setContentPane( tContentsPanel );
    }


    public class LogLevel
    {
        @TableAttribute( header = "On/Off", column = 0, preferedWidth = 20, justify = "center", editable = true)
        public boolean  mOnOff;
        @TableAttribute( header = "Description", column = 1,  justify = "left", preferedWidth = 260)
        public String     mDescription;

        public int        mLogMask;

        public LogLevel( boolean pChecked, int pLogMask, String pDescription) {
            mOnOff = pChecked;
            mLogMask = pLogMask;
            mDescription = pDescription;
        }

        int getLogMask() {
            if (mOnOff) {
                return mLogMask;
            }
            return 0;
        }
    }






    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            System.out.println( ex.getMessage());
        }
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jFrame.setTitle("Set Log Level");

        SetLogLevelDialog dialog = new SetLogLevelDialog(jFrame, "Dialog Set Log Level", 0, true);
        dialog.pack();
        dialog.setVisible(true);

        jFrame.pack();
        jFrame.setVisible(true);


        while( true ) {
            try {Thread.currentThread().sleep( 10000 ); }
            catch( InterruptedException e ) {}
        }
    }
}
