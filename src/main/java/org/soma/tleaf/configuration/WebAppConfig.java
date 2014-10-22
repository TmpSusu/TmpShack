package org.soma.tleaf.configuration;

import org.soma.tleaf.couchdb.CouchDbConn;
import org.soma.tleaf.couchdb.CouchDbConnImpl;
import org.soma.tleaf.couchdb.UserDao;
import org.soma.tleaf.couchdb.UserDaoImpl;
import org.soma.tleaf.dao.RestApiDao;
import org.soma.tleaf.dao.internal.RestApiDaoImple;
import org.soma.tleaf.service.RestApiService;
import org.soma.tleaf.service.internal.RestApiServiceImple;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


@Configuration
@EnableWebMvc  //same as <mvc:annotation-driven/>
@ComponentScan(basePackages = {"org.soma.tleaf"})//same as <context:component-scan base-package="com.tleaf.lifelog"/>
@PropertySource("classpath:couchdb.properties")

public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //...
    }

    //Set default servlet handler, this is the same as <mvc:default-servlet-handler/>
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    //Add bean for InternalResourceViewResolver
    @Bean
    public InternalResourceViewResolver getInternalResourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    //properties file
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public CouchDbConn couchDbConn() {
        return new CouchDbConnImpl();
    }
    
    @Bean
    public UserDao userDao() {
        return new UserDaoImpl();
    }
  
	@Bean
	public RestApiService resrApiService(){
		return new RestApiServiceImple();
	}
	
	@Bean
	public RestApiDao restApiDao(){
		return new RestApiDaoImple();
	}
}
