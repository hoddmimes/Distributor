package com.hoddmimes.distributor.samples.table;



import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TableModel<T> extends AbstractTableModel
{
    private int mMaxItems;
    LinkedList<T> mObjects = null;
    ColumnModel mTableColumnModel = null;
    ObjectRenderer mCellRenderer = new ObjectRenderer();
    HeaderRender mHeaderRender = new HeaderRender();
    TableAttributeHandler mTableAttributeHandle;


    public TableModel(Class<T> pObjectClass) {
        mMaxItems = Integer.MAX_VALUE;
        mObjects = new LinkedList<>();
        mTableAttributeHandle = new TableAttributeHandler(pObjectClass );
        mTableColumnModel = new ColumnModel( mTableAttributeHandle.getHeaders(), mTableAttributeHandle.getAttributeTypes());
    }

    public void setMaxItems( int pMaxItems ) {
        mMaxItems = pMaxItems;
    }

    public void addFirst( T pObject ) {
        mObjects.addFirst( pObject );
        if (mObjects.size() > mMaxItems) {
            mObjects.removeLast();
        }
        super.fireTableDataChanged();
    }

    public void add( T pObject ) {
        mObjects.add( pObject );
        if (mObjects.size() > mMaxItems) {
            mObjects.removeFirst();
        }
        super.fireTableDataChanged();
    }

    public void removeRow( int pRow ) {
        mObjects.remove( pRow );
        super.fireTableDataChanged();
    }


    @Override
    public int getRowCount() {
        return mObjects.size();
    }

    @Override
    public int getColumnCount() {
        return mTableColumnModel.getColumnCount();
    }



    @Override
    public Object getValueAt(int pRow, int pCol) {

        if ((pRow < 0) || (pRow > mObjects.size())) {
            return null;
        }
        Object tObject = mObjects.get( pRow );
        return mTableAttributeHandle.getAttribute( pCol, tObject );
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (row < 0) {
            return false;
        }
        return mTableAttributeHandle.getEditable( column );
    }

    public List<T> getObjects() {
        return this.mObjects;
    }


    @Override
    public void setValueAt(Object pValue, int pRow, int pColumn ) {
       // System.out.println("set value row: " + pRow + " col: " + pColumn + " class: " + pValue.getClass().getSimpleName() + " value: " + pValue.toString());

        TableAttributeHandler.TableAttributeEnity ta =  mTableAttributeHandle.getAttributeEnity( pColumn );
        Class clz = ta.mField.getType();
        T tModelObject = mObjects.get( pRow );

        Object tModelObjectField= TableAttributeHandler.getFieldsObject( tModelObject, pColumn );

        if (clz == JCheckBox.class) {
            JCheckBox chkbox = (JCheckBox) tModelObjectField;
            //chkbox.setSelected((boolean) pValue );
        }

        else if (clz == JComboBox.class) {
            JComboBox cmbbox = (JComboBox) tModelObjectField;
            cmbbox.setActionCommand( (String) pValue);
        } else {
            if (clz == String.class) {
                try {ta.mField.set( tModelObject, pValue );}
                catch( IllegalAccessException e) { e.printStackTrace(); }
            }

            if (clz == Integer.class) {
                Integer v = Integer.parseInt( pValue.toString());
                try {ta.mField.setInt(tModelObject, v );}
                catch( IllegalAccessException e) { e.printStackTrace(); }
            }

            if (clz == double.class) {
                Double v = Double.parseDouble( pValue.toString());
                try {ta.mField.setDouble(tModelObject, v );}
                catch( IllegalAccessException e) { e.printStackTrace(); }
            }
            if  (clz == long.class) {
                long v = Long.parseLong( pValue.toString());
                try {ta.mField.setLong(tModelObject, v );}
                catch( IllegalAccessException e) { e.printStackTrace(); }
            }
            if (clz == boolean.class) {
                boolean v = Boolean.parseBoolean( pValue.toString());
                try {ta.mField.setBoolean(tModelObject, v );}
                catch( IllegalAccessException e) { e.printStackTrace(); }
            }
        }
        super.fireTableDataChanged();
    }


    public T getObjectAtRow( int pRow  ) {
        if ((pRow >= 0) && (pRow < mObjects.size())) {
            return mObjects.get( pRow );
        }
        return null;
    }

    public TableColumnModel getTableColumnModel() {
        return mTableColumnModel;
    }

    public JTableHeader getTableHeader() {
        JTableHeader th = new JTableHeader(mTableColumnModel);
        return th;
    }


    TableCellEditor getCellEditor(int pRow, int pColumn ) {
        TableAttributeHandler.TableAttributeEnity ta =  mTableAttributeHandle.getAttributeEnity( pColumn );
        Class clz = ta.mField.getType();
        T tModelObject = mObjects.get( pRow );
        Object tModelObjectField= TableAttributeHandler.getFieldsObject( tModelObject, pColumn );

        if (clz == String.class) {
            return new DefaultCellEditor( new JTextField( (String) tModelObjectField ));
        }
        if ((clz == Boolean.class) || (clz == boolean.class)) {
            boolean tValue = (boolean) tModelObjectField;
            JCheckBox tChkBox = new JCheckBox();
            tChkBox.setSelected( tValue );
            DefaultCellEditor tEditor = new DefaultCellEditor( tChkBox );
            return tEditor;
        }

        if (clz == JComboBox.class) {
            JComboBox cmbbox = (JComboBox) tModelObjectField;
            return new DefaultCellEditor( cmbbox );
        }

        return new DefaultCellEditor( new JTextField( String.valueOf(tModelObjectField )));
    }

    class ColumnModel extends DefaultTableColumnModel
    {
        ColumnModel(String[] pHeaders, Class pClasses[] ) {
            super();
            init( pHeaders, pClasses );
        }

        private void init(String[] pHeaders, Class pClasses[]) {
            for( int i = 0; i < pHeaders.length; i++) {
                super.addColumn( new ColumnData(i, pHeaders[i], pClasses[i]));
            }
        }
    }

     class ColumnData extends TableColumn {
        int mJustify;
        Class mClass;

        ColumnData(int pIndex, String pHeader, Class pClass) {
            super();
            super.setHeaderValue(pHeader);
            super.setModelIndex( pIndex );
            super.setHeaderRenderer( mHeaderRender );
            super.setCellRenderer( mCellRenderer );



            mJustify = mTableAttributeHandle.getObjectJustify( pIndex );
            if (mTableAttributeHandle.getPreferredWidth( pIndex ) > 0) {
                super.setPreferredWidth( mTableAttributeHandle.getPreferredWidth( pIndex ) );
            }
            if (mTableAttributeHandle.getWidth( pIndex ) > 0) {
                super.setWidth( mTableAttributeHandle.getWidth( pIndex ) );
            }
        }

    }

    class ObjectRenderer implements TableCellRenderer
    {
        public Component getTableCellRendererComponent(
                JTable pJTable,
                Object pValue,
                boolean pIsSelected, boolean pHasFocus,
                int pRow, int pCol) {

            ColumnModel tColumnModel = (ColumnModel) pJTable.getColumnModel();
            ColumnData tColumnData = (ColumnData) tColumnModel.getColumn( pCol );

            if (pValue == null) {
                return stringRender("<null>", tColumnData);
            }



            if (pValue.getClass() == JComboBox.class) {
                JComboBox cb = (JComboBox) pValue;
                cb.setBorder( BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (mTableAttributeHandle.getPreferredWidth(pCol) > 0) {
                    Dimension tDim = cb.getPreferredSize();
                    tDim.setSize(mTableAttributeHandle.getPreferredWidth(pCol), tDim.height);
                    cb.setPreferredSize( tDim );
                }
                if (mTableAttributeHandle.getWidth(pCol)> 0) {
                    Dimension tDim = cb.getSize();
                    tDim.setSize(mTableAttributeHandle.getWidth(pCol), tDim.height);
                    cb.setSize( tDim );
                }
                return cb;
            }

            if ( (pValue.getClass() == boolean.class) || (pValue.getClass() == Boolean.class)) {
                JCheckBox cb = new JCheckBox();
                cb.setBackground( Color.white);
                cb.setSelected((boolean) pValue);

                cb.setBorder( BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (mTableAttributeHandle.getPreferredWidth(pCol) > 0) {
                    Dimension tDim = cb.getPreferredSize();
                    tDim.setSize(mTableAttributeHandle.getPreferredWidth(pCol), tDim.height);
                    cb.setPreferredSize( tDim );
                }
                if (mTableAttributeHandle.getWidth(pCol)> 0) {
                    Dimension tDim = cb.getSize();
                    tDim.setSize(mTableAttributeHandle.getWidth(pCol), tDim.height);
                    cb.setSize( tDim );
                }
                cb.setHorizontalAlignment( tColumnData.mJustify );
                return cb;
            }

            return stringRender(pValue.toString(), tColumnData );
        }


        private JLabel stringRender(String pValue, ColumnData tColumnData ) {
            JLabel tLabel = new JLabel();

            tLabel.setBackground(Color.WHITE);
            tLabel.setForeground(Color.BLACK);
            if (pValue != null) {
                tLabel.setText(pValue.toString());
            }

            tLabel.setHorizontalAlignment(tColumnData.mJustify);
            if (tColumnData.mJustify == JLabel.RIGHT) {
                tLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            }
            if (tColumnData.mJustify == JLabel.LEFT) {
                tLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            }
            if (tColumnData.mJustify == JLabel.CENTER) {
                tLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            }
            tLabel.setFont(new Font("Calibri",0,12));

            if (tColumnData.getPreferredWidth() > 0) {
                Dimension tDim = tLabel.getPreferredSize();
                tDim.setSize(tColumnData.getPreferredWidth(), tDim.height);
                tLabel.setPreferredSize( tDim );
            }
            if (tColumnData.getWidth() > 0) {
                Dimension tDim = tLabel.getSize();
                tDim.setSize(tColumnData.getWidth(), tDim.height);
                tLabel.setSize( tDim );
            }


            return tLabel;
        }
    }



    static class HeaderRender extends DefaultTableCellRenderer
    {
        public Component getTableCellRendererComponent(
                JTable pJTable, Object pValue,
                boolean pIsSelected, boolean pHasFocus,
                int pRow, int pCol) {



            JLabel tLabel = new JLabel(pValue.toString());
            tLabel.setForeground( Color.DARK_GRAY );
            tLabel.setHorizontalAlignment(JLabel.CENTER);
            tLabel.setFont( new Font("Arial", Font.PLAIN, 14));
            tLabel.setBorder( BorderFactory.createBevelBorder(0));
            return tLabel;
        }
    }
}
