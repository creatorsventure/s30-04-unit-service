package com.cv.s3004unitservice.service.component;

import com.cv.s10coreservice.config.props.AppProperties;
import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.context.RequestContext;
import com.cv.s10coreservice.dto.ContextParamDto;
import com.cv.s10coreservice.exception.ExceptionComponent;
import com.cv.s10coreservice.service.component.APIServiceCaller;
import com.cv.s2002orgservicepojo.constant.ORGConstant;
import com.cv.s3004unitservice.service.feign.OrgServiceClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@AllArgsConstructor
@Slf4j
@Component
public class RequestContextInterceptor implements HandlerInterceptor {

    private final AppProperties appProperties;
    private final ExceptionComponent exceptionComponent;
    private final APIServiceCaller apiServiceCaller;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            return true; // skip for CORS preflight
        }
        String unitId = request.getHeader(ApplicationConstant.X_HEADER_UNIT_ID);
        String unitCode = request.getHeader(ApplicationConstant.X_HEADER_UNIT_CODE); // add unitCode header extraction

        log.info("➡️ RequestContextInterceptor.preHandle unitId: {}, unitCode: {}", unitId, unitCode);

        try {
            // If unitId is not available but unitCode is provided
            if (!StringUtils.hasText(unitId) && StringUtils.hasText(unitCode)) {
                String uri = (appProperties.getOrgService().getContextPath() + ORGConstant.APP_NAVIGATION_API_UNIT + ORGConstant.APP_NAVIGATION_API_UNIT_RESOLVE_ID);
                log.info("🔍 UnitId not found, trying to fetch using UnitCode={}, path={}", unitCode, appProperties.getOrgService().getName() + uri);
                var dto = apiServiceCaller.call(OrgServiceClient.class,
                        client -> client.resolveContext(unitCode),
                        ContextParamDto.class);
                if (dto != null && StringUtils.hasText(dto.getUnitId())) {
                    log.info("✅ Retrieved UnitId={} for UnitCode={}", dto.getUnitId(), unitCode);
                    unitId = dto.getUnitId();
                } else {
                    throw exceptionComponent.expose("⚠️ Unable to get context param.", false);
                }
            }

            // Set values into RequestContext
            if (StringUtils.hasText(unitId)) {
                RequestContext.setUnitId(unitId);
            } else {
                throw exceptionComponent.expose("app.message.id.extraction.failure", true);
            }

        } catch (Exception ex) {
            log.error("❌ Failed to dynamically fetch UnitId using UnitCode={}, Error={}", unitCode, ex.getMessage(), ex);
            throw exceptionComponent.expose("app.message.id.extraction.failure", true);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        RequestContext.clear(); // cleanup
    }
}
