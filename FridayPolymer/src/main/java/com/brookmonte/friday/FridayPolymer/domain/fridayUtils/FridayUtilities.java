/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.fridayUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEventType;

/**
 * @author Pete
 *
 */
@Component
public class FridayUtilities
{                                       
    public static String googleApiKey = "AIzaSyAw9ZIE3VxDdlt0PjbkxE6zG5Al3RSvq1g";
    public static String mapquestApiKey = "Fmjtd%7Cluub20a7lu%2Ca5%3Do5-9urg1z";
    public static String bingMapsApiKey = "AsJm8NYDQhcN2rD5EdI1jbXmZUAxEhesXee-kXpwm8toz--UX9JwboAb-mAaH894";

    /**
     * readResponseFromUrl
     * @param urlString
     * @return
     * @throws Exception 
     */
    public String readResponseFromUrl(String urlString) throws Exception
    {
        StringBuffer response = new StringBuffer();
        BufferedReader in = null;

        try
        {
            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            
            urlConnection.setReadTimeout(30000);

            int status = urlConnection.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK)
            {
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                {
                    response.append(inputLine);
                    response.append("\n");
                }
            }
        }
        catch (Exception e)
        {
            // TODO: log the error
            response.append("Not available");
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }

        return response.toString();        
    }

    /**
     * getEventTypefromString
     * @param eventName
     * @return
     */
    public static FridayEventType getEventTypefromString(String eventName)
    {
        if (eventName.equalsIgnoreCase("HOLIDAY"))
        {
            return FridayEventType.HOLIDAY;
        }
        else if (eventName.equalsIgnoreCase("PARTY"))
        {
            return FridayEventType.PARTY;
        }
        else if (eventName.equalsIgnoreCase("CONCERT"))
        {
            return FridayEventType.CONCERT;
        }
        else if (eventName.equalsIgnoreCase("MOVIE"))
        {
            return FridayEventType.MOVIE;
        }
        else if (eventName.equalsIgnoreCase("PLAY"))
        {
            return FridayEventType.PLAY;
        }
        else if (eventName.equalsIgnoreCase("MEETING"))
        {
            return FridayEventType.MEETING;
        }
        else if (eventName.equalsIgnoreCase("BIRTHDAY"))
        {
            return FridayEventType.BIRTHDAY;
        }
        else if (eventName.equalsIgnoreCase("ANNIVERSARY"))
        {
            return FridayEventType.ANNIVERSARY;
        }
        else if (eventName.equalsIgnoreCase("TASK"))
        {
            return FridayEventType.TASK;
        }
        else if (eventName.equalsIgnoreCase("REMINDER"))
        {
            return FridayEventType.REMINDER;
        }
        else if (eventName.equalsIgnoreCase("OTHER"))
        {
            return FridayEventType.OTHER;
        }
        else
        {
            throw new RuntimeException("Unknown Event Type string: " + eventName);
        }
    }
    
    /**
     * convertRequestDateStringToDateTime
     * @param requestDateString
     * @return
     */
    public static DateTime convertRequestDateStringToDateTime(String requestDate)
    {
        // JodaTime cannot parse time zone names, so chop it off
        // Chrome: Fri Nov 29 2013 23:32:35 GMT-0800 (Pacific Standard Time)
        // IE10: Sat Nov 30 17:52:36 PST 2013
        int parenIdx = requestDate.indexOf("(");
        
        if (parenIdx > 0)
        {
            requestDate = requestDate.substring(0, parenIdx - 1);
        }
                
        // Chrome and IE10 formats for date and time for supported the
        // date/time sent in the request header (also works for Safari)
        // TODO: handle formats for other browsers
        String jsChromeLocalDateTimeFormat = "EEE MMM dd yyyy HH:mm:ss zZ";
        DateTimeFormatter chromeFmt = DateTimeFormat.forPattern(jsChromeLocalDateTimeFormat).withOffsetParsed();
        
        String jsIE10LocalDateTimeFormat = "EEE MMM dd HH:mm:ss z yyyy";
        DateTimeFormatter ie10Fmt = DateTimeFormat.forPattern(jsIE10LocalDateTimeFormat);
        
        DateTime requestDateTime = null;
        
        try
        {
            requestDateTime = chromeFmt.parseDateTime(requestDate);
        }
        catch (IllegalArgumentException iae)
        {
            requestDateTime = ie10Fmt.parseDateTime(requestDate);
        }
        
        return requestDateTime;
    }
    
    /**
     * generateYearIntervalDatesFromDescription
     * @param startDate
     * @param endDate
     * @param repCount
     * @param repIntervalValue
     * @param repIntervalDays
     * @return
     */
    public static List<DateTime> generateDayIntervalDatesFromDescription(DateTime startDate, DateTime endDate,
            Integer repCount, Integer repIntervalValue, String[] repIntervalDays)
    {
        //
        // Day format examples
        //
        //   [DI:2] (every other day)
        //   [DI:1] (every day)
        
        List<DateTime> eventDates = new ArrayList<DateTime>();
        
        DateTime originalStartDate = startDate.withTimeAtStartOfDay().toDateTime().withZone(DateTimeZone.UTC);

        startDate = originalStartDate
                .plusHours(startDate.getHourOfDay())
                .plusMinutes(startDate.getMinuteOfHour())
                .plusSeconds(startDate.getSecondOfMinute()).withZone(DateTimeZone.UTC);
        
        eventDates.add(startDate.withZone(DateTimeZone.UTC));
        repCount--;
                
        
        for (int i = 0; i < repCount; i++)
        {
            // increment to the next day using the repeat interval value
            DateTime eventDate = startDate.plusDays(repIntervalValue).withZone(DateTimeZone.UTC);
            
            eventDates.add(eventDate);
            
            startDate = eventDate;
            
            if (endDate != null && (startDate.isEqual(endDate) || startDate.isAfter(endDate))) break;            
        }
        
        return eventDates;
    }
    
    /**
     * generateWeekIntervalDatesFromDescription
     * @param startDate
     * @param endDate
     * @param repCount
     * @param repIntervalValue
     * @param repIntervalDays
     * @return
     */
    public static List<DateTime> generateWeekIntervalDatesFromDescription(DateTime startDate,
            DateTime endDate,
            Integer repCount,
            Integer repIntervalValue,
            String[] repIntervalDays)
    {
        List<DateTime> eventDates = new ArrayList<DateTime>();

        // set up week intervals and get the first week iteration
        
        DateTime originalStartDate = startDate.withTimeAtStartOfDay().toDateTime().withZone(DateTimeZone.UTC);
        List<Integer> dayNumbers = new ArrayList<Integer>();
        
        // days are day numbers (0-6)    
        for (String dayNumberStr : repIntervalDays)
        {
            String[] dwParts = dayNumberStr.split(":");
            
            if (!dwParts[0].trim().equalsIgnoreCase("DW"))
            {
                throw new RuntimeException("Incorrect day-of-week format");
            }
                                
            Integer dayNumber = Integer.parseInt(dwParts[1].trim());
            dayNumbers.add(dayNumber);
        }
        
        // make sure the days of the week are in numeric order;
        // this is critical to the next step in the algorithm
        Collections.sort(dayNumbers);
        
        // It cannot be assumed that the start date picked is actually
        // one of the days of the week specified for the event, so increment
        // the start date until it is on the first day of the week
        // specified for the event that is *on or after* the originally
        // picked start date; e.g. if the original start date
        // that was picked was a Thursday, but the event was
        // specified to take place on Monday, Wednesday, and Friday,
        // then increment the starting date to the first Friday
        // following the original Thursday start date
        int startDayOfWeek = startDate.dayOfWeek().get();
        
        boolean found = false;
        int dayCountIndex = 0;
        
        while (!found && startDayOfWeek < 7)
        {
            dayCountIndex = 0;
            
            for (Integer dayNumber: dayNumbers)
            {
                if (startDayOfWeek == dayNumber)
                {
                    found = true;
                }                        
                
                if (found) break;
                dayCountIndex++;
            }
            
            if (!found) startDayOfWeek++;
        }
        
        // add each day of the week for the initial week
        // starting with the startDate
        for (int k = dayCountIndex; k < dayNumbers.size(); k++)
        {
            DateTime eventDate = startDate.dayOfWeek().setCopy(dayNumbers.get(k)).withZone(DateTimeZone.UTC);
            
            if (eventDate.isEqual(startDate)
                    || eventDate.isAfter(startDate))
            {
                eventDates.add(eventDate);
            }
        }
        
        if (repCount > 0)
        {
            endDate = null;
        }        
        
        startDate = originalStartDate.withDayOfWeek(1).plusWeeks(repIntervalValue)
                .plusHours(startDate.getHourOfDay())
                .plusMinutes(startDate.getMinuteOfHour())
                .plusSeconds(startDate.getSecondOfMinute()).withZone(DateTimeZone.UTC);

        // now get the dates for the remaining iterations;
        // this is easy once the initial date is computed
        for (int i = 0; i < repCount - 1
                && (endDate == null || startDate.isEqual(endDate) || startDate.isBefore(endDate)); i++)
        {
            // for each day of the week specified
            for (int k = 0; k < dayNumbers.size(); k++)
            {
                DateTime eventDate = startDate.dayOfWeek().setCopy(dayNumbers.get(k)).withZone(DateTimeZone.UTC);
                
                if (eventDate.isEqual(startDate)
                        || eventDate.isAfter(startDate))

                {
                    eventDates.add(eventDate);
                }                    
            }
            
            // increment to the next week using the repeat interval value
            startDate = startDate.plusWeeks(repIntervalValue);
        }
        
        return eventDates;
    }

    /**
     * generateMonthIntervalDatesFromDescription
     * @param startDate
     * @param endDate
     * @param repCount
     * @param repIntervalValue
     * @param repIntervalDays
     * @return
     */
    public static List<DateTime> generateMonthIntervalDatesFromDescription(DateTime startDate, DateTime endDate,
            Integer repCount, Integer repIntervalValue, String[] repIntervalDays)
    {       
        // FORMAT 1A: Rnnnn[MI:<1-12>;DD:<1-31>]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDnnHnnMnnS
        // FORMAT 1B: Rnnnn[MI:<1-12>;<1-5>|LAST#<0-6>DW]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDnnHnnMnnS

        List<DateTime> eventDates = new ArrayList<DateTime>(); 
        DateTime originalStartDate = startDate;
        boolean isOriginalStandardOffset = DateTimeZone.getDefault().isStandardOffset(originalStartDate.getMillis());
        
        
        if (repCount > 0)
        {
            endDate = null;
        }
        
        for (int i = 0; i < repCount; i++)
        {
            // for each day of the week specified
            for (int k = 0; k < repIntervalDays.length; k++)
            {
                String dayDescription = repIntervalDays[k];
                
                int month = startDate.getMonthOfYear();
                
                // if the day description is a day of the month (DD:nn)
                // then it has to be re-arranged to match the expected format (nnDD)
                if (dayDescription.toUpperCase().contains("DD"))
                {
                    String[] dayDescrParts = dayDescription.split(":");                    
                    dayDescription = dayDescrParts[1] + "DD";
                }
                
                // append the month from the start date to the description and get the year
                // so that the findDateFromDescription method can determine the actual date
                // from the description
                dayDescription = dayDescription + month;
                
                DateTime eventDate = findDateFromDescription(dayDescription, startDate.getYear()).withTimeAtStartOfDay().toDateTime();
                
                eventDate = eventDate
                        .plusHours(startDate.getHourOfDay())
                        .plusMinutes(startDate.getMinuteOfHour())
                        .withZoneRetainFields(DateTimeZone.UTC);

                boolean isStandardOffset = DateTimeZone.getDefault().isStandardOffset(eventDate.getMillis());
                
                if (!isStandardOffset && isOriginalStandardOffset)
                {
                    eventDate = eventDate.minusHours(1);
                }
                else if (isStandardOffset && !isOriginalStandardOffset)
                {
                    eventDate = eventDate.plusHours(1);
                }
                
                if (eventDate != null)
                {
                    if (eventDate.isEqual(originalStartDate) || eventDate.isAfter(originalStartDate)
                            && ((endDate == null || eventDate.isEqual(endDate) || eventDate.isBefore(endDate))))
                    {
                        eventDates.add(eventDate);
                    }
                }
            }
            
            // increment to the next month using the repeat interval value
            startDate = startDate.plusMonths(repIntervalValue);            
        }
        
        return eventDates;
    }

    /**
     * generateYearIntervalDatesFromDescription
     * @param startDate
     * @param endDate
     * @param repCount
     * @param repIntervalValue
     * @param repIntervalDays
     * @return
     */
    public static List<DateTime> generateYearIntervalDatesFromDescription(DateTime startDate, DateTime endDate,
            Integer repCount, Integer repIntervalValue, String[] repIntervalDays)
    {
        //
        // Year format examples
        // (basically the same as month intervals, except uses the holiday-type date descriptions)
        //
        //   [YI:2; 4#4DW11] (every other year on Thanksgiving)
        //   [YI:1; 25DD12] (every year on Christmas Day)
        
        List<DateTime> eventDates = new ArrayList<DateTime>(); 
        DateTime originalStartDate = startDate;
        
        boolean isOriginalStandardOffset = DateTimeZone.getDefault().isStandardOffset(originalStartDate.getMillis());
                
        if (repCount > 0)
        {
            endDate = null;
        }
        
        for (int i = 0; i < repCount; i++)
        {
            // for each day of the week specified
            for (int k = 0; k < repIntervalDays.length; k++)
            {
                String dayDescription = repIntervalDays[k];
                                
                DateTime eventDate = findDateFromDescription(dayDescription, startDate.getYear()).withTimeAtStartOfDay().toDateTime();
                
                eventDate = eventDate
                        .plusHours(startDate.getHourOfDay())
                        .plusMinutes(startDate.getMinuteOfHour())
                        .withZoneRetainFields(DateTimeZone.UTC);

                boolean isStandardOffset = DateTimeZone.getDefault().isStandardOffset(eventDate.getMillis());
                
                if (!isStandardOffset && isOriginalStandardOffset)
                {
                    eventDate = eventDate.minusHours(1);
                }
                else if (isStandardOffset && !isOriginalStandardOffset)
                {
                    eventDate = eventDate.plusHours(1);
                }
                
                if (eventDate != null)
                {
                    if (eventDate.isEqual(originalStartDate) || eventDate.isAfter(originalStartDate)
                            && ((endDate == null || eventDate.isEqual(endDate) || eventDate.isBefore(endDate))))
                    {
                        eventDates.add(eventDate);
                    }
                }
            }
            
            // increment to the next month using the repeat interval value
            startDate = startDate.plusYears(repIntervalValue);            
        }        
                
        return eventDates;
    }
    
    /**
     * findDateFromDescription
     * @param description
     * @param year
     * @return
     */
    public static DateTime findDateFromDescription(String description, int year)
    {
        DateTime dt = null;
        
        // description is a date in the format [01-31]DD[01-12]
        // or description of the date in a month
        // in the format [1-5|LAST]#[1-]DW[01-12] (1st - 5th or LAST # 1=Monday - 7=Sunday DW 01=January - 12 = December)
        // where
        // e.g. 4#4DW11 (fourth Thursday in November = Thanksgiving Day in the USA)
        // or LAST#1DW05 (last Monday in May = Memorial Day in the USA)
        // or a date description followed by a simple calculation
        // e.g. 4#5DW11+3 = Cyber Monday - first Monday after 4th Friday in November
        // or a holiday name (for holidays that require more complex algorithms to determine the date)
        // e.g. EASTER (to invoke the algorithm to find the date for Easter Sunday in the given year)
        // or a simple calculation based on a holiday name
        // e.g. EASTER-7 (Palm Sunday) or EASTER-2 (Good Friday) (offsets of number of days before/after Easter)
        // others: PASSOVER, ROSH_HASHANAH, YOM_KIPPUR, HANUKKAH
                
        if (description.toUpperCase().contains("EASTER"))
        {
            dt = Easter(year);
            
            if (description.contains("-"))
            {
                String[] descriptionParts = description.split("-");
                
                int offset = Integer.parseInt(descriptionParts[1].trim());
                
                dt = dt.minusDays(offset);
            }
            else if (description.contains("+"))
            {
                String[] descriptionParts = description.split("+");
                
                int offset = Integer.parseInt(descriptionParts[1].trim());
                
                dt = dt.plusDays(offset);
            }
        }
        else if (description.toUpperCase().contains("DD"))
        {
            String[] descriptionParts = description.toUpperCase().split("DD");
            int month = Integer.parseInt(descriptionParts[1].trim());             
            int day = 0;
            
            if (descriptionParts[0].trim().equalsIgnoreCase("LAST"))
            {
                dt = new DateTime(year, month, 1, 0, 0, 0);
                DateTime lastDayOfMonth = dt.dayOfMonth().withMaximumValue();
                day = lastDayOfMonth.getDayOfMonth();                
            }
            else
            {
                day = Integer.parseInt(descriptionParts[0].trim());
                
                dt = new DateTime(year, month, 1, 0, 0, 0);
                DateTime lastDayOfMonth = dt.dayOfMonth().withMaximumValue();
                int lastday = lastDayOfMonth.getDayOfMonth();
                
                if (day > lastday)
                {
                    return null;
                }
            }
            
            dt = new DateTime(year, month, day, 0, 0, 0);            
        }
        else if ((description.toUpperCase().contains("DW"))
                && (description.toUpperCase().contains("#")))
        {
            String[] descriptionParts = description.toUpperCase().split("DW|#");
            
            int ordinal = 0;
            
            if (descriptionParts[0].trim().equalsIgnoreCase("LAST"))
            {
                ordinal = 7;
            }
            else
            {
                ordinal = Integer.parseInt(descriptionParts[0].trim());
            }

            int dayOfWeek = Integer.parseInt(descriptionParts[1].trim());
            
            int month = 0;
            int offset = 0;
            
            String monthStr = descriptionParts[2].trim();
            
            if (monthStr.contains("-"))
            {
                String[] monthParts = monthStr.split("-");
                month = Integer.parseInt(monthParts[0].trim());
                offset = -Integer.parseInt(monthParts[1].trim());                
            }
            else if (monthStr.contains("+"))
            {
                String[] monthParts = monthStr.split("\\+");
                month = Integer.parseInt(monthParts[0].trim());
                offset = Integer.parseInt(monthParts[1].trim());
            }
            else
            {
                month = Integer.parseInt(descriptionParts[2].trim());
            }
            
            
            while (dt == null)
            {
                dt = NthWeekDayofMonth(ordinal, dayOfWeek, month, year);
                ordinal--;
            }
            
            if (offset > 0)
            {
                dt = dt.plusDays(offset);
            }
            else if (offset < 0)
            {
                dt = dt.minusDays(Math.abs(offset));
            }
        }
        else
        {
            throw new RuntimeException("Unknown date description format");
        }
        
        return dt;
    }
    
    /**
     * Easter
     * 
     * Find the date of Easter in the given year
     * 
     * @param year
     * @return
     */
    public static DateTime Easter(int year)
    {
        if (year <= 1582)
        {
            throw new IllegalArgumentException("Algorithm invalid before April 1583");
        }
        
        DateTime easter = null;
        
        int golden = (year % 19) + 1;                   // E1: metonic cycle
        int century = (year / 100) + 1;                 // E2: e.g. 1984 was in 20th century
        int x = (3 * century / 4) - 12;                 // E3: leap year correction
        int z = ((8 * century + 5) / 25) - 5;           // E4: sync with moon's orbit
        int d = (5 * year / 4) - x - 10;
        int epact = (11 * golden + 20 + z - x) % 30;    // E5: epact
        
        if ((epact == 25 && golden > 11) || epact == 24) epact++;
        
        int n = 44 - epact;        
        n += 30 * (n < 21 ? 1 : 0);                     // E6
        n += 7 - ((d + n) % 7);
        
        if (n > 31)                                     // E7
        {
            easter = new DateTime(year, 4, n - 31, 0, 0, 0);            
        }
        else
        {
            easter = new DateTime(year, 3, n, 0, 0, 0);
        }

        return easter;
    }
    
    /**
     * NthWeekDayofMonth
     * 
     * This method will return a date for the nth weekday of the
     * given month in the given year, e.g. given an ordinal of 2,
     * a dayOfWeek of 6, month of 4, and year of 2013, it will return
     * the date of the 2nd Saturday in April of 2013 (April 13, 2013).
     * 
     * @param ordinal
     * @param dayOfWeek
     * @param month
     * @param year
     * @return
     */
    public static DateTime NthWeekDayofMonth(int ordinal, int dayOfWeek, int month, int year)
    {
        DateTime NthWeekDay = null;
        DateTime mdt = new DateTime(year, month, 1, 0, 0);
        
        int daysInMonth = mdt.dayOfMonth().getMaximumValue();
        
        int dayOfWeekCount = 0;
        
        for (int i = 1; i <= daysInMonth; i++)
        {
            DateTime dt = new DateTime(year, month, i, 0, 0);
            int currentDayOfWeek = dt.getDayOfWeek();
            
            if (currentDayOfWeek == dayOfWeek) dayOfWeekCount++;
            
            if (dayOfWeekCount == ordinal)
            {
                NthWeekDay = dt;
                break;
            }
        }
        
        return NthWeekDay;
    }
    
    /**
     * parseDateDescription
     * @param dateDesc
     * @return
     */
    public static DateTime parseDateDescription(String dateDesc)
    {
        DateTime dt = null;
        
        if (dateDesc.contains("T"))
        {
            // assume a date in ISO 8601 format
            dt = new DateTime(dateDesc);
        }
        else if (dateDesc.contains("#"))
        {
            String[] startDateDescParts = dateDesc.toUpperCase().split("DW|#|M");
            
            int ordinal = 0;
            
            if (startDateDescParts[0].trim().equalsIgnoreCase("LAST"))
            {
                ordinal = 7;
            }
            else
            {
                ordinal = Integer.parseInt(startDateDescParts[0].trim());
            }

            int dayOfWeek = Integer.parseInt(startDateDescParts[1].trim());
            
            int month = Integer.parseInt(startDateDescParts[2].trim());
            int year = 0;
            int offset = 0;
            
            String yearStr = startDateDescParts[3].trim();
            
            if (yearStr.contains("-"))
            {
                String[] yearParts = yearStr.split("-");
                year = Integer.parseInt(yearParts[0].trim());
                offset = -Integer.parseInt(yearParts[1].trim());                
            }
            else if (yearStr.contains("+"))
            {
                String[] yearParts = yearStr.split("\\+");
                year = Integer.parseInt(yearParts[0].trim());
                offset = Integer.parseInt(yearParts[1].trim());
            }
            else
            {
                year = Integer.parseInt(startDateDescParts[3].trim());
            }
            
            dt = FridayUtilities.NthWeekDayofMonth(ordinal, dayOfWeek, month, year);
            
            if (offset > 0)
            {
                dt = dt.plusDays(offset);
            }
            else if (offset < 0)
            {
                dt = dt.minusDays(Math.abs(offset));
            }            
        }
        else
        {
            // try a plain MMM dd YYYY format
            String dFormat = "MMM dd YYYY";
            DateTimeFormatter ddfmt = DateTimeFormat.forPattern(dFormat);
            dt = ddfmt.parseDateTime(dateDesc); 
        }
        
        return dt;
    }    
}
