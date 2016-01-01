/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.fridayUtils;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @author Pete
 * 
 */

public class ApplicationProperties
{
    @Autowired
    private static ApplicationContext appContext;
    
    public static String getProperty(String name)
    {        
        String value = null;

        // TODO: this won't work with Spring boot
        Properties prop = (Properties) appContext.getBean("configProperties");
        
        if (prop != null)
        {
            value = prop.getProperty(name);
        }
        
        return value;
    }
    
    public static String getFortuneCookie(String cookieProperty)
    {        
        String value = null;

        // TODO: this won't work with Spring boot
        Properties prop = (Properties) appContext.getBean("fortuneCookies");
        
        if (prop != null)
        {
            value = prop.getProperty(cookieProperty);
        }
        
        return value;
    }    
}
