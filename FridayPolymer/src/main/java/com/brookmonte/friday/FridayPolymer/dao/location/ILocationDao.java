/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.dao.location;

import com.brookmonte.friday.FridayPolymer.domain.location.FridayCountry;

/**
 * @author Pete
 *
 */
public interface ILocationDao
{
    /**
     * getCountryByCountryCode2
     * @param countryCode2
     * @return
     */
    public FridayCountry getCountryByCountryCode2(String countryCode2);
}
