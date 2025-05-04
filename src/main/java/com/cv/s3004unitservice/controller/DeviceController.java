package com.cv.s3004unitservice.controller;

import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.controller.generic.GenericController;
import com.cv.s10coreservice.dto.PaginationDto;
import com.cv.s10coreservice.enumeration.APIResponseType;
import com.cv.s3002unitservicepojo.constant.UnitConstant;
import com.cv.s3002unitservicepojo.dto.DeviceDto;
import com.cv.s3004unitservice.service.intrface.DeviceService;
import com.cv.s3004unitservice.util.StaticUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UnitConstant.APP_NAVIGATION_API_DEVICE)
@AllArgsConstructor
@Slf4j
public class DeviceController implements GenericController<DeviceDto> {
    private DeviceService service;

    @PostMapping
    @Override
    public ResponseEntity<Object> create(@RequestBody @Valid DeviceDto dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.info("DeviceController.create {}", result.getAllErrors());
                return StaticUtil.getFailureResponse(result);
            }
            return StaticUtil.getSuccessResponse(service.create(dto), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("DeviceController.create {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @PutMapping
    @Override
    public ResponseEntity<Object> update(@RequestBody @Valid DeviceDto dto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                log.info("DeviceController.update {}", result.getAllErrors());
                return StaticUtil.getFailureResponse(result);
            }
            return StaticUtil.getSuccessResponse(service.update(dto), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("DeviceController.update {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @GetMapping(ApplicationConstant.APP_NAVIGATION_METHOD_UPDATE_STATUS)
    @Override
    public ResponseEntity<Object> updateStatus(@RequestParam String id, @RequestParam boolean status) {
        try {
            return StaticUtil.getSuccessResponse(service.updateStatus(id, status), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("DeviceController.updateStatus {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @GetMapping
    @Override
    public ResponseEntity<Object> readOne(@RequestParam String id) {
        try {
            log.info("DeviceController.readOne {}", id);
            return StaticUtil.getSuccessResponse(service.readOne(id), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("DeviceController.readOne {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @PostMapping(ApplicationConstant.APP_NAVIGATION_METHOD_READ_PAGE)
    @Override
    public ResponseEntity<Object> readPage(@RequestBody PaginationDto dto) {
        try {
            return StaticUtil.getSuccessResponse(service.readAll(dto), APIResponseType.OBJECT_LIST);
        } catch (Exception e) {
            log.error("DeviceController.readPage {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @GetMapping(ApplicationConstant.APP_NAVIGATION_METHOD_READ_ID_NAME_MAP)
    @Override
    public ResponseEntity<Object> readIdNameMapping() {
        try {
            return StaticUtil.getSuccessResponse(service.readIdAndNameMap(), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("DeviceController.readIdNameMapping {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }

    @DeleteMapping
    @Override
    public ResponseEntity<Object> delete(@RequestParam String id) {
        try {
            return StaticUtil.getSuccessResponse(service.delete(id), APIResponseType.OBJECT_ONE);
        } catch (Exception e) {
            log.error("DeviceController.delete {}", ExceptionUtils.getStackTrace(e));
            return StaticUtil.getFailureResponse(e);
        }
    }
}
