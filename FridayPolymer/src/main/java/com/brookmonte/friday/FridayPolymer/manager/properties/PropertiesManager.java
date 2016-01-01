/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author Pete
 * 
 */
@Component
public class PropertiesManager
{
    private static org.apache.log4j.Logger log = Logger.getLogger(PropertiesManager.class);

    private Properties dbProperties = null;
    private Properties fridayProperties = null;
    
    /**
     * getDbProperties
     * 
     * @return the dbProperties
     */
    public Properties getDbProperties()
    {
        if (dbProperties != null)
        {
            return dbProperties;
        }

        dbProperties = new Properties();
        InputStream fis = this.getClass().getResourceAsStream("/META-INF/spring/database.properties");
        try
        {
            dbProperties.load(fis);
        }
        catch (IOException e1)
        {
            dbProperties = null;
            String msg = "Could not read properties from database.properties.";
            log.error(msg, e1);
            throw new RuntimeException(msg, e1);
        }

        return dbProperties;
    }
    

    /**
     * getFridayProperties
     * 
     * @return
     */
    public Properties getFridayProperties()
    {
        if (fridayProperties != null)
        {
            return fridayProperties;
        }

        fridayProperties = new Properties();
        InputStream fis = this.getClass().getResourceAsStream("/application.properties");
        try
        {
            fridayProperties.load(fis);
        }
        catch (IOException e1)
        {
            fridayProperties = null;
            String msg = "Could not read properties from application.properties.";
            log.error(msg, e1);
            throw new RuntimeException(msg, e1);
        }

        return fridayProperties;
    }    
}
