/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brookmonte.friday.FridayPolymer.manager.traffic.TrafficManager;

/**
 * @author Pete
 *
 */
@Controller
public class TrafficController
{
    @Autowired
    TrafficManager trafficManager;
    
    /**
     * getTrafficAlerts
     * @param northLat
     * @param westLon
     * @param southLat
     * @param eastLon
     * @return
     * @throws Exception
     */
    @RequestMapping("/getTrafficAlerts")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String getTrafficAlerts(@RequestParam("nLat") String northLat,
            @RequestParam("wLon") String westLon,
            @RequestParam("sLat") String southLat,
            @RequestParam("eLon") String eastLon) throws Exception
    {
        try
        {
            return trafficManager.getTrafficAlerts(northLat, westLon, southLat, eastLon);
        }
        catch (Exception e)
        {
            return "";
        }
    }
    

    
    @RequestMapping("/getTravelTime")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String getTravelTime(@RequestParam("origin") String origin,
            @RequestParam("destination") String destination,
            @RequestParam("mode") String mode,
            @RequestParam("departure_time") String departureTime,
            @RequestParam("traffic_model") String trafficModel) throws Exception
    {        
        try
        {
            return trafficManager.getTravelTime(origin, destination, mode, departureTime, trafficModel);
        }
        catch (Exception e)
        {
            return "";
        }
    }
    
    /**
     * getTrafficAlertsXml
     * @param northLat
     * @param westLon
     * @param southLat
     * @param eastLon
     * @return
     * @throws Exception
     */
    @RequestMapping("/getTrafficAlertsXml")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String getTrafficAlertsXml(@RequestParam("nLat") String northLat,
            @RequestParam("wLon") String westLon,
            @RequestParam("sLat") String southLat,
            @RequestParam("eLon") String eastLon) throws Exception
    {
        try
        {
            return trafficManager.getTrafficAlertsRss(northLat, westLon, southLat, eastLon);
        }
        catch (Exception e)
        {
            return "";
        }
    }    
}
