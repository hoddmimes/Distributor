package generated;

import com.hoddmimes.distributor.messaging.*;


public class TestMessage implements MessageInterface {
    public static final int MESSAGE_ID = ((1000 << 16) + 1);

    protected volatile byte[] mMessageBytesCached = null;


    private String[] mStringArray;
    private boolean mBoolValue;
    private byte mByteValue;
    private short mShortValue;
    private int mIntValue;
    private int[] mIntArray;
    private long mLongValue;
    private String mStrValue;
    private byte[] mBytesValue;
    private byte[][] mBytesArrayValue;

    public TestMessage() {

    }


    public TestMessage(byte[] pMessageByteArray) {

        MessageBinDecoder tDecoder = new MessageBinDecoder(pMessageByteArray);
        this.decode(tDecoder);
    }


    public void addStringArrayToArray(String pValue) {
        if (pValue == null) {
            return;
        }

        if (mStringArray == null) {

            String[] mStringArray = new String[1];
            mStringArray[0] = pValue;
        } else {
            int tSize = mStringArray.length + 1;

            String[] tArray = new String[tSize + 1];
            for (int i = 0; i < tSize - 1; i++) {
                tArray[i] = mStringArray[i];
            }
            tArray[tSize - 1] = pValue;
            mStringArray = tArray;
        }
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public String[] getStringArray() {
        return mStringArray;
    }

    public void setStringArray(String[] pStringArray) {
        mStringArray = pStringArray;
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public boolean getBoolValue() {
        return mBoolValue;
    }

    public void setBoolValue(boolean pBoolValue) {
        mBoolValue = pBoolValue;
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public byte getByteValue() {
        return mByteValue;
    }

    public void setByteValue(byte pByteValue) {
        mByteValue = pByteValue;
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public short getShortValue() {
        return mShortValue;
    }

    public void setShortValue(short pShortValue) {
        mShortValue = pShortValue;
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public int getIntValue() {
        return mIntValue;
    }

    public void setIntValue(int pIntValue) {
        mIntValue = pIntValue;
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public void addIntArrayToArray(int pValue) {

        if (mIntArray == null) {
            int[] mIntArray = new int[1];
            mIntArray[0] = pValue;
        } else {
            int tSize = mIntArray.length + 1;
            int[] tArray = new int[tSize];
            for (int i = 0; i < tSize - 1; i++) {
                tArray[i] = mIntArray[i];
            }
            tArray[tSize - 1] = pValue;
            mIntArray = tArray;
            synchronized (this) {
                mMessageBytesCached = null;
            }
        }
    }

    public int[] getIntArray() {
        return mIntArray;
    }

    public void setIntArray(int[] pIntArray) {
        mIntArray = pIntArray;
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public long getLongValue() {
        return mLongValue;
    }

    public void setLongValue(long pLongValue) {
        mLongValue = pLongValue;
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public String getStrValue() {
        return mStrValue;
    }

    public void setStrValue(String pStrValue) {
        mStrValue = pStrValue;
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public byte[] getBytesValue() {
        return mBytesValue;
    }

    public void setBytesValue(byte[] pBytesValue) {
        mBytesValue = pBytesValue;
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public void addBytesArrayValueToArray(byte[] pValue) {
        if (pValue == null) {
            return;
        }

        if (mBytesArrayValue == null) {

            byte[][] mBytesArrayValue = new byte[1][];
            mBytesArrayValue[0] = pValue;
        } else {
            int tSize = mBytesArrayValue.length + 1;

            byte[][] tArray = new byte[tSize + 1][];
            for (int i = 0; i < tSize - 1; i++) {
                tArray[i] = mBytesArrayValue[i];
            }
            tArray[tSize - 1] = pValue;
            mBytesArrayValue = tArray;
        }
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public byte[][] getBytesArrayValue() {
        return mBytesArrayValue;
    }

    public void setBytesArrayValue(byte[][] pBytesArrayValue) {
        mBytesArrayValue = pBytesArrayValue;
        synchronized (this) {
            mMessageBytesCached = null;
        }
    }

    public String getMessageName() {
        return "TestMessage";
    }

    public String getFullMessageName() {
        return "generated.TestMessage";
    }

    public int getMessageId() {
        return (1000 << 16) + 1;
    }


    public void encode(MessageBinEncoder pEncoder) {
        encode(pEncoder, false);
    }

    public void encode(MessageBinEncoder pEncoder, boolean pIsExtensionInvoked) {
        if (!pIsExtensionInvoked) {
            pEncoder.add(getMessageId());
        }

        /**
         * Encode Attribute: mStringArray Type: String
         */

        pEncoder.add(mStringArray);
        /**
         * Encode Attribute: mBoolValue Type: boolean
         */

        pEncoder.add(mBoolValue);
        /**
         * Encode Attribute: mByteValue Type: byte
         */

        pEncoder.add(mByteValue);
        /**
         * Encode Attribute: mShortValue Type: short
         */

        pEncoder.add(mShortValue);
        /**
         * Encode Attribute: mIntValue Type: int
         */

        pEncoder.add(mIntValue);
        /**
         * Encode Attribute: mIntArray Type: int
         */

        pEncoder.add(mIntArray);
        /**
         * Encode Attribute: mLongValue Type: long
         */

        pEncoder.add(mLongValue);
        /**
         * Encode Attribute: mStrValue Type: String
         */

        pEncoder.add(mStrValue);
        /**
         * Encode Attribute: mBytesValue Type: byte[]
         */

        pEncoder.add(mBytesValue);
        /**
         * Encode Attribute: mBytesArrayValue Type: byte[]
         */

        pEncoder.add(mBytesArrayValue);
    }


    public void decode(MessageBinDecoder pDecoder) {
        decode(pDecoder, false);
    }

    @SuppressWarnings("unchecked")
    public void decode(MessageBinDecoder pDecoder, boolean pIsExtensionInvoked) {
        String tStr = null;
        int tSize = 0;

        if (!pIsExtensionInvoked) {
            pDecoder.readInt();    // Read Message Id
        }


        /**
         * Decoding Attribute: mStringArray Type: String
         */

        mStringArray = pDecoder.readStringArray();
        /**
         * Decoding Attribute: mBoolValue Type: boolean
         */

        mBoolValue = pDecoder.readBoolean();
        /**
         * Decoding Attribute: mByteValue Type: byte
         */

        mByteValue = pDecoder.readByte();
        /**
         * Decoding Attribute: mShortValue Type: short
         */

        mShortValue = pDecoder.readShort();
        /**
         * Decoding Attribute: mIntValue Type: int
         */

        mIntValue = pDecoder.readInt();
        /**
         * Decoding Attribute: mIntArray Type: int
         */

        mIntArray = pDecoder.readIntArray();
        /**
         * Decoding Attribute: mLongValue Type: long
         */

        mLongValue = pDecoder.readLong();
        /**
         * Decoding Attribute: mStrValue Type: String
         */

        mStrValue = pDecoder.readString();
        /**
         * Decoding Attribute: mBytesValue Type: byte[]
         */

        mBytesValue = pDecoder.readBytes();
        /**
         * Decoding Attribute: mBytesArrayValue Type: byte[]
         */

        mBytesArrayValue = pDecoder.readBytesArray();
    }


    public byte[] messageToBytes() {
        synchronized (this) {

            if (mMessageBytesCached != null) {

                return mMessageBytesCached;
            } else {
                MessageBinEncoder tEncoder = new MessageBinEncoder();
                this.encode(tEncoder);
                mMessageBytesCached = tEncoder.getBytes();
                return mMessageBytesCached;
            }
        }
    }


    String blanks(int pCount) {
        if (pCount == 0) {
            return null;
        }
        String tBlanks = "                                                                                       ";
        return tBlanks.substring(0, pCount);
    }


    public String toString() {
        return this.toString(0);
    }

    public String toString(int pCount) {
        return toString(pCount, false);
    }

    public String toString(int pCount, boolean pExtention) {
        StringBuilder tSB = new StringBuilder(512);
        if (pCount > 0) {
            tSB.append(blanks(pCount));
        }

        if (pExtention) {
            tSB.append("<Extending Message: " + "\"TestMessage\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
        } else {
            tSB.append("Message: " + "\"TestMessage\"  Id: " + Integer.toHexString(getMessageId()) + "\n");
        }


        tSB.append(blanks(pCount + 2) + "mStringArray[]: ");
        tSB.append(MessageAux.format(mStringArray));
        tSB.append("\n");
        tSB.append(blanks(pCount + 2) + "mBoolValue: ");
        tSB.append(String.valueOf(mBoolValue));
        tSB.append("\n");
        tSB.append(blanks(pCount + 2) + "mByteValue: ");
        tSB.append(String.valueOf(mByteValue));
        tSB.append("\n");
        tSB.append(blanks(pCount + 2) + "mShortValue: ");
        tSB.append(String.valueOf(mShortValue));
        tSB.append("\n");
        tSB.append(blanks(pCount + 2) + "mIntValue: ");
        tSB.append(String.valueOf(mIntValue));
        tSB.append("\n");
        tSB.append(blanks(pCount + 2) + "mIntArray[]: ");
        tSB.append(MessageAux.format(mIntArray));
        tSB.append("\n");
        tSB.append(blanks(pCount + 2) + "mLongValue: ");
        tSB.append(String.valueOf(mLongValue));
        tSB.append("\n");
        tSB.append(blanks(pCount + 2) + "mStrValue: ");
        tSB.append(String.valueOf(mStrValue));
        tSB.append("\n");
        tSB.append(blanks(pCount + 2) + "mBytesValue: ");
        tSB.append(MessageAux.format(mBytesValue));
        tSB.append("\n");
        tSB.append(blanks(pCount + 2) + "mBytesArrayValue[]: ");
        tSB.append(MessageAux.format(mBytesArrayValue));
        tSB.append("\n");
        return tSB.toString();
    }


    public void treeAddMessageAsSuperClass(TreeNode treeNode) {


        treeNode.add(TreeNode.createArray("stringArray", mStringArray));
        treeNode.add(new TreeNode("boolValue" + " : " + String.valueOf(mBoolValue)));
        treeNode.add(new TreeNode("byteValue" + " : " + String.valueOf(mByteValue)));
        treeNode.add(new TreeNode("shortValue" + " : " + String.valueOf(mShortValue)));
        treeNode.add(new TreeNode("intValue" + " : " + String.valueOf(mIntValue)));
        treeNode.add(TreeNode.createArray("intArray", mIntArray));
        treeNode.add(new TreeNode("longValue" + " : " + String.valueOf(mLongValue)));
        treeNode.add(new TreeNode("strValue" + " : " + String.valueOf(mStrValue)));
        treeNode.add(TreeNode.createArray("bytesValue", mBytesValue));
        treeNode.add(TreeNode.createArray("bytesArrayValue", mBytesArrayValue));
    }


    public TreeNode getNodeTree(String pMessageAttributeName) {
        TreeNode treeNode = null;
        if (pMessageAttributeName == null) {
            treeNode = new TreeNode("TestMessage");
        } else {
            treeNode = new TreeNode(pMessageAttributeName + " [TestMessage]");
        }

        treeNode.add(TreeNode.createArray("stringArray", mStringArray));
        treeNode.add(new TreeNode("boolValue" + " : " + String.valueOf(mBoolValue)));
        treeNode.add(new TreeNode("byteValue" + " : " + String.valueOf(mByteValue)));
        treeNode.add(new TreeNode("shortValue" + " : " + String.valueOf(mShortValue)));
        treeNode.add(new TreeNode("intValue" + " : " + String.valueOf(mIntValue)));
        treeNode.add(TreeNode.createArray("intArray", mIntArray));
        treeNode.add(new TreeNode("longValue" + " : " + String.valueOf(mLongValue)));
        treeNode.add(new TreeNode("strValue" + " : " + String.valueOf(mStrValue)));
        treeNode.add(TreeNode.createArray("bytesValue", mBytesValue));
        treeNode.add(TreeNode.createArray("bytesArrayValue", mBytesArrayValue));
        return treeNode;
    }

}

