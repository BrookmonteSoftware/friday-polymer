package com.brookmonte.friday.FridayPolymer.web;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEvent;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEventInstance;
import com.brookmonte.friday.FridayPolymer.domain.fridayUtils.FridayUtilities;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;
import com.brookmonte.friday.FridayPolymer.manager.admin.AdministrationManager;
import com.brookmonte.friday.FridayPolymer.manager.calendar.CalendarManager;

@Controller
public class CalendarController
{
    private static org.apache.log4j.Logger log = Logger.getLogger(CalendarController.class);

    @Autowired
    CalendarManager calendarManager;

    @Autowired
    AdministrationManager adminManager;
    
    /**
     * getHolidays
     * @param startDateTimeStamp
     * @param endDateTimeStamp
     * @return
     * @throws Exception
     */
    @RequestMapping("/holidays")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String getHolidays(@RequestParam("start") String startDateTimeStamp,
            @RequestParam("end") String endDateTimeStamp,
            @RequestParam("maxTitleLength") int maxTitleLength) throws Exception
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        // convert from UNIX time stamps used by FullCalendar control
        // to JodaTime DateTime objects
        DateTime startDate = new DateTime(startDateTimeStamp).withTimeAtStartOfDay().toDateTime(); 
        DateTime endDate = new DateTime(endDateTimeStamp).withTimeAtStartOfDay().toDateTime();
                
        List<FridayEvent> holidays = calendarManager.getHolidaysForUser(currentUser, startDate, endDate);
        
        return calendarManager.convertFridayHolidayEventsToJson(holidays, maxTitleLength);
    }

    /**
     * getReminders
     * @param requestDate
     * @param startDateTimeStamp
     * @param endDateTimeStamp
     * @return
     * @throws Exception
     */
    @RequestMapping("/reminders")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public List<FridayEvent> getReminders(@RequestHeader(value="RequestDate") String requestDate,
            @RequestParam("start") String startDateTimeStamp,
            @RequestParam("end") String endDateTimeStamp) throws Exception
    {
        try
        {
            final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            FridayUser user = adminManager.getUserByName(currentUser);
            
            // convert from UNIX time stamps used by FullCalendar control
            // to JodaTime DateTime objects, and make sure times are
            // stated in UTC time
            DateTime requestDateTime = FridayUtilities.convertRequestDateStringToDateTime(requestDate);
            
            DateTime startDate = new DateTime(startDateTimeStamp).withZone(DateTimeZone.UTC).withTimeAtStartOfDay().toDateTime();
                //.withZoneRetainFields(requestDateTime.getZone())
                //.withZone(DateTimeZone.UTC);
            
            DateTime endDate = new DateTime(endDateTimeStamp).withZone(DateTimeZone.UTC).withTimeAtStartOfDay().toDateTime();
                //.withZoneRetainFields(requestDateTime.getZone())
                //.withZone(DateTimeZone.UTC);
                    
            List<FridayEventInstance> instances = calendarManager.getEventInstancesForUser(user.getUserId(),
                    requestDateTime, startDate, endDate);
            
            List<FridayEvent> dailyReminders = calendarManager.buildDailyReminderEvents(instances,
                    requestDateTime, startDate, endDate);
            
            return dailyReminders;
            //return calendarManager.convertEventsToJson(dailyReminders);
        }
        catch (Exception e)
        {
            log.error("An error occurred while retrieving reminders", e);
        }
        
        return null;
    }

    /**
     * getEvents
     * @param requestDate
     * @param startDateTimeStamp
     * @param endDateTimeStamp
     * @return
     * @throws Exception
     */
    @RequestMapping("/events")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String getEvents(@RequestHeader(value="RequestDate") String requestDate,
            @RequestParam("start") String startDateParam,
            @RequestParam("end") String endDateParam) throws Exception
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        FridayUser user = adminManager.getUserByName(currentUser);
        
        DateTime requestDateTime = FridayUtilities.convertRequestDateStringToDateTime(requestDate);

        // account for daylight savings time
        DateTime startDate = new DateTime(startDateParam).minusMinutes(61);
        
        DateTime endDate = new DateTime(endDateParam).plusMinutes(61);
        
        List<FridayEventInstance> instances = calendarManager.getEventInstancesForUser(user.getUserId(),
                requestDateTime, startDate, endDate);
                
        List<FridayEvent> events = calendarManager.buildEventsFromInstances(instances);

        return calendarManager.convertEventsToJson(events);
    }

    /**
     * newEvent
     * @param requestDate
     * @param eventMessage
     * @return
     * @throws Exception
     */
    @RequestMapping("/newEvent")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String newEvent(@RequestHeader(value="RequestDate") String requestDate,
            @RequestBody Map<String, Object> eventMessage) throws Exception
    {
        try
        {
            calendarManager.addEvent(requestDate, eventMessage);
        }
        catch (Exception e)
        {
            return "ERROR: " + e.getMessage();
        }
                
        return "OK";
    }

    /**
     * deleteEventInstance
     * @param requestDate
     * @param eventId
     * @param instanceDate
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteEventInstance")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String deleteEventInstance(@RequestHeader(value="RequestDate") String requestDate,
            @RequestParam("eventInstanceId") String eventInstanceId) throws Exception
    {
        try
        {
            final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            
            calendarManager.deleteEventInstance(currentUser, eventInstanceId);
        }
        catch (Exception e)
        {
            return "ERROR:" + e.getMessage();
        }
                
        return "OK";
    }
    
    /**
     * deleteEvent
     * @param requestDate
     * @param eventId
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteEvent")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String deleteEvent(@RequestHeader(value="RequestDate") String requestDate,
            @RequestParam("eventId") String eventId) throws Exception
    {
        try
        {
            final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            
            calendarManager.deleteEvent(currentUser, eventId);
        }
        catch (Exception e)
        {
            return "ERROR:" + e.getMessage();
        }
                
        return "OK";
    }    
}
