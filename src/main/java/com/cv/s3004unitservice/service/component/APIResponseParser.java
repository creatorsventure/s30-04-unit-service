package com.cv.s3004unitservice.service.component;

import com.cv.s10coreservice.dto.APIResponseDto;
import com.cv.s10coreservice.exception.ExceptionComponent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class APIResponseParser {

    private final ObjectMapper objectMapper;
    private final ExceptionComponent exceptionComponent;

    /**
     * For simple types (e.g., String, DTO)
     */
    public <T> T parse(Supplier<String> supplier, Class<T> clazz) {
        return parse(supplier.get(), clazz);
    }

    /**
     * For generic types (e.g., List<MyDto>, Map<String, Object>)
     */
    public <T> T parse(Supplier<String> supplier, TypeReference<T> typeRef) {
        JavaType javaType = objectMapper.getTypeFactory().constructType(typeRef.getType());
        return parse(supplier.get(), javaType);
    }

    /**
     * Parse a response where the expected object is a POJO (non-generic type)
     */
    public <T> T parse(String response, Class<T> responseType) {
        try {
            APIResponseDto apiResponseDto = objectMapper.readValue(response, APIResponseDto.class);
            log.info("ApiResponseParser.parse (Class) Parsed DTO: {}", apiResponseDto);
            if (apiResponseDto.isStatus()) {
                JsonNode objectNode = objectMapper.valueToTree(apiResponseDto.getObject());
                return objectMapper.treeToValue(objectNode, responseType);
            } else {
                throw exceptionComponent.expose("app.message.internal.api.failure", true);
            }
        } catch (Exception ex) {
            log.error("❌ ApiResponseParser.parse (Class) Error: {}", ExceptionUtils.getStackTrace(ex));
            throw exceptionComponent.expose("app.message.internal.api.failure", true);
        }
    }

    /**
     * Parse a response where the expected object is a generic type (e.g. List<DTO>, Map<String, Object>)
     */
    public <T> T parse(String response, JavaType responseType) {
        try {
            APIResponseDto apiResponseDto = objectMapper.readValue(response, APIResponseDto.class);
            log.info("ApiResponseParser.parse (JavaType) Parsed DTO: {}", apiResponseDto);
            if (apiResponseDto.isStatus()) {
                return objectMapper.convertValue(apiResponseDto.getObject(), responseType);
            } else {
                throw exceptionComponent.expose("app.message.internal.api.failure", true);
            }
        } catch (Exception ex) {
            log.error("❌ ApiResponseParser.parse (JavaType) Error: {}", ExceptionUtils.getStackTrace(ex));
            throw exceptionComponent.expose("app.message.internal.api.failure", true);
        }
    }
}
