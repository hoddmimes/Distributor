package com.hoddmimes.distributor.snoop;

import com.hoddmimes.distributor.samples.table.Table;
import com.hoddmimes.distributor.samples.table.TableAttribute;
import com.hoddmimes.distributor.samples.table.TableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class GuiConnections extends JPanel implements Runnable {
    private TableModel<GuiConnections.McaTabObj> mTableModel;
    private RepaintThread mRepaintThread;
    private Table mTable;

    public GuiConnections(Collection<McaEntry> pMcaEntries) {
        super(new BorderLayout());
        mRepaintThread = new RepaintThread( 1000, this );
        mRepaintThread.start();

        JPanel tContentPanel = new JPanel( new BorderLayout());
        JLabel jTitle = new JLabel("Multicast Groups");
        jTitle.setFont( new Font(Font.SANS_SERIF, Font.BOLD + Font.ITALIC, 14) );
        tContentPanel.add( jTitle, BorderLayout.NORTH );


        tContentPanel.setBorder( new EmptyBorder( 20,10,10,10));

        mTableModel = new TableModel<>(McaTabObj.class);
        for (McaEntry tMca : pMcaEntries) {
            mTableModel.add(new McaTabObj(tMca));
        }
        mTable = new Table(mTableModel, new Dimension(460, 140), null);
        tContentPanel.add(mTable, BorderLayout.CENTER);
        super.add( tContentPanel, BorderLayout.CENTER);
    }

    private McaTabObj getMcaTabObj( int pId ) {
        for( McaTabObj mto : mTableModel.getObjects()) {
            if (mto.mMca.getId() == pId) {
                return mto;
            }
        }
        return null;
    }

    public void distributorUpdate( int pMcaId, byte[] pPayLoad ) {
        McaTabObj mto = getMcaTabObj( pMcaId );
        if (mto != null) {
            synchronized( mTableModel ) {
                mto.distributorUpdate( pPayLoad );
            }
        }
    }

    @Override
    public void run()
    {
        synchronized ( mTableModel ) {
            mTableModel.fireTableDataChanged();
        }
    }


    public class McaTabObj {
        public McaEntry mMca;
        public long mBytes;

        @TableAttribute(header = "MC Address", column = 0)
        public String mMcaAddress;
        @TableAttribute(header = "MC Port ", column = 1)
        public int mMcaPort;
        @TableAttribute(header = "Updates", column = 2)
        public long mUpdatesReceived;
        @TableAttribute(header = "Bytes (kb)", column = 3)
        public long mKBytes;
        @TableAttribute(header = "Retransmissions", column = 4)
        public long mRetransmissions;


        public McaTabObj(McaEntry pMca) {
            mMca = pMca;
            mMcaAddress = mMca.getMcaAddress();
            mMcaPort = mMca.getMcaPort();
            mKBytes = 0;
            mUpdatesReceived = 0;
            mRetransmissions = 0;
        }

        void distributorUpdate( byte[] pPayLoad ) {
            mBytes += pPayLoad.length;
            mKBytes = (mBytes / 1000L);
            mUpdatesReceived++;
        }
    }


}
