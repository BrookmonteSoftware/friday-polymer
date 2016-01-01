/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brookmonte.friday.FridayPolymer.domain.contact.FridayUserContact;
import com.brookmonte.friday.FridayPolymer.domain.contact.FridayUserContactDetails;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;
import com.brookmonte.friday.FridayPolymer.manager.admin.AdministrationManager;
import com.brookmonte.friday.FridayPolymer.manager.contacts.ContactsManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Pete
 *
 */
@Controller
public class ContactsController
{
    @SuppressWarnings("unused")
    private static org.apache.log4j.Logger log = Logger.getLogger(ContactsController.class);

    @Autowired
    AdministrationManager                  adminManager;

    @Autowired
    ContactsManager                        contactsManager;

    /**
     * getContactsForUser
     * 
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "/getContactsForUser" })
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String getContactsForUser() throws Exception
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        FridayUser u = adminManager.getUserByName(currentUser);

        List<FridayUserContact> userContacts = contactsManager.getUserContactsByUserId(u);

        ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
        String json = writer.writeValueAsString(userContacts);

        return json;
    }

    /**
     * addOrUpdateContact
     * 
     * @param contactMessage
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = { "/addOrUpdateContact" }, consumes = "application/json")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String addOrUpdateContact(@RequestBody Map<String, Object> contactMessage) throws JsonProcessingException
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        FridayUser u = adminManager.getUserByName(currentUser);

        try
        {
            if (StringUtils.isEmpty((String) contactMessage.get("familyName"))
            || StringUtils.isEmpty((String) contactMessage.get("givenName")))
            {
                return "ERROR: Not all required fields were submitted.";
            }
            
            FridayUserContact contact = new FridayUserContact();
            contact.setUserContactId((String) contactMessage.get("userContactId"));
            contact.setUserId(u.getUserId());
            contact.setFamilyName((String) contactMessage.get("familyName"));
            contact.setGivenName((String) contactMessage.get("givenName"));          

            if (!StringUtils.isEmpty((String) contactMessage.get("middleName")))
            {
                contact.setMiddleName((String) contactMessage.get("middleName"));
            }

            String relationship = (String) contactMessage.get("relationship");
            if (StringUtils.isEmpty(relationship))
            {
                contact.setRelationship("OTHER");
            }
            else
            {
                contact.setRelationship((String) contactMessage.get("relationship"));
            }

            String relationshipStrength = (String) contactMessage.get("relationshipStrength");
            
            if (!StringUtils.isEmpty(relationshipStrength))
            {
                try
                {
                    contact.setRelationshipStrength(Integer.parseInt(relationshipStrength));
                }
                catch(NumberFormatException nfe)
                {
                    contact.setRelationshipStrength(5);
                }
            }
            else
            {
                contact.setRelationshipStrength(5);
            }
            
            if (!StringUtils.isEmpty((String) contactMessage.get("contactPicture")))
            {
                contact.setContactPicture((String) contactMessage.get("contactPicture"));
            }

            @SuppressWarnings("unchecked")
            ArrayList<HashMap<String, String>> contactDetailArray = (ArrayList<HashMap<String, String>>) contactMessage
                    .get("contactDetails");

            for (HashMap<String, String> detailItem : contactDetailArray)
            {
                String dataType = detailItem.get("dataType");
                String dataValue = detailItem.get("value");

                FridayUserContactDetails detailEntry = new FridayUserContactDetails();
                detailEntry.setDetailKeyName(dataType);
                detailEntry.setDetailKeyValue(dataValue);
                detailEntry.setUserContactId(contact.getUserContactId());

                contact.getContactDetails().add(detailEntry);
            }

            contactsManager.addOrUpdateContact(contact);
        }
        catch (Exception e)
        {
            return "ERROR: " + e.getMessage();
        }

        return "OK";
    }

    /**
     * deleteContact
     * 
     * @param contactMessage
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = { "/deleteContact" })
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String deleteContact(@RequestBody Map<String, Object> contactMessage) throws JsonProcessingException
    {
        try
        {
            String userContactId = (String) contactMessage.get("userContactId");
            
            if (! StringUtils.isEmpty(userContactId))
            {
                FridayUserContact contact = contactsManager.getUserContactByUserContactId(userContactId);
                contactsManager.deleteContact(contact);
            }
        }
        catch (Exception e)
        {
            return "ERROR: " + e.getMessage();
        }

        return "OK";
    }
}
