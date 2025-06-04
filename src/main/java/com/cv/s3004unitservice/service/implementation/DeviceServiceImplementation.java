package com.cv.s3004unitservice.service.implementation;

import com.cv.s10coreservice.context.RequestContext;
import com.cv.s10coreservice.dto.IdNameMapDto;
import com.cv.s10coreservice.dto.PaginationDto;
import com.cv.s10coreservice.exception.ExceptionComponent;
import com.cv.s10coreservice.service.component.APIServiceCaller;
import com.cv.s10coreservice.service.function.StaticFunction;
import com.cv.s10coreservice.util.StaticUtil;
import com.cv.s3002unitservicepojo.constant.UnitConstant;
import com.cv.s3002unitservicepojo.dto.DeviceDto;
import com.cv.s3002unitservicepojo.entity.Device;
import com.cv.s3002unitservicepojo.entity.Merchant;
import com.cv.s3002unitservicepojo.entity.UnitKey;
import com.cv.s3002unitservicepojo.entity.UnitOptions;
import com.cv.s3004unitservice.repository.DeviceRepository;
import com.cv.s3004unitservice.repository.MerchantRepository;
import com.cv.s3004unitservice.repository.UnitKeyRepository;
import com.cv.s3004unitservice.repository.UnitOptionsRepository;
import com.cv.s3004unitservice.service.feign.OrgServiceClient;
import com.cv.s3004unitservice.service.intrface.DeviceService;
import com.cv.s3004unitservice.service.mapper.DeviceMapper;
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
@CacheConfig(cacheNames = UnitConstant.APP_NAVIGATION_API_DEVICE)
@Transactional(rollbackOn = Exception.class)
public class DeviceServiceImplementation implements DeviceService {
    private final DeviceRepository repository;
    private final DeviceMapper mapper;
    private final MerchantRepository merchantRepository;
    private final UnitOptionsRepository unitOptionsRepository;
    private final UnitKeyRepository unitKeyRepository;
    private final ExceptionComponent exceptionComponent;
    private final APIServiceCaller apiServiceCaller;

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public DeviceDto create(DeviceDto dto) throws Exception {
        var entity = mapper.toEntity(dto);
        constructEntity(dto, entity);
        return mapper.toDto(repository.save(entity));
    }

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public DeviceDto update(DeviceDto dto) throws Exception {
        return mapper.toDto(repository.findById(dto.getId()).map(entity -> {
            BeanUtils.copyProperties(dto, entity);
            constructEntity(dto, entity);
            repository.save(entity);
            return entity;
        }).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)));
    }

    private void constructEntity(DeviceDto dto, Device entity) {
        entity.setMerchant(merchantRepository.findByIdAndStatusTrue(dto.getSelectedMerchantId(), Merchant.class)
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)));
        entity.setUnitOptions(unitOptionsRepository.findByIdAndStatusTrue(dto.getSelectedUnitOptionsId(), UnitOptions.class)
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)));
        entity.setUnitKey(unitKeyRepository.findByIdAndStatusTrue(dto.getSelectedUnitKeyId(), UnitKey.class)
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)));
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
    public DeviceDto readOne(String id) throws Exception {
        return repository.findByIdAndStatusTrue(id, Device.class)
                .map(entity -> {
                    var dto = mapper.toDto(entity);
                    dto.setSelectedMerchantId(entity.getMerchant().getId());
                    dto.setSelectedUnitOptionsId(entity.getUnitOptions().getId());
                    dto.setSelectedUnitKeyId(entity.getUnitKey().getId());
                    return dto;
                }).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
    }

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public Boolean delete(String id) throws Exception {
        repository.deleteById(id);
        return true;
    }

    @Cacheable(keyGenerator = "customUnitAwareCacheKeyGenerator")
    @Override
    public PaginationDto readAll(PaginationDto dto) throws Exception {
        Page<Device> page;
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
        return repository.findAllByStatusTrue(Device.class).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)).stream().collect(Collectors.toMap(Device::getId, Device::getName));
    }

    @Override
    public IdNameMapDto resolveOrgUnitIdNameMaps() throws Exception {
        return apiServiceCaller.callOptional(OrgServiceClient.class,
                        client -> client.resolveOrgUnitIdNameMaps(RequestContext.getUnitId()),
                        IdNameMapDto.class)
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
    }
}
