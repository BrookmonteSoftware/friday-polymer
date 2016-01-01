package com.brookmonte.friday.FridayPolymer;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@SpringBootApplication
@ComponentScan
@Configuration
public class FridayPolymerApplication
{
    // set the data source parameters from the application.properties file
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    @SuppressWarnings("unused")
    private static ApplicationContext appContext;

    /**
     * static main
     * @param args
     */
    public static void main(String[] args)
    {
        appContext = SpringApplication.run(FridayPolymerApplication.class, args);
    }

    /**
     * getDataSource
     * 
     * Construct the database data source
     * 
     * @return
     */
    @Bean
    public DataSource getDataSource()
    {        
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    /**
     * transactionManager
     * 
     * Create the database transaction manager
     * 
     * @return
     */
    @Bean
    public DataSourceTransactionManager transactionManager()
    {
        return new DataSourceTransactionManager(getDataSource());
    }

    /**
     * sqlSessionFactory
     * 
     * Create and configure the MyBatis SQL session.
     * 
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception
    {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        
        // set the MySQL data source in the MyBatis SQL session
        sessionFactory.setDataSource(getDataSource());
        
        // load the MyBatis configuration file as a resource
        // this includes the location of the mapper files,
        // so no need to use the MapperScan annotation
        ClassPathResource mybatisCfgResource = new ClassPathResource("myBatis/mybatis-config-mysql.xml");
                
        // set the MyBatis configuration location in the MyBatis SQL session
        sessionFactory.setConfigLocation(mybatisCfgResource);

        return sessionFactory.getObject();
    }
}
