/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.newsfeeds;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

import com.brookmonte.friday.FridayPolymer.dao.admin.UserDao;
import com.brookmonte.friday.FridayPolymer.domain.admin.UserPreference;
import com.brookmonte.friday.FridayPolymer.domain.fridayUtils.ApplicationProperties;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

/**
 * @author Pete
 *
 */
@Component
public class NewsFeedManager
{
    @Autowired
    UserDao userDao;
    
    @SuppressWarnings("unchecked")
    public List<SyndFeed>  readFeed(String[] urlStrings) throws Exception
    {        
        List<SyndFeed> syndFeeds = new ArrayList<SyndFeed>();
        try
        {
            for (String urlString : urlStrings)
            {
                URL feedUrl = new URL(urlString);

                SyndFeedInput input = new SyndFeedInput();
                
                //String xmlString = readFeedXml(urlString);
                                
                SyndFeed feed = input.build(new XmlReader(feedUrl.openStream(), true));
                
                feed.setForeignMarkup(null);
                feed.setModules(null);
                
                for (SyndEntry entry : (List<SyndEntry>) feed.getEntries())
                {
                    entry.setForeignMarkup(null);
                    entry.setModules(null);
                    entry.setCategories(null);
                }
                
                syndFeeds.add(feed);
            }
            
            return syndFeeds;
        }
        catch (Exception ex)
        {
            // TODO: logging
            throw ex;
        }
    }

    /**
     * readFeedXml
     * @throws Exception 
     */
    public String readFeedXml(String urlString) throws Exception
    {
        StringBuilder builder = new StringBuilder();

        InputStream is = null;
        BufferedReader br = null;

        try
        {
            URL feedUrl = new URL(urlString);
            is = feedUrl.openStream();
            br = new BufferedReader(new InputStreamReader(is));

            String line;

            while ((line = br.readLine()) != null)
            {
                builder.append(line);
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            br.close();
            is.close();
        }
        
        return builder.toString();
    }

    /**
     * getTwitterXml
     * @param screenNames
     * @return
     * @throws TwitterException 
     * @throws FeedException 
     */
    public String getTwitterXml(String screenNames) throws FeedException
    {
        //TODO: retrieve twitter screen names from DB for user
                       
        String consumerKey = "SEKtYNp0w0VcQxdLqgbpb87p0"; // The application's consumer key
        String consumerSecret = "ruZSXGNiXhopAgQaK3xpfxkvyXLvjAohQmkJ0SKWwnFYZxp11j"; // The application's consumer secret
        String accessToken = "337471649-3CJEvvvJJVCyGMP9CaKScypZ39bzyOIZIkoWM8Ls"; // The access token granted after OAuth authorization
        String accessTokenSecret = "SHuWX3AE2Bd1ZHgEyAt6132eTtkwqiYKMDeLavqaE0roh"; // The access token secret granted after OAuth authorization        
        
        Twitter twitter = new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        
        
        List<Tweet> allTweets = new ArrayList<Tweet>();
        
        if (!StringUtils.isEmpty(screenNames))
        {
            String[] screenNameArray = screenNames.split(",");
            
            if (screenNameArray.length > 0)
            {
                for (String screenName : screenNameArray)
                {
                    List<Tweet> tweets = twitter.timelineOperations().getUserTimeline(screenName);
                    
                    if (tweets != null && tweets.size() > 0)
                    {
                        allTweets.addAll(tweets);
                    }
                }
                
            }            
        }
        
        String feedType = "rss_2.0";

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);

        feed.setTitle("MyFriday Twitter RSS Feed");
        feed.setLink("https://www.myfriday.info");
        feed.setDescription("Customized MyFriday Twitter RSS feed");

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        
        for (Tweet tweet : allTweets)
        {           
            try
            {
                SyndEntry entry;
                SyndContent description;
                List<SyndContent> content = new ArrayList<SyndContent>();
                
                entry = new SyndEntryImpl();
                entry.setAuthor(tweet.getUser().getName());
                entry.setTitle(tweet.getText());
                
                //TODO: figure how to get link for tweet if it has an associated URL
                entry.setLink("");
                entry.setPublishedDate(tweet.getCreatedAt());
                
                description = new SyndContentImpl();
                description.setType("text/plain");
                description.setValue(tweet.getText());
                //entry.setDescription(description);
                
                content.add(description);
                entry.setContents(content);
                
                entries.add(entry);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }           
        }        

        feed.setEntries(entries);
        
        SyndFeedOutput output = new SyndFeedOutput();
        
        String feedXml = output.outputString(feed, true);     
        
        return feedXml;
                
    }

