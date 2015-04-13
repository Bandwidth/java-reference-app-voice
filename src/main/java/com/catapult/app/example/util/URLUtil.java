package com.catapult.app.example.util;

import javax.servlet.http.HttpServletRequest;

public class URLUtil {

    private static final String APP_BASE_URL = "%s://%s:%d/%s";

    /**
     * Configure the operations callback URL.
     * @return the callbacks URL.
     */
    public static String getCallbacksBaseUrl(final String appBaseUrl, final String userName) {
        return String.format("%s%s/callback", appBaseUrl, userName);
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