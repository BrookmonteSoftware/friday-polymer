/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.contact;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pete
 *
 */
public class FridayUserContact
{
    private String userContactId;
    private String userId;
    private String fridayUserId; // the friday user id of the contact - will be
                                 // null if the contact is not a friday user
    private String familyName;
    private String givenName;
    private String middleName;
    private boolean showFamilyNameFirst = false;
    private String gender;
    private String relationship; // the contact's relationship to the friday
                                 // user
    private String contactPicture;
    private int relationshipStrength;

    List<FridayUserContactDetails> contactDetails = new ArrayList<FridayUserContactDetails>();

    /**
     * @return the userContactId
     */
    public String getUserContactId()
    {
        return userContactId;
    }

    /**
     * @param userContactId the userContactId to set
     */
    public void setUserContactId(String userContactId)
    {
        this.userContactId = userContactId;
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
     * @return the fridayUserId
     */
    public String getFridayUserId()
    {
        return fridayUserId;
    }

    /**
     * @param fridayUserId the fridayUserId to set
     */
    public void setFridayUserId(String fridayUserId)
    {
        this.fridayUserId = fridayUserId;
    }

    /**
     * @return the familyName
     */
    public String getFamilyName()
    {
        return familyName;
    }

    /**
     * @param familyName the familyName to set
     */
    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
    }

    /**
     * @return the givenName
     */
    public String getGivenName()
    {
        return givenName;
    }

    /**
     * @param givenName the givenName to set
     */
    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    /**
     * @return the middleName
     */
    public String getMiddleName()
    {
        return middleName;
    }

    /**
     * @param middleName the middleName to set
     */
    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }

    /**
     * @return the gender
     */
    public String getGender()
    {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(String gender)
    {
        this.gender = gender;
    }

    /**
     * @return the relationship
     */
    public String getRelationship()
    {
        return relationship;
    }

    /**
     * @param relationship the relationship to set
     */
    public void setRelationship(String relationship)
    {
        this.relationship = relationship;
    }

    /**
     * @return the relationshipStrength
     */
    public int getRelationshipStrength()
    {
        return relationshipStrength;
    }

    /**
     * @param relationshipStrength the relationshipStrength to set
     */
    public void setRelationshipStrength(int relationshipStrength)
    {
        this.relationshipStrength = relationshipStrength;
    }

    /**
     * @return the contactPicture
     */
    public String getContactPicture()
    {
        return contactPicture;
    }

    /**
     * @param contactPicture the contactPicture to set
     */
    public void setContactPicture(String contactPicture)
    {
        this.contactPicture = contactPicture;
    }

    /**
     * @return the contactDetails
     */
    public List<FridayUserContactDetails> getContactDetails()
    {
        return contactDetails;
    }

    /**
     * @param contactDetails the contactDetails to set
     */
    public void setContactDetails(List<FridayUserContactDetails> contactDetails)
    {
        this.contactDetails = contactDetails;
    }

    /**
     * @return the showFamilyNameFirst
     */
    public boolean isShowFamilyNameFirst()
    {
        return showFamilyNameFirst;
    }

    /**
     * @param showFamilyNameFirst the showFamilyNameFirst to set
     */
    public void setShowFamilyNameFirst(boolean showFamilyNameFirst)
    {
        this.showFamilyNameFirst = showFamilyNameFirst;
    }
}
