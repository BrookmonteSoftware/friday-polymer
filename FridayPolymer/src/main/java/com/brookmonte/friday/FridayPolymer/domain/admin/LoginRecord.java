/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.admin;

import org.joda.time.DateTime;

/**
 * @author Pete
 *
 */
public class LoginRecord
{

    String loginHistoryId;
    String userId;
    String userName;
    String ipAddress;
    DateTime loginDateTime;
    Boolean loginSuccess;
    String failureMessage;
    
    /**
     * @return the loginHistoryId
     */
    public String getLoginHistoryId()
    {
      return loginHistoryId;
    }
    /**
     * @param loginHistoryId the loginHistoryId to set
     */
    public void setLoginHistoryId(String loginHistoryId)
    {
      this.loginHistoryId = loginHistoryId;
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
     * @return the userName
     */
    public String getUserName()
    {
      return userName;
    }
    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName)
    {
      this.userName = userName;
    }
    /**
     * @return the ipAddress
     */
    public String getIpAddress()
    {
      return ipAddress;
    }
    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress)
    {
      this.ipAddress = ipAddress;
    }
    /**
     * @return the loginDateTime
     */
    public DateTime getLoginDateTime()
    {
      return loginDateTime;
    }
    /**
     * @param loginDateTime the loginDateTime to set
     */
    public void setLoginDateTime(DateTime loginDateTime)
    {
      this.loginDateTime = loginDateTime;
    }
    /**
     * @return the loginSuccess
     */
    public Boolean getLoginSuccess()
    {
      return loginSuccess;
    }
    /**
     * @param loginSuccess the loginSuccess to set
     */
    public void setLoginSuccess(Boolean loginSuccess)
    {
      this.loginSuccess = loginSuccess;
    }
    /**
     * @return the failureMessage
     */
    public String getFailureMessage()
    {
      return failureMessage;
    }
    /**
     * @param failureMessage the failureMessage to set
     */
    public void setFailureMessage(String failureMessage)
    {
      this.failureMessage = failureMessage;
    }
    
    /**
     * toString
     */
    @Override
    public String toString()
    {
      StringBuffer out = new StringBuffer();
      
      out.append("\nLogin Date/Time: ");
      out.append(this.getLoginDateTime().toString());
      out.append("\nUser Id: ");
      out.append(this.getUserId());
      out.append("\nUser Name: ");
      out.append(this.getUserName());
      out.append("\nIP Address: ");
      out.append(this.getIpAddress());
      out.append("\nSuccessful Login: ");
      out.append(this.getLoginSuccess().toString());
      out.append("\nMessage: ");
      out.append(this.getFailureMessage());
      
      return out.toString();
    }

}
