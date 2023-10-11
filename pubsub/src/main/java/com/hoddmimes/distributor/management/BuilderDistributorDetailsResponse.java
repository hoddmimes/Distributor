package com.hoddmimes.distributor.management;

import com.hoddmimes.distributor.generated.messages.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class BuilderDistributorDetailsResponse extends AbstractHtmlBuilder
{
    long mDistributedId;

    BuilderDistributorDetailsResponse(HttpExchange cntx, MgmtServiceInterface mgmtInterface) {
        super(cntx, mgmtInterface);
        mDistributedId = Long.parseLong(this.getQueryParameters().get("ID"));

    }


    @Override
    protected void build() {
        MgmtDistributorView distMsg = null;
        MgmtDistributorBdx bdx = mMgmtInterface.getDistributorBdx( mDistributedId);

        // Get detailed Distributor views
        try {
            MgmtDistributorRsp response = (MgmtDistributorRsp) transcive( new MgmtDistributorRqst(), bdx.getIpAddress(), bdx.getMgmtPort());
            distMsg = response.getDistributor();
        }
        catch( IOException e) {
          buildErrorResponse("Failed to retrieve \"MgmtDistributor\" info from [" + bdx.getIpAddress() +
                  ":" + String.valueOf(bdx.getMgmtPort()), e);
          return;
        }

        this.html();
            this.head();
                this.title("Distributors [ " + bdx.getApplicationName() + " ]");
                this.style().text( getBodyStyle()).end();
            this.end();
            this.body();
                this.div().style("margin-left:20px;margin-top:40px;");
                    this.h1().text("Distributors [ " + bdx.getApplicationName() + " ]").end();

                    this.table().style("margin-left:20px;");
                        tableRow(td("Application Name"), td(distMsg.getApplicationName()));
                        tableRow(td("IP Address"), td(distMsg.getIpAddress()));
                        tableRow(td("Host Name"), td(distMsg.getHostName()));
                        tableRow(td("Distributor Id"), td(distMsg.getDistributorId()));
                        tableRow(td("Start Time"), td(distMsg.getStartTime()));
                        tableRow(td("Memory Max"), td(distMsg.getMemMax()));
                        tableRow(td("Memory Used"), td(distMsg.getMemUsed()));
                        tableRow(td("Memory Free"), td(distMsg.getMemFree()));
                    this.end();

                    this.h2().text("Connections").end();
                    this.table().style("margin-left:20px;");
                        tableHeader("MC Address",
                                           "MC Port",
                                           "Publishers",
                                           "Subscribers",
                                           "Subscriptions",
                                           "Retrans In",
                                           "Retrans Out");

                        for( MgmtConnection conn : distMsg.getConnections()) {
                            String url = "http://" + this.mCntx.getRequestHeaders().get("Host").get(0) +
                                         "/CONNECTION?CONN_ID=" + String.valueOf(conn.getConnectionId()) +
                                         "&DIST_ID=" + String.valueOf(distMsg.getDistributorId());
                            tableRow(
                              td(conn.getMcaAddress(), url),
                              td(conn.getMcaPort(), url),
                              td(bdx.getPublishers()),
                              td(bdx.getSubscribers()),
                              td(bdx.getSubscriptions()),
                              td(bdx.getRetransIn()),
                              td(bdx.getRetransOut()));
                        }
                    this.end();  // End table
                this.end(); // End Div
            this.end(); // End Body
        this.end(); // End HTML
    }
}

