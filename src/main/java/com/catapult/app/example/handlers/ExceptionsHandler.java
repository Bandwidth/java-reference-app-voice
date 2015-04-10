package com.catapult.app.example.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bandwidth.sdk.AppPlatformException;
import com.catapult.app.example.constants.ErrorMessages;
import com.catapult.app.example.exceptions.MissingFieldsException;
import com.catapult.app.example.exceptions.UserAlreadyExistsException;


@ControllerAdvice
public class ExceptionsHandler {
    
    @ExceptionHandler(MissingFieldsException.class)
    public void missingFieldsExceptionInterceptor(final HttpServletResponse response, final MissingFieldsException ex) {
        try {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(ex.getFields());
        } catch (final IOException e) {
            
        }
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public void userAlreadyExistsExceptionInterceptor(final HttpServletResponse response, final UserAlreadyExistsException ex) {
        try {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(ex.getErrorMessage());
        } catch (final IOException e) {
            
        }
    }
    
    @ExceptionHandler(AppPlatformException.class)
    public void appPlatformExceptionInterceptor(final HttpServletResponse response, final AppPlatformException ex) {
        try {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(ex.getMessage());
        } catch (final IOException e) {
            
        }
    }
    
    @ExceptionHandler(ParseException.class)
    public void parseExceptionInterceptor(final HttpServletResponse response, final ParseException ex) {
        try {
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            response.getWriter().print(ErrorMessages.GENERIC_ERROR);
        } catch (final IOException e) {
            
        }
    }
    
    @ExceptionHandler(Exception.class)
    public void exceptionInterceptor(final HttpServletResponse response, final Exception ex) {
        try {
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            response.getWriter().print(ErrorMessages.GENERIC_ERROR);
        } catch (final IOException e) {
            
        }
    }
    
    @ExceptionHandler(Throwable.class)
    public void throwableInterceptor(final HttpServletResponse response, final Throwable ex) {
        try {
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            response.getWriter().print(ErrorMessages.GENERIC_ERROR);
        } catch (final IOException e) {
            
        }
    }
}
