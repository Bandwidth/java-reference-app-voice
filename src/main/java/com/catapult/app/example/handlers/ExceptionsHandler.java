package com.catapult.app.example.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bandwidth.sdk.AppPlatformException;
import com.catapult.app.example.constants.ErrorMessages;
import com.catapult.app.example.exceptions.MissingFieldsException;
import com.catapult.app.example.exceptions.UserAlreadyExistsException;
import com.catapult.app.example.exceptions.UserNotFoundException;

/**
 * This calss intend to make exceptions handling.
 *
 */
@ControllerAdvice
public class ExceptionsHandler {
    
    private final static Logger LOG = Logger.getLogger(ExceptionsHandler.class);
    
    /**
     * Do the MissingFieldsException handling
     * @param response the response
     * @param ex the exception
     */
    @ExceptionHandler(MissingFieldsException.class)
    public void missingFieldsExceptionInterceptor(final HttpServletResponse response, final MissingFieldsException ex) {
        try {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(ex.getFields());
        } catch (final IOException e) {
            if(LOG.isInfoEnabled()) {
                LOG.error("ERROR writing response: ", e);
            }
        }
    }

    /**
     * Do the UserAlreadyExistsException handling
     * @param response the response
     * @param ex the exception
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public void userAlreadyExistsExceptionInterceptor(final HttpServletResponse response, final UserAlreadyExistsException ex) {
        try {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(ex.getErrorMessage());
            if(LOG.isInfoEnabled()) {
                LOG.error(String.format("User already exists: %s", ex.getErrorMessage()));
            }
        } catch (final IOException e) {
            if(LOG.isInfoEnabled()) {
                LOG.error("ERROR writing response: ", e);
            }
        }
    }
    
    /**
     * Do the AppPlatformException handling
     * @param response the response
     * @param ex the exception
     */
    @ExceptionHandler(AppPlatformException.class)
    public void appPlatformExceptionInterceptor(final HttpServletResponse response, final AppPlatformException ex) {
        try {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(ex.getMessage());
            if(LOG.isInfoEnabled()) {
                LOG.error(String.format("User already exists: %s", ex.getMessage()));
            }
        } catch (final IOException e) {
            if(LOG.isInfoEnabled()) {
                LOG.error("ERROR writing response: ", e);
            }
        }
    }
    
    /**
     * Do the ParseException handling
     * @param response the response
     * @param ex the exception
     */
    @ExceptionHandler(ParseException.class)
    public void parseExceptionInterceptor(final HttpServletResponse response, final ParseException ex) {
        try {
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            response.getWriter().print(ErrorMessages.GENERIC_ERROR);
            if(LOG.isInfoEnabled()) {
                LOG.error(String.format("Parse exception error: %s", ex.getMessage()));
            }
        } catch (final IOException e) {
            if(LOG.isInfoEnabled()) {
                LOG.error("ERROR writing response: ", e);
            }
        }
    }

    /**
     * Do the Exception handling
     * @param response the response
     * @param ex the exception
     */
    @ExceptionHandler(UserNotFoundException.class)
    public void userNotFoundExceptionInterceptor(final HttpServletResponse response, final UserNotFoundException ex) {
        try {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print(ex.getErrorMessage());
            if(LOG.isInfoEnabled()) {
                LOG.error(String.format("User not found exception: %s", ex));
            }
        } catch (final IOException e) {
            if(LOG.isInfoEnabled()) {
                LOG.error("ERROR writing response: ", e);
            }
        }
    }

    /**
     * Do the Exception handling
     * @param response the response
     * @param ex the exception
     */
    @ExceptionHandler(Exception.class)
    public void exceptionInterceptor(final HttpServletResponse response, final Exception ex) {
        try {
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            response.getWriter().print(ErrorMessages.GENERIC_ERROR);
            if(LOG.isInfoEnabled()) {
                LOG.error(String.format("Generic exception: %s", ex));
            }
        } catch (final IOException e) {
            if(LOG.isInfoEnabled()) {
                LOG.error("ERROR writing response: ", e);
            }
        }
    }
    
    /**
     * Do the Throwable handling
     * @param response the response
     * @param ex the exception
     */
    @ExceptionHandler(Throwable.class)
    public void throwableInterceptor(final HttpServletResponse response, final Throwable ex) {
        try {
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            response.getWriter().print(ErrorMessages.GENERIC_ERROR);
            if(LOG.isInfoEnabled()) {
                LOG.error(String.format("Unexpected exception: %s", ex));
            }
        } catch (final IOException e) {
            if(LOG.isInfoEnabled()) {
                LOG.error("ERROR writing response: ", e);
            }
        }
    }
}
