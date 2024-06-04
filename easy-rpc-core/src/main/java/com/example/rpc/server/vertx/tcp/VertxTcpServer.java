package com.example.rpc.server.vertx.tcp;

import com.example.rpc.server.NetServer;
import com.example.rpc.server.vertx.http.VertxHttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/3 9:34
 */
public class VertxTcpServer implements NetServer {

    private static final Logger log = LoggerFactory.getLogger(VertxHttpServer.class);

    private final static Vertx VERTX = Vertx.vertx();

    @Override
    public void start(int port) {
        NetServerOptions options = new NetServerOptions()
                .setPort(port);
        VERTX.createNetServer(options)
                .connectHandler(new VertxTcpServerHandler())
                .listen(res -> {
                    if (res.succeeded()) {
                        log.info("tcp server started on port = {}", port);
                    } else {
                        log.error("tcp server started unsuccessful , port = {} , cause = {}", port, res.cause());
                    }
                });
    }

    @Override
    public void stop() {
        if (null != VERTX) {
            VERTX.close();
        }
    }
}
