package com.cv.s3004unitservice.controller;

import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.enumeration.APIResponseType;
import com.cv.s3002unitservicepojo.dto.PasswordDto;
import com.cv.s3004unitservice.service.intrface.PasswordService;
import com.cv.s3004unitservice.util.StaticUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApplicationConstant.APP_NAVIGATION_API_PASSWORD)
@AllArgsConstructor
@Slf4j
public class PasswordController {

    private PasswordService service;

    @PostMapping
    public ResponseEntity<Object> changePassword(@RequestBody @Valid PasswordDto dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.info("PasswordController.changePassword {}", result.getAllErrors());
                return StaticUtil.getFailureResponse(result);
            }
            return StaticUtil.getSuccessResponse(service.changePassword(dto), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("PasswordController.changePassword {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @GetMapping(ApplicationConstant.APP_NAVIGATION_API_PASSWORD_ACTIVATE_ACCOUNT)
    public ResponseEntity<Object> activateAccount(@RequestParam String id) throws Exception {
        try {
            return StaticUtil.getSuccessResponse(service.activateAccount(id), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("PasswordController.activateAccount {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @GetMapping(ApplicationConstant.APP_NAVIGATION_API_PASSWORD_FORGOT)
    public ResponseEntity<Object> forgotPassword(@RequestParam String userId) throws Exception {
        try {
            return StaticUtil.getSuccessResponse(service.forgotPassword(userId), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("PasswordController.forgotPassword {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @PostMapping(ApplicationConstant.APP_NAVIGATION_API_PASSWORD_RESET)
    public ResponseEntity<Object> resetPassword(@RequestBody @Valid PasswordDto dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.info("PasswordController.resetPassword {}", result.getAllErrors());
                return StaticUtil.getFailureResponse(result);
            }
            return StaticUtil.getSuccessResponse(service.resetPassword(dto), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("PasswordController.resetPassword {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @GetMapping(ApplicationConstant.APP_NAVIGATION_API_PASSWORD_RESEND_EMAIL)
    public ResponseEntity<Object> resendPasswordEmail(@RequestParam String id) {
        try {
            return StaticUtil.getSuccessResponse(service.resendPasswordEmail(id), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("PasswordController.resendPasswordEmail {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

}
