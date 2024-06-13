package com.example.rpc.server.netty.http;

import cn.hutool.core.util.StrUtil;
import com.example.rpc.model.RpcRequest;
import com.example.rpc.serializer.JsonSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iumyxF
 * @description:
 * @date 2024/5/31 9:19
 */
public class PostRequestHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(PostRequestHandler.class);

    @Override
    public RpcRequest handle(FullHttpRequest request) {
        String contentType = this.getContentType(request.headers());
        if (StrUtil.equalsIgnoreCase(HttpHeaderValues.APPLICATION_JSON, contentType)) {
            String requestParamsJson = request.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
            try {
                return JsonSerializer.OBJECT_MAPPER.readValue(requestParamsJson, RpcRequest.class);
            } catch (JsonProcessingException e) {
                log.error("parse request params error ", e);
                return null;
            }
        } else {
            log.error("only receive application/json type data");
            return null;
        }
    }

    private String getContentType(HttpHeaders headers) {
        String typeStr = headers.get(HttpHeaderNames.CONTENT_TYPE);
        String[] list = typeStr.split(";");
        return list[0];
    }
}