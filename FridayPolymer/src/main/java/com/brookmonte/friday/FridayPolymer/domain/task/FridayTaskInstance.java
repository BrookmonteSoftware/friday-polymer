/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.task;

import java.util.ArrayList;

import org.joda.time.DateTime;

/**
 * @author Pete
 *
 */
public class FridayTaskInstance
{
//    `TASK_INSTANCE_ID` VARCHAR(64) NOT NULL,
//    `TASK_ID` VARCHAR(64) NOT NULL,
//    `PARENT_TASK_INSTANCE_ID` VARCHAR(64) NULL,
//    `USER_ID` VARCHAR(64) NOT NULL,
//    `DUE_DATE` BIGINT(20) NULL,
//    `IS_COMPLETE` TINYINT(1) NOT NULL DEFAULT 0,
//    `DESCRIPTION` VARCHAR(500) NOT NULL,
//    `NOTES` VARCHAR(2000) NULL,
    
    private String taskInstanceId;
    private String taskId;
    private String parentTaskInstanceId;
    private String userId;
    private DateTime dueDate;
    private boolean isComplete;
    private String description;
    private String notes;
    private int instanceCount;      // the number of instances of this task
    
    private ArrayList<FridayTaskInstance> subtaskInstances;
    
    /**
     * @return the taskInstanceId
     */
    public String getTaskInstanceId()
    {
        return taskInstanceId;
    }
    /**
     * @param taskInstanceId the taskInstanceId to set
     */
    public void setTaskInstanceId(String taskInstanceId)
    {
        this.taskInstanceId = taskInstanceId;
    }
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
     * @return the parentTaskInstanceId
     */
    public String getParentTaskInstanceId()
    {
        return parentTaskInstanceId;
    }
    /**
     * @param parentTaskInstanceId the parentTaskInstanceId to set
     */
    public void setParentTaskInstanceId(String parentTaskInstanceId)
    {
        this.parentTaskInstanceId = parentTaskInstanceId;
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
     * @return the dueDate
     */
    public DateTime getDueDate()
    {
        return dueDate;
    }
    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate(DateTime dueDate)
    {
        this.dueDate = dueDate;
    }
    /**
     * @return the isComplete
     */
    public boolean isComplete()
    {
        return isComplete;
    }
    /**
     * @param isComplete the isComplete to set
     */
    public void setComplete(boolean isComplete)
    {
        this.isComplete = isComplete;
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
     * @return the instanceCount
     */
    public int getInstanceCount()
    {
        return instanceCount;
    }
    /**
     * @param instanceCount the instanceCount to set
     */
    public void setInstanceCount(int instanceCount)
    {
        this.instanceCount = instanceCount;
    }
    /**
     * @return the subtaskInstances
     */
    public ArrayList<FridayTaskInstance> getSubtaskInstances()
    {
        return subtaskInstances;
    }
    /**
     * @param subtaskInstances the subtaskInstances to set
     */
    public void setSubtaskInstances(ArrayList<FridayTaskInstance> subtaskInstances)
    {
        this.subtaskInstances = subtaskInstances;
    }    
}
