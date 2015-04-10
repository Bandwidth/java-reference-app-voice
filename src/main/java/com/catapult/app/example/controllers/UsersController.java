package com.catapult.app.example.controllers;

import java.io.IOException;

import org.apache.log4j.Logger;
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
import com.catapult.app.example.exceptions.MissingFieldsException;
import com.catapult.app.example.exceptions.UserAlreadyExistsException;
import com.catapult.app.example.exceptions.UserNotFoundException;
import com.catapult.app.example.services.DomainServices;
import com.catapult.app.example.services.EndpointServices;
import com.catapult.app.example.services.UserServices;

@Controller
@RequestMapping("/users")
public class UsersController {

    private final static Logger LOG = Logger.getLogger(UsersController.class);

    @Autowired
    private UserServices userServices;

    @Autowired
    private DomainServices domainServices;
    
    @Autowired
    private EndpointServices endpointServices;
    
    @RequestMapping(method = RequestMethod.POST, headers = { "Content-Type=application/json" })
    public @ResponseBody User createUser(@RequestBody final UserAdapter userAdapter) 
            throws AppPlatformException, ParseException, Exception {
        
        LOG.info(String.format("Create user: userName %s", userAdapter.getUserName()));
        User user;
        try {
            user = userServices.createUser(userAdapter);
            LOG.info(String.format("User successfully created: %s", userAdapter.getUserName()));
            return user;
        } catch (final MissingFieldsException e) {
            LOG.error(String.format("Missing fields to create user %s: %s", userAdapter.getUserName(), e));
            throw e;
        } catch (final UserAlreadyExistsException e) {
            LOG.error(String.format("User already exists: %s: %s", userAdapter.getUserName(), e));
            throw e;
        } catch (final AppPlatformException e) {
            LOG.error(String.format("API request returned error for user %s: %s", userAdapter.getUserName(), e));
            throw e;
        } catch (final ParseException e) {
            LOG.error(String.format("Error parsing API response for user: %s: %s", userAdapter.getUserName(), e));
            throw e;
        } catch (final Exception e) {
            LOG.error(String.format("Error creating user %s: %s", userAdapter.getUserName(), e));
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody User getUser(@PathVariable("userName") final String userName) 
            throws UserNotFoundException {
        LOG.info(String.format("Get user: userName %s", userName));
        return userServices.getUser(userName);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String deleteUser(@PathVariable("userName") final String userName) 
            throws UserNotFoundException, AppPlatformException, IOException {
        LOG.info(String.format("Delete user: userId %s", userName));
        userServices.deleteUser(userName);
        return "Deleted userName " + userName;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody User updateUser(@PathVariable("userName") final String userName, @RequestBody final UserAdapter userAdapter) 
            throws UserNotFoundException, UserAlreadyExistsException {
        LOG.info(String.format("Update user: userName %s", userName));
        return userServices.updateUser(userName, userAdapter);
    }
}