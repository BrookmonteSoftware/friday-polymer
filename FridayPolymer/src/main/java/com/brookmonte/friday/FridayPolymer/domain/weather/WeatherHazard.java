/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.weather;

/**
 * @author Pete
 *
 */
public class WeatherHazard
{
    String hazardPhenomena;
    String hazardSignificance;
    String hazardType;
    String hazardTextUrl;
    
    /**
     * @return the hazardPhenomena
     */
    public String getHazardPhenomena()
    {
        return hazardPhenomena;
    }
    /**
     * @param hazardPhenomena the hazardPhenomena to set
     */
    public void setHazardPhenomena(String hazardPhenomena)
    {
        this.hazardPhenomena = hazardPhenomena;
    }
    /**
     * @return the hazardSignificance
     */
    public String getHazardSignificance()
    {
        return hazardSignificance;
    }
    /**
     * @param hazardSignificance the hazardSignificance to set
     */
    public void setHazardSignificance(String hazardSignificance)
    {
        this.hazardSignificance = hazardSignificance;
    }
    /**
     * @return the hazardType
     */
    public String getHazardType()
    {
        return hazardType;
    }
    /**
     * @param hazardType the hazardType to set
     */
    public void setHazardType(String hazardType)
    {
        this.hazardType = hazardType;
    }
    /**
     * @return the hazardTextUrl
     */
    public String getHazardTextUrl()
    {
        return hazardTextUrl;
    }
    /**
     * @param hazardTextUrl the hazardTextUrl to set
     */
    public void setHazardTextUrl(String hazardTextUrl)
    {
        this.hazardTextUrl = hazardTextUrl;
    }
}
