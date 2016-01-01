/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.calendar;

import java.io.IOException;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.TypeConversionException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEvent;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;
import com.brookmonte.friday.FridayPolymer.manager.admin.AdministrationManager;

/**
 * @author Pete
 *
 */
public class CalendarEmailIntegrationProcessor
{
    @Autowired
    private AdministrationManager adminManager;
    
    @Autowired
    private CalendarManager calendarManager;
    
    @Handler
    public void process(Exchange exchange)
    {
        // process the exchange
         Map<String, Object> headers = exchange.getIn().getHeaders();

        // get the sender
        String fromHeader = (String) headers.get("from");
        String originatingEmailHeader = (String) headers.get("x-originating-email");
        
        String deliveredTo = (String) headers.get("delivered-to");
        String envelopeTo = (String) headers.get("delivered-to");
        
        if (!deliveredTo.equals("calendars@myfriday.info")
                && !deliveredTo.equals("calendars@brookmonte.com"))
        {
            // discard this - don't know how it got here
            return;
        }

        if (!envelopeTo.equals("calendars@myfriday.info")
                && !envelopeTo.equals("calendars@brookmonte.com"))
        {
            // discard this - don't know how it got here
            return;
        }
        
        String from = this.getFromFromFromHeader(fromHeader);
        String originatingEmail = this.getOriginatingEmailFromHeader(originatingEmailHeader);
        
        if (StringUtils.isEmpty(from) || StringUtils.isEmpty(originatingEmail) || !from.equals(originatingEmail))
        {
            // discard this - from or originating email are not set,
            // or they do not match
            return;
        }
        
        FridayUser user = adminManager.getUserByName(originatingEmail);
        
        if (user == null || !user.getEnabled())
        {
            // discard - unknown user or disabled user
            return;
        }

        FridayEvent newEvent = new FridayEvent();
                
        Map<String, DataHandler> attachments = exchange.getIn().getAttachments();
        if (attachments.size() > 0)
        {
            // the attachment must be an ICS file
            for (String name : attachments.keySet())
            {
                DataHandler dh = attachments.get(name);
                // get the file name
                String filename = dh.getName();
                
                // filename has to end with .ics
                
                if (!filename.endsWith(".ics"))
                {
                    break;
                }

                // get the content and convert it to byte[]
                try
                {
                    String data = exchange.getContext().getTypeConverter().convertTo(String.class, dh.getInputStream());
                    //System.out.println(data);
                    
                    String[] icsFileLines = data.split("\r\n");
                    
                    if (!icsFileLines[0].equals("BEGIN:VCALENDAR")
                            || !icsFileLines[icsFileLines.length - 1].equals("END:VCALENDAR"))
                    {
                        // not a vcalendar file (or malformed), so discard it
                        break;
                    }
                    
                    // Event Instance Description format
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
                    
                    // build a FridayEvent from the ICS file data
                    DateTime startDateTime = null;
                    DateTime endDateTime = null;
                    boolean vEventFound = false;
                    String version = null;
                    
                    for (int i = 0; i < icsFileLines.length; i++)
                    {
                        System.out.println(icsFileLines[i]);
                                                
                        if (icsFileLines[i].startsWith("VERSION:"))
                        {
                            version = icsFileLines[i].substring("VERSION:".length());
                            
                            if (!version.equals("2.0"))
                            {
                                // not a ICS version 2.0 calendar format, so discard it
                                return;
                            }
                        }                        
                        else if (icsFileLines[i].startsWith("BEGIN:VEVENT:"))
                        {
                            vEventFound = true;
                        }
                        else if (icsFileLines[i].startsWith("DESCRIPTION:") && vEventFound)
                        {
                            // the event description
                            StringBuffer description = new StringBuffer();
                            
                            int descrIdx = i;
                            
                            String descrLine = icsFileLines[descrIdx].substring("DESCRIPTION:".length());
                            
                            description.append(descrLine.replaceAll("\\n", "\n"));
                            
                            while (!icsFileLines[descrIdx].endsWith("\\n") && descrIdx < icsFileLines.length)
                            {
                                descrIdx++;
                                descrLine = icsFileLines[descrIdx];
                                description.append(descrLine.replaceAll("\\n", "\n"));
                            }
                                                       
                            String descriptionString = description.toString();
                            descriptionString = descriptionString.replaceAll("\\\\n", "\n");
                            descriptionString = descriptionString.replaceAll("\t", "");
                            descriptionString = descriptionString.replaceAll("\\\\,", ",");
                            descriptionString = descriptionString.replaceAll("  ", " ");
                            newEvent.setDescription(descriptionString);
                        }
                        else if (icsFileLines[i].startsWith("SUMMARY") && vEventFound)
                        {
                            // the event title
                            int colonLoc = icsFileLines[i].indexOf(":");
                            
                            if (colonLoc > -1)
                            {
                                String title = icsFileLines[i].substring(colonLoc);
                                
                                if (!StringUtils.isEmpty(title))
                                {
                                    newEvent.setTitle(title);
                                }
                            }
                        }
                        else if (icsFileLines[i].startsWith("DTSTART") && vEventFound)
                        {
                            // the event start datetime
                            
                        }
                        else if (icsFileLines[i].startsWith("DTEND") && vEventFound)
                        {
                            // the event end datetime
                            
                        }
                        else if (icsFileLines[i].startsWith("RRULE") && vEventFound)
                        {
                            // the event repetition rule
                            
                        }                        
                    }
                    
                    if (StringUtils.isEmpty(version))
                    {
                        // no version was given, so discard
                        return;
                    }
                    
                    if (!vEventFound)
                    {
                        // no event, so discard
                        return;
                    }
                    
                    System.out.println(newEvent.getDescription());
                }
                catch (TypeConversionException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * getOriginatingEmailFromHeader
     * @param originatingEmailHeader
     * @return
     */
    private String getOriginatingEmailFromHeader(String originatingEmailHeader)
    {
        String originatingEmail = null;
        // should look like [pete-nelson@msn.com]
        
        if (!StringUtils.isEmpty(originatingEmailHeader))
        {
            originatingEmail = originatingEmailHeader.substring(1, originatingEmailHeader.length() - 1);
        }
        
        return originatingEmail;
    }
    

    /**
     * getFromFromFromHeader
     * @param fromHeader
     * @return
     */
    private String getFromFromFromHeader(String fromHeader)
    {
        String from = null;
        
        // looks like "Pete Nelson <pete-nelson@msn.com>"
        
        int firstBracketLoc = fromHeader.indexOf("<");
        int lastBracketLoc = fromHeader.indexOf(">");
        
        if (firstBracketLoc > -1 && lastBracketLoc > -1)
        {
            from = fromHeader.substring(firstBracketLoc + 1, lastBracketLoc);
        }
                
        return from;
    }
}
