package com.cv.s3004unitservice.controller;

import com.cv.s10coreservice.constant.ApplicationConstant;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                log.info("AuthenticationController.signup {}", result.getAllErrors());
                return StaticUtil.getFailureResponse(result);
            }
            return StaticUtil.getSuccessResponse(signupService.signup(dto), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("AuthenticationController.signup {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

}
