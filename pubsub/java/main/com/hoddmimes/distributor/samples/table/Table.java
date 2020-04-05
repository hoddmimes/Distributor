package com.hoddmimes.distributor.samples.table;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Table extends JScrollPane
{
    private ExtendedJTable mTable;
    private TableModel mTableModel;
    private TableCallbackInterface mCallbacks;

    public Table(TableModel pTableModel, Dimension pDimension, TableCallbackInterface pCallbacks ) {
        super();
        mTableModel = pTableModel;
        mTable = new ExtendedJTable();
        mTable.setAutoCreateColumnsFromModel(false);
        mTable.setModel( mTableModel );


        mTable.setColumnModel( mTableModel.getTableColumnModel());
        mTable.setTableHeader( mTableModel.getTableHeader() );
        if (pCallbacks != null) {
            mTable.addMouseListener( new MouseClickListener<>() );
        }
        mTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS );
        mCallbacks = pCallbacks;
        super.setViewportView( mTable );
        if (pDimension != null) {
            mTable.setPreferredScrollableViewportSize( pDimension );
            //super.setPreferredSize( pDimension );
        }
    }

    public Table( TableModel pTableModel ) {
        this( pTableModel, null, null);
    }



    class ExtendedJTable extends JTable
    {
        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            System.out.println("GetCellEditor row: " + row + " cell: " + column );

            TableModel tTableModel = (TableModel) this.getModel();
            TableCellEditor tEditor = tTableModel.getCellEditor( row, column );
            if (tEditor == null) {
                return super.getCellEditor( row,column);
            }
            return tEditor;
        }
    }

    public class MouseClickListener<E> extends MouseAdapter {
        E mObject;
        int mRow,mCol;

        private Timer timer = new Timer(300, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // timer has gone off, so treat as a single click
                singleClick();
                timer.stop();

            }
        });

        @Override
        public void mouseClicked(MouseEvent e) {

            mRow = mTable.rowAtPoint(e.getPoint());
            mCol = mTable.columnAtPoint(e.getPoint());

            mObject = (E) mTableModel.getObjectAtRow( mRow );

            // Check if timer is running
            // to know if there was an earlier click
            if (timer.isRunning()) {
                // There was an earlier click so we'll treat it as a double click
                timer.stop();
                doubleClick() ;
            } else {
                // (Re)start the timer and wait for 2nd click
                timer.restart();
            }
        }

        protected void singleClick()
        {

            Table.this.mCallbacks.tableMouseClick( mObject, mRow, mCol );
        }

        protected void doubleClick() {
            Table.this.mCallbacks.tableMouseDoubleClick( mObject, mRow, mCol );
        }
    }

    public static interface TableCallbackInterface<E>
    {
        public void tableMouseClick(E pObject, int pRow, int pCol);
        public void tableMouseDoubleClick(E pObject, int pRow, int pCol);
    }
}
