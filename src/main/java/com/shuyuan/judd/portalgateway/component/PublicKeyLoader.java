package com.shuyuan.judd.portalgateway.component;

import com.shuyuan.judd.portalgateway.feignservice.PlatCertificationFeignService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.shuyuan.judd.base.utils.RSAKeyUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
@Configuration
public class PublicKeyLoader {
    @Autowired
    private PlatCertificationFeignService platCertificationFeignService;
    @Bean
    public PublicKey publicKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        log.debug("-----------------------------------init_data--------------------------------------------");
        //return RSAKeyUtil.readPublicKeyFromCer(PublicKeyLoader.class.getClassLoader().getResourceAsStream("jwt.cer"));
        String pubkeyStr = platCertificationFeignService.getPublicKey().getData();
        log.debug("----------pubkey loaded {}-------------", pubkeyStr);
        return RSAKeyUtil.strToPublicKey(pubkeyStr);
    }
}
