package com.hoddmimes.distributor.snoop;

import com.hoddmimes.distributor.DistributorNewRemoteConnectionEvent;
import com.hoddmimes.distributor.DistributorRemoveRemoteConnectionEvent;
import com.hoddmimes.distributor.samples.table.Table;
import com.hoddmimes.distributor.samples.table.TableAttribute;
import com.hoddmimes.distributor.samples.table.TableModel;

import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;



public class GuiPublishers extends JPanel implements Runnable, Table.TableCallbackInterface {

    private TableModel<GuiPublishers.PublisherTabObj>   mTableModel;
    private Map<Integer, GuiPublishers.PublisherTabObj> mPublisherMap;
    private RepaintThread mRepaintThread;
    private Table mTable;
    private SnoopUpdateMsgListner mRegisterUpdateListnerIf;

    public GuiPublishers( SnoopUpdateMsgListner pRegisterUpdateListnerIf ) {
        super( new BorderLayout());
        mRepaintThread = new RepaintThread( 500, this );
        mRepaintThread.start();
        mPublisherMap = new HashMap<>();
        mRegisterUpdateListnerIf = pRegisterUpdateListnerIf;

        JPanel tContentPanel = new JPanel( new BorderLayout());
        tContentPanel.setBorder( new EmptyBorder( 20,10,10,10));
        JLabel jTitle = new JLabel("Publishers");
        jTitle.setFont( new Font(Font.SANS_SERIF, Font.BOLD + Font.ITALIC, 14) );
        tContentPanel.add( jTitle, BorderLayout.NORTH );

        mTableModel = new TableModel<>(GuiPublishers.PublisherTabObj.class);
        mTable = new Table(mTableModel, new Dimension(600, 140), this);
        tContentPanel.add(mTable, BorderLayout.CENTER);
        super.add(tContentPanel, BorderLayout.CENTER);
    }


    void addPublisher( DistributorNewRemoteConnectionEvent pEvent ) {
        if (mPublisherMap.containsKey( pEvent.getAppId())) {
            return;
        }

        PublisherTabObj tPublisher = new PublisherTabObj( pEvent );
        mTableModel.add( tPublisher );
        mPublisherMap.put( pEvent.getAppId(), tPublisher );
    }

    void distributorUpdate( int pAppId, String pSubject, byte[] pPayLoad ) {
        PublisherTabObj tPublisher = mPublisherMap.get( pAppId );
        if (tPublisher != null) {
            tPublisher.distributorUpdate( pSubject, pPayLoad );
            mTableModel.fireTableDataChanged();
        }
    }


    void removePublisher( DistributorRemoveRemoteConnectionEvent pEvent ) {
        List<GuiPublishers.PublisherTabObj> tPublishers = mTableModel.getObjects();
        for( int i = 0; i < tPublishers.size(); i++) {
            GuiPublishers.PublisherTabObj tPub = tPublishers.get(i);
            if (pEvent.getAppId() == tPub.mAppId) {
                mTableModel.removeRow( i );
                mPublisherMap.remove( pEvent.getAppId());
                return;
            }
        }
    }

    @Override
    public void run() {
        synchronized ( mTableModel ) {
            mTableModel.getObjects().stream().forEach( mto -> mto.refresh());
            mTableModel.fireTableDataChanged();
        }
    }

    @Override
    public void tableMouseClick(Object pObject, int pRow, int pCol) {

    }

    @Override
    public void tableMouseDoubleClick(Object pObject, int pRow, int pCol) {
        System.out.println("Double click object: " + pObject.toString() + " row: " + pRow + " col: " + pCol );
        GuiTraceMsgsFrame tTraceFrame = new GuiTraceMsgsFrame((Publisher) pObject, mRegisterUpdateListnerIf);
        tTraceFrame.pack();
        tTraceFrame.setVisible( true );

    }

    public class PublisherTabObj extends Publisher {

        @TableAttribute(header = "Name", column = 0)
        public String mToAppName;
        @TableAttribute(header = "Started", column = 1)
        public String mToStartTime;
        @TableAttribute(header = "Ip Addr", column = 2)
        public String mToRemoteIpAddress;
        @TableAttribute(header = "Subjects", column = 3)
        public long mTotSubjects;
        @TableAttribute(header = "Updates", column = 4)
        public long mToTotUpdates;
        @TableAttribute(header = "Bytes (kb)", column = 5)
        public long mToTotKbytes;

        public PublisherTabObj(DistributorNewRemoteConnectionEvent pEvent ) {
           super( pEvent );
           mToAppName = super.mAppName;
           mToRemoteIpAddress = super.mRemoteIpAddress;
           mToTotUpdates = super.mTotUpdates.get();
           mToTotKbytes = super.mTotKBytes.get();
           mToStartTime = super.mStartTime;
        }

        public void refresh() {
            mToTotUpdates = super.mTotUpdates.get();
            mToTotKbytes = super.mTotKBytes.get();
            mTotSubjects = super.mSubjects.size();
        }

    }
}
