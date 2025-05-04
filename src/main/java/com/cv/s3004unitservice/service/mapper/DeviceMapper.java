package com.cv.s3004unitservice.service.mapper;

import com.cv.s10coreservice.service.mapper.generic.GenericMapper;
import com.cv.s3002unitservicepojo.dto.DeviceDto;
import com.cv.s3002unitservicepojo.entity.Device;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceMapper extends GenericMapper<DeviceDto, Device> {
}
