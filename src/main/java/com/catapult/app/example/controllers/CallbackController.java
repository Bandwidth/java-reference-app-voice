package com.catapult.app.example.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.catapult.app.example.services.CallbackServices;
import com.catapult.app.example.util.URLUtil;

@Controller
@RequestMapping("/users")
public class CallbackController {

    @Autowired
    private CallbackServices callbackServices;

    private final static Logger LOG = Logger.getLogger(CallbackController.class);
    
    /**
     * Resource to receive the user callback requests.
     * @param event the callback event with the data.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{userName}/callback", headers = "Content-Type=application/json")
    public void receiveCallback(@RequestBody final String event,
                                @PathVariable("userName") final String userName, final HttpServletRequest request) {
        LOG.info(String.format("Received callback for event: %s. UserName:  %s", event, userName));
        callbackServices.handleCallback(event, userName, URLUtil.getAppBaseUrl(request));
    }
}