package com.cv.s3004unitservice.service.intrface;


import com.cv.s3002unitservicepojo.dto.PasswordDto;

public interface PasswordService {

    PasswordDto changePassword(PasswordDto dto) throws Exception;

    boolean activateAccount(String id) throws Exception;

    boolean forgotPassword(String userId) throws Exception;

    boolean resetPassword(PasswordDto dto) throws Exception;

    boolean resendPasswordEmail(String id) throws Exception;

}
