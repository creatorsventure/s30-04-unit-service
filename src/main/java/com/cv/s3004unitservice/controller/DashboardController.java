package com.cv.s3004unitservice.controller;

import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.enumeration.APIResponseType;
import com.cv.s3004unitservice.service.intrface.DashboardService;
import com.cv.s3004unitservice.util.StaticUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApplicationConstant.APP_NAVIGATION_API_DASHBOARD)
@AllArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService service;

    @GetMapping
    public ResponseEntity<Object> getCount() {
        try {
            return StaticUtil.getSuccessResponse(service.getCount(), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("DashboardController.getCount {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

}
