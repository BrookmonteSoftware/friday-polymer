/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.user;

import java.util.ArrayList;
import java.util.List;

import com.brookmonte.friday.FridayPolymer.domain.location.FridayLocation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FridayUser
{
    public static final int MAX_USERNAME_LENGTH = 99;
    public static final int MAX_EMAIL_LENGTH = 199;
    public static final int MAX_FIRSTNAME_LENGTH = 99;
    public static final int MAX_LASTNAME_LENGTH = 99;
    public static final int MIN_PASSWORD_LENGTH = 9;
    public static final int MAX_PASSWORD_LENGTH = 255;
    public static final int MAX_PHONE_LENGTH = 32;
    
    @JsonProperty("userId") private String  userId;    
    @JsonProperty("userName") private String  userName;   // same as email address, since user registers and logs in with email
    @JsonProperty("firstName") private String  firstName;
    @JsonProperty("lastName") private String  lastName;
    @JsonProperty("password") private String  password;
    @JsonProperty("accountEnabled") private boolean enabled = true;
    private boolean temporaryPassword = false;;
        
    @JsonProperty("homeLocations") private List<FridayLocation> homeLocations = new ArrayList<FridayLocation>();    
    @JsonProperty("workLocations") private List<FridayLocation> workLocations = new ArrayList<FridayLocation>();
    @JsonProperty("favoriteLocations") private List<FridayLocation> favoriteLocations = new ArrayList<FridayLocation>();

    @JsonProperty("workDefinitions") private List<FridayWorkDefinition> workDefinitions = new ArrayList<FridayWorkDefinition>();

    @JsonProperty("phoneNumbers") private List<FridayPhoneDefinition> phoneNumbers = new ArrayList<FridayPhoneDefinition>();
 
    @JsonProperty("emailAddresses") private List<FridayEmailDefinition> emailAddresses = new ArrayList<FridayEmailDefinition>();
    
    @JsonProperty("interestList") private List<String> interestList = new ArrayList<String>();
    
    /**
     * getUserId
     * @return
     */
    public String getUserId()
    {
      return this.userId;
    }

    /**
     * setUserId
     * @param id
     */
    public void setUserId(String id)
    {
      this.userId = id;
    }

    /**
     * getUserName
     * @return
     */
    public String getUserName()
    {
      return userName;
    }

    /**
     * setUserName
     * @param userName
     */
    public void setUserName(String userName)
    {
      this.userName = userName;
    }
    
    /**
     * @return the firstName
     */
    public String getFirstName()
    {
      return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName)
    {
      this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName()
    {
      return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName)
    {
      this.lastName = lastName;
    }

    /**
     * getPassword
     * @return
     */
    public String getPassword()
    {
      return password;
    }

    /**
     * setPassword
     * @param password
     */
    public void setPassword(String password)
    {
      this.password = password;
    }

    /**
     * getEnabled
     * @return
     */
    public Boolean getEnabled()
    {
      return enabled;
    }

    /**
     * setEnabled
     * @param enabled
     */
    public void setEnabled(Boolean enabled)
    {
      this.enabled = enabled;
    }

    /**
     * @return the temporaryPassword
     */
    public Boolean getTemporaryPassword()
    {
      return temporaryPassword;
    }

    /**
     * @param temporaryPassword the temporaryPassword to set
     */
    public void setTemporaryPassword(Boolean temporaryPassword)
    {
      this.temporaryPassword = temporaryPassword;
    }


    public List<FridayLocation> getHomeLocations()
    {
        return homeLocations;
    }


    public void setHomeLocations(List<FridayLocation> homeLocations)
    {
        this.homeLocations = homeLocations;
    }


    public List<FridayLocation> getWorkLocations()
    {
        return workLocations;
    }


    public void setWorkLocations(List<FridayLocation> workLocations)
    {
        this.workLocations = workLocations;
    }


    public List<FridayLocation> getFavoriteLocations()
    {
        return favoriteLocations;
    }


    public void setFavoriteLocations(List<FridayLocation> favoriteLocations)
    {
        this.favoriteLocations = favoriteLocations;
    }


    public List<FridayWorkDefinition> getWorkDefinitions()
    {
        return workDefinitions;
    }


    public void setWorkDefinitions(List<FridayWorkDefinition> workDefinitions)
    {
        this.workDefinitions = workDefinitions;
    }


    public List<FridayPhoneDefinition> getPhoneNumbers()
    {
        return phoneNumbers;
    }


    public void setPhoneNumbers(List<FridayPhoneDefinition> phoneNumbers)
    {
        this.phoneNumbers = phoneNumbers;
    }


    public List<FridayEmailDefinition> getEmailAddresses()
    {
        return emailAddresses;
    }


    public void setEmailAddresses(List<FridayEmailDefinition> emailAdresses)
    {
        this.emailAddresses = emailAdresses;
    }


    public List<String> getInterestList()
    {
        return interestList;
    }


    public void setInterestList(List<String> interestList)
    {
        this.interestList = interestList;
    }
}
