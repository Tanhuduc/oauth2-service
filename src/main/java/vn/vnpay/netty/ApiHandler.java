package vn.vnpay.netty;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.bean.constant.ResponseStatus;
import vn.vnpay.bean.controller.response.Response;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.config.OAuthServiceConfig;
import vn.vnpay.controller.Controller;
import vn.vnpay.controller.GetTokenController;
import vn.vnpay.controller.GetUserInfoController;
import vn.vnpay.controller.RefreshTokenController;
import vn.vnpay.controller.RevokeTokenController;
import vn.vnpay.controller.UserLoginController;
import vn.vnpay.controller.VerifyTokenController;

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
                responseResult.setCode(ResponseStatus.API_INVALID.getCode());
                responseResult.setMessage(ResponseStatus.API_INVALID.getMessage());
                response(context, responseResult);
                return;
            }
            String uri = httpRequest.uri();
            String body = httpRequest.content().toString(StandardCharsets.UTF_8);
            if (serviceConfig.getUris().getLoginUri().equals(uri)) {
                log.info("Start api login");
                controller = UserLoginController.getInstance();
            }
            if (serviceConfig.getUris().getGetTokenUri().equals(uri)) {
                log.info("Start api get token");
                controller = GetTokenController.getInstance();
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
                responseResult.setCode(ResponseStatus.API_INVALID.getCode());
                responseResult.setMessage(ResponseStatus.API_INVALID.getMessage());
                response(context, responseResult);
                return;
            }
            HttpHeaders headers = httpRequest.headers();
            responseResult = controller.handler(body, headers);
        } catch (Exception e) {
            log.error("Handle api fails with exception: ", e);
            responseResult.setCode(ResponseStatus.UNKNOWN_ERROR.getCode());
            responseResult.setMessage(ResponseStatus.UNKNOWN_ERROR.getMessage());
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
