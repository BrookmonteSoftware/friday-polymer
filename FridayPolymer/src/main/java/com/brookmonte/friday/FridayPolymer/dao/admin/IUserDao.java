/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.dao.admin;

import java.util.List;

import com.brookmonte.friday.FridayPolymer.domain.admin.UserPreference;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;

/**
 * @author Pete
 *
 */
public interface IUserDao
{
    /**
     * getUserByName
     * @param userName
     * @return
     */
    public FridayUser getUserByName(String userName);

    /**
     * addNewUser
     * @param newUser
     * @throws Exception 
     */
    public void addNewUser(FridayUser newUser) throws Exception;

    /**
     * updateUser
     * @param user
     * @throws Exception
     */
    public void updateUser(FridayUser user) throws Exception;
    
    /**
     * deleteUser
     * @param userId
     * @throws Exception
     */
    public void deleteUser(String userId) throws Exception;

    /**
     * getUserPreferences
     * @param user
     * @return
     */
    public List<UserPreference> getUserPreferences(FridayUser user);


    /**
     * insertUserPreferences
     * @param user
     * @param preferences
     * @throws Exception 
     */
    public void insertUserPreferences(FridayUser user, List<UserPreference> preferences) throws Exception;

    /**
     * deletePreferencesForUser
     * @param user
     */
    public void deletePreferencesForUser(FridayUser user);
}
