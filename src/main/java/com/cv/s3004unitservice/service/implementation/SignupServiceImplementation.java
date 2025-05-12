package com.cv.s3004unitservice.service.implementation;

import com.cv.core.s09coresecurity.component.HybridEncryptionComponent;
import com.cv.core.s09coresecurity.config.properties.CoreSecurityProperties;
import com.cv.core.s09coresecurity.util.TotpService;
import com.cv.s0402notifyservicepojo.dto.RecipientDto;
import com.cv.s0402notifyservicepojo.helper.NotifyHelper;
import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.dto.VerifyOTPDto;
import com.cv.s10coreservice.dto.VerifySignupDto;
import com.cv.s10coreservice.exception.ExceptionComponent;
import com.cv.s10coreservice.service.component.JsonComponent;
import com.cv.s3002unitservicepojo.dto.SignupDto;
import com.cv.s3002unitservicepojo.entity.*;
import com.cv.s3004unitservice.repository.*;
import com.cv.s3004unitservice.service.component.KafkaProducer;
import com.cv.s3004unitservice.service.component.RedisOtpComponent;
import com.cv.s3004unitservice.service.intrface.SignupService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(rollbackOn = Exception.class)
public class SignupServiceImplementation implements SignupService {

    private final MenuRepository menuRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final PasswordRepository passwordRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailRepository userDetailRepository;
    private final ExceptionComponent exceptionComponent;
    private final HybridEncryptionComponent encryptionComponent;
    private final RedisOtpComponent redisOtpComponent;
    private final KafkaProducer kafkaProducer;
    private final CoreSecurityProperties securityProperties;
    private final JsonComponent jsonComponent;
    private Environment environment;


    @Override
    public boolean signup(SignupDto signupDto) throws Exception {
        if (userDetailRepository.count() == 0) {
            var roleEntity = Role.builder()
                    .name((signupDto.getEntityCode() + "_role").toUpperCase())
                    .description((signupDto.getEntityCode() + "_role").toUpperCase())
                    .status(ApplicationConstant.APPLICATION_STATUS_ACTIVE)
                    .permissionList(permissionRepository.findAllByStatusTrue(Permission.class)
                            .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)))
                    .menuList(menuRepository.findAllByStatusTrue(Menu.class)
                            .orElseThrow(() -> exceptionComponent.expose("app.message.failure.object.unavailable", true)))
                    .build();
            roleEntity = roleRepository.save(roleEntity);

            var userDetail = userDetailRepository.save(
                    UserDetail.builder()
                            .name(signupDto.getName())
                            .userId(signupDto.getUserId())
                            .countryCode(signupDto.getCountryCode())
                            .mobileNumber(signupDto.getMobileNumber())
                            .email(signupDto.getEmail())
                            .status(ApplicationConstant.APPLICATION_STATUS_ACTIVE)
                            .role(roleEntity)
                            .build());

            // log.info("Hash Password {}", passwordEncoder.encode(signupDto.getPassword()));
            // log.info("Encrypted Password {}", encryptionComponent.encrypt(signupDto.getPassword()));
            passwordRepository.save(Password.builder()
                    .name(signupDto.getName())
                    .encryptedPassword(encryptionComponent.encrypt(signupDto.getPassword()))
                    .hashPassword(passwordEncoder.encode(signupDto.getPassword()))
                    .userDetail(userDetail)
                    .build());

            kafkaProducer.notify(NotifyHelper.notifyAccountInfo(
                    RecipientDto.builder()
                            .name(signupDto.getName())
                            .email(signupDto.getEmail())
                            .mobileNumber(signupDto.getMobileNumber())
                            .countryCode(signupDto.getCountryCode())
                            .status(ApplicationConstant.APPLICATION_STATUS_ACTIVE)
                            .build(),
                    Locale.ENGLISH,
                    (signupDto.getEntityCode() + "," + signupDto.getEntityName() + "," + userDetail.getUserId()).toUpperCase(),
                    environment.getProperty("app.unit-service.login-url"),
                    userDetail.getId()
            ));
            return true;
        } else {
            throw new Exception(exceptionComponent.expose("app.message.failure.user.exists", true));
        }
    }

    @Override
    public VerifySignupDto verifySignup(String payload) throws Exception {
        var decryptedDto = jsonComponent.fromJson(encryptionComponent.decrypt(payload), VerifySignupDto.class);
        if (decryptedDto.getCreatedAt().plusHours(securityProperties.getEmailLinkExpiryHrs()).isBefore(LocalDateTime.now())) {
            throw exceptionComponent.expose("app.message.failure.link.expired", true);
        } else if (userDetailRepository.count() > 0) {
            throw exceptionComponent.expose("app.message.failure.user.exists", true);
        } else {
            var key = (decryptedDto.getEntityId() + "-" + decryptedDto.getAdminUserId()).toLowerCase();
            var otp = TotpService.generateCurrentOtpAsString(key);
            redisOtpComponent.saveSecret(key, otp, Duration.ofMinutes(securityProperties.getOtpExpiryMins()));
            kafkaProducer.notify(NotifyHelper.notifyOTP(RecipientDto.builder()
                    .name(decryptedDto.getAdminName())
                    .email(decryptedDto.getAdminEmail())
                    .status(ApplicationConstant.APPLICATION_STATUS_ACTIVE)
                    .build(), Locale.ENGLISH, otp, decryptedDto.getEntityId()));
            return decryptedDto;
        }
    }

    @Override
    public boolean verifyOTP(VerifyOTPDto dto) throws Exception {
        var key = (dto.getUnitId() + "-" + dto.getUserId()).toLowerCase();
        var redisOtp = redisOtpComponent.getSecret(key);
        return redisOtp != null && dto.getOtp() != null && redisOtp.equals(dto.getOtp());
    }

}
