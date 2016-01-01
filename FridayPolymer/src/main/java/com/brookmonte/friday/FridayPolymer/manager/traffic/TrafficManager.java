/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.traffic;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.brookmonte.friday.FridayPolymer.domain.fridayUtils.FridayUtilities;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * @author Pete
 *
 */
@Component
public class TrafficManager
{
    @Autowired
    FridayUtilities utils;

    /**
     * getTrafficAlerts
     * 
     * Get traffic alerts for the area given by the bounding box from the MapQuest web service.
     * 
     * @param nLat
     * @param wLon
     * @param sLat
     * @param eLon
     * @return
     * @throws Exception
     */
    public String getTrafficAlerts(String nLat, String wLon, String sLat, String eLon) throws Exception
    {
        String trafficAlertUrl = "https://dev.virtualearth.net/REST/v1/Traffic/Incidents/";
        trafficAlertUrl += sLat + ",";
        trafficAlertUrl += wLon + ",";
        trafficAlertUrl += nLat + ",";
        trafficAlertUrl += eLon;
        trafficAlertUrl += "?key=" + FridayUtilities.bingMapsApiKey;
        
        return utils.readResponseFromUrl(trafficAlertUrl);
    }

    /**
     * getTrafficAlertsMapQuestXml
     * @param nLat
     * @param wLon
     * @param sLat
     * @param eLon
     * @return
     * @throws Exception
     */
    public String getTrafficAlertsXml(String nLat, String wLon, String sLat, String eLon) throws Exception
    {
        String trafficAlertUrl = "https://dev.virtualearth.net/REST/v1/Traffic/Incidents/";
        trafficAlertUrl += sLat + ",";
        trafficAlertUrl += wLon + ",";
        trafficAlertUrl += nLat + ",";
        trafficAlertUrl += eLon;
        trafficAlertUrl += "?key=" + FridayUtilities.bingMapsApiKey + "&o=xml";
        
        return utils.readResponseFromUrl(trafficAlertUrl);
        
    }

    /**
     * getTrafficAlertsRss
     * @param northLat
     * @param westLon
     * @param southLat
     * @param eastLon
     * @return
     * @throws Exception
     */
    public String getTrafficAlertsRss(String northLat, String westLon, String southLat, String eastLon) throws Exception
    {
        String trafficAlerts = this.getTrafficAlertsXml(northLat, westLon, southLat, eastLon);
        
        int i = 0;
        
        while (trafficAlerts.charAt(i) != '<')
        {
            i++;
        }
        
        trafficAlerts = trafficAlerts.substring(i);
        
        SAXBuilder builder = new SAXBuilder();
   
        String feedType = "rss_2.0";

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);

        feed.setTitle("MyFriday Traffic RSS Feed");
        feed.setLink("http://www.myfriday.info");
        feed.setDescription("Customized MyFriday Traffic Alerts RSS feed");        

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        Namespace ns = Namespace.getNamespace("http://schemas.microsoft.com/search/local/ws/rest/v1");
               
        try
        {
            Reader in = new StringReader(trafficAlerts);
            Document document = (Document) builder.build(in);
            Element rootNode = document.getRootElement();
            
            //rootNode.getChild
            Element resourceSets = rootNode.getChild("ResourceSets", ns);
            
            @SuppressWarnings("unchecked")
            List<Element> resourceSet = resourceSets.getChildren("ResourceSet", ns);
            
            for (Element aResourceSet : resourceSet)
            {
                @SuppressWarnings("unchecked")
                List<Element> resources = aResourceSet.getChildren("Resources", ns);
                
                for (Element aResource : resources)
                {
                    @SuppressWarnings("unchecked")
                    List<Element> trafficIncidents = aResource.getChildren("TrafficIncident", ns);
                    
                    for (Element incident : trafficIncidents)
                    {
                        SyndEntry entry;
                        SyndContent description;
                        List<SyndContent> content = new ArrayList<SyndContent>();
                        
                        String typeString = incident.getChildText("Type", ns);
                        
                        String sevString = incident.getChildText("Severity", ns);
                        String severityPrefix = "";
                        
                        entry = new SyndEntryImpl();
                        
                        if (typeString.equalsIgnoreCase("Accident")
                        || typeString.equalsIgnoreCase("Congestion")
                        || typeString.equalsIgnoreCase("DisabledVehicle")
                        || typeString.equalsIgnoreCase("Alert"))
                        {
                            if (sevString.equalsIgnoreCase("LowImpact"))
                            {
                                
                            }
                            else if (sevString.equalsIgnoreCase("Minor"))
                            {
                                
                            }
                            else if (sevString.equalsIgnoreCase("Moderate"))
                            {
                                severityPrefix = "SERIOUS INCIDENT:";
                            }
                            else if (sevString.equalsIgnoreCase("Serious"))
                            {
                                severityPrefix = "VERY SERIOUS INCIDENT:";
                            }
                            
                            entry.setTitle(severityPrefix + incident.getChildText("Description", ns));
                            
                            entry.setPublishedDate(new Date());
                            
                            description = new SyndContentImpl();
                            description.setType("text/plain");
                            description.setValue(severityPrefix + incident.getChildText("Description", ns));
                            //entry.setDescription(description);
                            
                            content.add(description);
                            entry.setContents(content);
                            
                            entries.add(entry);                            
                        }                       
                    }
                }
            }
        }
        catch (IOException io)
        {
            // TODO logging
            io.printStackTrace();
        }
        catch (JDOMException jdomex)
        {
            // TODO logging
            jdomex.printStackTrace();
        }     

        if (entries.size() == 0)
        {
            SyndEntry entry;
            SyndContent description;
            List<SyndContent> content = new ArrayList<SyndContent>();
            
            entry = new SyndEntryImpl();
            entry.setTitle("No traffic incidents or accidents to report at this time");
            entry.setPublishedDate(new Date());
            
            description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue("");
            entry.setDescription(description);
            
            content.add(description);
            entry.setContents(content);
            
            entries.add(entry);            
        }
        
        feed.setEntries(entries);
        
        SyndFeedOutput output = new SyndFeedOutput();
        
        String feedXml = output.outputString(feed, true);       
        
        return feedXml;
    }

    /**
     * getTravelTime
     * @param origin
     * @param destination
     * @param mode
     * @param departureTime
     * @param trafficModel
     * @return
     * @throws Exception
     */
    public String getTravelTime(String origin,
            String destination,
            String mode,
            String departureTime,
            String trafficModel) throws Exception
    {
        //https://maps.googleapis.com/maps/api/directions/output?parameters
        
        String travelTimeUrl = "https://maps.googleapis.com/maps/api/directions/json";
        travelTimeUrl += "?origin=" + origin;
        travelTimeUrl += "&destination=" + destination;
        travelTimeUrl += "&mode=" + mode;
        travelTimeUrl += "&departure_time=" + departureTime;
        travelTimeUrl += "&traffic_model=" + trafficModel;
        travelTimeUrl += "&key=" + FridayUtilities.googleApiKey;
        
        return utils.readResponseFromUrl(travelTimeUrl);
    }

}
