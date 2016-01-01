/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.weather;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pete
 *
 */
public class DailyWeather
{
    String weatherDate;
    String weatherDay;
    String maxDailyTemp;
    String minDailyTemp;
    String daytimePrecipChance;
    String nightPrecipChance;
    List<WeatherHazard> hazardConditions = new ArrayList<WeatherHazard>();
    String weatherConditionSummary;
    String weatherConditionIconUrl;
    
    /**
     * @return the weatherDate
     */
    public String getWeatherDate()
    {
        return weatherDate;
    }
    /**
     * @param weatherDate the weatherDate to set
     */
    public void setWeatherDate(String weatherDate)
    {
        this.weatherDate = weatherDate;
    }
    /**
     * @return the maxDailyTemp
     */
    public String getMaxDailyTemp()
    {
        return maxDailyTemp;
    }
    /**
     * @param maxDailyTemp the maxDailyTemp to set
     */
    public void setMaxDailyTemp(String maxDailyTemp)
    {
        this.maxDailyTemp = maxDailyTemp;
    }
    /**
     * @return the minDailyTemp
     */
    public String getMinDailyTemp()
    {
        return minDailyTemp;
    }
    /**
     * @param minDailyTemp the minDailyTemp to set
     */
    public void setMinDailyTemp(String minDailyTemp)
    {
        this.minDailyTemp = minDailyTemp;
    }
    /**
     * @return the daytimePrecipChance
     */
    public String getDaytimePrecipChance()
    {
        return daytimePrecipChance;
    }
    /**
     * @param daytimePrecipChance the daytimePrecipChance to set
     */
    public void setDaytimePrecipChance(String daytimePrecipChance)
    {
        this.daytimePrecipChance = daytimePrecipChance;
    }
    /**
     * @return the nightPrecipChance
     */
    public String getNightPrecipChance()
    {
        return nightPrecipChance;
    }
    /**
     * @param nightPrecipChance the nightPrecipChance to set
     */
    public void setNightPrecipChance(String nightPrecipChance)
    {
        this.nightPrecipChance = nightPrecipChance;
    }
    /**
     * @return the weatherConditionSummary
     */
    public String getWeatherConditionSummary()
    {
        return weatherConditionSummary;
    }
    /**
     * @param weatherConditionSummary the weatherConditionSummary to set
     */
    public void setWeatherConditionSummary(String weatherConditionSummary)
    {
        this.weatherConditionSummary = weatherConditionSummary;
    }
    /**
     * @return the weatherConditionIconUrl
     */
    public String getWeatherConditionIconUrl()
    {
        return weatherConditionIconUrl;
    }
    /**
     * @param weatherConditionIconUrl the weatherConditionIconUrl to set
     */
    public void setWeatherConditionIconUrl(String weatherConditionIconUrl)
    {
        this.weatherConditionIconUrl = weatherConditionIconUrl;
    }
    /**
     * @return the hazardConditions
     */
    public List<WeatherHazard> getHazardConditions()
    {
        return hazardConditions;
    }
    /**
     * @param hazardConditions the hazardConditions to set
     */
    public void setHazardConditions(List<WeatherHazard> hazardConditions)
    {
        this.hazardConditions = hazardConditions;
    }
    /**
     * @return the weatherDay
     */
    public String getWeatherDay()
    {
        return weatherDay;
    }
    /**
     * @param weatherDay the weatherDay to set
     */
    public void setWeatherDay(String weatherDay)
    {
        this.weatherDay = weatherDay;
    }    
}
