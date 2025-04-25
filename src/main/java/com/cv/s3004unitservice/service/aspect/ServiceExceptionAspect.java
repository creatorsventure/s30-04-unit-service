package com.cv.s3004unitservice.service.aspect;

import com.cv.s10coreservice.annotation.ForceFlush;
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
import org.hibernate.TransientObjectException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Aspect
@AllArgsConstructor
@Slf4j
@Component
public class ServiceExceptionAspect {

    private final ExceptionComponent exceptionComponent;
    private final EntityManager entityManager;

    @Pointcut("execution(* com.cv.s2004orgservice.service.implementation..*(..))")
    public void implementationMethods() {
    }

    @Around("implementationMethods()")  // Adjust package
    public Object handleImplementationExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("➡️ Entering {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        boolean isForceFlush = signature.getMethod().isAnnotationPresent(ForceFlush.class);
        try {
            Object result = joinPoint.proceed();
            entityManager.flush();

            /*if (isForceFlush) {
                log.debug("🔁 Flushing EntityManager after {}.{}", className, methodName);
                entityManager.flush(); // only flush if annotation is present
            }*/

            return result;

        } catch (DataAccessException | ConstraintViolationException | TransientObjectException dbEx) {
            log.error("❌ DB Exception in {}.{}: {}", className, methodName, ExceptionUtils.getStackTrace(dbEx));
            throw exceptionComponent.expose("app.message.failure.db.error", true);
        } catch (Exception ex) {
            log.error("❌ General Exception in {}.{}: {}", className, methodName, ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
            long timeTaken = System.currentTimeMillis() - start;
            log.info("⏱ Completed {}.{} in {} ms", className, methodName, timeTaken);
        }
    }
}
