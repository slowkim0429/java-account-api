package com.virnect.account.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.virnect.account.adapter.inbound.dto.request.LoginRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.security.SecurityUtil;

@Slf4j
@Component
@Aspect
public class LoginLog {
	@Pointcut("execution(* com.virnect.account.adapter.inbound.controller.AuthController.login(..))")
	public void singinPointcut() {
		// Pointcut Empty Method Body
	}

	@Pointcut("execution(* com.virnect.account.adapter.inbound.controller.AuthController.logout(..))")
	public void logoutPointcut() {
		// Pointcut Empty Method Body
	}

	@Before("singinPointcut()")
	public void singinBefore(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		for (Object arg : args) {
			if (arg instanceof LoginRequestDto) {
				LoginRequestDto loginRequestDto = (LoginRequestDto)arg;
				log.info("login attempted email : {}", loginRequestDto.getEmail());
			}
		}
	}

	@AfterReturning(value = "singinPointcut()", returning = "returnObj")
	public void signinAfterReturn(JoinPoint joinPoint, Object returnObj) {
		Object[] args = joinPoint.getArgs();
		for (Object arg : args) {
			if (arg instanceof LoginRequestDto) {
				LoginRequestDto loginRequestDto = (LoginRequestDto)arg;
				log.info("login successes email : {}", loginRequestDto.getEmail());
			}
		}

		if (((ResponseEntity<?>)returnObj).getBody() instanceof TokenResponseDto) {
			TokenResponseDto tokenResponseDto = (TokenResponseDto)((ResponseEntity<?>)returnObj).getBody();
			log.info("grant type : {}", tokenResponseDto.getGrantType());
		}
	}

	@AfterThrowing(value = "singinPointcut()", throwing = "ex")
	public void signinAfterThrow(JoinPoint joinPoint, Exception ex) {
		Object[] args = joinPoint.getArgs();
		for (Object arg : args) {
			if (arg instanceof LoginRequestDto) {
				LoginRequestDto loginRequestDto = (LoginRequestDto)arg;
				log.info("login successes failed : {} ", loginRequestDto.getEmail());
			}
		}
	}

	@Before("logoutPointcut()")
	public void logoutBefore(JoinPoint joinPoint) {
		String userEmail = SecurityUtil.getCurrentUserEmail();
		log.info("logout success Email : {}", userEmail);
	}

	@AfterReturning(value = "logoutPointcut()", returning = "returnObj")
	public void logoutAfterReturn(JoinPoint joinPoint, Object returnObj) {
		String userEmail = SecurityUtil.getCurrentUserEmail();
		log.info("logout success Email : {}", userEmail);
	}

	@AfterThrowing(value = "logoutPointcut()", throwing = "ex")
	public void logoutAfterThrow(JoinPoint joinPoint, Exception ex) {
		String userEmail = SecurityUtil.getCurrentUserEmail();
		log.info("logout failed Email : {}", userEmail);
	}
}