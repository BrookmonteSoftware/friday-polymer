/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * LoginController
 * @author Pete
 *
 */
@Controller

public class HomeController
{
    /**
     * home
     * @return
     */
    @RequestMapping("/")
    public String home()
    {
        return "/index";
    }
    
    @RequestMapping("/friday-app-page.html")
    public String app()
    {
        return "/friday-app";
    }
}
