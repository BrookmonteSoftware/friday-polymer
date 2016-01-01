/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.calendar;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.brookmonte.friday.FridayPolymer.dao.calendar.CalendarDao;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEvent;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEventDescription;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEventInstance;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEventType;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayHoliday;
import com.brookmonte.friday.FridayPolymer.domain.fridayUtils.FridayUtilities;
import com.brookmonte.friday.FridayPolymer.domain.location.FridayCountry;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;
import com.brookmonte.friday.FridayPolymer.manager.admin.AdministrationManager;
import com.brookmonte.friday.FridayPolymer.manager.location.LocationManager;

/**
 * @author Pete
 *
 */
@Component
public class CalendarManager
{
    @SuppressWarnings("unused")
    private static org.apache.log4j.Logger log = Logger.getLogger(CalendarManager.class);
    
    @Autowired
    AdministrationManager adminManager;
    
    @Autowired
    LocationManager locationManager;
    
    @Autowired
    CalendarDao calendarDao;
    
    /**
     * addEvent
     * 
     * Add a new event to the user's calendar
     * @param requestDate 
     * @param eventMessage
     * @throws Exception 
     */
    public void addEvent(String requestDate, Map<String, Object> eventMessage) throws Exception
    {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        
        FridayUser user = adminManager.getUserByName(userName);
        
        String eventId = (String) eventMessage.get("id");
        
        if (!StringUtils.isEmpty(eventId))
        {
            calendarDao.deleteEvent(user.getUserId(), eventId);
        }

        // TODO: validate the event inputs!
        
        DateTime requestDateTime = FridayUtilities.convertRequestDateStringToDateTime(requestDate);
        
        FridayEventDescription event = new FridayEventDescription();

        // create a GUID for the event description id
        event.setUserEventId(UUID.randomUUID().toString());
        
        // set the user id
        event.setUserId(user.getUserId());
        
        // event title and description
        event.setEventTitle((String) eventMessage.get("title"));
        event.setEventDescription((String) eventMessage.get("description"));

        // event location
        event.setEventLocation((String) eventMessage.get("location"));
        
        // TODO: see if a FridayLocation for the location can be retrieved
        
        // TODO: store event participants - see if a FridayUser can be determined and mapped to participants
        
        // TODO: store event reminders
        
        event.setEventType((String) eventMessage.get("type"));
        
        event.setEventInstanceDescription(buildEventOccurrenceDescription(requestDateTime, eventMessage));
        
        calendarDao.addEventDescription(event);
        
        // add event instances
        List<FridayEventInstance> instances = this.buildEventInstances(requestDateTime, event, eventMessage);
        
        calendarDao.bulkInsertEventInstances(instances);
    }


