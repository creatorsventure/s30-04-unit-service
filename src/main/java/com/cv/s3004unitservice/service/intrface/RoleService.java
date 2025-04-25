package com.cv.s3004unitservice.service.intrface;

import com.cv.s10coreservice.service.intrface.generic.GenericService;
import com.cv.s3002unitservicepojo.dto.RoleDto;
import com.cv.s3002unitservicepojo.dto.SideNaveDto;

import java.util.List;

public interface RoleService extends GenericService<RoleDto> {

    List<SideNaveDto> loadRoleMenu(String roleId) throws Exception;
}
