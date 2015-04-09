package com.catapult.app.example.services;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.bandwidth.sdk.AppPlatformException;
import com.catapult.app.example.configuration.UserConfiguration;

@Service
@Scope(value = "singleton")
public class EndpointServices {
    
    @Autowired
    private UserConfiguration userConfiguration;
    
    /**
     * Create an Endpoint
     * @return the created Endpoint.
     * @throws ParseException 
     * @throws AppPlatformException 
     * @throws Exception 
     */
    public com.bandwidth.sdk.model.Endpoint createEndpoint(final String domainId, 
            final String name, final String password, final String description) throws AppPlatformException, ParseException, Exception {
        return com.bandwidth.sdk.model.Endpoint.create(userConfiguration.getUserClient(), domainId, name, 
                password, description);
    }
    
    /**
     * Delete an Endpoint
     * @throws IOException 
     * @throws AppPlatformException 
     * @throws Exception 
     */
    public void deleteEndpoint(final String domainId, final String endpointId) throws AppPlatformException, IOException {
        com.bandwidth.sdk.model.Endpoint.delete(userConfiguration.getUserClient(), domainId, endpointId);
    }
}