    /**
     * buildEventInstances
     * @param requestDateTime
     * @param event
     * @param eventMessage
     * @return
     * @throws Exception
     */
    private List<FridayEventInstance> buildEventInstances(DateTime requestDateTime, FridayEventDescription event, Map<String, Object> eventMessage) throws Exception
    {
        //
        // Event date/time descriptions basically use the ISO8601 standard
        // format
        // (see http://en.wikipedia.org/wiki/ISO_8601); however, the
        // ISO 8601 format for repeating events is limited, so it must be
        // extended.
        // The ISO format for specifying when an event occurs is like this:
        // 2007-03-01T13:00:00Z/P1Y2M10DT2H30M
        // That is:
        // <time>/<duration>
        // where time is in the format YYYY-MM-DDTHH:MM:SSZ
        // and duration is in the format PnnYnnMnnWnnDTnnHnnMnnS
        // where P indicates that the following description is a duration,
        // T is the separator between YMD and HMS
        // nn is the number of the following unit, and Y, M, D, H, M, and S
        // are the unit designators:
        // Y = years, M = months, D = days, [T] H = hours, M = minutes, and S =
        // seconds
        //
        // The duration indicator for months and minutes is ambiguous
        //
        // In the ISO8601 standard, repetition is indicated by preceding
        // the start time and duration with an
        // R, followed optionally by a number of repetitions, e.g.
        // R5/2013-10-06T13:00:00Z/P2H
        // which means starting on Oct. 6, 2013 at 1:00PM GMT, there is an event
        // of 2 hours in length,
        // with 5 total instances of the event.
        // The problem with this format is that it apparently does not allow
        // any means (or the means isn't clear to me) for indicating *when* the
        // repetitions occur (only that there are
        // 5 repetitions of the 2-hour event starting on 2013-10-06T13:00:00Z).
        // So, a way to describe different forms of event repetition must be
        // created.
        //
        // Types of repetitions that need to be described:
        //
        // 1) events that occur on a particular day (or days) of a month, either
        // in perpetuity
        // or for a set number of repetitions; examples:
        // a. two-hour event at 2:30PM every month on the 15th of the month for
        // 6 months
        // b. every other month on the 5th and 20th of the month for a year
        // c. every month on the 2nd Monday of the month, forever
        // d. every month on the 1st Monday and 3rd Tuesday, for 9 months
        // 2) events that occur on particular days of the week, either every week
        // or skipping weeks in a regular pattern (every other week, every 3rd
        // week)
        // either in perpetuity or for a set number of weeks:
        // a. every Monday for 8 weeks
        // b. Monday, Wednesday, and Friday of every other week for 26 weeks
        // c. every day except Sunday, forever
        // 3) events that repeat according to types 1 or 2 above, but are given
        // a definite
        // end date rather than a number of repetitions:
        // a. two-hour event at 2:30PM every month on the 15th of the month,
        // starting on
        // October 15, 2013, ending on February 15, 2014
        // 4) events that occur on a particular day or days of the year, either
        // in perpetuity
        // or for a set number of years:
        // a. every year on September 29th, forever
        // b. every year on 1st Monday on or after April 15th, forever
        // c. every year on 1/15, 4/15, 7/15 and 10/15, for 5 years
        // 5) events that occur on multiple dates that are otherwise unrelated:
        // a) event occurs on May 12th, May 29th, June 1st, and August 14th
        //
        // Date/Time Descriptions - the <> brackets are *not* part of the
        // format; they
        // are there just to hold possible values
        //
        // THE GENERAL FORMAT FRIDAY USES FOR DESCRIBING WHEN EVENTS TAKE PLACE,
        // WHEN THEY REPEAT, AND HOW LONG THEY LAST:
        //
        // R<# of repetitions>[<repetition description>]/<start date and time
        // description>/<end date and time description>/P<length of the event>
        //
        // where R0000 indicates either repetition to the end date (if end date
        // and time is given) or
        // in perpetuity (repetition into the indefinite future, if no end date
        // and time is given)
        //
        // <repetition description> is indicated by an interval: DI, WI, MI, or YI
        // (daily interval, weekly interval, monthly interval, or yearly interval), followed by
        // a semicolon, followed by one or more day descriptions. Multiple day
        // descriptions are separated by commas.
        //
        // When the interval is a daily interval, the day description
        // is the number of days between each occurrence. So,
        // DI:1 indicates that the event occurs every day,
        // DI:2 indicates that the event occurs every other day, e.g. Mon, Wed, Fri, Sun, Tue, ...
        // DI:3 indicates that the event occurs every 3 days, e.g Mon, Thur, Sun, Wed, ...
        // and so on,
        //
        // When the interval is a week interval, the day description
        // is DW:<0-6>, where 0-6 indicate Sunday (0) through Saturday (6). So,
        // to indicate
        // that an event repeats every other week on Monday, Wednesday, and
        // Friday, the repetition description is [WI:2; DW:1,DW:3,DW:5]
        //
        // When the interval is a month interval, the day description can be in
        // two different formats,
        // either a specific day of the month, or a description similar to the
        // holiday date description. The day of the month can also be specified
        // as the last day of the month, no matter the actual date.
        // For example:
        // [MI:2; DD:5] (every other month on the 5th)
        // [MI:1; 4#2DW,4#5DW] (every month on the 4th Tuesday and 4th Friday)
        // [MI:3; DD:LAST] (every 3rd month on the last day of the month)
        //
        // When the interval is a year interval, the day description can be in
        // two different formats
        // also, using the holiday date description format. For example:
        // [YI:2; 4#4DW11] (every other year on Thanksgiving)
        // [YI:1; 25DD12] (every year on Christmas Day)
        //
        // Start and end dates and times can be described in any of several
        // ways:
        // 1. The ISO 8601 date and time format: YYYY-MM-DDTHH:MM:SS
        // 2. A format similar to the Friday holiday date description format:
        // [1-5|LAST]#[0-6]DW[01-12]Mnnnn
        // which means first through fifth or last Sunday through Monday of
        // January through December of the given year,
        // e.g. 4#4DW11M2013 (4th Thursday of November, 2013)
        // 3. A holiday name, e.g. EASTER
        // 4. A date description using the holiday description or name and a
        // simple calculation;
        // e.g. EASTER+1 (the Monday following Easter)
        //
        // FORMAT 0:
        // Rnnnn[YI:<1-n>; <day description>]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDnnHnnMnnS
        //
        // FORMAT 1A:
        // Rnnnn[MI:<1-n>;DD:<1-31>]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDnnHnnMnnS
        //
        // MI:<1-12> indicates that there can be between 1 and 12 months between
        // each
        // occurrence of the event; where MI:1 indicates that the event occurs
        // every month,
        //
        // MI:2 indicates that the event occurs every other month (e.g. Jan,
        // Mar, May, Jul, Sept, Nov, Jan, ...)
        // MI:3 indicates that the event occurs every 3d month (e.g. Jan, Apr,
        // Jul, Oct, Jan, ...)
        // MI:4 - every 4th month (Jan, May, Sept, Jan, ...)
        // MI:5 - every 5th month (Jan, Jun, Nov, Mar, Jul, Dec, ...)
        // and so on, up to
        // MI:12 - every 12 months (Jan, Jan + 1 year, Jan + 2 years, Jan + 3
        // years, ...)
        //
        // Example 1a: two-hour event at 2:30PM every month on the 15th of the
        // month for 6 months,
        // starting on April 15, 2013:
        // R0006[MI:1;DD:15]/2013-04-15T14:30:00//P2H
        //
        // Example 1b: all day event every other month on the 5th and 20th of
        // the month for a year
        // starting on July 5th, 2013:
        // R0012[MI:2;DD:5,DD:20]/2013-07-05T00:00:00//P24H
        //
        // FORMAT 1B:
        // Rnnnn[MI:<1-n>;<1-5>|LAST#<0-6>DW]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDnnHnnMnnS
        //
        // Example 1c: a 2:15 event at 8:00 AM every month on the 2nd Monday of
        // the month, forever,
        // starting on December 1st, 2013
        // R0000[MI1;2#2DW]/2013-12-01T08:00:00//P2H15M
        //
        // Example 1d: A 1:30 minute event at 12:00PM on the 1st Monday and 3rd
        // Tuesday of the month
        // for 9 months, starting April 1st, 2013
        // R0009[MI:1;1#2DW,3#3DW]/2013-04-01T12:00:00//P1H30M
        //
        // FORMAT 2:
        // Rnnnn[WI:<1-nnn>;DW:<0-6>]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDnnHnnMnnS
        //
        // Example 2a: 30 minute event every Monday at 9:00 for 8 weeks,
        // starting 10/10/2013:
        // R0008[WI:1;DW:1]/2013-10-10T08:00:00//P30M
        //
        // Example 2b: A 3 hour event at 10:00 AM every other Monday, Wednesday,
        // and Friday for 26 weeks,
        // starting on 10/10/2013:
        // R0026[WI:2; DW:1, DW:3; DW:5]/2013-10-10T10:00:00//P3H
        //
        // Example 2c: an all day event that occurs every day except Sunday, in
        // perpetuity, starting
        // on October 10, 2013:
        // R0000[WI:1; DW:1, DW:2, DW:3, DW:4, DW:5, DW:6]/2013-10-10T00:00:00//PT24H
        //
        // FORMAT 3:
        // Rnnnn[DI:2]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDnnHnnMnnS
        //
        // String eventDateTimeDescription =
        // "R0000[MI1;2#2DW]/2013-12-01T08:00:00//PT2H15M";

        // String eventDateTimeDescription =
        // "R0026[WI:1; DW:1, DW:2, DW:3, DW:5]/2013-10-01T10:00:00//PT3H";

        // String eventDateTimeDescription =
        // "R0012[MI:2;DD:5,DD:20]/2013-07-10T00:00:00//PT24H";

        // String eventDateTimeDescription =
        // "R0000[YI:2; 4#4DW11]/2013-11-01T08:00:00//PT2H15M";        
        String eventDateTimeDescription = event.getEventInstanceDescription();

        String[] descriptionParts = eventDateTimeDescription.split("/");

        String repetitionDesc = descriptionParts[0];
        String startDateDesc = descriptionParts[1];
        String endDateDesc = descriptionParts[2];
        String eventLengthDesc = descriptionParts[3];

        String[] repetitionParts = repetitionDesc.split("\\[");
        String repCountDesc = repetitionParts[0].substring(1);
        Integer repCount = Integer.parseInt(repCountDesc);

        // get the repetition interval and day description
        String repPartDesc = repetitionParts[1].substring(0, repetitionParts[1].length() - 1);

        String[] repIntervalAndDaysParts = null;
        String repIntervalDesc = null;
        String repDaysDesc = null;

        String[] repIntervalParts = null;
        String repIntervalType = null;
        Integer repIntervalValue = null;

        String[] repIntervalDays = null;

        if (repPartDesc.length() > 0)
        {
            repIntervalAndDaysParts = repPartDesc.split(";");
            repIntervalDesc = repIntervalAndDaysParts[0];

            if (repIntervalAndDaysParts.length > 1)
            {
                repDaysDesc = repIntervalAndDaysParts[1];
                repIntervalDays = repDaysDesc.split(",");
            }

            repIntervalParts = repIntervalDesc.split(":");
            repIntervalType = repIntervalParts[0];
            repIntervalValue = Integer.parseInt(repIntervalParts[1]);
        }
        else
        {
            repIntervalType = "DI";
            repIntervalValue = 1;
        }

        // get the starting date - start and end date are assumed to be in UTC time zone
        DateTime eventStartDate = FridayUtilities.parseDateDescription(startDateDesc).withZone(DateTimeZone.UTC);

        DateTime eventEndDate = null;

        if (!StringUtils.isEmpty(endDateDesc))
        {
            eventEndDate = FridayUtilities.parseDateDescription(endDateDesc).withZone(DateTimeZone.UTC);
        }
        
        if (repCount > 1)
        {
            // TODO: is anything supposed to happen here?
        }

        if (repCount == 0 && eventStartDate.equals(eventEndDate))
        {
            repCount = 1;
        }

        if (repCount == 0)
        {
            if (repIntervalType.equalsIgnoreCase("DI"))
            {
                repCount = 36500;   // 100 years of days
            }
            else if (repIntervalType.equalsIgnoreCase("WI"))
            {
                repCount = 5200;     // 100 years of weeks   
            }
            else if (repIntervalType.equalsIgnoreCase("MI"))
            {
                repCount = 1200;     // 100 years of months   
            }
            else if (repIntervalType.equalsIgnoreCase("YI"))
            {
                repCount = 100;     // 100 years   
            }            
        }

        List<DateTime> eventDates = generateEventDatesFromDescription(eventStartDate, eventEndDate, repCount,
                repIntervalType, repIntervalValue, repIntervalDays);

        
        List<FridayEventInstance> instances = new ArrayList<FridayEventInstance>();
        
        for (DateTime eventDateTime : eventDates)
        {
            FridayEventInstance instance = new FridayEventInstance();
            instance.setEventInstanceId(UUID.randomUUID().toString());
            instance.setDeleted(false);
            instance.setDescriptionStartTime(eventStartDate.getMillis());
            instance.setEventDescription(event.getEventDescription());
            instance.setEventEndTime(eventDateTime.plus(convertEventLengthDescriptionToDuration(eventLengthDesc)).getMillis());
            instance.setEventLocation(event.getEventLocation());
            instance.setEventLocationId(event.getEventLocationId());
            instance.setEventStartTime(eventDateTime.getMillis());
            instance.setEventTitle(event.getEventTitle());
            instance.setUserEventId(event.getUserEventId());
            instance.setUserId(event.getUserId());            
            instance.setEventType(event.getEventType());
            
            instances.add(instance);           
        }
        
        return instances;
    }
    
