package com.cv.s3004unitservice.controller;

import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.dto.AuthInfoDto;
import com.cv.s10coreservice.enumeration.APIResponseType;
import com.cv.s10coreservice.exception.ExceptionComponent;
import com.cv.s3004unitservice.service.intrface.AuthenticationService;
import com.cv.s3004unitservice.util.StaticUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApplicationConstant.APP_NAVIGATION_API_AUTHENTICATION)
@AllArgsConstructor
@Slf4j
public class AuthenticationController {

    private AuthenticationService authenticationService;
    private ExceptionComponent exceptionComponent;

    @PostMapping(ApplicationConstant.APP_NAVIGATION_API_AUTHENTICATION_LOGIN)
    public ResponseEntity<Object> login(@RequestBody @Valid AuthInfoDto dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.info("AuthenticationController.login {}", result.getAllErrors());
                return StaticUtil.getFailureResponse(result);
            } else if (!StringUtils.hasText(dto.getUserId()) || !StringUtils.hasText(dto.getPassword())) {
                return StaticUtil.getFailureResponse(exceptionComponent.expose("app.message.failure.credential.invalid", true));
            }
            return StaticUtil.getSuccessResponse(authenticationService.login(dto), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("AuthenticationController.login {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @PostMapping(ApplicationConstant.APP_NAVIGATION_API_AUTHENTICATION_REFRESH_TOKEN)
    public ResponseEntity<Object> refreshToken(@RequestBody @Valid AuthInfoDto dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.info("AuthenticationController.refreshToken {}", result.getAllErrors());
                return StaticUtil.getFailureResponse(result);
            } else if (!StringUtils.hasText(dto.getUserId()) || !StringUtils.hasText(dto.getRefreshToken())) {
                return StaticUtil.getFailureResponse(exceptionComponent.expose("app.message.failure.credential.invalid", true));
            }
            return StaticUtil.getSuccessResponse(authenticationService.refreshToken(dto), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("AuthenticationController.refreshToken {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @PostMapping(ApplicationConstant.APP_NAVIGATION_API_AUTHENTICATION_LOGOUT)
    public ResponseEntity<Object> logout(@RequestBody @Valid AuthInfoDto dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.info("AuthenticationController.logout {}", result.getAllErrors());
                return StaticUtil.getFailureResponse(result);
            } else if (!StringUtils.hasText(dto.getUserId()) || !StringUtils.hasText(dto.getRefreshToken())) {
                return StaticUtil.getFailureResponse(exceptionComponent.expose("app.message.failure.credential.invalid", true));
            }
            return StaticUtil.getSuccessResponse(authenticationService.logout(dto), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("AuthenticationController.logout {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }
}
