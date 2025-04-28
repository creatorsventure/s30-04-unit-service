package com.cv.s3004unitservice.service.component;

import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.context.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String unitId = request.getHeader(ApplicationConstant.X_HEADER_UNIT_ID);
        String merchantId = request.getHeader(ApplicationConstant.X_HEADER_MERCHANT_ID);
        log.info("➡️ RequestContextInterceptor.preHandle unitId: {}, merchantId: {}", unitId, merchantId);
        if (unitId != null) {
            RequestContext.set("unitId", unitId);
        }
        if (merchantId != null) {
            RequestContext.set("merchantId", merchantId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        RequestContext.clear(); // cleanup
    }
}
