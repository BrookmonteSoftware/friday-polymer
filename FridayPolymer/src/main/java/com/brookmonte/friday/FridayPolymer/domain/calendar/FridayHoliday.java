/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.calendar;

/**
 * @author Pete
 *
 */
public class FridayHoliday
{
    private String holidayId;
    private String holidayName;
    private String holidayDateDescription;
    private boolean legalHoliday;
    /**
     * @return the holidayId
     */
    public String getHolidayId()
    {
        return holidayId;
    }
    /**
     * @param holidayId the holidayId to set
     */
    public void setHolidayId(String holidayId)
    {
        this.holidayId = holidayId;
    }
    /**
     * @return the holidayName
     */
    public String getHolidayName()
    {
        return holidayName;
    }
    /**
     * @param holidayName the holidayName to set
     */
    public void setHolidayName(String holidayName)
    {
        this.holidayName = holidayName;
    }
    /**
     * @return the holidayDateDescription
     */
    public String getHolidayDateDescription()
    {
        return holidayDateDescription;
    }
    /**
     * @param holidayDateDescription the holidayDateDescription to set
     */
    public void setHolidayDateDescription(String holidayDateDescription)
    {
        this.holidayDateDescription = holidayDateDescription;
    }
    /**
     * @return the legalHoliday
     */
    public boolean getLegalHoliday()
    {
        return legalHoliday;
    }    
    /**
     * @param legalHoliday the legalHoliday to set
     */
    public void setLegalHoliday(boolean legalHoliday)
    {
        this.legalHoliday = legalHoliday;
    }
}
