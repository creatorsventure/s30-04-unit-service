package com.cv.s3004unitservice.service.intrface;


import com.cv.s10coreservice.dto.VerifyOTPDto;
import com.cv.s10coreservice.dto.VerifySignupDto;
import com.cv.s3002unitservicepojo.dto.SignupDto;

public interface SignupService {

    boolean signup(SignupDto signupDto) throws Exception;

    VerifySignupDto verifySignup(String payload) throws Exception;

    boolean verifyOTP(VerifyOTPDto dto) throws Exception;
}
