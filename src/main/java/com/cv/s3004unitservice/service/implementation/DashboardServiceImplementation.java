package com.cv.s3004unitservice.service.implementation;

import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.exception.ExceptionComponent;
import com.cv.s10coreservice.service.component.RepositoryRegistry;
import com.cv.s3002unitservicepojo.dto.CountDto;
import com.cv.s3002unitservicepojo.dto.DashboardDto;
import com.cv.s3002unitservicepojo.entity.Menu;
import com.cv.s3004unitservice.repository.MenuRepository;
import com.cv.s3004unitservice.service.intrface.DashboardService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = ApplicationConstant.APP_NAVIGATION_API_DASHBOARD)
@Transactional(rollbackOn = Exception.class)
public class DashboardServiceImplementation implements DashboardService {

    private final MenuRepository menuRepository;
    private final RepositoryRegistry repositoryRegistry;
    private final ExceptionComponent exceptionComponent;

    @Cacheable(keyGenerator = "cacheKeyGenerator")
    @Override
    public DashboardDto getCount() {
        return DashboardDto.builder()
                .countDtoList(menuRepository.findAllByStatusTrue(Menu.class)
                        .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true))
                        .stream()
                        .filter(Menu::isDashboardCountCard)
                        .map(menu -> {
                            long count = repositoryRegistry.getByCode(menu.getDescription())
                                    .map(CrudRepository::count)
                                    .orElse(0L);
                            return CountDto.builder()
                                    .title(menu.getName())
                                    .icon(menu.getIcon())
                                    .count(count)
                                    .build();
                        }).collect(Collectors.toList())).build();
    }
}
