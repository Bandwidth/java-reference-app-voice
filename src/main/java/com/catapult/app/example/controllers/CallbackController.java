package com.catapult.app.example.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.catapult.app.example.adapters.CallbackAdapter;
import com.catapult.app.example.services.CallbackServices;
import com.catapult.app.example.util.URLUtil;

import java.text.MessageFormat;
import java.util.List;

@Controller
@RequestMapping("/users")
public class CallbackController {

    @Autowired
    private CallbackServices callbackServices;

    private final static Logger LOG = Logger.getLogger(CallbackController.class);

    /**
     * Resource to receive incoming call callback events
     * @param event
     * @param userName
     * @param request
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{userName}/callback",
                    headers = "Content-Type=application/json")
    public void incomingCallback(@RequestBody final String event,
            @PathVariable("userName") final String userName, final HttpServletRequest request) {
        LOG.info(MessageFormat.format("Received INCOMING LEG callback event [{0}] for userName [{1}]",
                event, userName));
        callbackServices.handleIncomingCallback(event, userName, URLUtil.getAppBaseUrl(request));
    }

    /**
     * Resource to receive outgoing call callback events
     * @param event
     * @param userName
     * @param request
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{userName}/outgoing",
                    headers = "Content-Type=application/json")
    public void outgoingCallback(@RequestBody final String event,
            @PathVariable("userName") final String userName, final HttpServletRequest request) {
        LOG.info(MessageFormat.format("Received OUTGOING LEG callback event [{0}] for userName [{1}]",
                event, userName));
        callbackServices.handleOutgoingCallback(event, userName, URLUtil.getAppBaseUrl(request));
    }

    /**
     * Resource to receive the user callback requests.
     * @param userName
     * @param request
     * @return list of callback events
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{userName}/callback",
                    headers = "Content-Type=application/json")
    public @ResponseBody List<CallbackAdapter> getCallbacks(@PathVariable("userName") final String userName,
            final HttpServletRequest request) {
        LOG.info(MessageFormat.format("Get events for userName [{0}]", userName));
        return callbackServices.getUserCallbacks(userName);
    }

}