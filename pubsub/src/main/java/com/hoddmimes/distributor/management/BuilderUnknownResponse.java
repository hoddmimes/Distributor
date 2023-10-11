package com.hoddmimes.distributor.management;

import com.sun.net.httpserver.HttpExchange;

public class BuilderUnknownResponse extends AbstractHtmlBuilder
{
    String mErrorPath;
    BuilderUnknownResponse(HttpExchange cntx, MgmtServiceInterface mgmtInterface, String errorPath) {
        super(cntx, mgmtInterface);
        mErrorPath = "\"" + errorPath + "\"";
    }

    @Override
    protected void build() {
        this.html();
            this.head();
                this.title("Unknown Distributor Management Request");
            this.end();
            this.body().style( getBodyStyle());
                this.div().style("margin-left:20px;margin-top:40px;border:1px solid black;");
                    this.h1().style("color: #ff0000; text-align:center").text("Unknown Distributor Management Request").end();
                    this.p().style("margin-left:20px;margin-top:40px").text("Unknown URI service path: " + mErrorPath).end();
                this.end();
        this.end();
    }



    public static void main(String[] args) {
        BuilderUnknownResponse r = new BuilderUnknownResponse(null, null, "/frotz");
        r.test();
    }
}

