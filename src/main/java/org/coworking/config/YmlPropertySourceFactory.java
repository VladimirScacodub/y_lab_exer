package org.coworking.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Класс который конфигурирует PropertySourceFactory для реализации чтения Properties из application.yml файла
 */
public class YmlPropertySourceFactory implements PropertySourceFactory {


    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource.getResource());

        Properties properties = factoryBean.getObject();

        String filename = resource.getResource().getFilename();
        return new PropertiesPropertySource(filename, properties);
    }
}
