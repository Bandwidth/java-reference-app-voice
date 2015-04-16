package com.catapult.app.example.configuration;

import com.bandwidth.sdk.model.Domain;
import com.catapult.app.example.beans.CatapultUser;
import com.catapult.app.example.services.DomainServices;
import com.catapult.app.example.services.UserServices;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class AppContextAware implements ApplicationContextAware {

    private final static Logger LOG = Logger.getLogger(AppContextAware.class);

    @Autowired
    private DomainServices domainServices;

    @Autowired
    private UserServices userServices;

    @Autowired
    private UserConfiguration userConfiguration;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        // Create a domain when application is ready on container
        CatapultUser catapultUser = userServices.getCatapultUser(userConfiguration.getUserId());
        if (catapultUser == null) {
            catapultUser = new CatapultUser();
        }

        final String domainName = "ud-" + RandomStringUtils.randomAlphanumeric(12);
        final String domainDescription = MessageFormat.format("Sandbox Domain created for user {0}",
                userConfiguration.getUserId());

        try {
            Domain domain = domainServices.createDomain(domainName, domainDescription);
            catapultUser.setDomain(domain);

            LOG.info(MessageFormat.format("Sandbox Domain [{0}] [{1}] created for user [{2}]",
                    domain.getId(), domain.getName(), userConfiguration.getUserId()));
        } catch(final Exception e) {
            LOG.error(MessageFormat.format("Could not create a Domain when App was deployed for user [{0}]",
                    userConfiguration.getUserId()), e);
        }

        userServices.putCatapultUser(userConfiguration.getUserId(), catapultUser);
    }

}