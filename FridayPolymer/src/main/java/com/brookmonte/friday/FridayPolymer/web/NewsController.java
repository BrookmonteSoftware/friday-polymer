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

import com.brookmonte.friday.FridayPolymer.manager.newsfeeds.NewsFeedManager;
import com.sun.syndication.feed.synd.SyndFeed;

/**
 * @author Pete
 *
 */
@Controller
public class NewsController
{
    @Autowired
    NewsFeedManager newsFeedManager;
    
    /**
     * getNewsFeed
     * @param urlStringCommaSeparated
     * @return
     * @throws Exception 
     */
    @RequestMapping("/getNewsFeed")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public List<SyndFeed> getNewsFeed(@RequestParam("feedurls") String urlStringSeparated) throws Exception
    {
        try
        {
            String[] urlStrings = urlStringSeparated.split(";");
            
            List<SyndFeed> feeds = newsFeedManager.readFeed(urlStrings);
            
            return feeds;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
