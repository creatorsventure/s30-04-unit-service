package com.cv.s3004unitservice.service.intrface;


import com.cv.s3002unitservicepojo.dto.SignupDto;

public interface SignupService {

    boolean signup(SignupDto signupDto) throws Exception;

}
