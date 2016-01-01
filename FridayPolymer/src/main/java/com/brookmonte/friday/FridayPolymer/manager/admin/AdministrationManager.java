/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.brookmonte.friday.FridayPolymer.dao.admin.UserDao;
import com.brookmonte.friday.FridayPolymer.domain.admin.UserPreference;
import com.brookmonte.friday.FridayPolymer.domain.location.FridayLocation;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayEmailDefinition;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayPhoneDefinition;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;

/**
 * @author Pete
 *
 */
@Component
public final class AdministrationManager
{
    @SuppressWarnings("unused")
    private static org.apache.log4j.Logger log = Logger.getLogger(AdministrationManager.class);
    
    @Autowired
    UserDao userDao;

    /**
     * getUserByName
     * @param userName
     * @return
     */
    public final FridayUser getUserByName(final String userName)
    {
        return userDao.getUserByName(userName);
    }

    /**
     * addNewUser
     * 
     * Puts a new user user record into the Friday database
     * 
     * @param newUser
     * @return
     * @throws Exception 
     */
    public final void addNewUser(FridayUser newUser) throws Exception
    {
        // validate the user data
        this.validateNewUserData(newUser);
        
        // check if the user exists
        FridayUser existingUser = userDao.getUserByName(newUser.getUserName());
        
        if (existingUser != null)
        {
            throw new RuntimeException("User already exists");
        }
        else
        {
            userDao.addNewUser(newUser);
        }       
    }

    /**
     * getUserPreferences
     * @param user
     * @return
     */
    public final List<UserPreference> getUserPreferences(FridayUser user)
    {
        return userDao.getUserPreferences(user);        
    }

    /**
     * insertUserPreferences
     * 
     * Insert new user preferences. All previous preferences for the user are removed first.
     * 
     * @param user
     * @param preferences
     * @throws Exception 
     */
    public final void insertUserPreferences(FridayUser user, List<UserPreference> preferences) throws Exception
    {
        userDao.insertUserPreferences(user, preferences);
    }
    
