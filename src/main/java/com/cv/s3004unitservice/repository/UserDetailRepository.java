package com.cv.s3004unitservice.repository;

import com.cv.s10coreservice.repository.generic.GenericRepository;
import com.cv.s10coreservice.repository.generic.GenericSpecification;
import com.cv.s3002unitservicepojo.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailRepository extends GenericRepository, GenericSpecification<UserDetail>,
        JpaRepository<UserDetail, String>, JpaSpecificationExecutor<UserDetail> {

    Optional<UserDetail> findByUserIdIgnoreCaseAndStatusTrue(String username);
}
