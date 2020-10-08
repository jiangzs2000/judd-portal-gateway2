package com.shuyuan.judd.portalgateway.config;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 网关白名单配置
 * Created by macro on 2020/6/17.
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Component
@ConfigurationProperties(prefix="secure.ignore")
public class IgnoreUrlsConfig {
    private List<String> urls;
    @PostConstruct
    public void init(){
        urls = urls.stream().map(p->p.replaceAll("\\*",".*")).collect(Collectors.toList());
        log.debug(JSONObject.toJSONString(urls));
    }
}
