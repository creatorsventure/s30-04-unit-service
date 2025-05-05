package com.cv.s3004unitservice.service.feign;

import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s2002orgservicepojo.constant.ORGConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${app.org-service.name}")
public interface OrgServiceClient {

    @Cacheable(value = ApplicationConstant.APPLICATION_CONTEXT_CACHE, keyGenerator = "cacheKeyGenerator")
    @GetMapping("${app.org-service.contextPath}" + ORGConstant.APP_NAVIGATION_API_UNIT + ORGConstant.APP_NAVIGATION_API_UNIT_RESOLVE_ID)
    String resolveContext(@RequestParam("code") String code);

    @Cacheable(value = ApplicationConstant.APPLICATION_FEIGN_CACHE, keyGenerator = "cacheKeyGenerator")
    @GetMapping("${app.org-service.contextPath}" + ORGConstant.APP_NAVIGATION_API_OPTIONS)
    String resolveOptions(@RequestParam("code") String code);

}
