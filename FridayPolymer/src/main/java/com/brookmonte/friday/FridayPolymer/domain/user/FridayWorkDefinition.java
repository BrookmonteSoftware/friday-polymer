/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FridayWorkDefinition
{
    @JsonProperty("workCompany") private String workCompany;    // company name
    @JsonProperty("workIndustry") private String workIndustry;  // industry
    @JsonProperty("workPosition") private String workPosition;  // position in the company
    
    /**
     * @return the workCompany
     */
    public String getWorkCompany()
    {
        return workCompany;
    }
    /**
     * @param workCompany the workCompany to set
     */
    public void setWorkCompany(String workCompany)
    {
        this.workCompany = workCompany;
    }
    /**
     * @return the workIndustry
     */
    public String getWorkIndustry()
    {
        return workIndustry;
    }
    /**
     * @param workIndustry the workIndustry to set
     */
    public void setWorkIndustry(String workIndustry)
    {
        this.workIndustry = workIndustry;
    }
    /**
     * @return the workPosition
     */
    public String getWorkPosition()
    {
        return workPosition;
    }
    /**
     * @param workPosition the workPosition to set
     */
    public void setWorkPosition(String workPosition)
    {
        this.workPosition = workPosition;
    }
    
    
}
