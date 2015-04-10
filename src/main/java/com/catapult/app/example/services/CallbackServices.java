package com.catapult.app.example.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.catapult.app.example.adapters.CallbackAdapter;
import com.catapult.app.example.beans.BridgeDetails;

@Service
public class CallbackServices {

    
    private Map<String, Map<String, BridgeDetails>> a;
    
    /**
     * 
     * @param callbackAdapter
     */
    public void handleCallback(final CallbackAdapter callbackAdapter) {
        
        if("incomingcall".equalsIgnoreCase(callbackAdapter.getEventType())) {
            
            
            if(callbackAdapter.getFrom() != null && callbackAdapter.getFrom().startsWith("sip:")) {
                
                
                
            } else if(callbackAdapter.getTo() != null && callbackAdapter.getTo().startsWith("sip:")) {
                
                
                
            } else {
                //ERROR
            }
        } else if("answer".equalsIgnoreCase(callbackAdapter.getEventType())) {
            
            
            
            
            
            
            
            
        } else {
            //other event
        }
    }
}