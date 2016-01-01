/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.brookmonte.friday.FridayPolymer.domain.fridayUtils.FridayUtilities;

/**
 * @author Pete
 *
 */
@Component
public class ProxyManager
{
    @Autowired
    FridayUtilities utils;
    
    /**
     * getProxiedResponse
     * @param urlString
     * @return
     * @throws Exception
     */
    public String getProxiedResponse(String urlString) throws Exception
    {
        return utils.readResponseFromUrl(urlString);
    }
}
