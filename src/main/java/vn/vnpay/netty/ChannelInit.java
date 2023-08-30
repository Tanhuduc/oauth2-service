package vn.vnpay.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.Objects;

public class ChannelInit extends ChannelInitializer<SocketChannel> {
    private static ChannelInit instance;
    public static ChannelInit getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ChannelInit();
        }
        return instance;
    }
    private ChannelInit() {

    }
    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new HttpResponseEncoder());
        ch.pipeline().addLast(new HttpRequestDecoder());
        ch.pipeline().addLast(new HttpObjectAggregator(65536));
        ch.pipeline().addLast(TokenInjectionHandler.getInstance());
        ch.pipeline().addLast(ApiHandler.getInstance());
    }
}