    /**
     * validateUserData
     * 
     * Validates that the data for the new user does not violate any data
     * integrity rules - not too long, doesn't contain invalid characters,
     * etc.
     * 
     * @param newUser
     */
    private final void validateNewUserData(final FridayUser newUser)
    {
        // check user data
        
        // user id should be empty here
        if (!StringUtils.isEmpty(newUser.getUserId()))
        {
            throw new RuntimeException("User Id is already set for new user");
        }
        
        // user name validation        
        // user name is the primary email address
        if (StringUtils.isEmpty(newUser.getUserName()))
        {
            throw new RuntimeException("UserName is empty");            
        }
        
        if (newUser.getUserName().length() > FridayUser.MAX_USERNAME_LENGTH)
        {
            throw new RuntimeException("UserName exceeeds maximum length");   
        }
        
        validateEmailAddress(newUser.getUserName());
        
        // first name validation
        if (StringUtils.isEmpty(newUser.getFirstName()))
        {
            throw new RuntimeException("User first name is empty");            
        }
        
        if (newUser.getFirstName().length() > FridayUser.MAX_FIRSTNAME_LENGTH)
        {
            throw new RuntimeException("User first name exceeeds maximum length");            
        }
        
        final String disallowedFirstNameChars = "!@#$%^&*()+=[]\\;/{}|\":<>?0123456789";

        if (StringUtils.containsAny(newUser.getFirstName(), disallowedFirstNameChars))
        {
            throw new RuntimeException("First name contains disallowed characters.");
        }
        
        // last name validation
        if (StringUtils.isEmpty(newUser.getLastName()))
        {
            throw new RuntimeException("User last name is empty");            
        }
        
        if (newUser.getLastName().length() > FridayUser.MAX_FIRSTNAME_LENGTH)
        {
            throw new RuntimeException("User first name exceeeds maximum length");            
        }
        
        final String disallowedLastNameChars = "!@#$%^&*()+=[]\\;/{}|\":<>?0123456789";

        if (StringUtils.containsAny(newUser.getLastName(), disallowedLastNameChars))
        {
            throw new RuntimeException("Last name contains disallowed characters.");
        }
        
        // password validation
        if (StringUtils.isEmpty(newUser.getPassword()))
        {
            throw new RuntimeException("User password is empty");            
        }

        if (newUser.getPassword().length() < FridayUser.MIN_PASSWORD_LENGTH)
        {
            throw new RuntimeException("User password is too short");            
        }

        if (newUser.getPassword().length() > FridayUser.MAX_PASSWORD_LENGTH)
        {
            throw new RuntimeException("User password exceeeds maximum length");            
        }
        
        String disallowedPasswordChars = "{}\"<>'";
        
        if (StringUtils.containsAny(newUser.getPassword(), disallowedPasswordChars))
        {
            throw new RuntimeException("Password contains disallowed characters.");
        }
        
        // validate home locations
        for (FridayLocation fridayLocation: newUser.getHomeLocations())
        {
            validateLocation(fridayLocation);
        }
        
        // validate work locations
        for (FridayLocation fridayLocation: newUser.getWorkLocations())
        {
            validateLocation(fridayLocation);
        }
        
        // validate favorite locations - will be empty now
        for (FridayLocation fridayLocation: newUser.getFavoriteLocations())
        {
            validateLocation(fridayLocation);
        }
        
        // validate email addresses
        for (FridayEmailDefinition email: newUser.getEmailAddresses())
        {
            validateEmailAddress(email.getEmailAddress());
        }
        
        // validate phone numbers
        for (FridayPhoneDefinition phone: newUser.getPhoneNumbers())
        {
            validatePhoneNumber(phone.getFormattedPhoneNumber());
            validatePhoneNumber(phone.getAreaCode());
            validatePhoneNumber(phone.getInternationalPhoneNumber());
            validatePhoneNumber(phone.getPhoneExtension());
            validatePhoneNumber(phone.getPhoneNumber());
        }
        
        // validate work definitions
        // TODO - work definitions aren't created yet
        
        // validate interest list
        for (String interest: newUser.getInterestList())
        {
            validateInterest(interest);            
        }
    }

    /**
     * validateEmailAddress
     * @param email
     */
    private final void validateEmailAddress(final String email)
    {      
        if (email == null)
        {
            throw new RuntimeException("Email address is null");
        }
        
        if (email.length() > FridayUser.MAX_EMAIL_LENGTH)
        {
            throw new RuntimeException("Email address exceeeds maximum length");
        }
        
        if (email.indexOf('@') == -1)
        {
            throw new RuntimeException("Email address is malformed");
        }
        
        final String[] emailParts = email.split("@");
        
        if (emailParts.length != 2)
        {
            throw new RuntimeException("Email address is malformed");
        }
        
        final String localPart = emailParts[0];
        final String domainPart = emailParts[1];
        
        final String disallowedLocalEmailChars = "@^()[]\\;,{}|\":<>?";

        if (StringUtils.containsAny(localPart, disallowedLocalEmailChars))
        {
            throw new RuntimeException("Email address contains disallowed characters.");
        }
                
        final String disallowedDomainChars = "!@#$%^&*()+=[]\\\';,/{}|\":<>?";

        if (StringUtils.containsAny(domainPart, disallowedDomainChars))
        {
            throw new RuntimeException("Email address contains disallowed characters.");
        }
    }

