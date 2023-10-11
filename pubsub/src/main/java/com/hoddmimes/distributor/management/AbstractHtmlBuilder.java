package com.hoddmimes.distributor.management;

import com.googlecode.jatl.HtmlWriter;
import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.tcpip.TcpIpClient;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHtmlBuilder extends HtmlWriter
{
    protected HttpExchange mCntx;
    protected MgmtServiceInterface mMgmtInterface;


    AbstractHtmlBuilder(HttpExchange cntx, MgmtServiceInterface mgmtInterface ) {
        super();
        mCntx = cntx;
        mMgmtInterface = mgmtInterface;
    }

    String getResponse() {
        StringWriter tStrWrtr = new StringWriter();
        String tResponse = this.write( tStrWrtr ).getBuffer().toString();
        return tResponse;
    }

    void tableHeader(String ... cols) {
        this.tr();
        for(String col : cols )  {
            this.th().text(col).end();
        }
        this.end();
    }

     String href( String text, String url) {
        return null;
    }

    void tableRow(td ... cols) {
        this.tr();
        for(td t : cols )  {
            if (t.url == null) {
                this.td().text(t.text).end();
            } else {
                this.td().a().href( t.url ).text(t.text).end().end();
            }
        }
        this.end();
    }

    MessageInterface transcive( MessageInterface pRqstMsg, String pHost, int pPort ) throws IOException
    {
        TpsTransciev tps = new TpsTransciev( pHost, pPort);
        return tps.transciev( pRqstMsg );
    }



    protected Map<String,String> getQueryParameters() {
        Map<String,String> tMap = new HashMap<>();
        String qrystr = this.mCntx.getRequestURI().getQuery();
        if (qrystr == null) {
            return tMap;
        }
        String[] tKeyValArr = qrystr.split("&");
        for( int i = 0; i < tKeyValArr.length; i++) {
            String[] tKeyVal = tKeyValArr[i].split("=");
            tMap.put(tKeyVal[0], tKeyVal[1]);
        }
        return tMap;
    }
    protected String str( int pValue) {
        return String.valueOf( pValue );
    }
    protected String str( long pValue) {
        return String.valueOf( pValue );
    }
    protected String str( double pValue, int decimals) {
        if (decimals == 0) {
            return String.valueOf( pValue );
        } else {
            String fmt = "%." + String.valueOf(decimals) + "f";
            return String.format( fmt, pValue );
        }

    }

    public void buildErrorResponse( String errorMessage, Exception e )
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        PrintWriter pw = new PrintWriter(baos);
        e.printStackTrace(pw);

        this.html();
            this.head();
                this.title("Distributor Management Error");
                this.style().text( getBodyStyle()).end();
            this.end();

            this.body();
                this.div().style("margin-left:20px;margin-top:40px;border:1px solid black;");
                    this.h1().style("color: #ff0000; text-align:center").text("Distributor Management Error").end();
                    this.p().style("margin-left:20px;margin-top:40px").text( errorMessage ).end();
                    this.p().style("margin-left:40px;margin-top:40px").text( baos.toString() ).end();
                this.end();
            this.end();
        this.end();
    }



    protected String getBodyStyle() {
        return  "body{font-family: arial, serif; background-color: #f5f3ed; color: #201375;}" +
                "table, th, td {border: 1px solid black; border-collapse: collapse;}" +
                "th, td {padding-left:10px;padding-right:10px;}" +
                "font-family: arial;";

    }

    public void test() {
        try {
            Files.write(Paths.get("/home/bertilsson/test.html"), getResponse().getBytes());
            System.out.println("/home/bertilsson/test.html written");
        }
        catch( IOException e) {e.printStackTrace();}
    }

    protected td td( Object obj, String url ) {
        return new td( obj, url);
    }

    protected td td( Object obj ) {
        return new td( obj, null);
    }

    class td {
        String text;
        String url;

        td( Object obj) {
            this( obj, null);
        }

        td( Object obj, String url) {
            this.text = String.valueOf(obj);
            this.url = url;
        }

        public String toString() {
            return "text: " + this.text + " url: " + this.url;
        }
    }
}
