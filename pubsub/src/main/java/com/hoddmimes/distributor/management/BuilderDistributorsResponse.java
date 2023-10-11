package com.hoddmimes.distributor.management;

import com.hoddmimes.distributor.generated.messages.MgmtDistributorBdx;
import com.sun.net.httpserver.HttpExchange;
import java.util.List;

public class BuilderDistributorsResponse extends AbstractHtmlBuilder
{

    BuilderDistributorsResponse(HttpExchange cntx, MgmtServiceInterface mgmtInterface) {
        super(cntx, mgmtInterface);

    }

    @Override
    protected void build() {
        this.html();
            this.head();
                this.title("Distributors");
                this.style().text( getBodyStyle()).end();
            this.end();
            this.body();
                this.div().style("margin-left:20px;margin-top:40px;");
                    this.h1().text("Distributors").end();

                    List<MgmtDistributorBdx> tDistList = mMgmtInterface.getMgmtDistributorBdx();

                    this.table().style("margin-left:20px;");
                        tableHeader("Appl Name","Host","IP Address","Started","Connections","Publishers","Subscribers","Subscriptions","Retrans In","RetransOut");
                        for( MgmtDistributorBdx bdx : tDistList) {
                            String url = "http://" + this.mCntx.getRequestHeaders().get("Host").get(0) + "/DISTRIBUTOR?ID=" + String.valueOf(bdx.getDistributorId());

                            tableRow(
                              td(bdx.getApplicationName(), url),
                              td(bdx.getHostName(), url),
                              td(bdx.getIpAddress(), url),
                              td(bdx.getStartTime()),
                              td(bdx.getConnections()),
                              td(bdx.getPublishers()),
                              td(bdx.getSubscribers()),
                              td(bdx.getSubscriptions()),
                              td(bdx.getRetransIn()),
                              td(bdx.getRetransOut()));
                        }
                    this.end();
                this.end();
            this.end();
        this.end();
    }
}

