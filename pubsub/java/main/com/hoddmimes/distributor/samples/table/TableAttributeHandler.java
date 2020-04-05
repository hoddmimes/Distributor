package com.hoddmimes.distributor.samples.table;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.*;

public class TableAttributeHandler
{
    Map<Integer, TableAttributeEnity> mAttributes = null;


    public TableAttributeHandler( Class pTableObjectClass ) {
        parse( pTableObjectClass );
    }

    public String[] getHeaders() {
        List<TableAttributeEnity> tAttributes = new ArrayList<>();
        tAttributes.addAll( mAttributes.values());
        Collections.sort( tAttributes );
        String[] tHeader = new String[ tAttributes.size()];
        for( int i = 0; i < tAttributes.size(); i++) {
          tHeader[i] = tAttributes.get(i).mHeader;
        }
        return tHeader;
    }
    public int getPreferredWidth( int pColumn ) {
        if ((pColumn < 0) || (pColumn > mAttributes.size())) {
            return 0;
        }
        TableAttributeEnity ta = mAttributes.get( pColumn );
        return ta.mPreferredWidth;
    }
    public int getWidth( int pColumn ) {
        if ((pColumn < 0) || (pColumn > mAttributes.size())) {
            return 0;
        }
        TableAttributeEnity ta = mAttributes.get( pColumn );
        return ta.mWidth;
    }

    public TableAttributeEnity getAttributeEnity( int pColumn ) {
        return this.mAttributes.get( pColumn );
    }

    public boolean getEditable(int pColumn ) {

        if ((pColumn < 0) || (pColumn > mAttributes.size())) {
            return false;
        }

        TableAttributeEnity ta = mAttributes.get( pColumn );
        return ta.mEditable;
    }

    public int getObjectJustify(int pColumn ) {
        if ((pColumn < 0) || (pColumn > mAttributes.size())) {
            return JLabel.CENTER;

        }

        TableAttributeEnity ta = mAttributes.get( pColumn );
        return ta.mJustify;
    }

    public Object getAttribute(int pColumn, Object pTableObject ) {
        if ((pColumn < 0) || (pColumn > mAttributes.size())) {
            return null;
        }
        TableAttributeEnity ta = mAttributes.get( pColumn );
        try {
           return  ta.get( pTableObject );
        }
        catch( Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class[] getAttributeTypes() {
        List<TableAttributeEnity> tAttributes = new ArrayList<>();
        tAttributes.addAll( mAttributes.values());
        Collections.sort( tAttributes );
        Class[] tClasses = new Class[ tAttributes.size()];
        for( int i = 0; i < tAttributes.size(); i++) {
            tClasses[i] = tAttributes.get(i).mField.getType();
        }

        return tClasses;
    }


    public static Object getFieldsObject(Object pFieldObject, int pColumn ) {
        Field[] tObjectFields = pFieldObject.getClass().getFields();
        for( int i = 0; i < tObjectFields.length; i++) {
            Field tField = tObjectFields[i];
            if (tField.getAnnotation( TableAttribute.class ) != null) {
                TableAttribute ta = tField.getAnnotation( TableAttribute.class );
                if (ta.column() == pColumn) {
                    try {return tField.get(pFieldObject);}
                    catch( IllegalAccessException e) { e.printStackTrace();}
                }
            }
        }
        return null;
    }

    private void parse(  Class pTableObjectClass ) {
        mAttributes = new HashMap<>();
        Field[] tFields = pTableObjectClass.getFields();
        for( int i = 0; i < tFields.length; i++ ) {
            Field tField = tFields[i];



            TableAttribute ta = tField.getAnnotation( TableAttribute.class );
            if (ta != null) {
                mAttributes.put( ta.column(),
                        new TableAttributeEnity( ta.header(),
                                                 ta.column(),
                                                 tField,
                                                 ta.width(),
                                                 ta.preferedWidth(),
                                                 getAlignMent(ta,tField),
                                                 ta.editable()));
            }
        }
    }

    private int getAlignMent( TableAttribute ta, Field tField ) {
        String tJustStr = ta.justify();

        if (tJustStr.equals("byClass")) {
            Class clz = tField.getType();
            if (clz == Integer.class) {
                return JLabel.RIGHT;
            }
            if (clz == Short.class) {
                return JLabel.RIGHT;
            }
            if (clz == Byte.class) {
                return JLabel.RIGHT;
            }
            if (clz == Character.class) {
                return JLabel.RIGHT;
            }
            if (clz == Long.class) {
                return  JLabel.RIGHT;
            }
            if (clz == Double.class) {
                return  JLabel.RIGHT;
            }
            if (clz == Float.class) {
                return  JLabel.RIGHT;
            }
            return JLabel.CENTER;
        }

        if (tJustStr.equalsIgnoreCase("right")) {
            return JLabel.RIGHT;
        }
        if (tJustStr.equalsIgnoreCase("left")) {
            return JLabel.LEFT;
        }
        if (tJustStr.equalsIgnoreCase("center")) {
            return JLabel.CENTER;
        }

        return JLabel.CENTER;

    }



    class  TableAttributeEnity implements Comparable<TableAttributeEnity> {
        int     mColumn;
        String mHeader;
        Field mField;
        int     mWidth;
        int     mPreferredWidth;
        int     mJustify;
        boolean mEditable;


        public void set(Object pObject, Object pValue ) {
            try {mField.set( pObject, pValue );}
            catch( IllegalAccessException e) { e.printStackTrace(); }
        }

        public Object get(Object pObject ) {
            try {return mField.get( pObject );}
            catch( IllegalAccessException e) { e.printStackTrace(); }
            return null;
        }


        TableAttributeEnity(String pHeader, int pColumn, Field pField, int pWidth, int pPreferredWidth, int pJustify, boolean pEditable) {
            mColumn = pColumn;
            mHeader = pHeader;
            mField = pField;
            mWidth = pWidth;
            mPreferredWidth = pPreferredWidth;
            mJustify = pJustify;
            mEditable = pEditable;
        }

        @Override
        public int compareTo(TableAttributeEnity o) {
            if (this.mColumn < o.mColumn) {
                return -1;
            }
            if (this.mColumn > o.mColumn) {
                return 1;
            }
            return 0;
        }
    }
}
