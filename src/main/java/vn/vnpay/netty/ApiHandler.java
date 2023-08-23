package vn.vnpay.netty;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.config.OAuthServiceConfig;
import vn.vnpay.controller.GenerateTokenController;
import vn.vnpay.controller.Controller;
import vn.vnpay.controller.GetUserInfoController;
import vn.vnpay.controller.RefreshTokenController;
import vn.vnpay.controller.RevokeTokenController;
import vn.vnpay.controller.VerifyTokenController;
import vn.vnpay.netty.Error.Error;
import vn.vnpay.netty.response.Response;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@ChannelHandler.Sharable
public class ApiHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static ApiHandler instance;

    public static ApiHandler getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ApiHandler();
        }
        return instance;
    }

    private ApiHandler() {

    }

    private final Gson gson = GsonCommon.getInstance();
    private final OAuthServiceConfig serviceConfig = OAuthServiceConfig.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext context, FullHttpRequest httpRequest) {
        Response<Object> responseResult = new Response<>();
        Controller controller = null;
        try {
            if (!HttpMethod.POST.equals(httpRequest.method())) {
                log.info("HttpMethod does not match");
                responseResult.setCode(Error.API_INVALID.getCode());
                responseResult.setMessage(Error.API_INVALID.getMessage());
                response(context, responseResult);
                return;
            }
            String uri = httpRequest.uri();
            String body = httpRequest.content().toString(StandardCharsets.UTF_8);
            if (serviceConfig.getUris().getGenerateTokenUri().equals(uri)) {
                log.info("Start api generate token");
                controller = GenerateTokenController.getInstance();
            }
            if (serviceConfig.getUris().getVerifyTokenUri().equals(uri)) {
                log.info("Start api verify token");
                controller = VerifyTokenController.getInstance();
            }
            if (serviceConfig.getUris().getRefreshTokenUri().equals(uri)) {
                log.info("Start api refresh token");
                controller = RefreshTokenController.getInstance();
            }
            if (serviceConfig.getUris().getRevokeTokenUri().equals(uri)) {
                log.info("Start api revoke token");
                controller = RevokeTokenController.getInstance();
            }
            if (serviceConfig.getUris().getGetUserInfoUri().equals(uri)) {
                log.info("Start api get user info");
                controller = GetUserInfoController.getInstance();
            }
            if (Objects.isNull(controller)) {
                log.info("Uri dose not match");
                responseResult.setCode(Error.API_INVALID.getCode());
                responseResult.setMessage(Error.API_INVALID.getMessage());
                response(context, responseResult);
                return;
            }
            responseResult = controller.handler(body);
        } catch (Exception e) {
            log.error("Handle api fails with exception: ", e);
            responseResult.setCode(Error.UNKNOWN_ERROR.getCode());
            responseResult.setMessage(Error.UNKNOWN_ERROR.getMessage());
        } finally {
            response(context, responseResult);
            context.close();
        }

    }

    private void response(ChannelHandlerContext context, Response<Object> responseResult) {
        String result = gson.toJson(responseResult);
        FullHttpResponse httpResponse = createJsonResponse(result);
        context.writeAndFlush(httpResponse);
    }

    private FullHttpResponse createJsonResponse(String result) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.content().writeBytes(result.getBytes());
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
}
