package com.catapult.app.example.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bandwidth.sdk.AppPlatformException;
import com.catapult.app.example.adapters.UserAdapter;
import com.catapult.app.example.beans.User;
import com.catapult.app.example.constants.ErrorMessages;
import com.catapult.app.example.exceptions.MissingFieldsException;
import com.catapult.app.example.exceptions.UserAlreadyExistsException;
import com.catapult.app.example.exceptions.UserNotFoundException;
import com.catapult.app.example.generics.GenericResponse;
import com.catapult.app.example.services.DomainServices;
import com.catapult.app.example.services.EndpointServices;
import com.catapult.app.example.services.UserServices;

@Controller
@RequestMapping("/users")
public class UsersController {

    private final Logger LOG = Logger.getLogger(UsersController.class.getName());

    @Autowired
    private UserServices userServices;

    @Autowired
    private DomainServices domainServices;
    
    @Autowired
    private EndpointServices endpointServices;
    
    @RequestMapping(method = RequestMethod.POST, headers = { "Content-Type=application/json" })
    public @ResponseBody GenericResponse createUser(@RequestBody final UserAdapter userAdapter) throws MissingFieldsException, UserAlreadyExistsException {
        LOG.info(String.format("Create user: userName %s", userAdapter.getUserName()));
        User user;
        try {
            user = userServices.createUser(userAdapter);
            return new GenericResponse<User>(user);
        } catch (final MissingFieldsException e) {
            return new GenericResponse<Map<String, String>>(e.getFields());
        } catch (final UserAlreadyExistsException e) {
            return new GenericResponse<String>(e.getErrorMessage());
        } catch (final AppPlatformException e) {
            return new GenericResponse<String>(e.getMessage());
        } catch (final ParseException e) {
            return new GenericResponse<String>(ErrorMessages.GENERIC_ERROR);
        } catch (final Exception e) {
            return new GenericResponse<String>(ErrorMessages.GENERIC_ERROR);
        }
        
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody GenericResponse getUser(@PathVariable("userName") final String userName) {
        LOG.info(String.format("Get user: userName %s", userName));
        User user;
        try {
            user = userServices.getUser(userName);
            return new GenericResponse<User>(user);
        } catch (final UserNotFoundException e) {
            return new GenericResponse<String>(e.getErrorMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody GenericResponse deleteUser(@PathVariable("userName") final String userName) {
        LOG.info(String.format("Delete user: userId %s", userName));
        try {
            userServices.deleteUser(userName);
            return new GenericResponse<String>("Deleted userName " + userName);
        } catch (final UserNotFoundException e) {
            return new GenericResponse<String>(e.getErrorMessage());
        } catch (final AppPlatformException e) {
            return new GenericResponse<String>(e.getMessage());
        } catch (final IOException ex) {
            return new GenericResponse<String>(ErrorMessages.GENERIC_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody GenericResponse updateUser(@PathVariable("userName") final String userName, @RequestBody final UserAdapter userAdapter) {
        LOG.info(String.format("Update user: userName %s", userName));
        try {
            return new GenericResponse<User>(userServices.updateUser(userName, userAdapter));
        } catch (final UserNotFoundException e) {
            return new GenericResponse<String>(e.getErrorMessage());
        } catch (final UserAlreadyExistsException e) {
            return new GenericResponse<String>(e.getErrorMessage());
        }
    }
}