    /**
     * buildEventOccurenceDescription
     * @param requestDateTime 
     * @param eventMessage
     * @return
     */
    private String buildEventOccurrenceDescription(DateTime requestDateTime,
            Map<String, Object> eventMessage)
    {
        StringBuilder description = new StringBuilder();

        // FORMAT 0:
        // Rnnnn[YI:<1-n>; <day description>]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDTnnHnnMnnS
        // FORMAT 1A:
        // Rnnnn[MI:<1-n>;DD:<1-31>]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDTnnHnnMnnS        
        // FORMAT 1B:
        // Rnnnn[MI:<1-n>;<1-5>|LAST#<0-6>DW]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDTnnHnnMnnS
        // FORMAT 2:
        // Rnnnn[WI:<1-nnn>;DW:<0-6>]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDTnnHnnMnnS        
        // FORMAT 3:
        // Rnnnn[DI:2]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDTnnHnnMnnS
        
        description.append("R");
        
        int repCount = 0;
        
        if (eventMessage.get("repetitionCount") instanceof Integer)
        {
            repCount = (Integer) eventMessage.get("repetitionCount");
        }
        else if (eventMessage.get("repetitionCount") instanceof String)
        {
            repCount = Integer.parseInt((String) eventMessage.get("repetitionCount"));
        }
        
        description.append(String.format("%04d", repCount));
        
        String repType = (String) eventMessage.get("repetitionType");
        
        if (repType.equalsIgnoreCase("once"))
        {
            description.append("[]/"); 
        }        
        else if (repType.equalsIgnoreCase("daily"))
        {
            description.append("[DI:");            
            description.append(eventMessage.get("dayInterval"));
            description.append("]/");
        }
        else if (repType.equalsIgnoreCase("weekly"))
        {
            description.append("[WI:");
            description.append(eventMessage.get("weekInterval"));
            description.append(";");
            
            String weekDays = eventMessage.get("weekDays").toString();
            weekDays = weekDays.replace("[", "");
            weekDays = weekDays.replace("]", "");
            description.append(weekDays);
            description.append("]/");
        }
        else if (repType.equalsIgnoreCase("monthly"))
        {
            description.append("[MI:");
            description.append(eventMessage.get("monthInterval"));
            description.append(";");
            
            String monthDays = eventMessage.get("monthDayDescriptions").toString();
            monthDays = monthDays.replace("[", "");
            monthDays = monthDays.replace("]", "");
            description.append(monthDays);
            description.append("]/");            
        }        
        else if (repType.equalsIgnoreCase("yearly"))
        {
            description.append("[YI:");
            description.append(eventMessage.get("yearInterval"));
            description.append(";");
            
            String yearDays = eventMessage.get("yearDayDescriptions").toString();
            yearDays = yearDays.replace("[", "");
            yearDays = yearDays.replace("]", "");
            description.append(yearDays);
            description.append("]/");            
        }
        else
        {
            throw new RuntimeException("Unknown interval type");
        }
        
        // get the full start date and time with UTC time zone
        String startDateStr = (String) eventMessage.get("startDate");
        String startTimeStr = (String) eventMessage.get("startTime");
        
        DateTime startDateTime =
                new DateTime(startDateStr);
        
        MutableDateTime mutableStartDateTime = startDateTime.toMutableDateTime();
                
        String timeFormat = "hh:mm a";
        DateTimeFormatter fmt = DateTimeFormat.forPattern(timeFormat);
        DateTime startTime = fmt.parseDateTime(startTimeStr);
                
        mutableStartDateTime.setHourOfDay(startTime.getHourOfDay());
        mutableStartDateTime.setMinuteOfHour(startTime.getMinuteOfHour());
        
        mutableStartDateTime.setZoneRetainFields(requestDateTime.getZone());
        mutableStartDateTime.setZone(DateTimeZone.forID("UTC"));

        // convert to string
        startDateStr = mutableStartDateTime.toString();
        
        // get the end time so that the event duration can be calculated
        String endTimeStr = (String) eventMessage.get("endTime");
        DateTime endTime = fmt.parseDateTime(endTimeStr);
        
        MutableDateTime mutableEndDateTime = startDateTime.toMutableDateTime();
        mutableEndDateTime.setHourOfDay(endTime.getHourOfDay());
        mutableEndDateTime.setMinuteOfHour(endTime.getMinuteOfHour());
        
        mutableEndDateTime.setZoneRetainFields(requestDateTime.getZone());
        mutableEndDateTime.setZone(DateTimeZone.forID("UTC"));
        
        Period eventPeriod = new Period(mutableStartDateTime, mutableEndDateTime);
        
        // if start time and end time are both midnight,
        // then it is an all-day event
        if (startTimeStr.equalsIgnoreCase("12:00 AM")
                && endTimeStr.equalsIgnoreCase("12:00 AM"))
        {
            eventPeriod = Period.hours(24);
        }
                
        String endDateStr = (String) eventMessage.get("endDate");
        
        if (endDateStr == null)
        {
            endDateStr = "";
        }
        else
        {
            DateTime eventEndDateTime =
                    new DateTime(endDateStr);
            
            MutableDateTime mutableEventEndDateTime = eventEndDateTime.toMutableDateTime();
            
            // using startTime here is correct
            mutableEventEndDateTime.setHourOfDay(startTime.getHourOfDay());
            mutableEventEndDateTime.setMinuteOfHour(startTime.getMinuteOfHour());
            
            mutableEventEndDateTime.setZoneRetainFields(requestDateTime.getZone());
            mutableEventEndDateTime.setZone(DateTimeZone.forID("UTC"));

            // convert to string
            endDateStr = mutableEventEndDateTime.toString();          
        }
        
        
        description.append(startDateStr);
        description.append("/");
        description.append(endDateStr);
        description.append("/");
        description.append(eventPeriod.toString());
        
        return description.toString();
    }

