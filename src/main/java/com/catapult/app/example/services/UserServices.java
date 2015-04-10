package com.catapult.app.example.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.bandwidth.sdk.AppPlatformException;
import com.bandwidth.sdk.model.Application;
import com.bandwidth.sdk.model.Domain;
import com.bandwidth.sdk.model.Endpoint;
import com.bandwidth.sdk.model.PhoneNumber;
import com.catapult.app.example.adapters.UserAdapter;
import com.catapult.app.example.beans.CatapultUser;
import com.catapult.app.example.beans.User;
import com.catapult.app.example.configuration.EndpointsConfiguration;
import com.catapult.app.example.configuration.UserConfiguration;
import com.catapult.app.example.constants.ParametersConstants;
import com.catapult.app.example.exceptions.MissingFieldsException;
import com.catapult.app.example.exceptions.UserAlreadyExistsException;
import com.catapult.app.example.exceptions.UserNotFoundException;

@Service
@Scope(value = "singleton")
public class UserServices {

    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<String, User>();
    private final ConcurrentHashMap<String, CatapultUser> catapultUserData = new ConcurrentHashMap<String, CatapultUser>();
    
    private final static Logger LOG = Logger.getLogger(UserServices.class);

    @Autowired
    private DomainServices domainServices;
    
    @Autowired
    private EndpointServices endpointServices;
    
    @Autowired
    private UserConfiguration userConfiguration;
    
    @Autowired
    private EndpointsConfiguration endpointsConfiguration;
    
    @Autowired
    private PhoneNumberServices phoneServices;
    
    public User createUser(final UserAdapter userAdapter) 
            throws MissingFieldsException, UserAlreadyExistsException, AppPlatformException, ParseException, Exception {
        //Validate the adapter fields.
        if(userAdapter == null) {
            throw new MissingFieldsException();
        }
        userAdapter.validate();
        final User currentUser = users.get(userAdapter.getUserName());
        if(currentUser != null) {
            return currentUser;
        }
        
        final User newUser = new User(userAdapter);
        //Verify if the catapult user has a existing sandbox app domain created.
        CatapultUser currentCatapultUser = catapultUserData.get(userConfiguration.getUserId());
        Domain userDomain;
        if(currentCatapultUser == null) {
            currentCatapultUser = new CatapultUser();
        }
        
        //Create a domain
        if(currentCatapultUser.getDomain() == null) {
            final String userDomainName = "ud-" + RandomStringUtils.randomAlphanumeric(12);
            final String userDomainDescription = "Sandbox created Domain for user " + userConfiguration.getUserId();
            //Create a new Domain.
            try {
                userDomain = domainServices.createDomain(userDomainName, userDomainDescription);
                currentCatapultUser.setDomain(userDomain);
            } catch(AppPlatformException e) {
                LOG.error(String.format("Could not create a Domain: %s", e));
                throw e;
            }
        }
        
        //Allocate a number
        final List<PhoneNumber> phoneNumbers = phoneServices.searchAndAllocateANumber(1, "469", false);
        
        //Create a new Application
        final Map<String, Object> applicationParameters = new HashMap<String, Object>();
        final String userApplicationDescription = "Sandbox created Application for user " + userAdapter.getUserName();
        //Define the application description
        applicationParameters.put(ParametersConstants.NAME, userApplicationDescription);
        //Define the callback URL
        applicationParameters.put(ParametersConstants.INCOMING_CALL_URL, endpointsConfiguration.getCallbacksBaseUrl());
        final Application createdApplication = Application.create(userConfiguration.getUserClient(), applicationParameters);
        
        //Associate the number to the application
        phoneNumbers.get(0).setApplicationId(createdApplication.getId());
        phoneServices.updatePhoneNumber(phoneNumbers.get(0));
        
        //Create a new Endpoint.
        final String userEndpointName = "uep-" + RandomStringUtils.randomAlphanumeric(12);
        final String userEndpointDescription = "Sandbox created Endpoint for user " + userAdapter.getUserName();
        
        Endpoint createdEndpoint;
        
        try {
            createdEndpoint = endpointServices.createEndpoint(currentCatapultUser.getDomain().getId(), userEndpointName, 
                    userAdapter.getPassword(), userEndpointDescription);
        } catch(AppPlatformException e) {
            LOG.error(String.format("Could not create a Domain: %s", e));
            throw e;
        }
        
        newUser.setEndpoint(new com.catapult.app.example.beans.Endpoint(createdEndpoint));
        newUser.setPhoneNumber(phoneNumbers.get(0).getNumber());
        
        users.putIfAbsent(userAdapter.getUserName(), newUser);
        currentCatapultUser.getPhoneNumbers().addAll(phoneNumbers);
        catapultUserData.putIfAbsent(userConfiguration.getUserId(), currentCatapultUser);
        
        newUser.setPassword(null);
        return newUser;
    }

    /**
     * Get an user.
     * @param userName the user name to find.
     * @return the found user.
     * @throws UserNotFoundException
     */
    public User getUser(final String userName) throws UserNotFoundException {
        final User user = users.get(userName);
        if(user == null) {
            //User not found
            throw new UserNotFoundException(userName);
        }
        user.setPassword(null);
        return user;
    }

    /**
     * Delete the user.
     * @param userName the user name.
     * @throws UserNotFoundException 
     * @throws IOException 
     * @throws AppPlatformException 
     * @throws Exception
     */
    public void deleteUser(final String userName) throws UserNotFoundException, AppPlatformException, IOException {
        final User deletedUser = users.remove(userName);
        if(deletedUser == null) {
            //User not found
            throw new UserNotFoundException(userName);
        }
        
        //delete the number
        final CatapultUser currentCatapultUser = catapultUserData.get(userConfiguration.getUserId());
        PhoneNumber deletedNumber = null;
        for(final PhoneNumber currentnumber : currentCatapultUser.getPhoneNumbers()) {
            if(currentnumber.getNumber().equals(deletedUser.getPhoneNumber())) {
                deletedNumber = currentnumber;
                currentnumber.delete();
            }
        }
        currentCatapultUser.getPhoneNumbers().remove(deletedNumber);
        
        //delete the user endpoint
        endpointServices.deleteEndpoint(deletedUser.getDomain().getId(), deletedUser.getEndpoint().getId());
    }

    /**
     * 
     * @param name the user name to update.
     * @param newUserInfo the new user info.
     * @return the updated user.
     * @throws UserNotFoundException
     * @throws UserAlreadyExistsException 
     */
    public User updateUser(final String name, final UserAdapter newUserInfo) throws UserNotFoundException, UserAlreadyExistsException { 
        final User user = users.remove(name);
        if(user == null) {
            //User not found
            throw new UserNotFoundException(name);
        }
        //merge the user properties
        user.mergeProperties(newUserInfo);
        if(users.get(user.getUserName()) != null) {
            throw new UserAlreadyExistsException(user.getUserName());
        }
        users.putIfAbsent(newUserInfo.getUserName(), user);
        user.setPassword(null);
        return user;
    }
}
