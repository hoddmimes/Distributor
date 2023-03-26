package com.hoddmimes.distributor.samples;

import com.hoddmimes.distributor.samples.table.Table;
import com.hoddmimes.distributor.samples.table.TableAttribute;
import com.hoddmimes.distributor.samples.table.TableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ListNetwrkDialog extends JDialog implements Table.TableCallbackInterface {
    String mSelectedDevice = null;
    TableModel<NetworkDevice> mTableModel;

    public String getSelectedDevice() {
        return mSelectedDevice;
    }

    public ListNetwrkDialog(Frame pComponent, String pTitle, boolean pModale ) {
        super( pComponent, pTitle, pModale );
        initial();
    }

    private void initial() {
        JPanel tContentsPanel = new JPanel();
        mTableModel = new TableModel( NetworkDevice.class );
        JPanel tButtonPanel = new JPanel();

        try {
            Enumeration<NetworkInterface> tNics = NetworkInterface.getNetworkInterfaces();
            while (tNics.hasMoreElements()) {
                NetworkInterface tNetIf = tNics.nextElement();
                if (hasIp4Adress( tNetIf )) {
                    mTableModel.add( new NetworkDevice( tNetIf.getName(), getAddress( tNetIf), tNetIf.getDisplayName()));
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        tContentsPanel.setLayout( new BorderLayout());
        Table tTable = new Table( mTableModel, new Dimension( 460, 140), this );
        tContentsPanel.add( tTable, BorderLayout.CENTER );

        tButtonPanel = new JPanel();
        tButtonPanel.setLayout( new FlowLayout());
        JButton jCancelSendButton = new JButton();
        jCancelSendButton.setAction(new AbstractAction()
        {
            public void actionPerformed(ActionEvent arg0) {
                ListNetwrkDialog.this.dispose();
            }
        });
        jCancelSendButton.setBounds(new Rectangle(256, 5, 115, 20));
        jCancelSendButton.setText("Cancel");

        tButtonPanel = new JPanel();
        tButtonPanel.setLayout( new FlowLayout());
        tButtonPanel.add(jCancelSendButton);
        tContentsPanel.add( tButtonPanel, BorderLayout.SOUTH );
        this.setContentPane( tContentsPanel );
    }

    boolean hasIp4Adress(  NetworkInterface pNetIf ) {
        Enumeration<InetAddress> tAssignedIps = pNetIf.getInetAddresses();
        while (tAssignedIps.hasMoreElements()) {
            InetAddress tAddress = tAssignedIps.nextElement();
            if (tAddress instanceof Inet4Address) {
                return true;
            }
        }
        return false;
    }

    String getAddress(  NetworkInterface pNetIf ) {
        Enumeration<InetAddress> tAssignedIps = pNetIf.getInetAddresses();
        while (tAssignedIps.hasMoreElements()) {
            InetAddress tAddress = tAssignedIps.nextElement();
            if (tAddress instanceof Inet4Address) {
                return ((Inet4Address) tAddress).getHostAddress();
            }
        }
        return null;
    }



    @Override
    public void tableMouseClick(Object pObject, int pRow, int pCol) {

    }

    @Override
    public void tableMouseDoubleClick(Object pObject, int pRow, int pCol) {
         NetworkDevice tNetWrkDev = mTableModel.getObjectAtRow( pRow );
         if (tNetWrkDev != null) {
             mSelectedDevice = tNetWrkDev.getDevice();
         }
         ListNetwrkDialog.this.dispose();
    }

    public class NetworkDevice
    {
        @TableAttribute( header = "Eth Device", column = 0)
        public String  mEthDevice;
        @TableAttribute( header = "IP Address", column = 1)
        public String  mIP4Address;
        @TableAttribute( header = "Description", column = 2)
        public String  mDescription;

        String getDevice() {
            return mEthDevice;
        }

        public NetworkDevice( String pEthDevice, String pIP4Address, String pDescription ) {
            mEthDevice = pEthDevice;
            mIP4Address = pIP4Address;
            mDescription = pDescription;
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

        jFrame.setTitle("List Network Devices");

        ListNetwrkDialog dialog = new ListNetwrkDialog(jFrame, "Dialog Network Devices", true);
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
