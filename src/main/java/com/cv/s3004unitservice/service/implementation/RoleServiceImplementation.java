package com.cv.s3004unitservice.service.implementation;

import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.dto.PaginationDto;
import com.cv.s10coreservice.exception.ExceptionComponent;
import com.cv.s10coreservice.service.function.StaticFunction;
import com.cv.s10coreservice.util.StaticUtil;
import com.cv.s3002unitservicepojo.dto.RoleDto;
import com.cv.s3002unitservicepojo.dto.SideNaveDto;
import com.cv.s3002unitservicepojo.entity.Menu;
import com.cv.s3002unitservicepojo.entity.Permission;
import com.cv.s3002unitservicepojo.entity.Role;
import com.cv.s3004unitservice.repository.MenuRepository;
import com.cv.s3004unitservice.repository.PermissionRepository;
import com.cv.s3004unitservice.repository.RoleRepository;
import com.cv.s3004unitservice.service.intrface.RoleService;
import com.cv.s3004unitservice.service.mapper.RoleMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = ApplicationConstant.APP_NAVIGATION_API_ROLE)
@Transactional(rollbackOn = Exception.class)
public class RoleServiceImplementation implements RoleService {
    private final RoleRepository repository;
    private final RoleMapper mapper;
    private final ExceptionComponent exceptionComponent;

    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public RoleDto create(RoleDto dto) throws Exception {
        var entity = mapper.toEntity(dto);
        createRoleEntity(dto, entity);
        return mapper.toDto(repository.save(entity));
    }

    @CacheEvict(keyGenerator = "cacheKeyGenerator", allEntries = true)
    @Override
    public RoleDto update(RoleDto dto) throws Exception {
        return mapper.toDto(repository.findById(dto.getId()).map(entity -> {
            BeanUtils.copyProperties(dto, entity);
            createRoleEntity(dto, entity);
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
    public RoleDto readOne(String id) throws Exception {
        return repository.findByIdAndStatusTrue(id, Role.class)
                .map(entity -> {
                    RoleDto dto = mapper.toDto(entity);
                    dto.setSelectedPermissionIds(entity.getPermissionList().stream().map(Permission::getId).toList());
                    try {
                        dto.setSelectedMenuIds(loadMenuIdsForEdit(entity));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return dto;
                })
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
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
        Page<Role> page;
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
        return repository.findAllByStatusTrue(Role.class)
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true))
                .stream().collect(Collectors.toMap(Role::getId, Role::getName));
    }

    private List<String> loadMenuIdsForEdit(Role entity) throws Exception {
        // Fetch all active menus
        List<Menu> allMenus = menuRepository.findAllByStatusTrue(Menu.class)
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));

        // Build a lookup for selected menu IDs
        Set<String> selectedMenuIds = entity.getMenuList().stream()
                .map(Menu::getId)
                .collect(Collectors.toSet());

        // Group child menus by their rootMenuId
        Map<String, List<Menu>> childMenusByRoot = allMenus.stream()
                .filter(menu -> menu.getMenuType().equals(ApplicationConstant.MENU_TYPE_CHILD))
                .collect(Collectors.groupingBy(Menu::getRootMenuId));

        Set<String> finalSelectedIds = new HashSet<>();

        // Process root menus that have children
        childMenusByRoot.forEach((rootId, children) -> {
            boolean allSelected = children.stream()
                    .map(Menu::getId)
                    .allMatch(selectedMenuIds::contains);

            if (allSelected) {
                finalSelectedIds.add(rootId);
            } else {
                children.stream()
                        .map(Menu::getId)
                        .filter(selectedMenuIds::contains)
                        .forEach(finalSelectedIds::add);
            }
        });

        // Handle selected root menus with no children
        Set<String> rootsWithChildren = childMenusByRoot.keySet();

        entity.getMenuList().stream()
                .filter(menu -> menu.getMenuType().equals(ApplicationConstant.MENU_TYPE_PARENT))
                .filter(menu -> !rootsWithChildren.contains(menu.getId())) // no children
                .map(Menu::getId)
                .forEach(finalSelectedIds::add);
        return new ArrayList<>(finalSelectedIds);
    }

    private void createRoleEntity(RoleDto dto, Role entity) {
        // Set Permissions
        var permissions = permissionRepository.findAllByStatusTrueAndIdIn(
                dto.getSelectedPermissionIds()
        ).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
        entity.setPermissionList(permissions);

        // Process and Set Menus
        List<Menu> menuList = new ArrayList<>();
        for (String menuId : dto.getSelectedMenuIds()) {
            var menu = menuRepository.findByIdAndStatusTrue(menuId, Menu.class)
                    .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));

            menuList.add(menu);

            // If it's a parent menu, fetch and add its children
            if (ApplicationConstant.MENU_TYPE_PARENT == menu.getMenuType()) {
                var children = menuRepository.findAllByRootMenuIdAndMenuTypeAndStatusTrue(
                        menu.getId(),
                        ApplicationConstant.MENU_TYPE_CHILD
                ).orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));

