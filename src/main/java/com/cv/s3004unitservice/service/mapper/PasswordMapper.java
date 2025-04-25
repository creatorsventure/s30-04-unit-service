package com.cv.s3004unitservice.service.mapper;

import com.cv.s10coreservice.service.mapper.generic.GenericMapper;
import com.cv.s3002unitservicepojo.dto.PasswordDto;
import com.cv.s3002unitservicepojo.entity.Password;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordMapper extends GenericMapper<PasswordDto, Password> {
}
