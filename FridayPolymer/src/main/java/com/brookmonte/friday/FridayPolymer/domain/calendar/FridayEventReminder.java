/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.calendar;

/**
 * @author Pete
 *
 */
public class FridayEventReminder
{
    private String eventReminderId;
    private String userEventId;
    private Integer reminderTimeOffset;
    private Boolean sendEmail;
    private Boolean sendSms;
    private Boolean showPopup;
    
    /**
     * @return the eventReminderId
     */
    public String getEventReminderId()
    {
        return eventReminderId;
    }
    /**
     * @param eventReminderId the eventReminderId to set
     */
    public void setEventReminderId(String eventReminderId)
    {
        this.eventReminderId = eventReminderId;
    }
    /**
     * @return the userEventId
     */
    public String getUserEventId()
    {
        return userEventId;
    }
    /**
     * @param userEventId the userEventId to set
     */
    public void setUserEventId(String userEventId)
    {
        this.userEventId = userEventId;
    }
    /**
     * @return the reminderTimeOffset
     */
    public Integer getReminderTimeOffset()
    {
        return reminderTimeOffset;
    }
    /**
     * @param reminderTimeOffset the reminderTimeOffset to set
     */
    public void setReminderTimeOffset(Integer reminderTimeOffset)
    {
        this.reminderTimeOffset = reminderTimeOffset;
    }
    /**
     * @return the sendEmail
     */
    public Boolean getSendEmail()
    {
        return sendEmail;
    }
    /**
     * @param sendEmail the sendEmail to set
     */
    public void setSendEmail(Boolean sendEmail)
    {
        this.sendEmail = sendEmail;
    }
    /**
     * @return the sendSms
     */
    public Boolean getSendSms()
    {
        return sendSms;
    }
    /**
     * @param sendSms the sendSms to set
     */
    public void setSendSms(Boolean sendSms)
    {
        this.sendSms = sendSms;
    }
    /**
     * @return the showPopup
     */
    public Boolean getShowPopup()
    {
        return showPopup;
    }
    /**
     * @param showPopup the showPopup to set
     */
    public void setShowPopup(Boolean showPopup)
    {
        this.showPopup = showPopup;
    }
}
