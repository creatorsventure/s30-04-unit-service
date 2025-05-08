package com.cv.s3004unitservice.service.implementation;

import com.cv.s10coreservice.context.RequestContext;
import com.cv.s10coreservice.dto.IdNameMapDto;
import com.cv.s10coreservice.dto.PaginationDto;
import com.cv.s10coreservice.exception.ExceptionComponent;
import com.cv.s10coreservice.service.component.APIServiceCaller;
import com.cv.s10coreservice.service.function.StaticFunction;
import com.cv.s10coreservice.util.StaticUtil;
import com.cv.s3002unitservicepojo.constant.UnitConstant;
import com.cv.s3002unitservicepojo.dto.BinDto;
import com.cv.s3002unitservicepojo.entity.Bin;
import com.cv.s3004unitservice.repository.BinRepository;
import com.cv.s3004unitservice.service.feign.OrgServiceClient;
import com.cv.s3004unitservice.service.intrface.BinService;
import com.cv.s3004unitservice.service.mapper.BinMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = UnitConstant.APP_NAVIGATION_API_BIN)
@Transactional(rollbackOn = Exception.class)
public class BinServiceImplementation implements BinService {
    private final BinRepository repository;
    private final BinMapper mapper;
    private final ExceptionComponent exceptionComponent;
    private final APIServiceCaller apiServiceCaller;

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public BinDto create(BinDto dto) throws Exception {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public BinDto update(BinDto dto) throws Exception {
        return mapper.toDto(repository.findById(dto.getId()).map(entity -> {
            BeanUtils.copyProperties(dto, entity);
            repository.save(entity);
            return entity;
        }).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)));
    }

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public Boolean updateStatus(String id, boolean status) throws Exception {
        return repository.findById(id).map(entity -> {
            entity.setStatus(status);
            repository.save(entity);
            return true;
        }).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
    }

    @Cacheable(keyGenerator = "cacheKeyGenerator")
    @Override
    public BinDto readOne(String id) throws Exception {
        return mapper.toDto(repository.findByIdAndStatusTrue(id, Bin.class).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)));
    }

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public Boolean delete(String id) throws Exception {
        repository.deleteById(id);
        return true;
    }

    @Cacheable(keyGenerator = "cacheKeyGenerator")
    @Override
    public PaginationDto readAll(PaginationDto dto) throws Exception {
        Page<Bin> page;
        if (StaticUtil.isSearchRequest(dto.getSearchField(), dto.getSearchValue())) {
            page = repository.findAll(repository.searchSpec(dto.getSearchField(), dto.getSearchValue()), StaticFunction.generatePageRequest.apply(dto));
        } else {
            page = repository.findAll(StaticFunction.generatePageRequest.apply(dto));
        }
        dto.setTotal(page.getTotalElements());
        dto.setResult(page.stream().map(mapper::toDto).collect(Collectors.toList()));
        return dto;
    }

    @Cacheable(keyGenerator = "cacheKeyGenerator")
    @Override
    public Map<String, String> readIdAndNameMap() throws Exception {
        return repository.findAllByStatusTrue(Bin.class).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)).stream().collect(Collectors.toMap(Bin::getId, Bin::getName));
    }

    @Override
    public Map<String, String> resolveOrgUnitScheme() throws Exception {
        return apiServiceCaller.callOptional(OrgServiceClient.class,
                        client -> client.resolveOrgUnitScheme(RequestContext.getUnitId()),
                        IdNameMapDto.class)
                .map(dto -> dto.getIdNameMaps().get("scheme"))
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
    }
}
