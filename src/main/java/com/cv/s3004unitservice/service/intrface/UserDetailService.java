package com.cv.s3004unitservice.service.intrface;

import com.cv.s10coreservice.service.intrface.generic.GenericService;
import com.cv.s3002unitservicepojo.dto.UserDetailDto;

public interface UserDetailService extends GenericService<UserDetailDto> {

    Long getCount() throws Exception;
}
