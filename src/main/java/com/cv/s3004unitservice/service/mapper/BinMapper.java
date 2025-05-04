package com.cv.s3004unitservice.service.mapper;

import com.cv.s10coreservice.service.mapper.generic.GenericMapper;
import com.cv.s3002unitservicepojo.dto.BinDto;
import com.cv.s3002unitservicepojo.entity.Bin;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BinMapper extends GenericMapper<BinDto, Bin> {
}
