/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.admin;

/**
 * @author Pete
 *
 */
public class UserPreference
{
    private String userId;
    private String userPreferenceId;
    private String preferenceId;
    private String preferenceName;
    private String preferenceType;
    private String description;
    private String defaultValue;
    private String preferenceValue;
    
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
     * @return the userPreferenceId
     */
    public String getUserPreferenceId()
    {
        return userPreferenceId;
    }
    /**
     * @param userPreferenceId the userPreferenceId to set
     */
    public void setUserPreferenceId(String userPreferenceId)
    {
        this.userPreferenceId = userPreferenceId;
    }
    /**
     * @return the preferenceId
     */
    public String getPreferenceId()
    {
        return preferenceId;
    }
    /**
     * @param preferenceId the preferenceId to set
     */
    public void setPreferenceId(String preferenceId)
    {
        this.preferenceId = preferenceId;
    }
    /**
     * @return the preferenceName
     */
    public String getPreferenceName()
    {
        return preferenceName;
    }
    /**
     * @param preferenceName the preferenceName to set
     */
    public void setPreferenceName(String preferenceName)
    {
        this.preferenceName = preferenceName;
    }
    /**
     * @return the preferenceType
     */
    public String getPreferenceType()
    {
        return preferenceType;
    }
    /**
     * @param preferenceType the preferenceType to set
     */
    public void setPreferenceType(String preferenceType)
    {
        this.preferenceType = preferenceType;
    }
    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    /**
     * @return the defaultValue
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }
    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }
    /**
     * @return the preferenceValue
     */
    public String getPreferenceValue()
    {
        return preferenceValue;
    }
    /**
     * @param preferenceValue the preferenceValue to set
     */
    public void setPreferenceValue(String value)
    {
        this.preferenceValue = value;
    }
}
