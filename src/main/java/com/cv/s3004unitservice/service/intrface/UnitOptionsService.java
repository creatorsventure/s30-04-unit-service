package com.cv.s3004unitservice.service.intrface;

import com.cv.s10coreservice.service.intrface.generic.GenericService;
import com.cv.s2002orgservicepojo.dto.OptionsDto;
import com.cv.s3002unitservicepojo.dto.UnitOptionsDto;

public interface UnitOptionsService extends GenericService<UnitOptionsDto> {

    OptionsDto resolveOrgOptions() throws Exception;
}
