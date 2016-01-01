/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.dao.task;

import java.util.List;

import com.brookmonte.friday.FridayPolymer.domain.task.FridayTask;
import com.brookmonte.friday.FridayPolymer.domain.task.FridayTaskInstance;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;

/**
 * @author Pete
 *
 */
public interface ITaskDao
{
    /**
     * getUserTasksByUserId
     * @param user
     * @return
     */
    public List<FridayTask> getUserTasksByUserId(FridayUser user);
    
    /**
     * getUserTaskInstancesByUserId
     * @param user
     * @return
     */
    public List<FridayTaskInstance> getUserTaskInstancesByUserId(FridayUser user);

    /**
     * getUserTaskInstanceByTaskInstanceId
     * @param taskInstanceId
     * @return
     */
    public FridayTaskInstance getUserTaskInstanceByTaskInstanceId(String taskInstanceId);

    /**
     * deleteTask
     * @param task
     */
    public void deleteTask(FridayTask task);

    /**
     * markTask
     * @param taskId
     * @param taskComplete
     */
    public void markTask(String taskId, boolean taskComplete);

    /**
     * insertTask
     * @param task
     * @param taskInstances
     * @throws Exception 
     */
    public void insertTask(FridayTask task, List<FridayTaskInstance> taskInstances, String allOrSelected) throws Exception;

    /**
     * getUserTaskByTaskId
     * @param taskId
     * @return
     */
    public FridayTask getUserTaskByTaskId(String taskId);

    /**
     * deleteTaskInstance
     * @param taskInstanceId
     */
    public void deleteTaskInstance(String taskInstanceId);

    /**
     * getTaskInstancesForTask
     * @param task
     * @return
     */
    public List<FridayTaskInstance> getTaskInstancesForTask(FridayTask task);

    /**
     * updateTaskInstance
     * @param taskInstance
     */
    public void updateTaskInstance(FridayTaskInstance taskInstance);

    /**
     * updateTask
     * @param task
     * @param instances
     */
    public void updateTask(FridayTask task, List<FridayTaskInstance> instances);

}