    /**
     * getHolidaysForUser
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<FridayEvent> getHolidaysForUser(String userId, DateTime startDate, DateTime endDate)
    {
        FridayUser u = adminManager.getUserByName(userId);
        
        // TODO: add user preferences for what to see on calendar
        // TODO: add user preferences for religion and add to user object
        
        String countryCode = "USA";     // assume US holidays if no home locations are set for the user
        
        if (u.getHomeLocations() != null && u.getHomeLocations().size() > 0)
        {
            countryCode = u.getHomeLocations().get(0).getCountryAbbreviation();
            
            if (countryCode.length() == 2)
            {
                FridayCountry country = locationManager.getCountryByCountryCode2(countryCode);
                
                // need the country code 3 to be consistent in the query                 
                countryCode = country.getCountryCode3();
            }
        }
                
        List<FridayEvent> countryHolidays = this.getHolidaysForCountry(countryCode, startDate, endDate);

        List<FridayEvent> holidays = new ArrayList<FridayEvent>();
        
        holidays.addAll(countryHolidays);
        
        String religionName = "Christianity";
        
        List<FridayEvent> religiousHolidays = this.getHolidaysForReligion(religionName, startDate, endDate);
        
        // combine holidays - there may be duplicates (e.g. Christmas)
        
        for (FridayEvent religiousHoliday : religiousHolidays)
        {
            boolean found = false;
            for (FridayEvent holiday : countryHolidays)
            {
                if (holiday.getTitle().equalsIgnoreCase(religiousHoliday.getTitle())
                        && holiday.getStartDateTime().isEqual(religiousHoliday.getStartDateTime()))
                {
                    found = true;
                }               
            }
                        
            if (!found)
            {
                holidays.add(religiousHoliday);
            }             
        }
        
        return holidays;
    }

    /**
     * getHolidaysForCountry
     * @param countryCode
     * @param startDate
     * @param endDate
     * @return
     */
    public List<FridayEvent> getHolidaysForCountry(String countryCode, DateTime startDate, DateTime endDate)
    {
        List<FridayHoliday> holidays = calendarDao.getHolidaysForCountry(countryCode);
        List<FridayEvent> holidayEvents = new ArrayList<FridayEvent>();
        
        for (FridayHoliday holiday : holidays)
        {
            // convert from FridayHoliday to FridayEvent
            FridayEvent holidayEvent = this.convertHolidayToEvent(holiday, startDate.getYear());

            if (holidayEvent.getStartDateTime().isAfter(startDate.minusDays(1))
                    && holidayEvent.getStartDateTime().isBefore(endDate.plusDays(1)))
            {
                holidayEvents.add(holidayEvent);
            }
            else if (holidayEvent.getEndDateTime().isAfter(startDate.minusDays(1))
                    && holidayEvent.getEndDateTime().isBefore(endDate.plusDays(1)))
            {
                holidayEvents.add(holidayEvent);
            }
            
            if (startDate.getYear() != endDate.getYear())
            {
                holidayEvent = this.convertHolidayToEvent(holiday, endDate.getYear());

                if (holidayEvent.getStartDateTime().isAfter(startDate.minusDays(1))
                        && holidayEvent.getStartDateTime().isBefore(endDate.plusDays(1)))
                {
                    holidayEvents.add(holidayEvent);
                }                
                else if (holidayEvent.getEndDateTime().isAfter(startDate.minusDays(1))
                        && holidayEvent.getEndDateTime().isBefore(endDate.plusDays(1)))
                {
                    holidayEvents.add(holidayEvent);
                }                
            }
        }
               
        return holidayEvents;
    }

