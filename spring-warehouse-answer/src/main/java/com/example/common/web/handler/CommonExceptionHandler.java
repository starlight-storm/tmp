package com.example.common.web.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {
	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(RuntimeException.class)
	public String handleException() {
		log.error("*** " + messageSource.getMessage("error.common.exception", null, null) + "***");
		return "/error";
	}
}
