package com.example.mansshop_boot.config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class PropertiesConfig {

    @Bean(name = "jwt")
    public PropertiesFactoryBean jwtPropertiesFactoryBean() throws Exception {
        String jwtPropertiesPath = "jwt.properties";

        return setPropertiesFactoryBean(jwtPropertiesPath);
    }

    @Bean(name = "filePath")
    public PropertiesFactoryBean filePathPropertiesFactoryBean() throws Exception {
        String filePropertiesPath = "filePath.properties";

        return setPropertiesFactoryBean(filePropertiesPath);
    }


    private PropertiesFactoryBean setPropertiesFactoryBean(String path) throws Exception {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        ClassPathResource classPathResource = new ClassPathResource(path);

        propertiesFactoryBean.setLocation(classPathResource);

        return propertiesFactoryBean;
    }
}
