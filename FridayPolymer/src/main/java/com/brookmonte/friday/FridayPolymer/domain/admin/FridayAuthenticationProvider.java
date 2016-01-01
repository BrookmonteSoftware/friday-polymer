/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.admin;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.brookmonte.friday.FridayPolymer.dao.admin.UserDao;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;
import com.brookmonte.friday.FridayPolymer.manager.properties.PropertiesManager;

/**
 * @author Pete
 *
 */
@Component
public class FridayAuthenticationProvider implements AuthenticationProvider
{
    private static org.apache.log4j.Logger log = Logger.getLogger(FridayAuthenticationProvider.class);

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private PropertiesManager propertiesManager;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        final Integer defaultMaxLoginAttempts = 5;
        Integer maxLoginAttempts = 5;
        
        Properties props = propertiesManager.getFridayProperties();
        
        String loginAttemptsStr = props.getProperty("maxLoginAttempts");
        
        if (!StringUtils.isEmpty(loginAttemptsStr))
        {
          maxLoginAttempts = Integer.decode(loginAttemptsStr);
        }
        
        if (maxLoginAttempts < 1)
        {
          log.warn("Invalid maxLoginAttempts in friday.properties. Defaulting to 5.");
          maxLoginAttempts = defaultMaxLoginAttempts;
        }
        
        LoginRecord login = new LoginRecord();
        login.setLoginSuccess(true);
        login.setLoginDateTime(new DateTime());   // set login attempt date and time to now
        String failureMessage = "";
        
        try
        {      
          AuthenticationException authException = null;
          
          if (authentication == null)
          {
            failureMessage = "Authentication object is not defined";
            login.setFailureMessage(failureMessage);
            login.setLoginSuccess(false);
            authException = new AuthenticationCredentialsNotFoundException(failureMessage);
            throw authException;
          }

          WebAuthenticationDetails authDetails = (WebAuthenticationDetails) authentication.getDetails();
          
          if (authDetails != null)
          {
            String userIpAddress = authDetails.getRemoteAddress();
            login.setIpAddress(userIpAddress);
          }
          
          if (StringUtils.isEmpty((String)authentication.getPrincipal()))
          {
            failureMessage = "User name was not provided";
            login.setFailureMessage(failureMessage);
            login.setLoginSuccess(false);
            authException = new UsernameNotFoundException(failureMessage);
            throw authException;        
          }
          
          login.setUserName((String)authentication.getPrincipal());
          
          FridayUser user = this.userDao.getUserByName((String)authentication.getPrincipal());
          
          if (user == null)
          {
            failureMessage = "No user '"
                + (String)authentication.getPrincipal() + "' exists";
            login.setFailureMessage(failureMessage);
            login.setLoginSuccess(false);
            authException = new UsernameNotFoundException(failureMessage);
            throw authException;      
          }

          login.setUserId(user.getUserId());
          
          if (!user.getEnabled())
          {
            failureMessage = "The user account has been disabled. Contact My Friday for help.";
            login.setFailureMessage(failureMessage);
            login.setLoginSuccess(false);
            authException = new DisabledException(failureMessage);
            throw authException;
          }
          
          // encode the password with the username salt
          final ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);
          final String encodedPwd = encoder.encodePassword((String)authentication.getCredentials(),
              (String)authentication.getPrincipal());
          
          // check the encoded password against the user's encoded password
          if (!encodedPwd.equals(user.getPassword()))
          {
            // bad password
            failureMessage = "Invalid password";
            login.setFailureMessage(failureMessage);
            login.setLoginSuccess(false);
            authException = new BadCredentialsException(failureMessage);
            throw authException;
          }
          
          // TODO: check and set permissions and granted authorities
          // check to see if the password is temporary, insert login history, check login attempts
          
          //List<Permission> permissions = adminManager.getAllPermissions();
       
          // right now, everyone logs in with ADMIN role
          ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
          authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
          
//          for (Permission permission : permissions)
//          {
//            if (permissionMgr.getEffectivePermissionValueForUser(user, permission.getPermissionName()))
//            {
//              GrantedAuthorityImpl grantedAuth = new GrantedAuthorityImpl(permission.getPermissionName());
//              authorities.add(grantedAuth);
//            }
//          }
      
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
              authentication.getCredentials(), authorities);
          
          SecurityContextHolder.getContext().setAuthentication(authToken);
          
          log.info("\n*** LOGIN:" + login.toString());
          
          return authToken; 
        }
        catch (RuntimeException e)
        {
          // log the error
          log.error("\n*** LOGIN:" + login.toString(), e);
          
          throw e;      
        }
    }

    @Override
    public boolean supports(Class<? extends Object> authentication)
    {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
