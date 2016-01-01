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
public class FridayPhoneDefinition
{
    @JsonProperty("phoneDefinitionId") private String phoneDefinitionId;
    @JsonProperty("phoneUserId") private String phoneUserId;
    @JsonProperty("countryCode") private String countryCode;
    @JsonProperty("areaCode") private String areaCode;
    @JsonProperty("phoneNumber") private String phoneNumber;
    @JsonProperty("phoneExtension") private String phoneExtension;
    @JsonProperty("formattedPhoneNumber") private String formattedPhoneNumber;              // phone number in local format
    @JsonProperty("internationalPhoneNumber") private String internationalPhoneNumber;      // phone number in international format (including country code)
    @JsonProperty("phoneServiceProvider") private String phoneServiceProvider;              // phone service provider (AT&T, Verizon, etc.)
    @JsonProperty("phoneType") private String phoneType;                                    // home, mobile, business
    @JsonProperty("smsAllowed") private boolean smsAllowed = false;                         // can text messages be sent to this phone?
    @JsonProperty("voiceAlertsAllowed") private boolean voiceAlertsAllowed = false;           // can voice alerts be sent to this phone
    
    /**
     * @return the phoneDefinitionId
     */
    public String getPhoneDefinitionId()
    {
        return phoneDefinitionId;
    }
    /**
     * @param phoneDefinitionId the phoneDefinitionId to set
     */
    public void setPhoneDefinitionId(String phoneDefinitionId)
    {
        this.phoneDefinitionId = phoneDefinitionId;
    }
    /**
     * @return the phoneUserId
     */
    public String getPhoneUserId()
    {
        return phoneUserId;
    }
    /**
     * @param phoneUserId the phoneUserId to set
     */
    public void setPhoneUserId(String phoneUserId)
    {
        this.phoneUserId = phoneUserId;
    }
    /**
     * @return the countryCode
     */
    public String getCountryCode()
    {
        return countryCode;
    }
    /**
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }
    /**
     * @return the areaCode
     */
    public String getAreaCode()
    {
        return areaCode;
    }
    /**
     * @param areaCode the areaCode to set
     */
    public void setAreaCode(String areaCode)
    {
        this.areaCode = areaCode;
    }
    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber()
    {
        return phoneNumber;
    }
    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }
    /**
     * @return the phoneExtension
     */
    public String getPhoneExtension()
    {
        return phoneExtension;
    }
    /**
     * @param phoneExtension the phoneExtension to set
     */
    public void setPhoneExtension(String phoneExtension)
    {
        this.phoneExtension = phoneExtension;
    }
    /**
     * @return the formattedPhoneNumber
     */
    public String getFormattedPhoneNumber()
    {
        return formattedPhoneNumber;
    }
    /**
     * @param formattedPhoneNumber the formattedPhoneNumber to set
     */
    public void setFormattedPhoneNumber(String formattedPhoneNumber)
    {
        this.formattedPhoneNumber = formattedPhoneNumber;
    }
    /**
     * @return the internationalPhoneNumber
     */
    public String getInternationalPhoneNumber()
    {
        return internationalPhoneNumber;
    }
    /**
     * @param internationalPhoneNumber the internationalPhoneNumber to set
     */
    public void setInternationalPhoneNumber(String internationalPhoneNumber)
    {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }
    /**
     * @return the phoneServiceProvider
     */
    public String getPhoneServiceProvider()
    {
        return phoneServiceProvider;
    }
    /**
     * @param phoneServiceProvider the phoneServiceProvider to set
     */
    public void setPhoneServiceProvider(String phoneServiceProvider)
    {
        this.phoneServiceProvider = phoneServiceProvider;
    }
    /**
     * @return the phoneType
     */
    public String getPhoneType()
    {
        return phoneType;
    }
    /**
     * @param phoneType the phoneType to set
     */
    public void setPhoneType(String phoneType)
    {
        this.phoneType = phoneType;
    }
    /**
     * @return the smsAllowed
     */
    public boolean getSmsAllowed()
    {
        return smsAllowed;
    }   
    /**
     * @param smsAllowed the smsAllowed to set
     */
    public void setSmsAllowed(boolean smsAllowed)
    {
        this.smsAllowed = smsAllowed;
    }
    /**
     * @return the voiceAlertsAllowed
     */
    public boolean getVoiceAlertsAllowed()
    {
        return voiceAlertsAllowed;
    }
    /**
     * @param voiceAlertsAllowed the voiceAlertsAllowed to set
     */
    public void setVoiceAlertsAllowed(boolean voiceAlertsAllowed)
    {
        this.voiceAlertsAllowed = voiceAlertsAllowed;
    }   
   
}