    /**
     * getHolidaysForReligion
     * @param religionName
     * @param startDate
     * @param endDate
     * @return
     */
    public List<FridayEvent> getHolidaysForReligion(String religionName, DateTime startDate, DateTime endDate)
    {
        List<FridayHoliday> holidays = calendarDao.getHolidaysForReligion(religionName);
        List<FridayEvent> holidayEvents = new ArrayList<FridayEvent>();
        
        for (FridayHoliday holiday : holidays)
        {
            // convert from FridayHoliday to FridayEvent
            FridayEvent holidayEvent = this.convertHolidayToEvent(holiday, startDate.getYear());

            if (holidayEvent.getStartDateTime().isAfter(startDate.minusDays(1))
                    && holidayEvent.getStartDateTime().isBefore(endDate.plusDays(1)))
            {
                holidayEvents.add(holidayEvent);
            }            
            else if (holidayEvent.getEndDateTime().isAfter(startDate.minusDays(1))
                    && holidayEvent.getEndDateTime().isBefore(endDate.plusDays(1)))
            {
                holidayEvents.add(holidayEvent);
            }
            
            if (startDate.getYear() != endDate.getYear())
            {
                holidayEvent = this.convertHolidayToEvent(holiday, endDate.getYear());

                if (holidayEvent.getStartDateTime().isAfter(startDate.minusDays(1))
                        && holidayEvent.getStartDateTime().isBefore(endDate.plusDays(1)))
                {
                    holidayEvents.add(holidayEvent);
                }                
                else if (holidayEvent.getEndDateTime().isAfter(startDate.minusDays(1))
                        && holidayEvent.getEndDateTime().isBefore(endDate.plusDays(1)))
                {
                    holidayEvents.add(holidayEvent);
                }                
            }
        }
               
        return holidayEvents;
    }
    
    /**
     * convertFridayHolidayEventsToJson
     * @param holidayEvents
     * @param maxTitleLength 
     * @return
     */
    public String convertFridayHolidayEventsToJson(List<FridayEvent> holidayEvents, int maxTitleLength)
    {        
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        int holidayCount = 0;
        
        for (FridayEvent holiday : holidayEvents)
        {
            if (holidayCount > 0)
            {
                sb.append(", ");
            }
            
            sb.append("{");
            
            sb.append("\"id\": \"");
            sb.append(holiday.getEventId());
            sb.append("\", ");
            
            String holidayTitle = holiday.getTitle();
            
            if (holidayTitle.length() > maxTitleLength)
            {
                holidayTitle = holidayTitle.substring(0, maxTitleLength - 1) + "...";
            }
            
            sb.append("\"title\": \"");
            sb.append(holidayTitle);
            sb.append("\", ");

            sb.append("\"description\": \"");
            sb.append(holiday.getDescription());
            sb.append("\", ");

            sb.append("\"type\": \"");
            sb.append("holiday");
            sb.append("\", ");
            
            sb.append("\"start\": \"");
            sb.append(fmt.print(holiday.getStartDateTime()));
            sb.append("\", ");
            
            sb.append("\"end\": \"");
            sb.append(fmt.print(holiday.getEndDateTime()));
            sb.append("\", ");
            
            sb.append("\"allDay\": true,");
            sb.append("\"className\": \"holidayEvent\"");
            
            sb.append("}");
            
            holidayCount++;
            
        }
        
        sb.append("]");
        
        return sb.toString();
    }
    
