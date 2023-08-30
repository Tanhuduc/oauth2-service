package vn.vnpay.netty;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.MDC;

import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 30/08/2023
 **/
@ChannelHandler.Sharable
public class TokenInjectionHandler extends ChannelInboundHandlerAdapter {
    private static TokenInjectionHandler instance;

    public static TokenInjectionHandler getInstance() {
        if (Objects.isNull(instance)) {
            instance = new TokenInjectionHandler();
        }
        return instance;
    }

    private TokenInjectionHandler() {
    }

    private static final String TOKEN_ID_KEY = "tokenId:";
    private static final String SHOW_TOKEN_ID = "showTokenId";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            MDC.put(SHOW_TOKEN_ID, TOKEN_ID_KEY);
            MDC.put(TOKEN_ID_KEY, generateTokenId());
            super.channelRead(ctx, msg);
        } finally {
            MDC.remove(TOKEN_ID_KEY);
        }
    }

    private String generateTokenId() {
        return NanoIdUtils.randomNanoId();
    }
}