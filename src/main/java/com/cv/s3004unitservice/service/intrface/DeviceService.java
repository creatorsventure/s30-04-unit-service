package com.cv.s3004unitservice.service.intrface;

import com.cv.s10coreservice.dto.IdNameMapDto;
import com.cv.s10coreservice.service.intrface.generic.GenericService;
import com.cv.s3002unitservicepojo.dto.DeviceDto;

public interface DeviceService extends GenericService<DeviceDto> {

    IdNameMapDto resolveOrgUnitIdNameMaps() throws Exception;
}
