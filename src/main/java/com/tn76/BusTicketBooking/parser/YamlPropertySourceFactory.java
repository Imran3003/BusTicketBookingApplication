/**
 * YamlPropertySourceFactory.java
 *
 * @module com.nmsworks.rananalyticsbe.sftp.parser
 * @author Oviya R (oviya@nmsworks.co.in)
 * @created Mar 31, 2022
 */
package com.tn76.BusTicketBooking.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory
{
    private static final Logger logger = LogManager.getLogger();

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException
    {

        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());

        logger.info("File = " + resource.getResource().getFilename());

        Properties properties = factory.getObject();

        return new PropertiesPropertySource(Objects.requireNonNull(resource.getResource().getFilename()), properties);
    }
}
