package com.cv.s3004unitservice.util;

import com.cv.s10coreservice.dto.APIResponseDto;
import com.cv.s10coreservice.enumeration.APIResponseType;
import com.cv.s10coreservice.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

@Slf4j
public class StaticUtil {

    public static ResponseEntity<Object> getSuccessResponse(Object o, APIResponseType type) {
        try {
            return new ResponseEntity<>(
                    new APIResponseDto(true, "app.message.success.000", type.getValue(), o),
                    HttpStatus.OK);
        } catch (Exception ex) {
            log.error("StaticUtil.getSuccessResponse", ex);
            return new ResponseEntity<>(new APIResponseDto(false, "app.message.failure.000",
                    APIResponseType.MESSAGE_ACTUAL.getValue(), ExceptionUtils.getMessage(ex)),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static ResponseEntity<Object> getFailureResponse(Object o) {
        try {
            if (o instanceof ApplicationException) {
                return new ResponseEntity<>(
                        new APIResponseDto(false, ((ApplicationException) o).getMessage(),
                                APIResponseType.MESSAGE_CODE.getValue(), null),
                        HttpStatus.BAD_REQUEST);
            } else if (o instanceof BindingResult) {
                return new ResponseEntity<>(
                        new APIResponseDto(false, "app.message.failure.000",
                                APIResponseType.MESSAGE_CODE_LIST.getValue(), ((BindingResult) o).getAllErrors()),
                        HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(new APIResponseDto(false,
                        "app.message.failure.000",
                        APIResponseType.MESSAGE_ACTUAL.getValue(), o),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            log.error("StaticUtil.getFailureResponse {1}", ex);
            return new ResponseEntity<>(new APIResponseDto(false, "app.message.failure.000",
                    APIResponseType.MESSAGE_ACTUAL.getValue(), ExceptionUtils.getMessage(ex)),
                    HttpStatus.BAD_REQUEST);
        }
    }

}
