package com.cv.s3004unitservice.repository;

import com.cv.s10coreservice.repository.generic.GenericRepository;
import com.cv.s10coreservice.repository.generic.GenericSpecification;
import com.cv.s3002unitservicepojo.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends GenericRepository, GenericSpecification<Device>,
        JpaRepository<Device, String>, JpaSpecificationExecutor<Device> {
}
