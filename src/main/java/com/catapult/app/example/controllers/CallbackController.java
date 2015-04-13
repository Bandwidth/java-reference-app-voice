package com.catapult.app.example.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.catapult.app.example.adapters.CallbackAdapter;
import com.catapult.app.example.services.CallbackServices;

@Controller
@RequestMapping("/users")
public class CallbackController {

    @Autowired
    private CallbackServices callbackServices;
    
    /**
     * Resource to receive the user callback requests.
     * @param callbackAdapter the callback adapter with the data.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{userName}/callback", headers = "Content-Type=application/json")
    public void receiveCallback(@RequestBody final CallbackAdapter callbackAdapter,
                                @PathVariable("userName") final String userName) {
        callbackServices.handleCallback(callbackAdapter, userName);
    }
}