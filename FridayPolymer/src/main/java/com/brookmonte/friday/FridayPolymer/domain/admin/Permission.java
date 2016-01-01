/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.admin;

/**
 * @author Pete
 *
 */
public class Permission
{
    private String  permissionId;
    private String  permissionName;
    private String  permissionDescription;
    private Boolean permissionValue;
    
    
    /**
     * @return the permissionId
     */
    public String getPermissionId()
    {
      return permissionId;
    }
    /**
     * @param permissionId the permissionId to set
     */
    public void setPermissionId(String permissionId)
    {
      this.permissionId = permissionId;
    }
    /**
     * @return the permissionName
     */
    public String getPermissionName()
    {
      return permissionName;
    }
    /**
     * @param permissionName the permissionName to set
     */
    public void setPermissionName(String permissionName)
    {
      this.permissionName = permissionName;
    }  
    /**
     * @return the permissionDEscription
     */
    public String getPermissionDescription()
    {
      return permissionDescription;
    }
    /**
     * @param permissionDescription the permissionDescription to set
     */
    public void setPermissionDEscription(String permissionDescription)
    {
      this.permissionDescription = permissionDescription;
    }
    /**
     * @return the permissionValue
     */
    public Boolean getPermissionValue()
    {
      return permissionValue;
    }
    /**
     * @param permissionValue the permissionValue to set
     */
    public void setPermissionValue(Boolean permissionValue)
    {
      this.permissionValue = permissionValue;
    }

}
