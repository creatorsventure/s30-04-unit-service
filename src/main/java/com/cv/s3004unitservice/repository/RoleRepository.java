package com.cv.s3004unitservice.repository;

import com.cv.s10coreservice.repository.generic.GenericRepository;
import com.cv.s10coreservice.repository.generic.GenericSpecification;
import com.cv.s3002unitservicepojo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends GenericRepository, GenericSpecification<Role>,
        JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    Optional<List<Role>> findAllByStatusTrueAndIdIn(List<String> ids);
}
