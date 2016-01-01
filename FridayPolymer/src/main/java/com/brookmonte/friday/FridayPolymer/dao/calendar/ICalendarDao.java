/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.dao.calendar;

import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEvent;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEventDescription;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEventInstance;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayHoliday;

/**
 * @author Pete
 *
 */
public interface ICalendarDao
{
    /**
     * getHolidaysForCountry
     * @param countryCode3
     * @return
     */
    public List<FridayHoliday> getHolidaysForCountry(String countryCode3);

    /**
     * getHolidaysForReligion
     * @param religionName
     * @return
     */
    public List<FridayHoliday> getHolidaysForReligion(String religionName);

    
//    /**
//     * getEventsForUser
//     * @param userId
//     * @return
//     */
//    public List<FridayEventDescription> getEventDescriptionsForUser(String userId);
    
    /**
     * addEventDescription
     * @param event
     * @throws Exception 
     */
    public void addEventDescription(FridayEventDescription event) throws Exception;
    
    /**
     * getEventInstanceForEvent
     * @param event
     * @return
     * @throws Exception
     */
    public FridayEventInstance getEventInstanceForEvent(FridayEvent event) throws Exception;

    /**
     * insertEventInstance
     * @param event
     * @throws Exception 
     */
    public void insertEventInstance(FridayEventInstance eventInstance) throws Exception;

    /**
     * updateEventInstance
     * @param eventInstance
     * @throws Exception
     */
    public void updateEventInstance(FridayEventInstance eventInstance) throws Exception;
    
    /**
     * deleteEvent
     * @param userId
     * @param eventId
     * @throws Exception
     */
    public void deleteEvent(String userId, String eventId) throws Exception;


    /**
     * getEventInstancesForUserBetweenDates
     * @param userId
     * @param requestDateTime
     * @param instanceStartTime
     * @param instanceEndTime
     * @return
     * @throws Exception 
     */
    public List<FridayEventInstance> getEventInstancesForUserBetweenDates(String userId, DateTime requestDateTime,
            DateTime instanceStartTime, DateTime instanceEndTime) throws Exception;

    /**
     * getEventForEventInstance
     * @param eventInstance
     * @return
     * @throws Exception
     */
    public FridayEventDescription getEventForEventInstance(FridayEventInstance eventInstance) throws Exception;

    /**
     * getFridayEventTypes
     * @return
     */
    public HashMap<String, String> getFridayEventTypes();

    /**
     * getEventInstanceByInstanceId
     * @param userId
     * @param eventInstanceId
     * @return
     * @throws Exception
     */
    public FridayEventInstance getEventInstanceByInstanceId(String userId, String eventInstanceId) throws Exception;

;
}
