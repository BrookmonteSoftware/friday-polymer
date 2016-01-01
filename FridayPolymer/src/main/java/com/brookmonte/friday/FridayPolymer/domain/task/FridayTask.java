/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.task;

import java.util.ArrayList;


/**
 * @author Pete
 *
 */
public class FridayTask
{
//    `TASK_ID` VARCHAR(64) NOT NULL,
//    `USER_ID` VARCHAR(64) NOT NULL,
//    `PARENT_TASK_ID` VARCHAR(64) NULL,
//    `DESCRIPTION` VARCHAR(500) NOT NULL,
//    `NOTES` VARCHAR(2000) NULL,
//    `TASK_INSTANCE_DESCRIPTION` VARCHAR(128) NOT NULL,
    
    private String taskId;
    private String userId;
    private String parentTaskId;
    private String description;
    private String notes;
    private String taskInstanceDescription;
    
    private ArrayList<FridayTask> subtasks;
    
    /**
     * @return the taskId
     */
    public String getTaskId()
    {
        return taskId;
    }
    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
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
     * @return the parentTaskId
     */
    public String getParentTaskId()
    {
        return parentTaskId;
    }
    /**
     * @param parentTaskId the parentTaskId to set
     */
    public void setParentTaskId(String parentTaskId)
    {
        this.parentTaskId = parentTaskId;
    }
    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    /**
     * @return the notes
     */
    public String getNotes()
    {
        return notes;
    }
    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes)
    {
        this.notes = notes;
    }
    /**
     * @return the taskInstanceDescription
     */
    public String getTaskInstanceDescription()
    {
        return taskInstanceDescription;
    }
    /**
     * @param taskInstanceDescription the taskInstanceDescription to set
     */
    public void setTaskInstanceDescription(String taskInstanceDescription)
    {
        this.taskInstanceDescription = taskInstanceDescription;
    }
    /**
     * @return the subtasks
     */
    public ArrayList<FridayTask> getSubtasks()
    {
        return subtasks;
    }
    /**
     * @param subtasks the subtasks to set
     */
    public void setSubtasks(ArrayList<FridayTask> subtasks)
    {
        this.subtasks = subtasks;
    }    
}
