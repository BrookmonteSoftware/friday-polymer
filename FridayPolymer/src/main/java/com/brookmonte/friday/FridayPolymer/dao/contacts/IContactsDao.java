/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.dao.contacts;

import java.util.List;

import com.brookmonte.friday.FridayPolymer.domain.contact.FridayUserContact;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;

/**
 * @author Pete
 *
 */
public interface IContactsDao
{

    /**
     * getUserContactsByUserId
     * @param user
     * @return
     */
    public List<FridayUserContact> getUserContactsByUserId(FridayUser user);

    /**
     * insertContact
     * @param contact
     */
    public void insertContact(FridayUserContact contact);

    /**
     * updateContact
     * @param contact
     */
    public void updateContact(FridayUserContact contact);

    /**
     * deleteContact
     * @param contact
     */
    public void deleteContact(FridayUserContact contact);

    /**
     * getUserContactByUserContactId
     * @param userContactId
     * @return
     */
    public FridayUserContact getUserContactByUserContactId(String userContactId);

}
