package com.cv.s3004unitservice.controller;

import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.dto.VerifyOTPDto;
import com.cv.s10coreservice.enumeration.APIResponseType;
import com.cv.s3002unitservicepojo.dto.SignupDto;
import com.cv.s3004unitservice.service.intrface.SignupService;
import com.cv.s3004unitservice.util.StaticUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApplicationConstant.APP_NAVIGATION_API_SIGNUP)
@AllArgsConstructor
@Slf4j
public class SignupController {

    private SignupService signupService;

    @PostMapping
    public ResponseEntity<Object> signup(@RequestBody @Valid SignupDto dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.info("SignupController.signup {}", result.getAllErrors());
                return StaticUtil.getFailureResponse(result);
            }
            return StaticUtil.getSuccessResponse(signupService.signup(dto), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("SignupController.signup {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @GetMapping(ApplicationConstant.APP_NAVIGATION_API_VERIFY_SIGNUP)
    public ResponseEntity<Object> verifySignup(@RequestParam String payload) {
        try {
            return StaticUtil.getSuccessResponse(signupService.verifySignup(payload), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("SignupController.verifySignup {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @PostMapping(ApplicationConstant.APP_NAVIGATION_API_VERIFY_OTP)
    public ResponseEntity<Object> verifyOTP(@RequestBody @Valid VerifyOTPDto dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.info("SignupController.verifyOTP {}", result.getAllErrors());
                return StaticUtil.getFailureResponse(result);
            }
            return StaticUtil.getSuccessResponse(signupService.verifyOTP(dto), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("SignupController.verifyOTP {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

}
