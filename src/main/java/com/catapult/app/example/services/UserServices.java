package com.catapult.app.example.services;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.catapult.app.example.exceptions.MissingFieldsException;
import com.catapult.app.example.exceptions.UserAlreadyExistsException;
import com.catapult.app.example.exceptions.UserNotFoundException;

@Service
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

    @Autowired
    private ApplicationServices applicationServices;

    public User createUser(final UserAdapter userAdapter) throws MissingFieldsException, UserAlreadyExistsException,
            AppPlatformException, ParseException, Exception {

        // Validate the adapter fields.
        if (userAdapter == null) {
            throw new MissingFieldsException();
        }

        userAdapter.validate();

        final User currentUser = users.get(userAdapter.getUserName());
        if (currentUser != null) {
            return currentUser;
        }

        final User newUser = new User(userAdapter);

        // Verify if the catapult user has a existing sandbox app domain created.
        CatapultUser currentCatapultUser = catapultUserData.get(userConfiguration.getUserId());
        Domain userDomain;
        if (currentCatapultUser == null) {
            currentCatapultUser = new CatapultUser();
        }

        // Create a domain
        if (currentCatapultUser.getDomain() == null) {
            final String userDomainName = "ud-" + RandomStringUtils.randomAlphanumeric(12);
            final String userDomainDescription = "Sandbox created Domain for user " + userConfiguration.getUserId();
            //Create a new Domain.
            try {
                userDomain = domainServices.createDomain(userDomainName, userDomainDescription);
                currentCatapultUser.setDomain(userDomain);
            } catch(final AppPlatformException e) {
                LOG.error(String.format("Could not create a Domain: %s", e));
                throw e;
            }
        }
        
        // Allocate a number
        final List<PhoneNumber> phoneNumbers = phoneServices.searchAndAllocateANumber(1, "469", false);

        // Create a new Application
        final Application createdApplication = applicationServices.create(userAdapter.getUserName());

        // Associate the number to the application
        phoneNumbers.get(0).setApplicationId(createdApplication.getId());
        phoneServices.updatePhoneNumber(phoneNumbers.get(0));

        // Create a new Endpoint.
        final String userEndpointName = "uep-" + RandomStringUtils.randomAlphanumeric(12);
        final String userEndpointDescription = "Sandbox created Endpoint for user " + userAdapter.getUserName();

        Endpoint createdEndpoint;
        try {
            createdEndpoint = endpointServices.createEndpoint(currentCatapultUser.getDomain().getId(), userEndpointName, 
                    userAdapter.getPassword(), userEndpointDescription);
            //Add the application ID to the endpoint.
            endpointServices.updateEndpoint(createdEndpoint, userAdapter.getPassword(), createdApplication.getId());
        } catch(final AppPlatformException e) {
            LOG.error(String.format("Could not create a Domain: %s", e));
            throw e;
        }
        com.catapult.app.example.beans.Endpoint endpoint = new com.catapult.app.example.beans.Endpoint(createdEndpoint);
        endpoint.setApplicationId(createdApplication.getId());
        newUser.setEndpoint(endpoint);
        newUser.setNumber(phoneNumbers.get(0).getNumber());

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
     * @throws Exception
     */
    public void deleteUser(final String userName) throws Exception {
        final User deletedUser = users.remove(userName);
        if(deletedUser == null) {
            //User not found
            throw new UserNotFoundException(userName);
        }

        //Remove the number
        final CatapultUser currentCatapultUser = catapultUserData.get(userConfiguration.getUserId());
        PhoneNumber deletedNumber = null;
        for(final PhoneNumber currentnumber : currentCatapultUser.getPhoneNumbers()) {
            if(currentnumber.getNumber().equals(deletedUser.getNumber())) {
                deletedNumber = currentnumber;
                currentnumber.delete();
            }
        }
        currentCatapultUser.getPhoneNumbers().remove(deletedNumber);
        //Remove the application
        applicationServices.findApplication(deletedUser.getEndpoint().getApplicationId());
        //Remove the user endpoint
        endpointServices.deleteEndpoint(deletedUser.getDomain().getId(), deletedUser.getEndpoint().getId());
        
        //If there is no users we should remove the domain
        if(users.size() == 0) {
            //Delete the domain
            domainServices.deleteDomain(currentCatapultUser.getDomain().getId());
            currentCatapultUser.setDomain(null);
        }
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