    /**
     * validateLocation
     * @param fridayLocation
     */
    private final void validateLocation(FridayLocation fridayLocation)
    {
        final String disallowedUUIDChars = "!@#$%^&*()+=[]\\\';,./{}|\":<>?ghijklmnopqrstuvwxyzGHIJKLMNOPQRTSUVWXYZ";

        if (!StringUtils.isEmpty(fridayLocation.getLocationId()))
        {
            if (StringUtils.containsAny(fridayLocation.getLocationId(), disallowedUUIDChars))
            {
                throw new RuntimeException("Location id contains invalid characters");
            }
        }

        if (!StringUtils.isEmpty(fridayLocation.getUserId()))
        {
            if (StringUtils.containsAny(fridayLocation.getUserId(), disallowedUUIDChars))
            {
                throw new RuntimeException("User id contains invalid characters");
            }
        }
        
        if (!StringUtils.isEmpty(fridayLocation.getLocationType()))
        {
            final String disalllowedLocTypeChars = "!@#$%^&*()+=-[]\\\';,./{}|\":<>?";

            if (StringUtils.containsAny(fridayLocation.getLocationType(), disalllowedLocTypeChars))
            {
                throw new RuntimeException("Location type contains invalid characters");
            }
        }
                
        final String disalllowedPlaceNameChars = "!@#$%^&*()+=-[]\\\';,./{}|\":<>?";

        if (! StringUtils.isEmpty(fridayLocation.getName()))
        {
            if (StringUtils.containsAny(fridayLocation.getName(), disalllowedPlaceNameChars))
            {
                throw new RuntimeException("Location place name contains invalid characters");
            }
            
            // additional checks to help prevent code injection
            if (fridayLocation.getName().indexOf("javascript:") != -1)
            {
                throw new RuntimeException("Location place name is invalid");
            }
            
            if (fridayLocation.getName().indexOf("function") != -1)
            {
                throw new RuntimeException("Location place name is invalid");
            }
            
            if (fridayLocation.getName().indexOf(" class ") != -1)
            {
                throw new RuntimeException("Location place name is invalid");
            }            
        }

        final String disalllowedStreetChars = "!@$%^&*()+=[]\\\';,./{}|\":<>?";
        
        if (!StringUtils.isEmpty(fridayLocation.getStreetNumber()))
        {            
            if (StringUtils.containsAny(fridayLocation.getStreetNumber(), disalllowedStreetChars))
            {
                throw new RuntimeException("Location street number contains invalid characters");
            }
        }

        if (!StringUtils.isEmpty(fridayLocation.getStreetAddress1()))
        {          
            if (StringUtils.containsAny(fridayLocation.getStreetAddress1(), disalllowedStreetChars))
            {
                throw new RuntimeException("Location street address contains invalid characters");
            }
        }        

        if (!StringUtils.isEmpty(fridayLocation.getStreetAddress2()))
        {          
            if (StringUtils.containsAny(fridayLocation.getStreetAddress2(), disalllowedStreetChars))
            {
                throw new RuntimeException("Location street address contains invalid characters");
            }
        }
        
        if (!StringUtils.isEmpty(fridayLocation.getCity()))
        {
            if (StringUtils.containsAny(fridayLocation.getCity(), disalllowedPlaceNameChars))
            {
                throw new RuntimeException("Location city contains invalid characters");
            }
        }
        
        if (!StringUtils.isEmpty(fridayLocation.getCounty()))
        {
            if (StringUtils.containsAny(fridayLocation.getCounty(), disalllowedPlaceNameChars))
            {
                throw new RuntimeException("Location county contains invalid characters");
            }
        }
        
        if (!StringUtils.isEmpty(fridayLocation.getState()))
        {
            if (StringUtils.containsAny(fridayLocation.getState(), disalllowedPlaceNameChars))
            {
                throw new RuntimeException("Location state contains invalid characters");
            }
        }
        
        if (!StringUtils.isEmpty(fridayLocation.getStateAbbreviation()))
        {
            if (StringUtils.containsAny(fridayLocation.getStateAbbreviation(), disalllowedPlaceNameChars))
            {
                throw new RuntimeException("Location state abbreviation contains invalid characters");
            }
        }
        
        if (!StringUtils.isEmpty(fridayLocation.getCountry()))
        {
            if (StringUtils.containsAny(fridayLocation.getCountry(), disalllowedPlaceNameChars))
            {
                throw new RuntimeException("Location country contains invalid characters");
            }
        }
        
        if (!StringUtils.isEmpty(fridayLocation.getCountryAbbreviation()))
        {
            if (StringUtils.containsAny(fridayLocation.getCountryAbbreviation(), disalllowedPlaceNameChars))
            {
                throw new RuntimeException("Location country abbreviation contains invalid characters");
            }
        }
                
        final String disalllowedAddressChars = "<>";
        
        if (!StringUtils.isEmpty(fridayLocation.getFormattedAddress()))
        {
            if (StringUtils.containsAny(fridayLocation.getFormattedAddress(), disalllowedAddressChars))
            {
                throw new RuntimeException("Location formattedAddress contains invalid characters");
            }
        }
              

        final String disallowedLatLonChars = "!@#$%^&*()_+=[]\\\';,/{}|\":<>?abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRTSUVWXYZ";

        if (!StringUtils.isEmpty(fridayLocation.getLat()))
        {
            if (StringUtils.containsAny(fridayLocation.getLat(), disallowedLatLonChars))
            {
                throw new RuntimeException("Location latitude contains invalid characters");
            }
        }

        if (!StringUtils.isEmpty(fridayLocation.getLon()))
        {
            if (StringUtils.containsAny(fridayLocation.getLon(), disallowedLatLonChars))
            {
                throw new RuntimeException("Location longitude contains invalid characters");
            }
        }
                
        final String disallowedPostCodeChars = "!@#$%^&*()_+=[]\\\';,/{}|\":<>?";
        
        if (!StringUtils.isEmpty(fridayLocation.getPostalCode()))
        {
            if (StringUtils.containsAny(fridayLocation.getPostalCode(), disallowedPostCodeChars))
            {
                throw new RuntimeException("Location postal code contains invalid characters");
            }
        }
        
        if (!StringUtils.isEmpty(fridayLocation.getSideOfStreet()))
        {
            if (fridayLocation.getSideOfStreet().length() != 1)
            {
                throw new RuntimeException("Location side of street is invalid");
            }
            
            if (!fridayLocation.getSideOfStreet().equalsIgnoreCase("L")
                && !fridayLocation.getSideOfStreet().equalsIgnoreCase("R")
                && !fridayLocation.getSideOfStreet().equalsIgnoreCase("N"))
            {
                throw new RuntimeException("Location side of street is invalid");
            }            
        }
        
        if (!StringUtils.isEmpty(fridayLocation.getGeocodeQuality()))
        {         
            // values for geocodeQuality are from MapQuest documentation
            // see http://www.mapquestapi.com/geocoding/geocodequality.html#granularity
            
            if (!fridayLocation.getGeocodeQuality().equalsIgnoreCase("POINT")
                && !fridayLocation.getGeocodeQuality().equalsIgnoreCase("ADDRESS")
                && !fridayLocation.getGeocodeQuality().equalsIgnoreCase("INTERSECTION")
                && !fridayLocation.getGeocodeQuality().equalsIgnoreCase("STREET")
                && !fridayLocation.getGeocodeQuality().equalsIgnoreCase("COUNTRY")
                && !fridayLocation.getGeocodeQuality().equalsIgnoreCase("STATE")
                && !fridayLocation.getGeocodeQuality().equalsIgnoreCase("COUNTY")
                && !fridayLocation.getGeocodeQuality().equalsIgnoreCase("CITY")
                && !fridayLocation.getGeocodeQuality().equalsIgnoreCase("ZIP")
                && !fridayLocation.getGeocodeQuality().equalsIgnoreCase("ZIP_EXTENDED"))
            {
                throw new RuntimeException("Location geocode quality is invalid");
            }            
        }
        
        if (!StringUtils.isEmpty(fridayLocation.getGeocodeQualityCode()))
        {
            if (fridayLocation.getGeocodeQualityCode().length() != FridayLocation.GEOCODE_QUALITY_CODE_LENGTH)
            {
                throw new RuntimeException("Location geocode quality code is invalid");
            }
            
            final String disallowedQualityCodeChars = "!@#$%^&*()_+=[]\\\';,.-/{}|\":<>?67890abcdefghijklmonopqrstuvwxyzDEFGHJKMNOQRTSUVWY";

            if (StringUtils.containsAny(fridayLocation.getGeocodeQualityCode(), disallowedQualityCodeChars))
            {
                throw new RuntimeException("Location geocode quality code is invalid");
            }            
        }


        if (!StringUtils.isEmpty(fridayLocation.getProvidedLocation()))
        {
            if (StringUtils.containsAny(fridayLocation.getProvidedLocation(), disalllowedAddressChars))
            {
                throw new RuntimeException("Location provided location is invalid");
            }
        }        
    }

