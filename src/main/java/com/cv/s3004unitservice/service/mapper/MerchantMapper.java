package com.cv.s3004unitservice.service.mapper;

import com.cv.s10coreservice.service.mapper.generic.GenericMapper;
import com.cv.s3002unitservicepojo.dto.MerchantDto;
import com.cv.s3002unitservicepojo.entity.Merchant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MerchantMapper extends GenericMapper<MerchantDto, Merchant> {
}
