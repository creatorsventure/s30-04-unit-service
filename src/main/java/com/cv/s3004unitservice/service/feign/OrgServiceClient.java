package com.cv.s3004unitservice.service.feign;

import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s2002orgservicepojo.constant.ORGConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "localhost:8020")
public interface OrgServiceClient {

    @Cacheable(value = ApplicationConstant.APPLICATION_CONTEXT_CACHE, keyGenerator = "cacheKeyGenerator")
    @GetMapping("${app.org-service.contextPath}" + ORGConstant.APP_NAVIGATION_API_UNIT + ORGConstant.APP_NAVIGATION_API_RESOLVE_UNIT_ID)
    String resolveContext(@RequestParam String code);

    @Cacheable(value = ApplicationConstant.APPLICATION_FEIGN_CACHE, keyGenerator = "cacheKeyGenerator")
    @GetMapping("${app.org-service.contextPath}" + ORGConstant.APP_NAVIGATION_API_UNIT + ORGConstant.APP_NAVIGATION_API_RESOLVE_OPTIONS)
    String resolveOrgOptions(@RequestParam("unitId") String unitId);

    @Cacheable(value = ApplicationConstant.APPLICATION_FEIGN_CACHE, keyGenerator = "cacheKeyGenerator")
    @GetMapping("${app.org-service.contextPath}" + ORGConstant.APP_NAVIGATION_API_UNIT + ORGConstant.APP_NAVIGATION_API_RESOLVE_UNIT_SCHEME)
    String resolveOrgUnitScheme(@RequestParam("unitId") String unitId);

    @Cacheable(value = ApplicationConstant.APPLICATION_FEIGN_CACHE, keyGenerator = "cacheKeyGenerator")
    @GetMapping("${app.org-service.contextPath}" + ORGConstant.APP_NAVIGATION_API_MERCHANT_CATEGORY + ApplicationConstant.APP_NAVIGATION_METHOD_READ_ID_NAME_MAP)
    String resolveOrgMcc();

    @Cacheable(value = ApplicationConstant.APPLICATION_FEIGN_CACHE, keyGenerator = "cacheKeyGenerator")
    @GetMapping("${app.org-service.contextPath}" + ORGConstant.APP_NAVIGATION_API_UNIT + ApplicationConstant.APP_NAVIGATION_METHOD_READ_ONE)
    String resolveOrgUnit(@RequestParam("id") String id);

    @Cacheable(value = ApplicationConstant.APPLICATION_FEIGN_CACHE, keyGenerator = "cacheKeyGenerator")
    @GetMapping("${app.org-service.contextPath}" + ORGConstant.APP_NAVIGATION_API_UNIT + ORGConstant.APP_NAVIGATION_API_RESOLVE_UNIT_ID_NAME_MAPS)
    String resolveOrgUnitIdNameMaps(@RequestParam("unitId") String unitId);

}