                menuList.addAll(children);
            }
        }
        entity.setMenuList(menuList);

    }

    @Override
    public List<SideNaveDto> loadRoleMenu(String roleId) throws Exception {
        Set<Menu> rootMenus = new HashSet<>();
        Set<Menu> childMenus = new HashSet<>();

        // Step 1: Load all roles in one pass, avoid repeated DB calls
        List<Role> roles = roleRepository.findByIdAndStatusTrue(roleId, Role.class).map(List::of)
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));

        // Step 2: Classify menus
        roles.stream()
                .flatMap(role -> role.getMenuList().stream())
                .forEach(menu -> {
                    if (ApplicationConstant.NOT_APPLICABLE.equals(menu.getRootMenuId())) {
                        rootMenus.add(menu);
                    } else {
                        childMenus.add(menu);
                    }
                });

        // Step 3: Precompute root menu IDs to avoid repeated scanning
        Set<String> rootMenuIds = rootMenus.stream()
                .map(Menu::getId)
                .collect(Collectors.toSet());

        // Step 4: Fetch missing submenus for root menus (only for those without linked children)
        Set<String> missingSubmenuRootIds = rootMenus.stream()
                .filter(root -> childMenus.stream().noneMatch(sub -> root.getId().equals(sub.getRootMenuId())))
                .map(Menu::getId)
                .collect(Collectors.toSet());

        for (String rootId : missingSubmenuRootIds) {
            List<Menu> subMenusFromDb = menuRepository.findAllByRootMenuIdAndStatusTrue(rootId)
                    .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
            childMenus.addAll(subMenusFromDb);
        }

        // Step 5: Fetch missing root menus for child menus
        List<String> missingRootIds = childMenus.stream()
                .map(Menu::getRootMenuId)
                .filter(Objects::nonNull)
                .filter(rootId -> !rootMenuIds.contains(rootId))
                .collect(Collectors.toList());

        if (!missingRootIds.isEmpty()) {
            List<Menu> missingRootMenus = menuRepository.findAllByStatusTrueAndIdIn(missingRootIds, Menu.class)
                    .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
            rootMenus.addAll(missingRootMenus);
        }

        return buildSideNavTree(rootMenus, childMenus);
    }

    private List<SideNaveDto> buildSideNavTree(Set<Menu> rootMenus, Set<Menu> childMenus) {
        // Convert child menus to a map grouped by rootMenuId
        Map<String, List<Menu>> childrenByRootId = childMenus.stream()
                .filter(menu -> menu.getRootMenuId() != null)
                .collect(Collectors.groupingBy(Menu::getRootMenuId));

        // Build the SideNaveDto tree
        return rootMenus.stream()
                .sorted(Comparator.comparingInt(Menu::getDisplayPosition))
                .map(root -> {
                    SideNaveDto rootDto = convertToDto(root);

                    List<Menu> subMenus = childrenByRootId.getOrDefault(root.getId(), Collections.emptyList());

                    List<SideNaveDto> childDtos = subMenus.stream()
                            .sorted(Comparator.comparingInt(Menu::getDisplayPosition))
                            .map(this::convertToDto)
                            .collect(Collectors.toList());

                    rootDto.setSubMenuList(childDtos);
                    return rootDto;
                })
                .collect(Collectors.toList());
    }

    private SideNaveDto convertToDto(Menu menu) {
        return SideNaveDto.builder()
                .title(menu.getName())
                .path(menu.getPath())
                .icon(menu.getIcon())
                .iconType(menu.getIconType())
                .iconTheme(menu.getIconTheme())
                .subMenuList(new ArrayList<>())
                .build();
    }
}
