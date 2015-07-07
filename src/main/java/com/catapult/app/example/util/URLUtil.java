package com.catapult.app.example.util;

import javax.servlet.http.HttpServletRequest;

public class URLUtil {

    private static final String APP_BASE_URL = "%s://%s:%d%s";

    /**
     * Configure the incoming callback URL.
     * @return the incoming call callbacks URL.
     */
    public static String getIncomingCallbackUrl(final String appBaseUrl, final String userName) {
        return String.format("%s/users/%s/callback", appBaseUrl, userName);
    }

    /**
     * Configure the outgoing callback URL.
     * @return the outgoing call callback URL.
     */
    public static String getOutgoingCallbackUrl(final String appBaseUrl, final String userName) {
        return String.format("%s/users/%s/outgoing", appBaseUrl, userName);
    }

    /**
     * Configure the static resources URL
     * @return the url for a static resource
     */
    public static String getStaticResourceUrl(final String appBaseUrl, final String resourceFolder, final String resourceName) {
        return String.format("%s/static/%s/%s", appBaseUrl, resourceFolder, resourceName);
    }

    /**
     * Get the server base URL.
     * @param request the http servlet request
     * @return the app base URL.
     */
    public static String getAppBaseUrl(final HttpServletRequest request) {
        return String.format(APP_BASE_URL, request.getScheme(),
                request.getServerName(), request.getServerPort(), request.getContextPath());
    }
}