    /**
     * getMessagesForUser
     * @param u
     * @throws Exception 
     */
    public String getMessagesForUser(FridayUser u) throws Exception
    {
        final String msgSeparator = "||";
        String messages = "";
        
        // get system messages and add to the message string

        // cannot show Amazon associates link now
        //messages += msgSeparator + "<table style='width: 336px; border:none; margin-top:-4px; padding-top: 0px;'><tbody><tr style='border:none; margin-top:0px; padding-top: 0px;'><td><iframe src='https://rcm-na.amazon-adsystem.com/e/cm?t=brookmsoftwa-20&o=1&p=41&l=ur1&category=amazonhomepage&f=ifr' width='88' height='28' scrolling='no' border='0' marginwidth='0' style='border:none;' frameborder='0'></iframe></td><td><a href='http://astore.amazon.com/brookmsoftwa-20' target='_blank' style='border: none; font-size: 0.9em; display: inline-block; vertical-align: top; color:#ED7D31'>MyFriday is an Amazon Affiliate. Purchases through MyFriday support this site!</a></td></tr></tbody></table>";
        
        List<UserPreference> userPrefs = userDao.getUserPreferences(u);
        
        for (UserPreference preference : userPrefs)
        {
            if (preference.getPreferenceName().equalsIgnoreCase("showFortuneCookie"))
            {
                // get fortune cookie and add it to the message string
                String cookieCountString = ApplicationProperties.getFortuneCookie("cookiecount");
                Integer cookieCount = Integer.parseInt(cookieCountString);
                
                Random rand = new Random();

                // nextInt is normally exclusive of the top value,
                // so add 1 to make it inclusive
                int randomNum = rand.nextInt((cookieCount - 1) + 1) + 1;
                messages += msgSeparator + ApplicationProperties.getFortuneCookie(String.valueOf(randomNum));
            }
            else if (preference.getPreferenceName().equalsIgnoreCase("showDailyBibleVerse"))
            {
                // get Bible verse from RSS feed and add it to the message string
                String[] bibleUrls = new String[1];
                bibleUrls[0] = "https://www.biblegateway.com/votd/get/?format=atom";
                
                List<SyndFeed> verseEntry = null;
                        
                try
                {
                    verseEntry = readFeed(bibleUrls);
                }
                catch (Exception ex)
                {
                    messages += msgSeparator + "<div style='display: inline-block; font:normal bold 12px Calibri, sans-serif;'>" + "Bible verse not availabe at this time." + "</div>";
                }
                
                if (verseEntry != null && verseEntry.get(0) != null)
                {
                    @SuppressWarnings("unchecked")
                    List<SyndEntryImpl> verseList = (List<SyndEntryImpl>) verseEntry.get(0).getEntries();
                    
                    if (verseList != null && verseList.size() > 0)
                    {
                        SyndEntryImpl verse = verseList.get(0);
                        
                        if (verse.getContents() != null && verse.getContents().size() > 0)
                        {
                            if (((SyndContentImpl) verse.getContents().get(0)).getValue() != null)
                            {
                                String verseTxt = ((SyndContentImpl) verse.getContents().get(0)).getValue().trim();
                                
                                if (verseTxt.length() > 100)
                                {
                                    verseTxt = verseTxt.substring(0, 99) + "...";
                                }
                                
                                verseTxt = "<a target='_blank' style='color:inherit' href='" + verse.getLink() + "'>" + verseTxt + "</a>";
                                                
                                messages += msgSeparator + "<div style='display: inline-block; font:normal bold 12px Calibri, sans-serif;'>" + verseTxt + " - " + verse.getTitle() + "</div>";
                            }
                        }
                    }
                }
            }
            else if (preference.getPreferenceName().equalsIgnoreCase("showLuckyNumbers"))
            {
                String luckyNumStr = "<div style='display: inline-block; color: #ED7D31; font:normal bold 16px Calibri, sans-serif; margin-right: 10px; '>Lucky Numbers </div>";
                
                // generate lucky numbers and add to the message string
                String numSpanStart = "<div style='border-radius: 14px; text-align: center; display: inline-block; height: 26px; width: 25px; background-color: #5B9BD5; color: white; font:normal bold 16px \"Palatino Linotype\", \"Book Antiqua\", Palatino, serif; margin-right: 5px; margin-top: 1px; padding-left: -2px; padding-top: 0px;'>";
                String numSpanEnd = "</div>";
                
                int[] numberArray = new int[6];
                
                for (int i = 0; i < 6; i++)
                {
                    numberArray[i] = 0;
                }
                
                final int LUCKY_MIN = 1;
                final int LUCKY_MAX = 59;
                final int POWER_MAX = 35;
                
                Random rand = new Random();
                
                for (int i = 0; i < 6; i++)
                {
                    boolean found = true;
                    
                    do
                    {
                        found = false;
                        
                        // nextInt is normally exclusive of the top value,
                        // so add 1 to make it inclusive
                        int randomNum = rand.nextInt((LUCKY_MAX - LUCKY_MIN) + LUCKY_MIN) + 1;
                        
                        for (int j = 0; j < 6; j++)
                        {
                            if (numberArray[j] == randomNum) found = true;                            
                        }
                        
                        if (!found)
                        {
                            numberArray[i] = randomNum;
                        }
                        
                    } while (found);
                }

                Arrays.sort(numberArray);
                
                for (int i = 0; i < 6; i++)
                {
                    luckyNumStr += numSpanStart + numberArray[i]  + numSpanEnd;
                }

                int randomNum = rand.nextInt((POWER_MAX - LUCKY_MIN) + LUCKY_MIN) + 1;

                String pwrSpanStart = "<div style='border-radius: 14px; text-align: center; display: inline-block; height: 26px; width: 25px; background-color: red; color: white; font:normal bold 16px \"Palatino Linotype\", \"Book Antiqua\", Palatino, serif; margin-right: 5px; margin-top: 1px; padding-left: -2px; padding-top: 0px;'>";
                String pwrSpanEnd = "</div>";

                luckyNumStr += pwrSpanStart + randomNum + pwrSpanEnd;
                
                
                messages += msgSeparator + luckyNumStr;
            }            
            
        }
                
        return messages;
    }
}
