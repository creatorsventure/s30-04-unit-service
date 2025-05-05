package com.cv.s3004unitservice.service.mapper;

import com.cv.s10coreservice.service.mapper.generic.GenericMapper;
import com.cv.s2002orgservicepojo.dto.OptionsDto;
import com.cv.s3002unitservicepojo.dto.UnitOptionsDto;
import com.cv.s3002unitservicepojo.entity.UnitOptions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UnitOptionsMapper extends GenericMapper<UnitOptionsDto, UnitOptions> {

    @Mapping(target = "id", ignore = true)
    UnitOptions toUnitOptionsEntity(OptionsDto dto);
}
