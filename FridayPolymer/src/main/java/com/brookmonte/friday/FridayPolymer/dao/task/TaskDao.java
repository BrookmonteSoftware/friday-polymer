/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.dao.task;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.brookmonte.friday.FridayPolymer.domain.task.FridayTask;
import com.brookmonte.friday.FridayPolymer.domain.task.FridayTaskInstance;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;

/**
 * @author Pete
 *
 */
@Component
public class TaskDao extends SqlSessionDaoSupport implements ITaskDao
{
    @Autowired
    private DataSourceTransactionManager transactionManager;

    public SqlSessionFactory sqlSessionFactory;
    
    @Autowired
    public TaskDao(SqlSessionFactory sqlSessionFactory)
    {
        this.sqlSessionFactory = sqlSessionFactory;
    }
    
    @PostConstruct
    public void initIt() throws Exception
    {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
    
    /* TASKS */
    /**
     * getUserTasksByUserId
     */
    @Override
    public List<FridayTask> getUserTasksByUserId(FridayUser user)
    {
        return getSqlSession().selectList("com.happydog.friday.dao.task.TaskDao.getUserTasksByUserId", user.getUserId());
    }

    /**
     * getUserTaskByTaskId
     */
    @Override
    public FridayTask getUserTaskByTaskId(String taskId)
    {
        return getSqlSession().selectOne("com.happydog.friday.dao.task.TaskDao.getUserTaskByTaskId", taskId);
    }

    /**
     * insertTask
     * @param taskInstances 
     * @throws Exception 
     */
    @Override
    public void insertTask(FridayTask task, List<FridayTaskInstance> taskInstances, String allOrSelected) throws Exception
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            if (StringUtils.isEmpty(task.getTaskId()))
            {
                throw new Exception("insertTask: taskId is not set.");
            }
            
            getSqlSession().insert("com.happydog.friday.dao.task.TaskDao.insertTask", task);
            
            if (allOrSelected.equalsIgnoreCase("all") && !StringUtils.isEmpty(task.getParentTaskId()))
            {
                // get all instances of the parent task
                FridayTask parentTask = this.getUserTaskByTaskId(task.getParentTaskId());
                List<FridayTaskInstance> parentInstances = this.getTaskInstancesForTask(parentTask);

                // add the subtask instances (there will only be one) to the parent task instances
                
                if (taskInstances.size() > 1)
                {
                    throw new Exception("Multiple instances of a subtask");
                }
                
                for (FridayTaskInstance parentInstance : parentInstances)
                {
                    for (FridayTaskInstance taskInstance : taskInstances)
                    {
                        taskInstance.setTaskInstanceId(UUID.randomUUID().toString());
                        taskInstance.setParentTaskInstanceId(parentInstance.getTaskInstanceId());
                        getSqlSession().insert("com.happydog.friday.dao.task.TaskDao.insertTaskInstance", taskInstance);
                    }
                }
            }
            else
            {
                for (FridayTaskInstance taskInstance : taskInstances)
                {
                    getSqlSession().insert("com.happydog.friday.dao.task.TaskDao.insertTaskInstance", taskInstance);
                }
            }
            
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }        
    }
    
    /**
     * updateTask
     * @param instances 
     */
    @Override
    public void updateTask(FridayTask task, List<FridayTaskInstance> instances)
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            getSqlSession().update("com.happydog.friday.dao.task.TaskDao.updateTask", task);
            
            for (FridayTaskInstance instance : instances)
            {
                getSqlSession().update("com.happydog.friday.dao.task.TaskDao.updateTaskInstance", instance);
            }

            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }         
    }    

    /**
     * deleteTask
     */
    @Override
    public void deleteTask(FridayTask task)
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            // make sure the task is fully populated with all subtasks by retrieving it from the DB
            FridayTask theTask = this.getUserTaskByTaskId(task.getTaskId());
            
            if (theTask.getSubtasks() != null && theTask.getSubtasks().size() > 0)
            {
                for (FridayTask subtask : theTask.getSubtasks())
                {
                    // get all instances of the subtask
                    List<FridayTaskInstance> subtaskInstances = getSqlSession().selectList("com.happydog.friday.dao.task.TaskDao.selectTaskInstancesForTask", subtask.getTaskId());

                    if (subtaskInstances != null && subtaskInstances.size() > 0)
                    {
                        for (FridayTaskInstance subtaskInstance : subtaskInstances)
                        {
                            getSqlSession().delete("com.happydog.friday.dao.task.TaskDao.deleteTaskInstance", subtaskInstance.getTaskInstanceId());
                        }
                    }
                }
            }
            
            // delete the task instances
            // get all instances of the task
            List<FridayTaskInstance> taskInstances = getSqlSession().selectList("com.happydog.friday.dao.task.TaskDao.selectTaskInstancesForTask", task.getTaskId());

            if (taskInstances != null && taskInstances.size() > 0)
            {
                for (FridayTaskInstance taskInstance : taskInstances)
                {
                    if (taskInstance.getSubtaskInstances() != null && taskInstance.getSubtaskInstances().size() > 0)
                    {
                        for (FridayTaskInstance subtask : taskInstance.getSubtaskInstances())
                        {
                            deleteTaskInstance(subtask.getTaskInstanceId());
                        }
                    }
                    
                    getSqlSession().delete("com.happydog.friday.dao.task.TaskDao.deleteSubtaskInstancesForTaskInstance", taskInstance.getTaskInstanceId());
                    getSqlSession().delete("com.happydog.friday.dao.task.TaskDao.deleteTaskInstance", taskInstance.getTaskInstanceId());
                }
            }
            
            // delete the subtasks
            getSqlSession().delete("com.happydog.friday.dao.task.TaskDao.deleteSubtasksForTask", task.getTaskId());

            getSqlSession().delete("com.happydog.friday.dao.task.TaskDao.deleteTask", task.getTaskId());
            
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }        
    }
    
    /* TASK INSTANCES */
    /**
     * getUserTaskInstancesByUserId
     */
    @Override
    public List<FridayTaskInstance> getUserTaskInstancesByUserId(FridayUser user)
    {
        return getSqlSession().selectList("com.happydog.friday.dao.task.TaskDao.getUserTaskInstancesByUserId", user.getUserId());
    }
    
    /**
     * getUserTaskInstanceByTaskInstanceId
     */
    @Override
    public FridayTaskInstance getUserTaskInstanceByTaskInstanceId(String taskInstanceId)
    {
        return getSqlSession().selectOne("com.happydog.friday.dao.task.TaskDao.getUserTaskInstanceByTaskInstanceId", taskInstanceId);
    }
    
    /**
     * getTaskInstancesForTask
     */
    @Override
    public List<FridayTaskInstance> getTaskInstancesForTask(FridayTask task)
    {
        return getSqlSession().selectList("com.happydog.friday.dao.task.TaskDao.selectTaskInstancesForTask", task.getTaskId());
    }
    
    /**
     * markTask
     */
    @Override
    public void markTask(String taskInstanceId, boolean taskComplete)
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("taskInstanceId", taskInstanceId);
            params.put("isComplete", taskComplete ? 1 : 0);
            
            getSqlSession().update("com.happydog.friday.dao.task.TaskDao.markTaskInstance", params);
           
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }         
    }

    /**
     * deleteTaskInstance
     */
    @Override
    public void deleteTaskInstance(String taskInstanceId)
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        try
        {
            FridayTaskInstance taskInstance = this.getUserTaskInstanceByTaskInstanceId(taskInstanceId);
            
            FridayTask task = this.getUserTaskByTaskId(taskInstance.getTaskId());
            
            List<FridayTaskInstance> taskInstances = getSqlSession().selectList("com.happydog.friday.dao.task.TaskDao.selectTaskInstancesForTask", task.getTaskId());

            if (taskInstances.size() <= 1)
            {
                // no or only 1 task instance for the task, and the task instance is being deleted
                // so delete the whole task
                this.deleteTask(task);
            }
            else
            {
                if (taskInstance.getSubtaskInstances() != null && taskInstance.getSubtaskInstances().size() > 0)
                {
                    for (FridayTaskInstance subtask : taskInstance.getSubtaskInstances())
                    {
                        deleteTaskInstance(subtask.getTaskInstanceId());
                    }
                }
                
                // delete subtasks
                getSqlSession().delete("com.happydog.friday.dao.task.TaskDao.deleteSubtaskInstancesForTaskInstance", taskInstance.getTaskInstanceId());
                // delete this task instance
                getSqlSession().delete("com.happydog.friday.dao.task.TaskDao.deleteTaskInstance", taskInstanceId);
            }
            
            transactionManager.commit(status);            
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }        
    }

    /**
     * updateTaskInstance
     * @param taskInstance
     */
    @Override
    public void updateTaskInstance(FridayTaskInstance taskInstance)
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            getSqlSession().update("com.happydog.friday.dao.task.TaskDao.updateTaskInstance", taskInstance);
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }
        
    }   
}
