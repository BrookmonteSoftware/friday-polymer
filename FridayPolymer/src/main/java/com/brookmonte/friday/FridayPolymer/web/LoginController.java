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
@RequestMapping("/login")
public class LoginController
{
    /**
     * login
     * @return
     */
    @RequestMapping(method=RequestMethod.GET)
    public String login()
    {
        return "/friday-login";
    }
}
