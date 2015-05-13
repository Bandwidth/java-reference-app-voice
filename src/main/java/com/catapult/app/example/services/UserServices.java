package com.catapult.app.example.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bandwidth.sdk.AppPlatformException;
import com.bandwidth.sdk.model.Application;
import com.bandwidth.sdk.model.Endpoint;
import com.bandwidth.sdk.model.PhoneNumber;
import com.catapult.app.example.adapters.UserAdapter;
import com.catapult.app.example.beans.CatapultUser;
import com.catapult.app.example.beans.User;
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
    private PhoneNumberServices phoneServices;

    @Autowired
    private ApplicationServices applicationServices;

    public User createUser(final UserAdapter userAdapter, final String baseAppUrl) throws MissingFieldsException, UserAlreadyExistsException,
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

        // The Domain is created when app is deployed on container. Check AppContextAware.java
        // The catapultUser must exists at this point
        CatapultUser catapultUser = catapultUserData.get(userConfiguration.getUserId());

        // Allocate a number
        final List<PhoneNumber> phoneNumbers = phoneServices.searchAndAllocateANumber(1, "469", false);

        // Create a new Application
        final Application createdApplication = applicationServices.create(userAdapter.getUserName(), baseAppUrl);

        // Associate the number to the application
        phoneNumbers.get(0).setApplicationId(createdApplication.getId());
        phoneServices.updatePhoneNumber(phoneNumbers.get(0));

        // Create a new Endpoint.
        final String userEndpointName = "uep-" + RandomStringUtils.randomAlphanumeric(12);
        final String userEndpointDescription = "Sandbox created Endpoint for user " + userAdapter.getUserName();

        Endpoint createdEndpoint;
        try {
            createdEndpoint = endpointServices.createEndpoint(catapultUser.getDomain().getId(), userEndpointName,
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
        newUser.setPhoneNumber(phoneNumbers.get(0).getNumber());

        newUser.setUserUrl(baseAppUrl + "/users/" + userAdapter.getUserName());
        users.putIfAbsent(userAdapter.getUserName(), newUser);
        catapultUser.getPhoneNumbers().addAll(phoneNumbers);

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
        user.setUserUrl(null);
        return user;
    }
    
    /**
     * Get an user.
     * @param userName the user name to find.
     * @return the found user.
     */
    public List<User> listUsers() {
        List<User> usersList = new ArrayList<User>();
        for(User currentUser : users.values()) {
            currentUser.setPassword(null);
            usersList.add(currentUser);
        }
        
        return usersList;
    }

    /**
     *
     * @param userId
     * @return
     */
    public CatapultUser getCatapultUser(String userId) {
        return catapultUserData.get(userId);
    }

    /**
     *
     * @param userId
     * @param catapultUser
     */
    public void putCatapultUser(String userId, CatapultUser catapultUser) {
        catapultUserData.putIfAbsent(userId, catapultUser);
    }

    /**
     * Delete the user.
     * @param userName the user name.
     * @throws Exception
     */
    public void deleteUser(final String userName) throws Exception {
        final User deletedUser = users.remove(userName);
        if (deletedUser == null) {
            //User not found
            throw new UserNotFoundException(userName);
        }

        //Remove the number
        final CatapultUser catapultUser = catapultUserData.get(userConfiguration.getUserId());

        PhoneNumber deletedNumber = null;
        for (final PhoneNumber number : catapultUser.getPhoneNumbers()) {
            if (number.getNumber().equals(deletedUser.getPhoneNumber())) {
                deletedNumber = number;
                number.delete();
                break;
            }
        }

        catapultUser.getPhoneNumbers().remove(deletedNumber);

        //Remove the application
        Application application = applicationServices.findApplication(deletedUser.getEndpoint().getApplicationId());
        if (application != null) {
            application.delete();
        }

        //Remove the user endpoint
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
        if (user == null) {
            //User not found
            throw new UserNotFoundException(name);
        }
        //merge the user properties
        user.mergeProperties(newUserInfo);
        if (users.get(user.getUserName()) != null) {
            throw new UserAlreadyExistsException(user.getUserName());
        }
        users.putIfAbsent(newUserInfo.getUserName(), user);
        user.setPassword(null);
        return user;
    }
}
