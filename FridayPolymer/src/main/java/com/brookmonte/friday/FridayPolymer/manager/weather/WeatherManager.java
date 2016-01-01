/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.weather;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.brookmonte.friday.FridayPolymer.domain.fridayUtils.FridayUtilities;
import com.brookmonte.friday.FridayPolymer.domain.weather.DailyWeather;
import com.brookmonte.friday.FridayPolymer.domain.weather.WeatherHazard;

/**
 * @author Pete
 * 
 */
@Component
public class WeatherManager
{
    @Autowired
    FridayUtilities utils;
    
    /**
     * getWeatherData
     * 
     * Retrieves summarized 7-day weather forecast from the NOAA
     * restful web service.
     * 
     * See http://graphical.weather.gov/xml/rest.php
     * 
     * @param latitude
     * @param longitude
     * @return
     * @throws Exception
     */
    public String getWeatherData(float latitude, float longitude) throws Exception
    {
        // http://graphical.weather.gov/xml/sample_products/browser_interface/ndfdBrowserClientByDay.php?lat=38.99&lon=-77.01&format=24+hourly&numDays=7

        String urlString = "http://graphical.weather.gov/xml/sample_products/browser_interface/ndfdBrowserClientByDay.php?lat="
                + latitude
                + "&lon="
                + longitude
                + "&format=24+hourly&numDays=7";
        
        return utils.readResponseFromUrl(urlString);
    }

    /**
     * getCurrentWeatherData
     * 
     * Retrieves current weather conditions from Dark Sky service
     * 
     * @param latitude
     * @param longitude
     * @return
     * @throws Exception
     */
    public String getCurrentWeatherData(float latitude, float longitude) throws Exception
    {
        String urlString = "https://api.forecast.io/forecast/22bf3666e4be4d1ff4e92a560ea11067/"
        + latitude + ","
        + longitude;
        
        return utils.readResponseFromUrl(urlString);
    }
    