    /**
     * validatePhoneNumber
     * @param phoneNumber
     */
    private final void validatePhoneNumber(final String phoneNumber)
    {
        final String disallowedPhoneNumberChars = "!@$%^&_=[]\\\';,/{}|\":<>?abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRTSUVWXYZ";

        if (StringUtils.containsAny(phoneNumber, disallowedPhoneNumberChars))
        {
            throw new RuntimeException("Phone number is invalid");
        }
        
        
        // DB can hold longer values, but each field cannot be
        // longer than 32, so don't allow total length to be longer than 32, either
        // (phone numbers usually are 10 or 11 digits max, anyway)
        if (phoneNumber.length() > FridayUser.MAX_PHONE_LENGTH)
        {
            throw new RuntimeException("Phone number is invalid");
        }        
    }

    /**
     * validateInterest
     * @param interestName
     */
    private final void validateInterest(final String interestName)
    {
        final String disallowedInterestNameChars = "!@#$%^&*()_+=[]\\\';,/{}|\":<>?1234567890ABCDEFGHIJKLMNOPQRTSUVWXYZ";

        if (StringUtils.containsAny(interestName, disallowedInterestNameChars))
        {
            throw new RuntimeException("Interest name is invalid");
        }        
    }

    /**
     * buildPreferenceListFromMap
     * @param preferences
     * @return
     */
    public List<UserPreference> buildPreferenceListFromMap(List<HashMap<String, Object>> preferenceList)
    {
        List<UserPreference>  preferences = new ArrayList<UserPreference>();
        
        for (HashMap<String, Object> map : preferenceList)
        {
            UserPreference preference = new UserPreference();
            
            for (Object key : map.keySet())
            {
                String keyStr = (String) key;
                
                if (keyStr.equalsIgnoreCase("userPreferenceId"))
                {
                    preference.setUserPreferenceId((String)map.get(key));
                }
                else if (keyStr.equalsIgnoreCase("userId"))
                {
                    preference.setUserId((String)map.get(key));
                }
                else if (keyStr.equalsIgnoreCase("preferenceId"))
                {
                    preference.setPreferenceId((String)map.get(key));
                }
                else if (keyStr.equalsIgnoreCase("preferenceName"))
                {
                    preference.setPreferenceName((String)map.get(key));
                }
                else if (keyStr.equalsIgnoreCase("preferenceType"))
                {
                    preference.setPreferenceType((String)map.get(key));
                }
                else if (keyStr.equalsIgnoreCase("description"))
                {
                    preference.setDescription((String)map.get(key));
                }
                else if (keyStr.equalsIgnoreCase("defaultValue"))
                {
                    preference.setDefaultValue((String)map.get(key));
                }
                else if (keyStr.equalsIgnoreCase("preferenceValue"))
                {
                    preference.setPreferenceValue((String)map.get(key));
                }                
            }
            
            preferences.add(preference);            
        }
        
        return preferences;
    }
}
