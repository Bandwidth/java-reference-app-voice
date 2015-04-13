package com.catapult.app.example.services;

import java.io.IOException;

import com.bandwidth.sdk.model.Endpoint;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.bandwidth.sdk.AppPlatformException;
import com.catapult.app.example.configuration.UserConfiguration;

@Service
public class EndpointServices {

    /**
     * Create an Endpoint
     * @return the created Endpoint.
     * @throws ParseException 
     * @throws AppPlatformException 
     * @throws Exception 
     */
    public Endpoint createEndpoint(final String domainId,
            final String name, final String password, final String description) throws AppPlatformException, ParseException, Exception {
        return Endpoint.create(domainId, name, password, description);
    }

    /**
     * Delete an Endpoint
     * @throws IOException 
     * @throws AppPlatformException 
     * @throws Exception 
     */
    public void deleteEndpoint(final String domainId, final String endpointId) throws AppPlatformException, IOException {
        Endpoint.delete(domainId, endpointId);
    }
    
    /**
     * Update an Endpoint
     * @throws AppPlatformException 
     * @throws ParseException 
     * @throws Exception 
     * @throws IOException
     */
    public void updateEndpoint(final Endpoint endpoint, final String endpointPassword, final String applicationId) 
            throws AppPlatformException, ParseException, Exception, IOException {
        Endpoint.update(endpoint.getDomainId(), endpoint.getId(), endpointPassword, applicationId, endpoint.isEnabled());
    }

}
