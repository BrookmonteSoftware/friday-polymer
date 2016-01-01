/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author Pete
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FridayLocation
{
    public static final int GEOCODE_QUALITY_CODE_LENGTH = 5;
    
    @JsonProperty("locationId") private String locationId;
    @JsonProperty("userId") private String userId;
    @JsonProperty("name") private String name;
    @JsonProperty("streetNumber") private String streetNumber;
    @JsonProperty("streetAddress1") private String streetAddress1;
    @JsonProperty("streetAddress2") private String streetAddress2;
    @JsonProperty("city") private String city;
    @JsonProperty("county") private String county;
    @JsonProperty("state") private String state;
    @JsonProperty("stateAbbreviation") private String stateAbbreviation;
    @JsonProperty("country") private String country;
    @JsonProperty("countryAbbreviation") private String countryAbbreviation;
    @JsonProperty("formattedAddress") private String formattedAddress;
    @JsonProperty("googlePlaceId") private String googlePlaceId;
    @JsonProperty("googleReferenceId") private String googleReferenceId;
    @JsonProperty("utcOffsetMinutes") private int utcOffsetMinutes;
    @JsonProperty("lat") private String lat;
    @JsonProperty("lon") private String lon;
    @JsonProperty("postalCode") private String postalCode;
    @JsonProperty("sideOfStreet") private String sideOfStreet;
    @JsonProperty("geocodeQuality") private String geocodeQuality;
    @JsonProperty("geocodeQualityCode") private String geocodeQualityCode;
    @JsonProperty("providedLocation") private String providedLocation;
    @JsonProperty("locationType") private String locationType;
    
    /**
     * @return the locationId
     */
    public String getLocationId()
    {
        return locationId;
    }
    /**
     * @param locationId the locationId to set
     */
    public void setLocationId(String locationId)
    {
        this.locationId = locationId;
    }    
    /**
     * @return the locationType
     */
    public String getLocationType()
    {
        return locationType;
    }
    /**
     * @param locationType the locationType to set
     */
    public void setLocationType(String locationType)
    {
        this.locationType = locationType;
    }
    /**
     * @return the userId
     */
    public String getUserId()
    {
        return userId;
    }
    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    /**
     * @return the streetNumber
     */
    public String getStreetNumber()
    {
        return streetNumber;
    }
    /**
     * @param streetNumber the streetNumber to set
     */
    public void setStreetNumber(String streetNumber)
    {
        this.streetNumber = streetNumber;
    }
    /**
     * @return the streetAddress1
     */
    public String getStreetAddress1()
    {
        return streetAddress1;
    }
    /**
     * @param streetAddress1 the streetAddress1 to set
     */
    public void setStreetAddress1(String streetAddress1)
    {
        this.streetAddress1 = streetAddress1;
    }
    /**
     * @return the streetAddress2
     */
    public String getStreetAddress2()
    {
        return streetAddress2;
    }
    /**
     * @param streetAddress2 the streetAddress2 to set
     */
    public void setStreetAddress2(String streetAddress2)
    {
        this.streetAddress2 = streetAddress2;
    }
    /**
     * @return the city
     */
    public String getCity()
    {
        return city;
    }
    /**
     * @param city the city to set
     */
    public void setCity(String city)
    {
        this.city = city;
    }
    /**
     * @return the county
     */
    public String getCounty()
    {
        return county;
    }
    /**
     * @param county the county to set
     */
    public void setCounty(String county)
    {
        this.county = county;
    }
    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }
    /**
     * @param state the state to set
     */
    public void setState(String state)
    {
        this.state = state;
    }
    /**
     * @return the stateAbbreviation
     */
    public String getStateAbbreviation()
    {
        return stateAbbreviation;
    }
    /**
     * @param stateAbbreviation the stateAbbreviation to set
     */
    public void setStateAbbreviation(String stateAbbreviation)
    {
        this.stateAbbreviation = stateAbbreviation;
    }
    /**
     * @return the country
     */
    public String getCountry()
    {
        return country;
    }
    /**
     * @param country the country to set
     */
    public void setCountry(String country)
    {
        this.country = country;
    }
    /**
     * @return the countryAbbreviation
     */
    public String getCountryAbbreviation()
    {
        return countryAbbreviation;
    }
    /**
     * @param countryAbbreviation the countryAbbreviation to set
     */
    public void setCountryAbbreviation(String countryAbbreviation)
    {
        this.countryAbbreviation = countryAbbreviation;
    }
    /**
     * @return the formattedAddress
     */
    public String getFormattedAddress()
    {
        return formattedAddress;
    }
    /**
     * @param formattedAddress the formattedAddress to set
     */
    public void setFormattedAddress(String formattedAddress)
    {
        this.formattedAddress = formattedAddress;
    }
    /**
     * @return the googlePlaceId
     */
    public String getGooglePlaceId()
    {
        return googlePlaceId;
    }
    /**
     * @param googlePlaceId the googlePlaceId to set
     */
    public void setGooglePlaceId(String googlePlaceId)
    {
        this.googlePlaceId = googlePlaceId;
    }
    /**
     * @return the googleReferenceId
     */
    public String getGoogleReferenceId()
    {
        return googleReferenceId;
    }
    /**
     * @param googleReferenceId the googleReferenceId to set
     */
    public void setGoogleReferenceId(String googleReferenceId)
    {
        this.googleReferenceId = googleReferenceId;
    }
    /**
     * @return the utcOffsetMinutes
     */
    public int getUtcOffsetMinutes()
    {
        return utcOffsetMinutes;
    }
    /**
     * @param utcOffsetMinutes the utcOffsetMinutes to set
     */
    public void setUtcOffsetMinutes(int utcOffsetMinutes)
    {
        this.utcOffsetMinutes = utcOffsetMinutes;
    }
    /**
     * @return the lat
     */
    public String getLat()
    {
        return lat;
    }
    /**
     * @param lat the lat to set
     */
    public void setLat(String lat)
    {
        this.lat = lat;
    }
    /**
     * @return the lon
     */
    public String getLon()
    {
        return lon;
    }
    /**
     * @param lon the lon to set
     */
    public void setLon(String lon)
    {
        this.lon = lon;
    }
    /**
     * @return the postalCode
     */
    public String getPostalCode()
    {
        return postalCode;
    }
    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }
    /**
     * @return the sideOfStreet
     */
    public String getSideOfStreet()
    {
        return sideOfStreet;
    }
    /**
     * @param sideOfStreet the sideOfStreet to set
     */
    public void setSideOfStreet(String sideOfStreet)
    {
        this.sideOfStreet = sideOfStreet;
    }
    /**
     * @return the geocodeQuality
     */
    public String getGeocodeQuality()
    {
        return geocodeQuality;
    }
    /**
     * @param geocodeQuality the geocodeQuality to set
     */
    public void setGeocodeQuality(String geocodeQuality)
    {
        this.geocodeQuality = geocodeQuality;
    }
    /**
     * @return the geocodeQualityCode
     */
    public String getGeocodeQualityCode()
    {
        return geocodeQualityCode;
    }
    /**
     * @param geocodeQualityCode the geocodeQualityCode to set
     */
    public void setGeocodeQualityCode(String geocodeQualityCode)
    {
        this.geocodeQualityCode = geocodeQualityCode;
    }
    /**
     * @param providedLocation the providedLocation to set
     */
    public void setProvidedLocation(String providedLocation)
    {
        this.providedLocation = providedLocation;
    }
    /**
     * @return the providedLocation
     */
    public String getProvidedLocation()
    {
        return providedLocation;
    }    
}
