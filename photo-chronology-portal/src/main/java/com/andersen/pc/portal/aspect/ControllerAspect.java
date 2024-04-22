package com.andersen.pc.portal.aspect;

import com.andersen.pc.common.model.dto.response.ErrorResponse;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

import static com.andersen.pc.common.constant.Constant.Service.Logging.CALL_MESSAGE;
import static com.andersen.pc.common.constant.Constant.Service.Logging.RESULT_ERROR_MESSAGE;
import static com.andersen.pc.common.constant.Constant.Service.Logging.RESULT_ERROR_MESSAGE_WITH_TRACE;
import static com.andersen.pc.common.constant.Constant.Service.Logging.RESULT_MESSAGE;

@Aspect
@Component
@Log4j2
public class ControllerAspect {

    @Pointcut("within(com.andersen.pc.portal.api.controller..*) " +
            "&& @within(org.springframework.web.bind.annotation.RestController)")
    public void controllerPointcut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void mappingPointcut() {
    }

    @Pointcut("within(com.andersen.pc.portal.api.advice.ControllerAdvice)")
    public void controllerAdvicePointcut() {
    }

    @Pointcut("execution(* com.andersen.pc.portal.api.advice.ControllerAdvice.handleException(..))")
    public void unhandledExceptionPointcut() {
    }

    @Pointcut("within(com.andersen.pc.portal.security.jwt.JwtAuthenticationEntryPoint)")
    public void unauthorizedPointcut() {
    }

    @Before("controllerPointcut() && mappingPointcut()")
    public void beforeCallEndpointLog() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logInfoMessage(CALL_MESSAGE, authentication, request);
    }

    @AfterReturning("controllerPointcut() && mappingPointcut()")
    public void afterCallEndpointLog() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logInfoMessage(RESULT_MESSAGE, authentication, request);
    }

    @AfterReturning(pointcut = "controllerAdvicePointcut()", returning = "responseEntity")
    public void afterHandleExceptions(ResponseEntity<ErrorResponse> responseEntity) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String cause = Objects.requireNonNull(responseEntity.getBody()).description();

        logErrorMessage(authentication, request, cause);
    }

    @AfterThrowing(pointcut = "unhandledExceptionPointcut()", throwing = "ex")
    public void afterEndpointThrowingLog(Throwable ex) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logErrorMessageWithStackTrace(authentication, request, ex);
    }

    @After("unauthorizedPointcut()")
    public void afterSecurityThrowing() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String cause = HttpStatusCode.valueOf(Objects.requireNonNull(response).getStatus()).toString();

        logErrorMessage(authentication, request, cause);
    }

    private void logInfoMessage(String messageTemplate, Authentication authentication, HttpServletRequest request) {
        String path = getRequestPath(request);
        String userId = getUserId(authentication);
        String userProperty = getUserProperty(authentication);

        log.info(messageTemplate,
                request.getMethod(),
                path,
                userProperty,
                userId);
    }

    private void logErrorMessageWithStackTrace(
            Authentication authentication,
            HttpServletRequest request,
            Throwable ex) {
        String path = getRequestPath(request);
        String userId = getUserId(authentication);
        String userProperty = getUserProperty(authentication);

        log.error(RESULT_ERROR_MESSAGE_WITH_TRACE,
                request.getMethod(),
                path,
                userProperty,
                userId,
                ExceptionUtils.getMessage(ex),
                ExceptionUtils.getStackTrace(ex));
    }

    private void logErrorMessage(
            Authentication authentication,
            HttpServletRequest request,
            String cause) {
        String path = getRequestPath(request);
        String userId = getUserId(authentication);
        String userProperty = getUserProperty(authentication);

        log.error(RESULT_ERROR_MESSAGE,
                request.getMethod(),
                path,
                userProperty,
                userId,
                cause);
    }

    private String getRequestPath(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder()
                .append(request.getServletPath());
        if (StringUtils.isNotBlank(request.getQueryString())) {
            builder.append("?")
                    .append(request.getQueryString());
        }
        return builder.toString();
    }

    private String getUserId(Authentication authentication) {
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;
            return (Objects.nonNull(jwtAuthentication)) ? jwtAuthentication.getUserId().toString() : "NULL";
        } else {
            return authentication.getName();
        }
    }

    private String getUserProperty(Authentication authentication) {
        return (authentication instanceof AnonymousAuthenticationToken) ? "userId" : "user";
    }
}
