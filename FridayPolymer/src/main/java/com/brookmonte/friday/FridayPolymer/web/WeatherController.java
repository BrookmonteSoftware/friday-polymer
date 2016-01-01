/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brookmonte.friday.FridayPolymer.domain.weather.DailyWeather;
import com.brookmonte.friday.FridayPolymer.manager.weather.WeatherManager;

/**
 * @author Pete
 *
 */
@Controller
public class WeatherController
{
    @Autowired
    WeatherManager weatherManager;
    
    @RequestMapping("/noaaWeatherData")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public List<DailyWeather> getNoaaWeatherData(@RequestParam("latitude") String latitude, @RequestParam("longitude") String longitude) throws Exception
    {
        float fLatitude = Float.parseFloat(latitude);
        float fLongitude = Float.parseFloat(longitude);
        
        String weatherXml = weatherManager.getWeatherData(fLatitude, fLongitude);
                
        //System.out.println(weatherXml);
        
        List<DailyWeather> dailyWeather = weatherManager.processWeatherDataXml(weatherXml);
        return dailyWeather;
    }
    
    @RequestMapping("/currentWeatherData")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public String getCurrentWeatherData(@RequestParam("latitude") String latitude, @RequestParam("longitude") String longitude) throws Exception
    {
        float fLatitude = Float.parseFloat(latitude);
        float fLongitude = Float.parseFloat(longitude);
        
        String currentWeatherJson = weatherManager.getCurrentWeatherData(fLatitude, fLongitude);

        return currentWeatherJson;
    }
}