    /**
     * convertHolidayToEvent
     * @param holiday
     * @return
     */
    private FridayEvent convertHolidayToEvent(FridayHoliday holiday, int year)
    {
        FridayEvent event = new FridayEvent();
        
        event.setEventId(holiday.getHolidayId());
        
        event.setAllDayEvent(true);
        event.setDescription(holiday.getHolidayName());
        
        List<DateTime> holidayDates = this.findDateRangeFromDescription(holiday.getHolidayDateDescription(), year);
        
        event.setStartDateTime(holidayDates.get(0));
        event.setEndDateTime(holidayDates.get(holidayDates.size() - 1));
        
        event.setEventType(FridayEventType.HOLIDAY);
        
        event.setTitle(holiday.getHolidayName());
        
        return event;
    }
    
    /**
     * findDateRangeFromDescription
     * @param description
     * @param year
     * @return
     */
    public List<DateTime> findDateRangeFromDescription(String description, int year)
    {
        List<DateTime> holidayDates = new ArrayList<DateTime>();
        
        if (description.contains(":"))
        {
            // if the description of the holiday contains a ":" character
            // then the holiday falls on a range of dates, where the start date
            // is described in the part before the ":" and the end date
            // is described in the part after the ":"
            String[] descriptions = description.split("\\:");
            String startDescription = descriptions[0];
            String endDescription = descriptions[1];
            
            DateTime startDate = FridayUtilities.findDateFromDescription(startDescription, year);
            DateTime endDate = FridayUtilities.findDateFromDescription(endDescription, year);
            
            DateTime holidayDate = startDate;
            endDate = endDate.plusDays(1);
            
            while (holidayDate.isBefore(endDate))
            {
                holidayDates.add(holidayDate);
                holidayDate = holidayDate.plusDays(1);
            }
        }
        else
        {
            // otherwise, the holiday is a single date, so find it
            // from the description
            DateTime dt = FridayUtilities.findDateFromDescription(description, year);
            holidayDates.add(dt);
        }
        
        return holidayDates;
    }

    /**
     * getEventInstancesForUser
     * @param userId
     * @param requestDateTime
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public List<FridayEventInstance> getEventInstancesForUser(String userId, DateTime requestDateTime, DateTime startDate, DateTime endDate) throws Exception
    {       
        List<FridayEventInstance> instances = calendarDao.getEventInstancesForUserBetweenDates(userId, requestDateTime, startDate, endDate);

        return instances;
    }

    /**
     * buildDailyReminderEvents
     * @param reminders
     * @param startDate
     * @param endDate
     * @param endDate2 
     * @return
     */
    public List<FridayEvent> buildDailyReminderEvents(List<FridayEventInstance> reminders,  DateTime requestDateTime, DateTime startDate, DateTime endDate)
    {
        List<FridayEvent> dateReminders = new ArrayList<FridayEvent>();
        
        DateTime currentDate = startDate;
        
        //while (currentDate.isBefore(endDate.plusDays(1)))
        //{           
            for (FridayEventInstance eventInstance : reminders)
            {
                FridayEvent reminderEvent = new FridayEvent();
                reminderEvent.setDescription(eventInstance.getEventDescription());
                reminderEvent.setEndDateTime(eventInstance.getEventEndTime());
                reminderEvent.setEventId(eventInstance.getUserEventId());
                reminderEvent.setEventInstanceDescription(eventInstance.getEventDescription());
                reminderEvent.setEventInstanceId(eventInstance.getEventInstanceId());
                reminderEvent.setEventType(FridayEventType.valueOf(eventInstance.getEventType()));
                reminderEvent.setTitle(eventInstance.getEventTitle());
                reminderEvent.setEventUserId(eventInstance.getUserId());
                reminderEvent.setLocation(eventInstance.getEventLocation());
                reminderEvent.setStartDateTime(eventInstance.getEventStartTime());
                reminderEvent.setDuration(new Duration(reminderEvent.getStartDateTime(), reminderEvent.getEndDateTime()));
                reminderEvent.setAllDay(eventInstance.getEventStartTime().plusDays(1).equals(eventInstance.getEventEndTime()));
                reminderEvent.setAllDayEvent(reminderEvent.isAllDay());
                dateReminders.add(reminderEvent);
            }                
                        
            currentDate = currentDate.plusDays(1);
        //}
        
        return dateReminders;
    }

