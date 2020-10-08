package com.shuyuan.judd.portalgateway.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jose.JWSObject;
import com.shuyuan.judd.client.model.merchant.User;
import com.shuyuan.judd.client.utils.JWTUtil;
import com.shuyuan.judd.portalgateway.config.IgnoreUrlsConfig;
import com.shuyuan.judd.portalgateway.constant.ServiceConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import spring.shuyuan.judd.base.model.Response;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.text.ParseException;

/**
 * 将登录用户的JWT转化成用户信息的全局过滤器
 * Created by macro on 2020/6/17.
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private PublicKey publicKey;
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("X-Token");
        log.debug("the X-Token is: {}", token);
        String path = exchange.getRequest().getURI().getPath();
        log.debug("request path: {}", path);
        if (StrUtil.isEmpty(token)) {
            if(ignoreUrlsConfig.getUrls().stream().anyMatch(path::matches)){
                log.info("match ignoreUrlsConfig");
                return chain.filter(exchange);
            } else {
                log.info("need authentication");
                ServerHttpResponse response = exchange.getResponse();
                Response res = Response.createNativeFail("非法请求");
                byte[] datas = JSONObject.toJSONString(res).getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(datas);
                response.setStatusCode(HttpStatus.OK);
                response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                return response.writeWith(Mono.just(buffer));
            }
        }
        try {
            //从token中解析用户信息并设置到Header中去
            JWSObject jwsObject = JWSObject.parse(token);
            User user = JWTUtil.getUserFromJWT(token, publicKey);
            log.info("AuthGlobalFilter.filter() user:{}", user);
            ServerHttpRequest request = exchange.getRequest().mutate().header(ServiceConstants.CURRENT_LOGIN_USER, user.toString()).build();
            exchange = exchange.mutate().request(request).build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
