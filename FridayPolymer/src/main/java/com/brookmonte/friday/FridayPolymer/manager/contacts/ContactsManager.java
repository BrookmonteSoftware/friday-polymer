/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.contacts;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.brookmonte.friday.FridayPolymer.dao.contacts.ContactsDao;
import com.brookmonte.friday.FridayPolymer.domain.contact.FridayUserContact;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;
import com.brookmonte.friday.FridayPolymer.manager.admin.AdministrationManager;

/**
 * @author Pete
 *
 */
@Component
public class ContactsManager
{
    @SuppressWarnings("unused")
    private static org.apache.log4j.Logger log = Logger.getLogger(ContactsManager.class);
    
    @Autowired
    ContactsDao contactsDao;
    
    @Autowired
    AdministrationManager adminManager;

    /**
     * getUserContactsByUserId
     * @param user
     * @return
     * @throws Exception 
     */
    @PreAuthorize("isAuthenticated()")
    public List<FridayUserContact> getUserContactsByUserId(FridayUser user) throws Exception
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        FridayUser u = adminManager.getUserByName(currentUser);
        
        if (!u.getUserId().equalsIgnoreCase(user.getUserId()))
        {
            throw new Exception("Unauthorized");
        }
        
        return contactsDao.getUserContactsByUserId(user);
    }

    /**
     * getUserContactsByUserId
     * @param user
     * @return
     * @throws Exception 
     */
    @PreAuthorize("isAuthenticated()")
    public FridayUserContact getUserContactByUserContactId(String userContactId) throws Exception
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        FridayUser u = adminManager.getUserByName(currentUser);
        
        FridayUserContact contact = contactsDao.getUserContactByUserContactId(userContactId);
        
        if (!contact.getUserId().equalsIgnoreCase(u.getUserId()))
        {
            throw new Exception("Unauthorized");            
        }
        
        return contact;
    }    
    
    /**
     * addOrUpdateContact
     * @param contact
     * @throws Exception 
     */
    @PreAuthorize("isAuthenticated()")
    public void addOrUpdateContact(FridayUserContact contact) throws Exception
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        FridayUser u = adminManager.getUserByName(currentUser);
        
        if (!u.getUserId().equalsIgnoreCase(contact.getUserId()))
        {
            throw new Exception("Unauthorized");
        }
        
        if (StringUtils.isEmpty(contact.getUserContactId()))
        {
            contactsDao.insertContact(contact);
        }
        else
        {
            contactsDao.updateContact(contact);
        }
    }

    /**
     * deleteContact
     * @param contact
     * @throws Exception 
     */
    @PreAuthorize("isAuthenticated()")
    public void deleteContact(FridayUserContact contact) throws Exception
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        FridayUser u = adminManager.getUserByName(currentUser);
        
        if (!u.getUserId().equalsIgnoreCase(contact.getUserId()))
        {
            throw new Exception("Unauthorized");
        }
        
        contactsDao.deleteContact(contact);
    }
}
