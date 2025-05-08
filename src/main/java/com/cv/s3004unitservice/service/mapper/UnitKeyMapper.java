package com.cv.s3004unitservice.service.mapper;

import com.cv.s10coreservice.service.mapper.generic.GenericMapper;
import com.cv.s3002unitservicepojo.dto.UnitKeyDto;
import com.cv.s3002unitservicepojo.entity.UnitKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UnitKeyMapper extends GenericMapper<UnitKeyDto, UnitKey> {

    @Mapping(target = "unitPrivateKeyPassword", ignore = true)
    UnitKey toEntity(UnitKeyDto dto);
}
