package com.cv.s3004unitservice.service.intrface;

import com.cv.s10coreservice.service.intrface.generic.GenericService;
import com.cv.s3002unitservicepojo.dto.BinDto;

import java.util.Map;

public interface BinService extends GenericService<BinDto> {

    Map<String, String> resolveOrgUnitScheme() throws Exception;
}
