package com.cv.s3004unitservice.service.intrface;


import com.cv.s10coreservice.dto.AuthInfoDto;

public interface AuthenticationService {

    AuthInfoDto login(AuthInfoDto dto) throws Exception;

    AuthInfoDto refreshToken(AuthInfoDto dto) throws Exception;

    boolean logout(AuthInfoDto dto) throws Exception;
}
