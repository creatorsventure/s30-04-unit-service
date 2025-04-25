package com.cv.s3004unitservice.service.mapper;

import com.cv.s10coreservice.service.mapper.generic.GenericMapper;
import com.cv.s3002unitservicepojo.dto.UserDetailDto;
import com.cv.s3002unitservicepojo.entity.UserDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDetailMapper extends GenericMapper<UserDetailDto, UserDetail> {
}
