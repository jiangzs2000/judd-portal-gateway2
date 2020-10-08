package com.shuyuan.judd.portalgateway.feignservice;

import com.shuyuan.judd.portalgateway.constant.ServiceConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import spring.shuyuan.judd.base.model.Response;

@FeignClient(value = ServiceConstants.PLATCERTIFICATION_SERVICE_NAME, path = "/openapi/rsa", decode404 = true)
public interface PlatCertificationFeignService {
    @GetMapping("/publicKey")
    Response<String> getPublicKey();
}
