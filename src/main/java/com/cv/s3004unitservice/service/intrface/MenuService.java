package com.cv.s3004unitservice.service.intrface;


import com.cv.s10coreservice.service.intrface.generic.GenericService;
import com.cv.s3002unitservicepojo.dto.MenuDto;
import com.cv.s3002unitservicepojo.dto.MenuTreeDto;

import java.util.List;

public interface MenuService extends GenericService<MenuDto> {

    List<MenuTreeDto> readMenuAsTree() throws Exception;
}
