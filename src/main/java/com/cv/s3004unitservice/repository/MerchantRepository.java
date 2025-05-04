package com.cv.s3004unitservice.repository;

import com.cv.s10coreservice.repository.generic.GenericRepository;
import com.cv.s10coreservice.repository.generic.GenericSpecification;
import com.cv.s3002unitservicepojo.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends GenericRepository, GenericSpecification<Merchant>,
        JpaRepository<Merchant, String>, JpaSpecificationExecutor<Merchant> {
}
