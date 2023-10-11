package com.hoddmimes.distributor.management;

import com.hoddmimes.distributor.generated.messages.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.text.DecimalFormat;

public class BuilderConnectionDetailsResponse extends AbstractHtmlBuilder
{
    long mConnectionId;
    long mDistributorId;

    BuilderConnectionDetailsResponse(HttpExchange cntx, MgmtServiceInterface mgmtInterface) {
        super(cntx, mgmtInterface);
        mConnectionId = Long.parseLong(this.getQueryParameters().get("CONN_ID"));
        mDistributorId = Long.parseLong(this.getQueryParameters().get("DIST_ID"));
    }


    @Override
    protected void build() {
        MgmtConnectionView connMsg = null;
        MgmtDistributorBdx bdx = mMgmtInterface.getDistributorBdx( mDistributorId );

        // Get detailed Distributor views
        try {
            MgmtConnectionRqst tRqstMsg = new MgmtConnectionRqst();
            tRqstMsg.setConnectionId(mConnectionId);
            MgmtConnectionRsp response = (MgmtConnectionRsp) transcive( tRqstMsg, bdx.getIpAddress(), bdx.getMgmtPort());
            connMsg = response.getConnection();
        }
        catch( IOException e) {
          buildErrorResponse("Failed to retrieve \"MgmtConnection\" info from [" + bdx.getIpAddress() +
                  ":" + String.valueOf(bdx.getMgmtPort()), e);
          return;
        }

        this.html();
            this.head();
                this.title("Connection [ " + connMsg.getMcaAddress() + ":" + connMsg.getMcaPort() + " ]");
                this.style().text( getBodyStyle()).end();
            this.end();
            this.body();
                this.div().style("margin-left:20px;margin-top:40px;");
                    this.h1().text("Connection [ " + connMsg.getMcaAddress() + ":" + connMsg.getMcaPort() + " ]").end();

                    MgmtConnectionTrafficInfo ti = connMsg.getTrafficInfo();

                    this.table().style("margin-left:20px;");
                        tableRow(td("Total bytes received"), td(scale(ti.getRcvTotalBytes())));
                        tableRow(td("Total bytes sent"), td(scale(ti.getXtaTotalBytes())));
                        tableRow(td("Total messages received"), td(scale(ti.getRcvTotalSegments())));
                        tableRow(td("Total messages sent"), td(scale(ti.getXtaTotalSegments())));
                        tableRow(td("Total updates received"), td(scale(ti.getRcvTotalUpdates())));
                        tableRow(td("Total update sent"), td(scale(ti.getXtaTotalUpdates())));
                    this.end();  // End table
                    this.br().br().br();

                    this.table().style("margin-left:20px;");
                    tableHeader("Attribute","Total","Current","Peak","PeakTime");
                        dataRateItemToTabRow(ti.getRcvBytes());
                        dataRateItemToTabRow(ti.getRcvBytes1min());
                        dataRateItemToTabRow(ti.getRcvBytes5min());
                        dataRateItemToTabRow(ti.getRcvSegments());
                        dataRateItemToTabRow(ti.getRcvSegments1min());
                        dataRateItemToTabRow(ti.getRcvSegments5min());
                        dataRateItemToTabRow(ti.getRcvUpdates());
                        dataRateItemToTabRow(ti.getRcvUpdates1min());
                        dataRateItemToTabRow(ti.getRcvUpdates5min());
                        dataRateItemToTabRow(ti.getXtaBytes());
                        dataRateItemToTabRow(ti.getXtaBytes1min());
                        dataRateItemToTabRow(ti.getXtaBytes5min());
                        dataRateItemToTabRow(ti.getXtaSegments());
                        dataRateItemToTabRow(ti.getXtaSegments1min());
                        dataRateItemToTabRow(ti.getXtaSegments5min());
                        dataRateItemToTabRow(ti.getXtaUpdates());
                        dataRateItemToTabRow(ti.getXtaUpdates1min());
                        dataRateItemToTabRow(ti.getXtaUpdates5min());
                    this.end();  // End table

                MgmtConnectionRetransmissionInfo ri = connMsg.getRetransmissionInfo();
                this.h2().text("Retransmissions").end();
                    this.table().style("margin-left:20px;");
                        tableRow(td("Total IN retransmission requests"), td( ri.getTotalInRqst()));
                        tableRow(td("Total OUT retransmission requests"), td(ri.getTotalOutRqst()));
                        tableRow(td("Total retransmission seen"), td(ri.getTotalSeenRqst()));
                    this.end();  // End table

                    if ((ri.getInHosts() != null) && (ri.getInHosts().length > 0)) {
                        this.h3().style("margin-left:20px;").text("Retransmissions IN").end();
                        this.table().style("margin-left:40px;");
                        for (int i = 0; i < ri.getInHosts().length; i++) {
                            tableRow(td(ri.getInHosts()[i]));
                        }
                        this.end();  // End table
                    }
                    if ((ri.getOutHosts() != null) && (ri.getOutHosts().length > 0)) {
                        this.h3().style("margin-left:20px;").text("Retransmissions OUT").end();
                        this.table().style("margin-left:40px;");
                        for (int i = 0; i < ri.getOutHosts().length; i++) {
                            tableRow(td(ri.getOutHosts()[i]));
                        }
                        this.end();  // End table
                    }

                String[] st = connMsg.getSubscriptionTopic();
                if ((st != null) && (st.length > 0)) {
                    this.h2().text("Subscriptions").end();
                    this.table().style("margin-left:20px;");
                    for (int i = 0; i < st.length; i++) {
                        this.tr().td().pre().text(st[i]).end().end().end();
                    }
                    this.end();  // End table
                }

                this.end(); // End Div
            this.end(); // End Body
        this.end(); // End HTML
    }

    void dataRateItemToTabRow( DataRateItem pDataRateItem ) {
        tableRow(
                td( pDataRateItem.getAttribute()),
                td( scale( pDataRateItem.getTotal())),
                td( scale(pDataRateItem.getCurrValue())),
                td( scale( pDataRateItem.getPeakValue())),
                td( pDataRateItem.getPeakTime()));
    }


    private String scale( long pValue) {
        if ( pValue < 1024) {
            return String.valueOf(pValue);
        } else if (pValue < (1024 * 1024)) {
            double d = (double) pValue / 1024.0;
            return String.format("%.2f",d) + " K";
        } else {
            double d = (double) pValue / (1024.0 * 1024.0);
            return String.format("%.2f",d) + " M";
        }
    }
}

