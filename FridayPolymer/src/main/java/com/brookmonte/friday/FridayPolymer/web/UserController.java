/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;
import com.brookmonte.friday.FridayPolymer.manager.admin.AdministrationManager;


/**
 * UserController
 * @author Pete
 *
 */
@Controller
@RequestMapping("/user")
public class UserController
{
    @Autowired
    AdministrationManager adminManager;
    
    /**
     * fridayUser
     * @return
     */
    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody FridayUser fridayUser()
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        FridayUser user = adminManager.getUserByName(currentUser);

        // the user object is converted to JSON and written to the HTTP response
        return user;
    }
}
