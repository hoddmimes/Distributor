package com.hoddmimes.transform;


import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MessageCompiler
{
    static final boolean cDegugTrace = false;
    static final SimpleDateFormat cSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private final String mXSLFile = "Message.xsl";
    private final String mXSLMessageFactoryFile = "MessageFactory.xsl";



    private String mWorkingDirectory;

    private String mXmlDefinitionSourceFile = null;
    private List<MessageSourceFile> mMessageFiles = new ArrayList<>();
    private List<MessageSourceFile> mMessageFactoryFiles = new ArrayList<>();




    public static void main( String[] pArgs ) {
        MessageCompiler tGenerator = new MessageCompiler();
        tGenerator.parseParameters( pArgs  );
        try {
            for( int i = 0; i < tGenerator.mMessageFiles.size(); i++) {
                tGenerator.transformMessageFile( tGenerator.mMessageFiles.get(i));
            }
            tGenerator.messageFactoryTransform();

        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }

    private String getOutPath( String pPath ) {
        // Check if path is absolut or relative. If it is relative return the absolute path relative to the current
        // working directory otherwise return the absolute path
        Path p = Paths.get(pPath);
        if (p.isAbsolute()) {
            return pPath;
        }
        return mWorkingDirectory + pPath;
    }


    private void transformMessageFile( MessageSourceFile pSource) throws Exception
    {
        File tPath = new File( pSource.mXmlSourceFile );
        String tXmlSourcePath = tPath.getCanonicalFile().getParent().replace('\\','/');

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer tTransformer = tFactory.newTransformer( new XslFiles().getXslStreamSource(mXSLFile));
        tTransformer.setParameter("outPath", getOutPath( pSource.mOutPath ));
        tTransformer.setParameter("package", pSource.mPackage );
        tTransformer.setParameter("inputXml", pSource.mXmlSourceFile );
        tTransformer.setParameter("messageBase", pSource.mMessageBase );
        tTransformer.setParameter("inputXsl",  mXSLFile );
        tTransformer.setParameter("inputXmlPath", tXmlSourcePath);

        tTransformer.transform(new StreamSource(new File(pSource.mXmlSourceFile)), new StreamResult(new NullStream()));
    }


    private void messageFactoryTransform() throws Exception
    {
        File tPath = new File( mXmlDefinitionSourceFile );
        String tSystemId = tPath.toURI().toURL().toExternalForm();
        String tXmlSourcePath = tPath.getCanonicalFile().getParent().replace('\\','/');

        for(int i = 0; i < mMessageFactoryFiles.size(); i++ )
        {
            MessageSourceFile tSource = mMessageFactoryFiles.get(i);
            String tMessages = mergeXMLFiles();
            if (tMessages != null) {
                XslFiles tXslFiles = new XslFiles();
                StreamSource tStreamSource = new XslFiles().getXslStreamSource(mXSLMessageFactoryFile);

                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer tTransformer = tFactory.newTransformer( tStreamSource );
                tTransformer.setParameter("outPath", getOutPath(tSource.mOutPath ));
                tTransformer.setParameter("package", tSource.mPackage );
                tTransformer.transform(new StreamSource(new StringReader(tMessages), tSystemId), new StreamResult(new NullStream()));
            }
        }
    }

    private String mergeXMLFiles() {
        StringBuilder tStringBuilder = new StringBuilder();
        tStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        tStringBuilder.append("<MessagesRoot>");
        boolean tAnyFileLoaded = false;
        for(int i = 0; i < mMessageFiles.size(); i++ ) {
            MessageSourceFile tSrc = mMessageFiles.get(i);
            tAnyFileLoaded = true;
            String tFileContents = readXMLFile( tSrc.mXmlSourceFile, tSrc.mPackage );
            tStringBuilder.append( tFileContents );
        }
        tStringBuilder.append("</MessagesRoot>");
        if (!tAnyFileLoaded) {
            return null;
        }
        return tStringBuilder.toString();
    }

    private String getModulenameFromFilename( String pFilename ) {
        String tFilename = pFilename.replace("\\", "/");
        int tStart = (tFilename.lastIndexOf("/") < 0) ? 0 : tFilename.lastIndexOf("/") + 1;
        if (tFilename.lastIndexOf(".") > 0) {
            int tEnd = tFilename.lastIndexOf(".");
            return tFilename.substring(tStart, tEnd );
        } else {
            return tFilename.substring(tStart);
        }
    }
    private String readXMLFile( String pFilename, String pPackage ) {
        InputStream tInputStream = null;
        try {
            tInputStream = new FileInputStream(pFilename);
            String tModule = getModulenameFromFilename( pFilename );
            String tString = new XslFiles().convertStreamToString(tInputStream, true);
            tString = tString.replaceAll("<\\?[\\p{Print}]+\\?>", "");
            tString = tString.replaceAll("<Messages", "<Messages module=\"" + tModule +"\" package=\"" + pPackage + "\" ");
            return tString;
        }
        catch( IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void parseParameters( String[] pArgs )
    {
        // Find out what the current working directory is as absolute path
        String tCurrentPath = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
        this.mWorkingDirectory = (tCurrentPath.endsWith(".")) ? tCurrentPath.substring(0,tCurrentPath.length() - 1) : tCurrentPath;

        int i = 0;
        while( i < pArgs.length ) {
            if (pArgs[i].compareToIgnoreCase("-xml") == 0) {
                mXmlDefinitionSourceFile = pArgs[ i + 1 ].replace('\\','/');
                i++;
            }
            i++;
        }

        if (mXmlDefinitionSourceFile == null) {
            System.out.println("usage: Transform -xml <message-definition-source-file>.xml");
            System.exit(0);
        }

        Element tRoot = loadAndParseXml(mXmlDefinitionSourceFile).getDocumentElement();


        /**
         * Parse ordinary message file
         */
        NodeList tNodeList = tRoot.getElementsByTagName("MessageFile");
        if (tNodeList != null) {
            for( i = 0; i < tNodeList.getLength(); i++) {
                if (tNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element tFileElement = (Element) tNodeList.item(i);
                    String tXmlFile = tFileElement.getAttribute("file");
                    String tOutPath =  tFileElement.getAttribute("outPath");
                    String tPackage = tFileElement.getAttribute("package");
                    int tMessageBase = Integer.parseInt( tFileElement.getAttribute("messageBase"));

                    boolean tDebugFlag = false;
                    if ((tFileElement.getAttribute("debug") != null) && (tFileElement.getAttribute("debug").length() > 0)) {
                        tDebugFlag = Boolean.parseBoolean(tFileElement.getAttribute("debug"));
                    }
                    mMessageFiles.add( new MessageSourceFile( tXmlFile, tOutPath, tPackage, tMessageBase, tDebugFlag ));
                }
            }
        }





        /**
         * Parse message factory entries
         */
        if ((tRoot.getElementsByTagName("MessageFactory") != null) && (tRoot.getElementsByTagName("MessageFactory").getLength() > 0)) {
            tNodeList = tRoot.getElementsByTagName("MessageFactory");
            if (tNodeList != null) {
                for (i = 0; i < tNodeList.getLength(); i++) {
                    if (tNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element tFileElement = (Element) tNodeList.item(i);
                        String tPackage = tFileElement.getAttribute("package");
                        String tOutPath = tFileElement.getAttribute("outPath");
                        int tMessageBase = Integer.parseInt(tFileElement.getAttribute("messageBase"));

                        boolean tDebugFlag = false;
                        if ((tFileElement.getAttribute("debug") != null) && (tFileElement.getAttribute("debug").length() > 0)) {
                            tDebugFlag = Boolean.parseBoolean(tFileElement.getAttribute("debug"));
                        }
                        mMessageFactoryFiles.add(new MessageSourceFile(mXmlDefinitionSourceFile, tOutPath, tPackage, tMessageBase, tDebugFlag));
                    }
                }
            }
        }
    }

    public void transform() {

        try {
            for (int i = 0; i < this.mMessageFiles.size(); i++) {
                this.transformMessageFile(this.mMessageFiles.get(i));
            }
            this.messageFactoryTransform();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Document loadAndParseXml( String pXmlFilename ) {
        try {
            File tFile = new File( pXmlFilename );
            if (!tFile.exists()) {
                String tStr = tFile.getAbsolutePath();
                throw new InvalidParameterException("XML file \"" + tStr +"\" does not exists");
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document tDocument = db.parse(tFile);
            return tDocument;
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }




    static public class Extensions
    {
        static public String extractFilename( String pName) {
            int tIdx = pName.replace('\\', '/').lastIndexOf('/');
            if (tIdx < 0 ) {
                return pName;
            }
            return pName.substring(tIdx + 1);
        }

        static public String upperCase( String pString)
        {
            return pString.toUpperCase();
        }

        static public String lowerCase( String pString)
        {
            return pString.toLowerCase();
        }


        static public String getDateAndTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sdf.format( System.currentTimeMillis());
        }

        static public String lowerFirst(String pString)
        {
            return pString.substring(0, 1).toLowerCase() + pString.substring(1);
        }

        static public String upperFirst(String pString)
        {
            return pString.substring(0, 1).toUpperCase() + pString.substring(1);
        }

        static public String replace( String pString, String pOldPattern, String pNewPattern ) {
            String tString =  pString.replace( pOldPattern, pNewPattern );
            return tString;
        }

        static public String compileFilename( String pOutPath, String pJavaPackage, String pFilename ) {

            return null;
        }



        static public boolean endsWith( String pString, String pSuffix )
        {
            if (pString == null)
                return false;

            if (pString.endsWith(pSuffix))
                return true;
            else
                return false;
        }

        static public HashMap<String,String> findDbSupportMessages( NodeList pNodeList ) {
            HashMap<String, DbMessage> tMsgs = new  HashMap<>();

            // Get the root element "Messages"
            Element tMsgElement = (Element) pNodeList.item(0);
            // Find all "Message" sub elements
            NodeList tMsgsNodeList = tMsgElement.getElementsByTagName("Message");
            // Add all Message name to a map for each
            for( int i = 0; i < tMsgsNodeList.getLength(); i++ ) {
                if (tMsgsNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element me = (Element)   tMsgsNodeList.item(i);
                    boolean tDbSupport = (me.hasAttribute("db")) ? Boolean.parseBoolean(me.getAttribute("db")) : false;
                    String tMessageName = me.getAttribute("name");
                    String tExtendMessageName =  (me.hasAttribute("db")) ? me.getAttribute("extends") : null;

                    tMsgs.put( tMessageName, new DbMessage( tMessageName, tExtendMessageName, me, tDbSupport));
                }
            }
            // Traverse all messages to see what messages require db support
            for( DbMessage dm : tMsgs.values()) {
               if (dm.mDbSupport) {
                  checkDbSupportForChildren( dm, tMsgs );
                  checkDbSupportForExtentions( dm, tMsgs );
               }
            }

            // return a map with the message names that have db support enabled
            HashMap<String,String> tNameList = new HashMap<>();
            for( DbMessage dm : tMsgs.values()) {
                if (dm.mDbSupport) {
                    tNameList.put( dm.mMessageName, dm.mMessageName);
                    System.out.println("DB supported message: " +  dm.mMessageName);
                }
            }
            return tNameList;
        }
    }

    static private void checkDbSupportForExtentions( DbMessage pDbMsg, HashMap<String, DbMessage> pDbMsgs) {
        if ((pDbMsg.mDbSupport) && (pDbMsg.mExtensionMessageName != null)) {
            DbMessage tExtendMsg = (DbMessage) pDbMsgs.get(pDbMsg.mExtensionMessageName );
            if (tExtendMsg != null) {
                tExtendMsg.mDbSupport = true;
            }
        }
    }


    static private void checkDbSupportForChildren( DbMessage pDbMsg, HashMap<String, DbMessage> pDbMsgs) {
        NodeList tAttrList = pDbMsg.mMsgElement.getElementsByTagName("Attribute");
        for( int i = 0; i < tAttrList.getLength(); i++) {
            if (tAttrList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element tAttrElement = (Element) tAttrList.item(i);
                String tType = (tAttrElement.hasAttribute("type")) ? tAttrElement.getAttribute("type") : "";
                DbMessage tDbMsg = pDbMsgs.get( tType );
                if (tDbMsg != null) {
                    tDbMsg.mDbSupport = true;
                    checkDbSupportForChildren( tDbMsg, pDbMsgs );
                }
            }
        }
    }

    static public class Redirect extends org.apache.xalan.lib.Redirect
    {

        public void open(XSLProcessorContext context, ElemExtensionCall elem) throws java.net.MalformedURLException, FileNotFoundException, IOException, javax.xml.transform.TransformerException
        {
            String fileName = getFilename(context, elem);
            super.open(context, elem);
        }


        /**
         * Write the evalutation of the element children to the given file. Then close the file
         * unless it was opened with the open extension element and is in the formatter listener's table.
         */
        public void write(XSLProcessorContext context, ElemExtensionCall elem) throws java.net.MalformedURLException, FileNotFoundException, IOException, javax.xml.transform.TransformerException
        {

            String fileName = getFilename(context, elem);
            //System.out.println( fileName );
            //File file = new File( cXmlSourcePath, fileName );

            File file = new File( fileName );
            String s = file.getAbsolutePath();
            s = file.getParent().toString();
            s = file.getCanonicalFile().getCanonicalPath();
            s = file.getCanonicalFile().getAbsolutePath();
            boolean exists = file.exists();
            try
            {
                if (exists)
                    exists = !file.delete();
            }
            catch (Throwable e)
            {
                System.out.println("Error - problems deleting file" + file.getCanonicalPath());
                System.out.println("Error - " + e.getMessage() );
            }

            if (exists)
            {
                System.out.println("Error - problems deleting file" + file.getCanonicalPath());
            }
            context.getTransformer().setParameter("timeStamp", cSDF.format(new Date()));

            String currentXslFile = context.getStylesheet().getHref().toString();
            if (currentXslFile.startsWith("file:///"))
            {
                currentXslFile = currentXslFile.substring("file:///".length());
            }
            URL url = new URL(currentXslFile.replace('\\','/'));
            context.getTransformer().setParameter("currentXslFile", URLDecoder.decode(url.getFile(),"UTF-8"));

            if (cDegugTrace)
            {
                System.out.println("[Generating: "+file.getCanonicalPath()+"]");
                System.out.println("[xsl used:   "+currentXslFile.replace('\\','/')+"]");
            }

            try {
                super.write(context, elem);
            }
            catch( Exception e )
            {
                System.out.println("Error - writing to file" + file.getCanonicalPath());
                System.out.println("Error - " + e.getMessage() );
                e.printStackTrace();
                // eat it.
            }
            System.out.println("["+file.getAbsolutePath()+"]");
        }




        /**
         * Get the filename from the 'select' or the 'file' attribute.
         */
        public String getFilename(XSLProcessorContext context, ElemExtensionCall elem) throws java.net.MalformedURLException, FileNotFoundException, IOException, javax.xml.transform.TransformerException
        {
            String fileName;
            String fileNameExpr = ((ElemExtensionCall) elem).getAttribute("select", context.getContextNode(), context.getTransformer());
            if (null != fileNameExpr)
            {
                org.apache.xpath.XPathContext xctxt = context.getTransformer().getXPathContext();
                XPath myxpath = new XPath(fileNameExpr, elem, xctxt.getNamespaceContext(), XPath.SELECT);
                XObject xobj = myxpath.execute(xctxt, context.getContextNode(), elem);
                fileName = xobj.str();
                if ((null == fileName) || (fileName.length() == 0))
                {
                    fileName = elem.getAttribute("file", context.getContextNode(), context.getTransformer());
                }
            }
            else
            {
                fileName = elem.getAttribute("file", context.getContextNode(), context.getTransformer());
            }
            if (null == fileName)
            {
                context.getTransformer().getMsgMgr().error(elem, elem, context.getContextNode(), XSLTErrorResources.ER_REDIRECT_COULDNT_GET_FILENAME);
                //"Redirect extension: Could not get filename - file or select attribute must return vald string.");
            }
            return fileName;
        }
    }


    class NullStream extends OutputStream
    {
        @Override
        public void write(byte[] b) throws IOException
        {
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
        }

        @Override
        public void flush() throws IOException
        {
        }

        @Override
        public void write(int b) throws IOException {
        }

        @Override
        public void close() throws IOException
        {
        }
    }

    static class DbMessage {
        String      mMessageName;
        boolean     mDbSupport;
        Element     mMsgElement;
        String      mExtensionMessageName;


        DbMessage( String pMsgName, String pExtentionMsgName, Element pMsgElement, boolean pDbSupport  ) {
            mDbSupport = pDbSupport;
            mMessageName = pMsgName;
            mMsgElement = pMsgElement;
            mExtensionMessageName = pExtentionMsgName;
        }
    }

    class MessageSourceFile
    {
        String 			mXmlSourceFile;
        String 			mOutPath;
        String 			mPackage;
        boolean			mDebug;
        int             mMessageBase;

        MessageSourceFile( String pXmlSourceFile, String pOutPath,  String pPackage, int pMessageBase,  boolean pDebug )
        {
            mXmlSourceFile = pXmlSourceFile;
            mOutPath = pOutPath;
            mDebug = pDebug;
            mPackage = pPackage;
            mMessageBase = pMessageBase;
        }
    }
}