    /**
     * convertEventLengthDescriptionToDuration
     * @param eventLengthDesc
     * @return
     */
    private Duration convertEventLengthDescriptionToDuration(String eventLengthDesc)
    {        
        eventLengthDesc = eventLengthDesc.substring(1); // chop off leading "P"
        
        int years = 0;
        int months = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        
        // convert duration string
        // Years, months, and days are in lengthDescParts[0]
        // Hours, minutes, seconds are in lengthDescParts[1]
        String[] lengthDescParts = eventLengthDesc.split("T");
                
        while (!StringUtils.isEmpty(lengthDescParts[0]))
        {
            String lengthUnit = "";
            Integer lengthValue = 0;
            Integer chopIndex = 0;
            
            boolean unitFound = false;
            
            int Yindex = lengthDescParts[0].indexOf("Y");
            
            if (!unitFound && Yindex != -1)
            {
                lengthUnit = "Y";
                lengthValue = Integer.parseInt(lengthDescParts[0].substring(0, Yindex));
                chopIndex = Yindex + 1;
                unitFound = true;
            }
            
            int Mindex = lengthDescParts[0].indexOf("M");
            
            if (!unitFound && Mindex != -1)
            {
                lengthUnit = "M";
                lengthValue = Integer.parseInt(lengthDescParts[0].substring(0, Mindex));
                chopIndex = Mindex + 1;
                unitFound = true;
            }
            
            int Dindex = lengthDescParts[0].indexOf("D");
            
            if (!unitFound && Dindex != -1)
            {
                lengthUnit = "D";
                lengthValue = Integer.parseInt(lengthDescParts[0].substring(0, Dindex));
                chopIndex = Dindex + 1;
                unitFound = true;
            }
            
            if (lengthUnit.equalsIgnoreCase("Y"))
            {
                years = lengthValue;
            }
            else if (lengthUnit.equalsIgnoreCase("M"))
            {
                months = lengthValue;
            }
            else if (lengthUnit.equalsIgnoreCase("D"))
            {
                days = lengthValue;
            }
            
            lengthDescParts[0] = lengthDescParts[0].substring(chopIndex);
        }

        if (lengthDescParts.length > 1)
        {
            while (!StringUtils.isEmpty(lengthDescParts[1]))
            {
                String lengthUnit = "";
                Integer lengthValue = 0;
                Integer chopIndex = 0;

                boolean unitFound = false;

                int Hindex = lengthDescParts[1].indexOf("H");

                if (!unitFound && Hindex != -1)
                {
                    lengthUnit = "H";
                    lengthValue = Integer.parseInt(lengthDescParts[1].substring(0, Hindex));
                    chopIndex = Hindex + 1;
                    unitFound = true;
                }

                int Mindex = lengthDescParts[1].indexOf("M");

                if (!unitFound && Mindex != -1)
                {
                    lengthUnit = "M";
                    lengthValue = Integer.parseInt(lengthDescParts[1].substring(0, Mindex));
                    chopIndex = Mindex + 1;
                    unitFound = true;
                }

                int Sindex = lengthDescParts[1].indexOf("S");

                if (!unitFound && Sindex != -1)
                {
                    lengthUnit = "S";
                    lengthValue = Integer.parseInt(lengthDescParts[1].substring(0, Sindex));
                    chopIndex = Sindex + 1;
                    unitFound = true;
                }

                if (lengthUnit.equalsIgnoreCase("H"))
                {
                    hours = lengthValue;
                }
                else if (lengthUnit.equalsIgnoreCase("M"))
                {
                    minutes = lengthValue;
                }
                else if (lengthUnit.equalsIgnoreCase("S"))
                {
                    seconds = lengthValue;
                }

                lengthDescParts[1] = lengthDescParts[1].substring(chopIndex);
            }
        }
        
        Period period = new Period(years, months, 0, days, hours, minutes, seconds, 0);
        
        return period.toStandardDuration();        
    }
    

    /**
     * generateEventDatesFromDescription
     * @param startDate
     * @param endDate
     * @param repCount
     * @param repIntervalType
     * @param repIntervalValue
     * @param repIntervalDays
     * @return
     */
    private List<DateTime> generateEventDatesFromDescription(DateTime startDate, DateTime endDate,
            Integer repCount, String repIntervalType, Integer repIntervalValue, String[] repIntervalDays)
    {
        List<DateTime> eventDates = new ArrayList<DateTime>();

        if (repIntervalType.equalsIgnoreCase("DI"))
        {
            eventDates = FridayUtilities.generateDayIntervalDatesFromDescription(startDate, endDate,
                    repCount, repIntervalValue, repIntervalDays);
        }
        else if (repIntervalType.equalsIgnoreCase("WI"))
        {
            eventDates = FridayUtilities.generateWeekIntervalDatesFromDescription(startDate, endDate,
                    repCount, repIntervalValue, repIntervalDays);
        }
        else if (repIntervalType.equalsIgnoreCase("MI"))
        {
            eventDates = FridayUtilities.generateMonthIntervalDatesFromDescription(startDate, endDate,
                    repCount, repIntervalValue, repIntervalDays);            
        }
        else if (repIntervalType.equalsIgnoreCase("YI"))
        {
            eventDates = FridayUtilities.generateYearIntervalDatesFromDescription(startDate, endDate,
                    repCount, repIntervalValue, repIntervalDays);            
        }
        else
        {
            throw new RuntimeException("Unknown event interval type");
        }

        
        return eventDates;
    }
    
    /**
     * convertEventInstancesToJson
     * @param events
     * @return
     * @throws UnsupportedEncodingException 
     */
    public String convertEventInstancesToJson(List<FridayEventInstance> events) throws UnsupportedEncodingException
    {       
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        int eventCount = 0;
        
        for (FridayEventInstance event : events)
        {
            if (eventCount > 0)
            {
                sb.append(", ");
            }
            
            sb.append("{");
            
            sb.append("\"id\": \"");
            sb.append(event.getUserEventId());
            sb.append("\", ");
            
            //String title = event.getTitle();
            
            //if (title.length() > MAX_EVENT_TITLE_LENGTH)
            //{
            //    title = title.substring(0, MAX_EVENT_TITLE_LENGTH - 1) + "...";
            //}
            
            sb.append("\"title\": \"");
            sb.append(event.getEventTitle());
            sb.append("\", ");

            sb.append("\"description\": \"");
            sb.append(event.getEventDescription());
            sb.append("\", ");

            sb.append("\"type\": \"");
            sb.append(event.getEventType().toString());
            sb.append("\", ");
            
            sb.append("\"start\": \"");
            sb.append(event.getEventStartTime().toString());
            sb.append("\", ");
            
            sb.append("\"end\": \"");
            sb.append(event.getEventEndTime().toString());
            sb.append("\", ");

            boolean isAllDay = false;
            
            if (event.getEventStartTime().equals(event.getEventEndTime()))
            {
                isAllDay = true;
            }
            
            sb.append("\"allDay\":" + isAllDay);
            sb.append(", ");
                        

            sb.append("\"location\": \"");
            sb.append(event.getEventLocation());
            sb.append("\"");
                        
            sb.append("}");
            
            eventCount++;
            
        }
        
        sb.append("]");
        
        return sb.toString();
    }

