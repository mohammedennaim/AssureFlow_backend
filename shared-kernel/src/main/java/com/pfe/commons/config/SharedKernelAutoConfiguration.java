package com.pfe.commons.config;

import com.pfe.commons.web.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(GlobalExceptionHandler.class)
public class SharedKernelAutoConfiguration {
}
