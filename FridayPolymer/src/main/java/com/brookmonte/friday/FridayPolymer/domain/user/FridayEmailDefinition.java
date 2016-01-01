/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FridayEmailDefinition
{
    @JsonProperty("userEmailId") private String userEmailId;
    @JsonProperty("userId") private String userId;
    @JsonProperty("emailAddress") private String emailAddress;
    @JsonProperty("isPrimaryEmail") private boolean isPrimaryEmail;

    
    /**
     * @return the userEmailId
     */
    public String getUserEmailId()
    {
        return userEmailId;
    }
    /**
     * @param userEmailId the userEmailId to set
     */
    public void setUserEmailId(String userEmailId)
    {
        this.userEmailId = userEmailId;
    }
    /**
     * @return the userId
     */
    public String getUserId()
    {
        return userId;
    }
    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    /**
     * @return the emailAddress
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }
    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }
    /**
     * @return the isPrimaryEmail
     */
    public boolean isPrimaryEmail()
    {
        return isPrimaryEmail;
    }
    /**
     * @param isPrimaryEmail the isPrimaryEmail to set
     */
    public void setPrimaryEmail(boolean isPrimaryEmail)
    {
        this.isPrimaryEmail = isPrimaryEmail;
    }    
}
