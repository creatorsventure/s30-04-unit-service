package com.cv.s3004unitservice.service.mapper;

import com.cv.s3002unitservicepojo.dto.MenuDto;
import com.cv.s3002unitservicepojo.entity.Menu;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    // @Mapping(source = "menu.module.id", target = "moduleId")
    com.cv.s3002unitservicepojo.dto.MenuDto toDto(Menu menu);

    Menu toEntity(MenuDto menuDto);

}
