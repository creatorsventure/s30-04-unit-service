package com.cv.s3004unitservice.service.implementation;

import com.cv.s0402notifyservicepojo.dto.RecipientDto;
import com.cv.s0402notifyservicepojo.helper.NotifyHelper;
import com.cv.s10coreservice.config.props.CoreSecurityProperties;
import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.exception.ExceptionComponent;
import com.cv.s10coreservice.service.component.HybridEncryptionComponent;
import com.cv.s3002unitservicepojo.dto.PasswordDto;
import com.cv.s3002unitservicepojo.entity.Password;
import com.cv.s3002unitservicepojo.entity.UserDetail;
import com.cv.s3004unitservice.repository.PasswordRepository;
import com.cv.s3004unitservice.repository.UserDetailRepository;
import com.cv.s3004unitservice.service.component.KafkaProducer;
import com.cv.s3004unitservice.service.intrface.PasswordService;
import com.cv.s3004unitservice.service.mapper.PasswordMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = ApplicationConstant.APP_NAVIGATION_API_PASSWORD)
@Transactional(rollbackOn = Exception.class)
public class PasswordServiceImplementation implements PasswordService {

    private final PasswordRepository repository;
    private final PasswordMapper mapper;
    private final ExceptionComponent exceptionComponent;
    private final UserDetailRepository userDetailRepository;
    private final PasswordEncoder passwordEncoder;
    private final HybridEncryptionComponent encryptionComponent;
    private final KafkaProducer kafkaProducer;
    private final Environment environment;
    private final PasswordRepository passwordRepository;
    private final CoreSecurityProperties securityProperties;

    public PasswordDto changePassword(PasswordDto dto) throws Exception {
        var entity = mapper.toEntity(dto);
        var userEntity = userDetailRepository.findByUserIdIgnoreCaseAndStatusTrue(dto.getUserDetailId())
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
        constructEntity(dto, entity, userEntity);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public boolean activateAccount(String id) throws Exception {
        var actualId = encryptionComponent.decrypt(id);
        var entity = userDetailRepository.findById(actualId)
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
        if (entity.isStatus()) {
            throw exceptionComponent.expose("app.message.failure.user.already.activated", true);
        } else if (entity.getCreatedAt().plusHours(securityProperties.getEmailLinkExpiryHrs()).isBefore(LocalDateTime.now())) {
            throw exceptionComponent.expose("app.message.failure.link.expired", true);
        } else {
            entity.setStatus(ApplicationConstant.APPLICATION_STATUS_ACTIVE);
            userDetailRepository.save(entity);
            return true;
        }
    }

    @Override
    public boolean forgotPassword(String userId) throws Exception {
        var entity = userDetailRepository.findByUserIdIgnoreCaseAndStatusTrue(userId)
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
        entity.getPassword().setModifiedAt(LocalDateTime.now());
        entity.getPassword().setStatus(ApplicationConstant.APPLICATION_STATUS_INACTIVE);
        userDetailRepository.save(entity);
        return sendPasswordResetEmail(entity);
    }

    @Override
    public boolean resetPassword(PasswordDto dto) throws Exception {
        var actualId = encryptionComponent.decrypt(dto.getUserDetailId());
        var entity = mapper.toEntity(dto);
        var userEntity = userDetailRepository.findByIdAndStatusTrue(actualId, UserDetail.class)
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
        if (userEntity.getPassword().isStatus() || userEntity.getPassword().getModifiedAt().plusHours(securityProperties.getEmailLinkExpiryHrs()).isBefore(LocalDateTime.now())) {
            throw exceptionComponent.expose("app.message.failure.link.expired", true);
        } else {
            constructEntity(dto, entity, userEntity);
            repository.save(entity);
            return true;
        }
    }

    @Override
    public boolean resendPasswordEmail(String id) throws Exception {
        var entity = userDetailRepository.findByIdAndStatusTrue(id, UserDetail.class)
                .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true));
        if (Optional.ofNullable(entity.getPassword()).isPresent()) {
            entity.getPassword().setStatus(ApplicationConstant.APPLICATION_STATUS_INACTIVE);
            entity.setModifiedAt(LocalDateTime.now());
            userDetailRepository.save(entity);
        } else {
            String tempPassword = UUID.randomUUID().toString();
            passwordRepository.save(Password.builder()
                    .name(entity.getName())
                    .modifiedAt(LocalDateTime.now())
                    .status(ApplicationConstant.APPLICATION_STATUS_INACTIVE)
                    .encryptedPassword(encryptionComponent.encrypt(tempPassword))
                    .hashPassword(passwordEncoder.encode(tempPassword))
                    .userDetail(entity)
                    .build());
        }

        return sendPasswordResetEmail(entity);
    }

    private boolean sendPasswordResetEmail(UserDetail entity) throws Exception {
        kafkaProducer.notify(NotifyHelper.notifyPasswordReset(
                RecipientDto.builder()
                        .name(entity.getName())
                        .email(entity.getEmail())
                        .mobileNumber(entity.getMobileNumber())
                        .countryCode(entity.getCountryCode())
                        .status(ApplicationConstant.APPLICATION_STATUS_ACTIVE)
                        .build(),
                Locale.ENGLISH,
                environment.getProperty("app.org-service.reset-password-url") + encryptionComponent.encrypt(entity.getId()),
                entity.getId()
        ));
        return true;
    }

    private void constructEntity(PasswordDto dto, Password entity, UserDetail userEntity) throws Exception {
        if (passwordEncoder.matches(dto.getPassword(), userEntity.getPassword().getHashPassword())) {
            throw exceptionComponent.expose("app.message.failure.same.password", true);
        }
        entity.setId(userEntity.getPassword().getId());
        entity.setName(userEntity.getName());
        entity.setHashPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setEncryptedPassword(encryptionComponent.encrypt(dto.getPassword()));
        entity.setStatus(ApplicationConstant.APPLICATION_STATUS_ACTIVE);
        entity.setUserDetail(userEntity);
    }

}
