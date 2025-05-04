package com.cv.s3004unitservice.service.aspect;

import com.cv.s10coreservice.constant.ApplicationConstant;
import com.cv.s10coreservice.context.RequestContext;
import com.cv.s10coreservice.exception.ExceptionComponent;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.TransientObjectException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@AllArgsConstructor
@Slf4j
@Component
public class GlobalServiceAspect {

    private final ExceptionComponent exceptionComponent;
    private final EntityManager entityManager;

    @Pointcut("execution(* com.cv.s3004unitservice.service.implementation..*(..))")
    public void implementationMethods() {
    }

    @Around("implementationMethods()")  // Adjust package
    public Object aroundImplementationMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("➡️ aroundImplementationMethods:Entering {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        // boolean isForceFlush = signature.getMethod().isAnnotationPresent(ForceFlush.class);
        try {
            applyHibernateFilters(className);
            Object result = joinPoint.proceed();
            entityManager.flush();

            /*if (isForceFlush) {
                log.debug("🔁 Flushing EntityManager after {}.{}", className, methodName);
                entityManager.flush(); // only flush if annotation is present
            }*/

            return result;

        } catch (DataAccessException | ConstraintViolationException | TransientObjectException dbEx) {
            log.error("❌ aroundImplementationMethods: DB Exception in {}.{}: {}", className, methodName, ExceptionUtils.getStackTrace(dbEx));
            throw exceptionComponent.expose("app.message.failure.db.error", true);
        } catch (Exception ex) {
            log.error("❌ aroundImplementationMethods: General Exception in {}.{}: {}", className, methodName, ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
            long timeTaken = System.currentTimeMillis() - start;
            log.info("⏱ aroundImplementationMethods:Completed {}.{} in {} ms", className, methodName, timeTaken);
        }
    }

    public void applyHibernateFilters(String className) throws Throwable {
        log.info("➡️ applyHibernateFilters: Entering {}.applyHibernateFilters", className);
        long start = System.currentTimeMillis();
        try {
            Session session = entityManager.unwrap(Session.class);
            Map<String, String> contextValues = RequestContext.getAll();

            // Enable unit filter if isn't already enabled
            Filter unitFilter = session.getEnabledFilter(ApplicationConstant.HIBERNATE_UNIT_FILTER_NAME);
            if (unitFilter == null) {
                unitFilter = session.enableFilter(ApplicationConstant.HIBERNATE_UNIT_FILTER_NAME);
            }

            if (unitFilter != null && contextValues.get("unitId") != null) {
                log.debug("➡️ applyHibernateFilters: Applying unitId filter param = {}", contextValues.get("unitId"));
                unitFilter.setParameter("unitId", contextValues.get("unitId"));
            } else {
                throw exceptionComponent.expose("⚠️ applyHibernateFilters: unitId is not set in RequestContext; and merchantFilter is null.", false);
            }

            /*
            // Enable merchant filter if not already enabled
            Filter merchantFilter = session.getEnabledFilter(ApplicationConstant.HIBERNATE_MERCHANT_FILTER_NAME);
            if (merchantFilter == null) {
                merchantFilter = session.enableFilter(ApplicationConstant.HIBERNATE_UNIT_FILTER_NAME);
            }

            if (merchantFilter != null && contextValues.get("merchantId") != null) {
                log.debug("➡️ Applying merchantId filter param = {}", contextValues.get("merchantId"));
                merchantFilter.setParameter("merchantId", contextValues.get("merchantId"));
            } else {
                throw exceptionComponent.expose("⚠️ merchantId is not set in RequestContext; and merchantFilter is null.", false);
            }
            */
        } catch (Exception ex) {
            log.error("❌ applyHibernateFilters: Exception in {}", ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
            long timeTaken = System.currentTimeMillis() - start;
            log.info("⏱ applyHibernateFilters:Completed {}.{} in {} ms", className, "applyHibernateFilters", timeTaken);
        }
    }
}
