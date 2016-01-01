/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.location;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.brookmonte.friday.FridayPolymer.dao.location.LocationDao;
import com.brookmonte.friday.FridayPolymer.domain.location.FridayCountry;

/**
 * @author Pete
 *
 */
@Component
public class LocationManager
{
    @SuppressWarnings("unused")
    private static org.apache.log4j.Logger log = Logger.getLogger(LocationManager.class);
    
    @Autowired
    LocationDao locationDao;

    /**
     * getCountryByCountryCode2
     * @param countryCode2
     * @return
     */
    public final FridayCountry getCountryByCountryCode2(final String countryCode2)
    {
        return locationDao.getCountryByCountryCode2(countryCode2);
    }

}
