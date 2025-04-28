package com.cv.s3004unitservice.exception;

import com.cv.s3004unitservice.util.StaticUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<Object> handleException(Exception e) {
        log.error("GlobalExceptionHandler.handleException {}", ExceptionUtils.getMessage(e));
        return StaticUtil.getFailureResponse(e);
    }
}
