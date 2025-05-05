package com.cv.s3004unitservice.service.implementation;

import com.cv.s10coreservice.context.RequestContext;
import com.cv.s10coreservice.dto.PaginationDto;
import com.cv.s10coreservice.exception.ExceptionComponent;
import com.cv.s10coreservice.service.component.APIServiceCaller;
import com.cv.s10coreservice.service.function.StaticFunction;
import com.cv.s10coreservice.util.StaticUtil;
import com.cv.s2002orgservicepojo.dto.OptionsDto;
import com.cv.s3002unitservicepojo.constant.UnitConstant;
import com.cv.s3002unitservicepojo.dto.UnitOptionsDto;
import com.cv.s3002unitservicepojo.entity.UnitOptions;
import com.cv.s3004unitservice.repository.UnitOptionsRepository;
import com.cv.s3004unitservice.service.feign.OrgServiceClient;
import com.cv.s3004unitservice.service.intrface.UnitOptionsService;
import com.cv.s3004unitservice.service.mapper.UnitOptionsMapper;
import feign.Client;
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
@CacheConfig(cacheNames = UnitConstant.APP_NAVIGATION_API_UNIT_OPTIONS)
@Transactional(rollbackOn = Exception.class)
public class UnitOptionsServiceImplementation implements UnitOptionsService {
    private final UnitOptionsRepository repository;
    private final UnitOptionsMapper mapper;

    private final APIServiceCaller apiServiceCaller;
    private final ExceptionComponent exceptionComponent;
    private final Client client;

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public UnitOptionsDto create(UnitOptionsDto dto) throws Exception {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public UnitOptionsDto update(UnitOptionsDto dto) throws Exception {
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
    public UnitOptionsDto readOne(String id) throws Exception {
        return mapper.toDto(repository.findByIdAndStatusTrue(id, UnitOptions.class).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)));
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
        Page<UnitOptions> page;
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
        return repository.findAllByStatusTrue(UnitOptions.class).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)).stream().collect(Collectors.toMap(UnitOptions::getId, UnitOptions::getName));
    }

    @Override
    public boolean syncOptions() throws Exception {
        var optionsDto = apiServiceCaller.callOptional(OrgServiceClient.class,
                client -> client.resolveOptions(RequestContext.getUnitId()),
                OptionsDto.class);
        if (optionsDto.isPresent()) {
            var unitOptions = repository.findByUnitIdAndStatusTrue(RequestContext.getUnitId())
                    .orElseGet(UnitOptions::new);
            unitOptions = mapper.toUnitOptionsEntity(optionsDto.get());
            unitOptions.setUnitId(RequestContext.getUnitId());
            repository.save(unitOptions);
        } else {
            throw exceptionComponent.expose("org-service.failure.options.sync", true);
        }
        return false;
    }
}
