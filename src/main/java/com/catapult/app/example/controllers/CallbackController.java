package com.catapult.app.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.catapult.app.example.adapters.CallbackAdapter;

@Controller
@RequestMapping("/users")
public class CallbackController {

    
    /**
     * Resource to receive the user callback requests.
     * @param callbackAdapter the callback adapter with the data.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{userName}/callback", headers = "Content-Type=application/json")
    public void receiveCallback(@RequestBody final CallbackAdapter callbackAdapter) {
        
    }
    
    /**
     * Resource to return the user callback requests.
     * @param callbackAdapter the callback adapter with the data.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{userName}/callback", headers = "Content-Type=application/json")
    public void getCallback(@RequestBody final CallbackAdapter callbackAdapter) {
        
    }
}
