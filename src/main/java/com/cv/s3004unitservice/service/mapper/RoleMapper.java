package com.cv.s3004unitservice.service.mapper;

import com.cv.s10coreservice.service.mapper.generic.GenericMapper;
import com.cv.s3002unitservicepojo.dto.RoleDto;
import com.cv.s3002unitservicepojo.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper extends GenericMapper<RoleDto, Role> {
}
