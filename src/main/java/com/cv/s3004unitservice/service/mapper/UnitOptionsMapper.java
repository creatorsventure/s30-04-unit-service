package com.cv.s3004unitservice.service.mapper;

import com.cv.s10coreservice.service.mapper.generic.GenericMapper;
import com.cv.s3002unitservicepojo.dto.UnitOptionsDto;
import com.cv.s3002unitservicepojo.entity.UnitOptions;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnitOptionsMapper extends GenericMapper<UnitOptionsDto, UnitOptions> {
}