    /**
     * processWeatherDataXml
     * 
     * Convert the weather data XML returned from NOAA
     * to a list of weather data objects.
     * 
     * @param weatherDataXml
     * @return
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws XPathExpressionException 
     */
    public List<DailyWeather> processWeatherDataXml(String weatherDataXml) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException
    {
        // construct the list of DailyWeather elements - assume it is a one-week forecast
        List<DailyWeather> dailyWeatherList = new ArrayList<DailyWeather>(7);
        
        for (int i = 0; i < 7; i++)
        {
            dailyWeatherList.add(new DailyWeather());
        }

        // construct a DOM document from the XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(weatherDataXml)));

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        
        final String errorPathStr = "/error";
        XPathExpression errorExp = xpath.compile(errorPathStr);
        
        // get the node containing the forecast start time
        Node errorNode = (Node) errorExp.evaluate(document, XPathConstants.NODE);
        
        if (errorNode != null)
        {
            return null;
        }

        // construct an XPath to the element giving the starting time of the forecast
        final String xpathStr = "/dwml/data/time-layout[@summarization=\"24hourly\"]/start-valid-time";
        XPathExpression startDateExp = xpath.compile(xpathStr);

        // get the node containing the forecast start time
        Node startDateNode = (Node) startDateExp.evaluate(document, XPathConstants.NODE);

        // get the text of the start time
        String nodeText = startDateNode.getTextContent();
        
        // start time is in this format: 2013-07-21T06:00:00-07:00 (UTC time format)
        DateTimeFormatter utcFormatter = ISODateTimeFormat.dateTimeParser();

        // construct a JODA DateTime object from the string
        DateTime startDateTime = utcFormatter.parseDateTime(nodeText);
        
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMM dd"); 
        String date = dateFormatter.print(startDateTime);
        
        // set the initial starting date
        dailyWeatherList.get(0).setWeatherDate(date);

        DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("EEE"); 
        String day = dayFormatter.print(startDateTime);
        
        dailyWeatherList.get(0).setWeatherDay(day);
        
        // set the date and time for the subsequent days of the week
        // by incrementing the date and time by one through six days
        // Joda Time handles this beautifully, even accounting for
        // daylight/standard time differences
        for (int i = 1; i < 7; i++)
        {
            DateTime plusPeriod = startDateTime.plus(Period.days(i));
             
            date = dateFormatter.print(plusPeriod);
            
            // set the date and day
            dailyWeatherList.get(i).setWeatherDate(date);
            
            day = dayFormatter.print(plusPeriod);
            
            dailyWeatherList.get(i).setWeatherDay(day);            
        }
        
        // now get the rest of the weather data for each day via XPath
        // and set it in each DailyWeather element
        
        for (int i = 0; i < 7; i++)
        {
            // max temp
            String maxTempXPathStr = "/dwml/data/parameters/temperature[@type='maximum']/value[" + (i + 1) + "]";
            
            XPathExpression maxTempExp = xpath.compile(maxTempXPathStr);

            // get the node containing the max temperature
            Node maxTempNode = (Node) maxTempExp.evaluate(document, XPathConstants.NODE);

            // get the text of the max temp
            String maxTempText = maxTempNode.getTextContent();
            
            dailyWeatherList.get(i).setMaxDailyTemp(maxTempText);

            // min temp
            String minTempXPathStr = "/dwml/data/parameters/temperature[@type='minimum']/value[" + (i + 1) + "]";

            XPathExpression minTempExp = xpath.compile(minTempXPathStr);
            
            // get the node containing the max temperature
            Node minTempNode = (Node) minTempExp.evaluate(document, XPathConstants.NODE);

            // get the text of the max temp
            String minTempText = minTempNode.getTextContent();
            
            dailyWeatherList.get(i).setMinDailyTemp(minTempText);
            
            // daytime precip chance
            String dayPrecipXPathStr = "/dwml/data/parameters/probability-of-precipitation[@type='12 hour']/value[" + ((i * 2) + 1) + "]";
            XPathExpression dayPrecipExp = xpath.compile(dayPrecipXPathStr);
            Node dayPrecipNode = (Node) dayPrecipExp.evaluate(document, XPathConstants.NODE);
            String dayPrecipText = dayPrecipNode.getTextContent();            
            dailyWeatherList.get(i).setDaytimePrecipChance(dayPrecipText);
            
            // night precip chance
            String nightPrecipXPathStr = "/dwml/data/parameters/probability-of-precipitation[@type='12 hour']/value[" + ((i * 2) + 2) + "]";
            XPathExpression nightPrecipExp = xpath.compile(nightPrecipXPathStr);
            Node nightPrecipNode = (Node) nightPrecipExp.evaluate(document, XPathConstants.NODE);
            String nightPrecipText = nightPrecipNode.getTextContent();            
            dailyWeatherList.get(i).setNightPrecipChance(nightPrecipText);

            // weather summary
            String weatherXPathStr = "/dwml/data/parameters/weather/weather-conditions[" + (i + 1) + "]";
            XPathExpression weatherExp = xpath.compile(weatherXPathStr);
            Node weatherNode = (Node) weatherExp.evaluate(document, XPathConstants.NODE);
            Node weatherSummaryNode= weatherNode.getAttributes().getNamedItem("weather-summary");
            String weatherSummaryText = weatherSummaryNode.getTextContent();            
            dailyWeatherList.get(i).setWeatherConditionSummary(weatherSummaryText);
            
            // weather cions
            String iconsXPathStr = "/dwml/data/parameters/conditions-icon/icon-link[" + (i + 1) + "]";
            XPathExpression iconExp = xpath.compile(iconsXPathStr);
            Node iconNode = (Node) iconExp.evaluate(document, XPathConstants.NODE);
            String iconLink = iconNode.getTextContent();            
            dailyWeatherList.get(i).setWeatherConditionIconUrl(iconLink);            
        }

        // weather hazards
        String hazardsXPathStr = "/dwml/data/parameters/hazards/hazard-conditions/hazard";
        XPathExpression hazardExp = xpath.compile(hazardsXPathStr);
        NodeList hazardNodes = (NodeList) hazardExp.evaluate(document, XPathConstants.NODESET);
        
        for(int i = 0; i < hazardNodes.getLength(); i++)
        {            
            Node node = hazardNodes.item(i);
            
            WeatherHazard hazard = new WeatherHazard();
            hazard.setHazardPhenomena(node.getAttributes().getNamedItem("phenomena").getTextContent());
            hazard.setHazardSignificance(node.getAttributes().getNamedItem("significance").getTextContent());
            hazard.setHazardType(node.getAttributes().getNamedItem("hazardType").getTextContent());
            
            NodeList hazardChildren = node.getChildNodes();
            
            for (int k = 0; k < hazardChildren.getLength(); k++)
            {
                Node hazardChild = hazardChildren.item(k);
                
                if (hazardChild.getNodeName().equals("hazardTextURL"))
                {
                    hazard.setHazardTextUrl(hazardChild.getTextContent());
                }
            }
            
            
            // TODO: match time-layout definitions to time-layouts in
            // hazards so that hazard conditions are accurately matched
            // to the days where they are forecasted to occur
            for (int j = 0; j < 7; j++)
            {
                dailyWeatherList.get(i).getHazardConditions().add(hazard);
                
            }
        }
        
        
        return dailyWeatherList;
    }
    

}
