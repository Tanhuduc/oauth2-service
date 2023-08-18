package vn.vnpay.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.config.OAuthServiceConfig;

import java.util.Objects;

/**
 * @author: DucTN
 * Created: 02/08/2023
 **/
@Slf4j
public class NettyService {
    private final ServerBootstrap serverBootstrap;
    private final OAuthServiceConfig serviceConfig;

    private static final EventLoopGroup BOSS_GROUP = new NioEventLoopGroup();
    private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup();

    private static NettyService instance;

    private NettyService(ServerBootstrap serverBootstrap, OAuthServiceConfig serviceConfig) {
        this.serverBootstrap = serverBootstrap;
        this.serviceConfig = serviceConfig;
    }

    public static NettyService getInstance() {
        if (Objects.isNull(instance)) {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(BOSS_GROUP, WORKER_GROUP)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(ChannelInit.getInstance());
            instance = new NettyService(bootstrap, OAuthServiceConfig.getInstance());
        }
        return instance;
    }

    public void start() throws InterruptedException {
        try {
            log.info("Service is starting");
            Channel channel = this.serverBootstrap.bind(this.serviceConfig.getHost(), this.serviceConfig.getPort()).sync().channel();
            channel.closeFuture().sync();
            log.info("Service has started!");
        } finally {
            close();
        }
    }

    private void close() {
        BOSS_GROUP.shutdownGracefully();
        WORKER_GROUP.shutdownGracefully();
    }

}
