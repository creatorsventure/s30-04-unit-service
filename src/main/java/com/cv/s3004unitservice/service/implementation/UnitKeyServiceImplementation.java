package com.cv.s3004unitservice.service.implementation;

import com.cv.s10coreservice.dto.PaginationDto;
import com.cv.s10coreservice.exception.ExceptionComponent;
import com.cv.s10coreservice.service.function.StaticFunction;
import com.cv.s10coreservice.util.StaticUtil;
import com.cv.s3002unitservicepojo.constant.UnitConstant;
import com.cv.s3002unitservicepojo.dto.UnitKeyDto;
import com.cv.s3002unitservicepojo.entity.UnitKey;
import com.cv.s3004unitservice.repository.UnitKeyRepository;
import com.cv.s3004unitservice.service.intrface.UnitKeyService;
import com.cv.s3004unitservice.service.mapper.UnitKeyMapper;
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
@CacheConfig(cacheNames = UnitConstant.APP_NAVIGATION_API_UNIT_KEY)
@Transactional(rollbackOn = Exception.class)
public class UnitKeyServiceImplementation implements UnitKeyService {
    private final UnitKeyRepository repository;
    private final UnitKeyMapper mapper;
    private final ExceptionComponent exceptionComponent;

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public UnitKeyDto create(UnitKeyDto dto) throws Exception {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public UnitKeyDto update(UnitKeyDto dto) throws Exception {
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
    public UnitKeyDto readOne(String id) throws Exception {
        return mapper.toDto(repository.findByIdAndStatusTrue(id, UnitKey.class).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)));
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
        Page<UnitKey> page;
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
        return repository.findAllByStatusTrue(UnitKey.class).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)).stream().collect(Collectors.toMap(UnitKey::getId, UnitKey::getName));
    }
}
