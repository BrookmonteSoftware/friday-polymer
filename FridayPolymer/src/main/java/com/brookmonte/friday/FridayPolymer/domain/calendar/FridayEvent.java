/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.calendar;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.brookmonte.friday.FridayPolymer.domain.location.FridayLocation;

/**
 * @author Pete
 *
 */
public class FridayEvent
{
    private String eventId;
    private String eventUserId;
    private DateTime startDateTime;
    private DateTime endDateTime;
    private Duration duration;
    private boolean allDay;
    private boolean isRecurring;
    private FridayLocation fridayLocation;
    private String location;
    private String title;
    private String description;
    private FridayEventType eventType;
    private String eventInstanceDescription;
    private String eventInstanceId;
    
    // this is a list of the reminders that are sent/shown prior to the event    
    private List<FridayEventReminder> reminders = new ArrayList<FridayEventReminder>();    
    
    /**
     * @return the eventId
     */
    public String getEventId()
    {
        return eventId;
    }
    /**
     * @param eventId the eventId to set
     */
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }    
    /**
     * @return the eventUserId
     */
    public String getEventUserId()
    {
        return eventUserId;
    }
    /**
     * @param eventUserId the eventUserId to set
     */
    public void setEventUserId(String eventUserId)
    {
        this.eventUserId = eventUserId;
    }
    /**
     * @return the allDay
     */
    public boolean isAllDay()
    {
        return allDay;
    }
    /**
     * @param allDay the allDay to set
     */
    public void setAllDay(boolean allDay)
    {
        this.allDay = allDay;
    }
    /**
     * @return the startDateTime
     */
    public DateTime getStartDateTime()
    {
        return startDateTime;
    }
    /**
     * @param startDateTime the startDateTime to set
     */
    public void setStartDateTime(DateTime startDateTime)
    {
        this.startDateTime = startDateTime;
    }
    /**
     * @return the endDateTime
     */
    public DateTime getEndDateTime()
    {
        return endDateTime;
    }
    /**
     * @param endDateTime the endDateTime to set
     */
    public void setEndDateTime(DateTime endDateTime)
    {
        this.endDateTime = endDateTime;
    }    
    /**
     * @return the duration
     */
    public Duration getDuration()
    {
        return duration;
    }
    /**
     * @param duration the duration to set
     */
    public void setDuration(Duration duration)
    {
        this.duration = duration;
    }
    /**
     * @return the allDayEvent
     */
    public boolean isAllDayEvent()
    {
        return allDay;
    }
    /**
     * @param allDayEvent the allDayEvent to set
     */
    public void setAllDayEvent(boolean allDayEvent)
    {
        this.allDay = allDayEvent;
    }
    /**
     * @return the isRecurring
     */
    public boolean isRecurring()
    {
        return isRecurring;
    }
    /**
     * @param isRecurring the isRecurring to set
     */
    public void setRecurring(boolean isRecurring)
    {
        this.isRecurring = isRecurring;
    }
    /**
     * @return the location
     */
    public FridayLocation getFridayLocation()
    {
        return fridayLocation;
    }
    /**
     * @param location the location to set
     */
    public void setFridayLocation(FridayLocation location)
    {
        this.fridayLocation = location;
    }
    /**
     * @return the location
     */
    public String getLocation()
    {
        return location;
    }
    /**
     * @param location the location to set
     */
    public void setLocation(String location)
    {
        this.location = location;
    }
    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
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
     * @return the eventType
     */
    public FridayEventType getEventType()
    {
        return eventType;
    }
    /**
     * @param eventType the eventType to set
     */
    public void setEventType(FridayEventType eventType)
    {
        this.eventType = eventType;
    }
    /**
     * @return the reminders
     */
    public List<FridayEventReminder> getReminders()
    {
        return reminders;
    }
    /**
     * @param reminders the reminders to set
     */
    public void setReminders(List<FridayEventReminder> reminders)
    {
        this.reminders = reminders;
    }
    /**
     * @return the eventInstanceDescription
     */
    public String getEventInstanceDescription()
    {
        return eventInstanceDescription;
    }
    /**
     * @param eventInstanceDescription the eventInstanceDescription to set
     */
    public void setEventInstanceDescription(String eventInstanceDescription)
    {
        this.eventInstanceDescription = eventInstanceDescription;
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
}
