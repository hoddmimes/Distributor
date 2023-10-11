
package com.hoddmimes.distributor.management;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DistributorHttpService implements HttpHandler
{
    static final Logger cLogger = LogManager.getLogger( DistributorHttpService.class.getSimpleName());
    MgmtServiceInterface mMgmtInterface;
    HttpServer  mHttpServer;

    DistributorHttpService( int httpPort, MgmtServiceInterface mgmtInterface  ) {
        mMgmtInterface = mgmtInterface;
        try {
            mHttpServer = HttpServer.create(new InetSocketAddress(httpPort), 0);
            mHttpServer.createContext("/", this);
            mHttpServer.setExecutor(null); // creates a default executor
            mHttpServer.start();
        }
        catch( IOException e) {
            cLogger.fatal("failed to start Management Http Server", e);

        }
    }

    @Override
    public void handle(HttpExchange cntx) throws IOException {
        String tPath = cntx.getRequestURI().getPath().toUpperCase();
        String tResponse = null;
        if (tPath.equals("/") || (tPath.equals("/DISTRIBUTORS"))) {
           tResponse = new BuilderDistributorsResponse( cntx, mMgmtInterface ).getResponse();
        } else if (tPath.equals("/DISTRIBUTOR")) {
            tResponse = new BuilderDistributorDetailsResponse( cntx, mMgmtInterface ).getResponse();
        } else if (tPath.equals("/CONNECTION")) {
            tResponse = new BuilderConnectionDetailsResponse( cntx, mMgmtInterface ).getResponse();
        }
        else {
           tResponse = new BuilderUnknownResponse( cntx, mMgmtInterface, tPath).getResponse();
        }

        cntx.sendResponseHeaders(200, tResponse.length());
        OutputStream os = cntx.getResponseBody();
        os.write(tResponse.getBytes());
        os.close();
    }
}
