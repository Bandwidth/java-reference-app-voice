package com.catapult.app.example.services;

import java.io.IOException;

import com.bandwidth.sdk.model.Domain;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.bandwidth.sdk.AppPlatformException;
import com.catapult.app.example.configuration.UserConfiguration;

@Service
public class DomainServices {

    /**
     * Create a Domain
     * @return the created Domain.
     * @throws ParseException 
     * @throws AppPlatformException 
     * @throws Exception 
     */
    public com.bandwidth.sdk.model.Domain createDomain(final String name, 
            final String description) throws AppPlatformException, ParseException, Exception {
        return Domain.create(name, description);
    }
    
    /**
     * Delete a Domain
     * @throws IOException 
     * @throws AppPlatformException 
     * @throws Exception 
     */
    public void deleteDomain(final String domainId) throws AppPlatformException, IOException {
        Domain.delete(domainId);
    }
}