    /**
     * convertEventInstancesToJson
     * @param events
     * @return
     * @throws UnsupportedEncodingException 
     */
    public String convertEventsToJson(List<FridayEvent> events) throws UnsupportedEncodingException
    {       
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        int eventCount = 0;
        
        for (FridayEvent event : events)
        {
            if (eventCount > 0)
            {
                sb.append(", ");
            }
            
            sb.append("{");
            
            sb.append("\"id\": \"");
            sb.append(event.getEventId());
            sb.append("\", ");
            
            //String title = event.getTitle();
            
            //if (title.length() > MAX_EVENT_TITLE_LENGTH)
            //{
            //    title = title.substring(0, MAX_EVENT_TITLE_LENGTH - 1) + "...";
            //}
            
            sb.append("\"title\": \"");
            sb.append(event.getTitle());
            sb.append("\", ");

            sb.append("\"description\": \"");
            sb.append(event.getDescription());
            sb.append("\", ");

            sb.append("\"type\": \"");
            sb.append(event.getEventType().toString());
            sb.append("\", ");
            
            sb.append("\"start\": \"");
            sb.append(event.getStartDateTime().toString());
            sb.append("\", ");
            
            sb.append("\"end\": \"");
            sb.append(event.getEndDateTime().toString());
            sb.append("\", ");
            
            sb.append("\"eventInstanceDescription\": \"");
            sb.append(event.getEventInstanceDescription());
            sb.append("\", ");

            sb.append("\"duration\": \"");
            sb.append(event.getDuration().toPeriod().toString());
            sb.append("\", ");
            
            sb.append("\"allDay\":" + event.isAllDay());
            sb.append(", ");

            sb.append("\"recurring\":" + event.isRecurring());
            sb.append(", ");

            if (event.getFridayLocation() != null)
            {
                sb.append("\"location\": \"");
                sb.append(event.getFridayLocation().getFormattedAddress());
                sb.append("\"");                
            }
            else
            {
                sb.append("\"location\": \"");
                sb.append(event.getLocation());
                sb.append("\"");
            }

            sb.append(", ");
            
            if (event.getEventInstanceId() != null)
            {
                sb.append("\"instanceId\": \"");
                sb.append(event.getEventInstanceId());
                sb.append("\"");                
            }
            else
            {
                sb.append("\"instanceId\": \"");
                sb.append("");
                sb.append("\"");                 
            }
            
            sb.append("}");
            
            eventCount++;
            
        }
        
        sb.append("]");
        
        return sb.toString();
    }    
    /**
     * deleteEventInstance
     * @param eventId
     * @param instanceDate
     * @param instanceDate2 
     * @throws Exception 
     */
    public void deleteEventInstance(String currentUser, String eventInstanceId) throws Exception
    {
        FridayUser user = adminManager.getUserByName(currentUser);
        
        
        FridayEventInstance eventInstance =
                calendarDao.getEventInstanceByInstanceId(user.getUserId(), eventInstanceId);

        if (eventInstance != null)
        {
            eventInstance.setDeleted(true);        
            calendarDao.updateEventInstance(eventInstance);
        }
        else
        {
            throw new RuntimeException("Could not locate event instance");
        }
    }


    /**
     * deleteEvent
     * @param currentUser
     * @param eventId
     */
    public void deleteEvent(String currentUser, String eventId) throws Exception
    {
        FridayUser user = adminManager.getUserByName(currentUser);
        
        calendarDao.deleteEvent(user.getUserId(), eventId);
        
    }

    /**
     * buildEventsFromInstances
     * @param instances
     * @return
     * @throws Exception 
     */
    public List<FridayEvent> buildEventsFromInstances(List<FridayEventInstance> instances) throws Exception
    {
        List<FridayEvent> events = new ArrayList<FridayEvent>();
        
        for (FridayEventInstance instance : instances)
        {
            FridayEvent event = new FridayEvent();
            
            FridayEventDescription descr = calendarDao.getEventForEventInstance(instance);
            
            event.setAllDay(instance.getEventStartTime().plusDays(1).equals(instance.getEventEndTime()));
            event.setDescription(instance.getEventDescription());
            event.setEventId(instance.getUserEventId());
            event.setEventInstanceDescription(descr.getEventInstanceDescription());
            event.setEventType(FridayEventType.valueOf(FridayEventType.class, descr.getEventType()));
            event.setEventUserId(instance.getUserId());
            
            // TODO: set location object from locationId
            
            event.setLocation(instance.getEventLocation());
            event.setRecurring(determineRecurringEvent(descr.getEventInstanceDescription()));
            
            event.setStartDateTime(instance.getEventStartTime());
            event.setEndDateTime(instance.getEventEndTime());

            event.setDuration(new Duration(instance.getEventStartTime(), instance.getEventEndTime()));

            event.setTitle(instance.getEventTitle());
            
            event.setEventInstanceId(instance.getEventInstanceId());
            
            events.add(event);
        }
        
        return events;
    }
    
    /**
     * determineRecurringEvent
     * 
     * Determine if the event is a recurring event; this is determined
     * by the repetition count being greater than 0 or the repetition
     * description length being greater than 0.
     * 
     * @param eventInstanceDescription
     * @return
     */
    private boolean determineRecurringEvent(String eventInstanceDescription)
    {
        boolean isRecurring = false;

        String[] descriptionParts = eventInstanceDescription.split("/");

        String repetitionDesc = descriptionParts[0];


        String[] repetitionParts = repetitionDesc.split("\\[");
        String repCountDesc = repetitionParts[0].substring(1);
        Integer repCount = Integer.parseInt(repCountDesc);

        // get the repetition interval and day description
        String repPartDesc = repetitionParts[1].substring(0, repetitionParts[1].length() - 1);
        
        if (repPartDesc.length() > 0 || repCount > 0)
        {
            isRecurring = true;
        }
        
        return isRecurring;
    }
    
}
