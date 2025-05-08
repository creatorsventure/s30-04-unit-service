package com.cv.s3004unitservice.service.intrface;

import com.cv.s10coreservice.service.intrface.generic.GenericService;
import com.cv.s3002unitservicepojo.dto.MerchantDto;

import java.util.Map;

public interface MerchantService extends GenericService<MerchantDto> {

    Map<String, String> resolveOrgMcc() throws Exception;
}
