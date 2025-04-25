package com.cv.s3004unitservice.repository;

import com.cv.s10coreservice.repository.generic.GenericRepository;
import com.cv.s10coreservice.repository.generic.GenericSpecification;
import com.cv.s3002unitservicepojo.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends GenericRepository, GenericSpecification<Menu>,
        JpaRepository<Menu, String>, JpaSpecificationExecutor<Menu> {

    List<Menu> findAllByMenuTypeAndStatusTrue(Integer menuType);

    Optional<List<Menu>> findAllByRootMenuIdAndMenuTypeAndStatusTrue(String rootMenuId, Integer menuType);

    Optional<List<Menu>> findAllByRootMenuIdAndStatusTrue(String rootMenuId);

}
