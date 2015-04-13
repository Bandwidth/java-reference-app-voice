package com.catapult.app.example.configuration;

import org.springframework.stereotype.Component;

@Component
public class EndpointsConfiguration {

    private final String appBaseUrlFormat = "%s://%s:%d/%s";

    /**
     * Configure the operations callback URL.
     * @return the callbacks URL.
     */
    public String getCallbacksBaseUrl(final String appBaseUrl, final String userName) {
        return String.format("%s%s/callback", appBaseUrl, userName);
    }
    
    /**
     * Get the server base URL.
     * @param scheme the Scheme
     * @param serverName the server name.
     * @param port the server port.
     * @param contextPath the contextPath.
     * @return the app base URL.
     */
    public String getAppBaseUrl(final String scheme, final String serverName, final int port, final String contextPath) {
        return String.format(appBaseUrlFormat, scheme, serverName, port, contextPath);
    }
}