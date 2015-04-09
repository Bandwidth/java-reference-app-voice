package com.catapult.app.example.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.bandwidth.sdk.model.AvailableNumber;
import com.bandwidth.sdk.model.PhoneNumber;
import com.catapult.app.example.configuration.UserConfiguration;
import com.catapult.app.example.constants.ParametersConstants;
import com.catapult.app.example.exceptions.AllocatePhoneNumberException;
import com.catapult.app.example.exceptions.SearchPhoneNumberException;

@Service
@Scope(value = "singleton")
public class PhoneNumberServices {

    private final Logger LOG = Logger.getLogger(PhoneNumberServices.class.getName());
    
    @Autowired
    private UserConfiguration userConfiguration;
    
    /**
     * Search for an available number.
     * @param quantity the number quantity
     * @param state the user state.
     * @param isToolFree if the requested number must be a toolfree number.
     * @return the available number.
     * @throws SearchPhoneNumberException 
     */
    public List<AvailableNumber> searchPhoneNumber(final int quantity, final String areaCode, final boolean isToolFree) throws SearchPhoneNumberException {
        try {
            final Map<String, Object> params = new HashMap <String, Object>();
            params.put(ParametersConstants.QUANTITY, quantity);
            params.put(ParametersConstants.AREA_CODE, areaCode);
            if(isToolFree) {
                return AvailableNumber.searchTollFree(userConfiguration.getUserClient(), params);
            }
            return AvailableNumber.searchLocal(userConfiguration.getUserClient(), params);
        } catch (final Exception e) {
            throw new SearchPhoneNumberException();
        }
    }
    
    /**
     * Allocate a number.
     * @param availableNumber the available number to allocate.
     * @return the allocated number
     * @throws AllocatePhoneNumberException 
     */
    public PhoneNumber allocatePhoneNumber(final AvailableNumber availableNumber) throws AllocatePhoneNumberException {
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put(ParametersConstants.NUMBER, availableNumber.getNumber());
            return PhoneNumber.create(userConfiguration.getUserClient(), params);
        } catch (final Exception e) {
            throw new AllocatePhoneNumberException();
        }
    }
    
    /**
     * Search and allocate numbers;
     * @param quantity
     * @param state the user state.
     * @param isToolFree if it must be toolfreeNumbers or not
     * @return the list of the allocated numbers
     * @throws AllocatePhoneNumberException .
     */
    public List<PhoneNumber> searchAndAllocateANumber(int quantity, final String areaCode, final boolean isToolFree) 
            throws AllocatePhoneNumberException {
        
        int maxAttempts = 3;
        int currentAttempt = 0;
        int requestedQuantity = quantity;
        
        List<PhoneNumber> allocatedNumbers = new ArrayList<PhoneNumber>();
        while(allocatedNumbers.size() < requestedQuantity && currentAttempt < maxAttempts) {
            try {
                List<AvailableNumber> foundNumbers = searchPhoneNumber(quantity, areaCode, isToolFree); 
                for(AvailableNumber currentAvailableNumber : foundNumbers) {
                    if(foundNumbers.size() < quantity) {
                        LOG.log(Level.INFO, String.format("Server returned only %s numbers instead of %s numbers.", foundNumbers.size(), quantity));
                    }
                    allocatedNumbers.add(allocatePhoneNumber(currentAvailableNumber));
                    quantity--;
                }
            } catch (SearchPhoneNumberException | AllocatePhoneNumberException e) {
                //Continue the flow to try again
                LOG.log(Level.SEVERE, String.format("Faced some problems while searching/allocating numbers: %s", e));
            }
            currentAttempt++;
        }

        if(allocatedNumbers.size() < requestedQuantity){
            for(PhoneNumber currentnumber : allocatedNumbers) {
                try {
                    currentnumber.delete();
                } catch (IOException e) {
                    //Ignored in order to try to delete all other allocated numbers.
                }
            }
            throw new AllocatePhoneNumberException();
        }
        return allocatedNumbers;
    }
    
    /**
     * Update a phoneNumber.
     * @param phoneNumber the phoneNumber to update
     * @throws Exception SDK exception.
     */
    public void updatePhoneNumber(final PhoneNumber phoneNumber) throws Exception {
        phoneNumber.commit();
    }
}