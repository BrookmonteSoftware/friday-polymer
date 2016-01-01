/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.calendar;

import java.util.UUID;

import org.joda.time.DateTime;

/**
 * @author Pete
 *
 */
public class FridayEventInstance
{
    private String eventInstanceId;
    private String userEventId;
    private String userId;
    private String eventType;    
    private String eventTitle;
    private String eventDescription;
    private String eventLocation;
    private String eventLocationId;
    private DateTime eventStartTime;
    private DateTime eventEndTime;
    private DateTime descriptionStartTime;
    private boolean deleted;

    public FridayEventInstance()
    {}
    
    /**
     * ctor from FridayEvent object
     * @param event
     */
    public FridayEventInstance(FridayEvent event)
    {
        this.eventInstanceId = UUID.randomUUID().toString();
        this.userEventId = event.getEventId();
        this.userId = event.getEventUserId();
        this.eventTitle = event.getTitle();
        this.eventDescription = event.getDescription();
        this.eventLocation = event.getLocation();
        
        if (event.getFridayLocation() != null)
        {
            this.eventLocationId = event.getFridayLocation().getLocationId();
        }
        
        this.eventType = event.getEventType().toString();
        this.eventStartTime = event.getStartDateTime();
        this.eventEndTime = event.getEndDateTime();
        this.descriptionStartTime = event.getStartDateTime();
        this.deleted = false;
    }
    
    /**
     * @return the eventInstanceId
     */
    public String getEventInstanceId()
    {
        return eventInstanceId;
    }
    /**
     * @param eventInstanceId the eventInstanceId to set
     */
    public void setEventInstanceId(String eventInstanceId)
    {
        this.eventInstanceId = eventInstanceId;
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
     * @return the eventTitle
     */
    public String getEventTitle()
    {
        return eventTitle;
    }
    /**
     * @param eventTitle the eventTitle to set
     */
    public void setEventTitle(String eventTitle)
    {
        this.eventTitle = eventTitle;
    }
    /**
     * @return the eventDescription
     */
    public String getEventDescription()
    {
        return eventDescription;
    }
    /**
     * @param eventDescription the eventDescription to set
     */
    public void setEventDescription(String eventDescription)
    {
        this.eventDescription = eventDescription;
    }
    /**
     * @return the eventLocation
     */
    public String getEventLocation()
    {
        return eventLocation;
    }
    /**
     * @param eventLocation the eventLocation to set
     */
    public void setEventLocation(String eventLocation)
    {
        this.eventLocation = eventLocation;
    }
    /**
     * @return the eventLocationId
     */
    public String getEventLocationId()
    {
        return eventLocationId;
    }
    /**
     * @param eventLocationId the eventLocationId to set
     */
    public void setEventLocationId(String eventLocationId)
    {
        this.eventLocationId = eventLocationId;
    }
    /**
     * @return the eventStartTime
     */
    public DateTime getEventStartTime()
    {
        return eventStartTime;
    }
    /**
     * @param eventStartTime the eventStartTime to set
     */
    public void setEventStartTime(Long eventStartTime)
    {
        this.eventStartTime = new DateTime(eventStartTime);
    }
    /**
     * @param eventStartTime the eventStartTime to set
     */
    public void setEventStartDateTime(DateTime eventStartTime)
    {
        this.eventStartTime = eventStartTime;
    }    
    /**
     * @return the eventEndTime
     */
    public DateTime getEventEndTime()
    {
        return eventEndTime;
    }
    /**
     * @param eventEndTime the eventEndTime to set
     */
    public void setEventEndTime(Long eventEndTime)
    {
        this.eventEndTime = new DateTime(eventEndTime);
    }
    /**
     * @param eventEndTime the eventEndTime to set
     */
    public void setEventEndDateTime(DateTime eventEndTime)
    {
        this.eventEndTime = eventEndTime;
    }    
    /**
     * @return the descriptionStartTime
     */
    public DateTime getDescriptionStartTime()
    {
        return descriptionStartTime;
    }
    /**
     * @param descriptionStartTime the descriptionStartTime to set
     */
    public void setDescriptionStartTime(Long descriptionStartTime)
    {
        this.descriptionStartTime = new DateTime(descriptionStartTime);
    }    
    /**
     * @return the deleted
     */
    public boolean getDeleted()
    {
        return deleted;
    }
    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(boolean deleted)
    {
        this.deleted = deleted;
    }

    /**
     * @return the eventType
     */
    public String getEventType()
    {
        return eventType;
    }

    /**
     * @param eventType the eventType to set
     */
    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }    
}
