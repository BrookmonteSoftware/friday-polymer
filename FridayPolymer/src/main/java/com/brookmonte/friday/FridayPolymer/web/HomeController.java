/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * LoginController
 * @author Pete
 *
 */
@Controller
@RequestMapping("/")
public class HomeController
{
    /**
     * home
     * @return
     */
    @RequestMapping(method=RequestMethod.GET)
    public String home()
    {
        return "/index";
    }
}
