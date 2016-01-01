/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.manager.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.brookmonte.friday.FridayPolymer.dao.task.TaskDao;
import com.brookmonte.friday.FridayPolymer.domain.fridayUtils.FridayUtilities;
import com.brookmonte.friday.FridayPolymer.domain.task.FridayTask;
import com.brookmonte.friday.FridayPolymer.domain.task.FridayTaskInstance;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;
import com.brookmonte.friday.FridayPolymer.manager.admin.AdministrationManager;

/**
 * @author Pete
 *
 */
@Component
public class TaskManager
{
    @SuppressWarnings("unused")
    private static org.apache.log4j.Logger log = Logger.getLogger(TaskManager.class);
    
    @Autowired
    TaskDao taskDao;
    
    @Autowired
    AdministrationManager adminManager;

    
    /**
     * getTaskByTaskId
     * @param taskId
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    public FridayTask getTaskByTaskId(String taskId)
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        FridayUser u = adminManager.getUserByName(currentUser);
        
        FridayTask task = taskDao.getUserTaskByTaskId(taskId);
        
        if (!u.getUserId().equalsIgnoreCase(task.getUserId()))
        {
            throw new RuntimeException("Unauthorized");
        }
        
        return task;
    }

    /**
     * getTaskInstancesForTask
     * @param task
     * @return
     */
    public List<FridayTaskInstance> getTaskInstancesForTask(FridayTask task)
    {
        return taskDao.getTaskInstancesForTask(task);
    }
    
    
    /**
     * getUserTaskInstancesByUserId
     * @param user
     * @return
     * @throws Exception
     */
    @PreAuthorize("isAuthenticated()")
    public List<FridayTaskInstance> getUserTaskInstancesByUserId(FridayUser user)
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        FridayUser u = adminManager.getUserByName(currentUser);
        
        if (!u.getUserId().equalsIgnoreCase(user.getUserId()))
        {
            throw new RuntimeException("Unauthorized");
        }
        
        return taskDao.getUserTaskInstancesByUserId(user);
    }

    /**
     * getTaskInstanceByTaskInstanceId
     * @param taskId
     * @return
     * @throws Exception
     */
    public FridayTaskInstance getTaskInstanceByTaskInstanceId(String taskInstanceId)
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        FridayUser u = adminManager.getUserByName(currentUser);
        
        FridayTaskInstance taskInstance = taskDao.getUserTaskInstanceByTaskInstanceId(taskInstanceId);
        
        if (!taskInstance.getUserId().equalsIgnoreCase(u.getUserId()))
        {
            throw new RuntimeException("Unauthorized");
        }
        
        return taskInstance;
    }
    /**
     * markTask
     * @param taskId
     * @param complete
     */
    @PreAuthorize("isAuthenticated()")
    public void markTask(String taskInstanceId, boolean complete)
    {        
        taskDao.markTask(taskInstanceId, complete);
    }

    /**
     * addTask
     * @param requestDate
     * @param taskMap
     * @throws Exception 
     */
    public void addTask(String requestDate, Map<String, Object> taskMap, String allOrSelected) throws Exception
    {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        
        FridayUser user = adminManager.getUserByName(userName);
        
        String taskId = (String) taskMap.get("id");
        
        if (!StringUtils.isEmpty(taskId))
        {
            throw new Exception("Adding a task with an existing taskInstanceId.");
        }
        
        DateTime requestDateTime = FridayUtilities.convertRequestDateStringToDateTime(requestDate);
        
        // construct the task from the HashMap
        FridayTask task = new FridayTask();
        task.setTaskId(UUID.randomUUID().toString());
        task.setUserId(user.getUserId());
        task.setDescription((String) taskMap.get("description"));
        task.setNotes((String) taskMap.get("notes"));
        task.setParentTaskId((String) taskMap.get("parentTaskId"));
        
        task.setTaskInstanceDescription(this.buildTaskOccurrenceDescription(requestDateTime, taskMap));
        
        // there must be at least one task instance for a task to be inserted
        // create task instances for the task
        List<FridayTaskInstance> taskInstances = this.buildTaskInstances(requestDateTime, task, taskMap);
        
        if (taskInstances != null && taskInstances.size() > 0)
        {
          // add the task and task instances
          taskDao.insertTask(task, taskInstances, allOrSelected);
        }   
    }

    /**
     * updateTask
     * @param requestDate
     * @param taskMap
     * @param allOrSelected
     */
    public void updateTask(String requestDate, Map<String, Object> taskMap, String allOrSelected)
    {
        FridayTaskInstance taskInstance = taskDao.getUserTaskInstanceByTaskInstanceId((String) taskMap.get("id"));
        DateTime requestDateTime = FridayUtilities.convertRequestDateStringToDateTime(requestDate);

        if (taskInstance != null)
        {
            Boolean complete = (Boolean) taskMap.get("complete");
                    
            if (complete != null)
            {
                taskInstance.setComplete(complete);
            }
            
            taskInstance.setDescription((String) taskMap.get("description"));
            taskInstance.setNotes((String) taskMap.get("notes"));
            
            String dueDateString = (String) taskMap.get("dueDate");
            String dueTimeString = (String) taskMap.get("dueTime");
            
            DateTime dueDateTime = null;
            
            if (!StringUtils.isEmpty(dueDateString))
            {
                try
                {
                    dueDateTime = new DateTime(dueDateString);
                }
                catch (Exception e)
                {
                    if (!StringUtils.isEmpty(dueTimeString))
                    {
                        dueDateString += "T" + dueTimeString;

                        String dueDateTimeFormat = "MMM dd YYYYThh:mm a";
                        DateTimeFormatter dtfmt = DateTimeFormat.forPattern(dueDateTimeFormat);
                        dueDateTime = dtfmt.parseDateTime(dueDateString).withZoneRetainFields(requestDateTime.getZone())
                                .toDateTime(DateTimeZone.UTC);
                    }
                    else
                    {
                        String dueDateTimeFormat = "MMM dd YYYY";
                        DateTimeFormatter dtfmt = DateTimeFormat.forPattern(dueDateTimeFormat);
                        dueDateTime = dtfmt.parseDateTime(dueDateString).withZoneRetainFields(requestDateTime.getZone())
                                .toDateTime(DateTimeZone.UTC);
                    }
                }

                taskInstance.setDueDate(dueDateTime);
            }
            
            if (allOrSelected.equalsIgnoreCase("all"))
            {
                if (!StringUtils.isEmpty(taskInstance.getTaskId()))
                {
                    FridayTask task = taskDao.getUserTaskByTaskId(taskInstance.getTaskId());

                    if (task != null)
                    {
                        task.setDescription(taskInstance.getDescription());
                        task.setNotes(taskInstance.getNotes());
                        
                        // update task instances
                        List<FridayTaskInstance> instances = taskDao.getTaskInstancesForTask(task);
                        
                        for (FridayTaskInstance instance : instances)
                        {
                            instance.setDescription(taskInstance.getDescription());
                            instance.setNotes(taskInstance.getNotes());
                            
                            if (instance.getTaskInstanceId().equalsIgnoreCase(taskInstance.getTaskInstanceId()))
                            {
                                instance.setDueDate(taskInstance.getDueDate());
                                instance.setComplete(taskInstance.isComplete());
                            }
                        }
                        
                        taskDao.updateTask(task, instances);
                    }
                }
            }
            else
            {
                taskDao.updateTaskInstance(taskInstance);
            }
        }
    }
    
    /**
     * buildTaskOccurrenceDescription
     * @param requestDate
     * @param taskMap
     * @return
     */
    private String buildTaskOccurrenceDescription(DateTime requestDate, Map<String, Object> taskMap)
    {
        StringBuilder description = new StringBuilder();

        // FORMAT 0:
        // Rnnnn[YI:<1-n>; <day description>]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDTnnHnnMnnS
        // FORMAT 1A:
        // Rnnnn[MI:<1-n>;DD:<1-31>]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDTnnHnnMnnS        
        // FORMAT 1B:
        // Rnnnn[MI:<1-n>;<1-5>|LAST#<0-6>DW]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDTnnHnnMnnS
        // FORMAT 2:
        // Rnnnn[WI:<1-nnn>;DW:<0-6>]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDTnnHnnMnnS        
        // FORMAT 3:
        // Rnnnn[DI:2]/YYYY-MM-DDTHH:MM:SS/YYYY-MM-DDTHH:MM:SS/PnnYnnMnnDTnnHnnMnnS
        
        description.append("R");
        
        int repCount = 0;
        
        if (taskMap.get("repetitionCount") instanceof Integer)
        {
            repCount = (Integer) taskMap.get("repetitionCount");
        }
        else if (taskMap.get("repetitionCount") instanceof String)
        {
            repCount = Integer.parseInt((String) taskMap.get("repetitionCount"));
        }
        
        description.append(String.format("%04d", repCount));
        
        String repType = (String) taskMap.get("repetitionType");
        
        if (repType.equalsIgnoreCase("once"))
        {
            description.append("[]/"); 
        }        
        else if (repType.equalsIgnoreCase("daily"))
        {
            description.append("[DI:");            
            description.append(taskMap.get("dayInterval"));
            description.append("]/");
        }
        else if (repType.equalsIgnoreCase("weekly"))
        {
            description.append("[WI:");
            description.append(taskMap.get("weekInterval"));
            description.append(";");
            
            String weekDays = taskMap.get("weekDays").toString();
            weekDays = weekDays.replace("[", "");
            weekDays = weekDays.replace("]", "");
            description.append(weekDays);
            description.append("]/");
        }
        else if (repType.equalsIgnoreCase("monthly"))
        {
            description.append("[MI:");
            description.append(taskMap.get("monthInterval"));
            description.append(";");
            
            String monthDays = taskMap.get("monthDayDescriptions").toString();
            monthDays = monthDays.replace("[", "");
            monthDays = monthDays.replace("]", "");
            description.append(monthDays);
            description.append("]/");            
        }        
        else if (repType.equalsIgnoreCase("yearly"))
        {
            description.append("[YI:");
            description.append(taskMap.get("yearInterval"));
            description.append(";");
            
            String yearDays = taskMap.get("yearDayDescriptions").toString();
            yearDays = yearDays.replace("[", "");
            yearDays = yearDays.replace("]", "");
            description.append(yearDays);
            description.append("]/");            
        }
        else
        {
            throw new RuntimeException("Unknown interval type");
        }
        
        // get the full due date and time with UTC time zone
        String dueDateStr = (String) taskMap.get("dueDate");
        String dueTimeStr = (String) taskMap.get("dueTime");
        String endDateStr = "";

        Period taskPeriod = new Period(0);
        
        if (!StringUtils.isEmpty(dueDateStr))
        {
            String dueDateFormat = "MMM dd YYYY";
            DateTimeFormatter ddfmt = DateTimeFormat.forPattern(dueDateFormat);
            DateTime dueDateTime = ddfmt.parseDateTime(dueDateStr);          

            MutableDateTime mutableDueDateTime = dueDateTime.toMutableDateTime();

            if (!StringUtils.isEmpty(dueTimeStr))
            {
                String timeFormat = "hh:mm a";
                DateTimeFormatter fmt = DateTimeFormat.forPattern(timeFormat);
                DateTime dueTime = fmt.parseDateTime(dueTimeStr);

                mutableDueDateTime.setHourOfDay(dueTime.getHourOfDay());
                mutableDueDateTime.setMinuteOfHour(dueTime.getMinuteOfHour());

                mutableDueDateTime.setZoneRetainFields(requestDate.getZone());
                mutableDueDateTime.setZone(DateTimeZone.forID("UTC"));

                // convert to string
                dueDateStr = mutableDueDateTime.toString();

                // the event duration is set to 30 minutes;
                MutableDateTime mutableEndDateTime = dueDateTime.toMutableDateTime();
                mutableEndDateTime.setHourOfDay(dueDateTime.getHourOfDay());
                mutableEndDateTime.setMinuteOfHour(dueDateTime.plusMinutes(30).getMinuteOfHour());

                mutableEndDateTime.setZoneRetainFields(requestDate.getZone());
                mutableEndDateTime.setZone(DateTimeZone.forID("UTC"));

                taskPeriod = new Period(dueDateTime, mutableEndDateTime);
            }

            if (!StringUtils.isEmpty(endDateStr))
            {
                DateTime eventEndDateTime = new DateTime(endDateStr);

                MutableDateTime mutableEventEndDateTime = eventEndDateTime.toMutableDateTime();

                // using dueTime here is correct
                mutableEventEndDateTime.setHourOfDay(dueDateTime.getHourOfDay());
                mutableEventEndDateTime.setMinuteOfHour(dueDateTime.getMinuteOfHour());

                mutableEventEndDateTime.setZoneRetainFields(requestDate.getZone());
                mutableEventEndDateTime.setZone(DateTimeZone.forID("UTC"));

                // convert to string
                endDateStr = mutableEventEndDateTime.toString();
            }
        }
        
        description.append(dueDateStr);
        description.append("/");
        description.append(endDateStr);
        description.append("/");
        description.append(taskPeriod.toString());
        
        return description.toString();
    }
    

    /**
     * buildTaskInstances
     * @param requestDateTime
     * @param task
     * @param taskMap
     * @return
     * @throws Exception
     */
    private List<FridayTaskInstance> buildTaskInstances(DateTime requestDateTime, FridayTask task, Map<String, Object> taskMap) throws Exception
    {
        // see EventManager for a description of the repetition string and how it works
        String taskDateTimeDescription = task.getTaskInstanceDescription();

        String[] descriptionParts = taskDateTimeDescription.split("/");

        String repetitionDesc = descriptionParts[0];
        String dueDateDesc = descriptionParts[1];
        String endDateDesc = descriptionParts[2];
        //String taskLengthDesc = descriptionParts[3];

        String[] repetitionParts = repetitionDesc.split("\\[");
        String repCountDesc = repetitionParts[0].substring(1);
        Integer repCount = Integer.parseInt(repCountDesc);

        // get the repetition interval and day description
        String repPartDesc = repetitionParts[1].substring(0, repetitionParts[1].length() - 1);

        String[] repIntervalAndDaysParts = null;
        String repIntervalDesc = null;
        String repDaysDesc = null;

        String[] repIntervalParts = null;
        String repIntervalType = null;
        Integer repIntervalValue = null;

        String[] repIntervalDays = null;

        if (repPartDesc.length() > 0)
        {
            repIntervalAndDaysParts = repPartDesc.split(";");
            repIntervalDesc = repIntervalAndDaysParts[0];

            if (repIntervalAndDaysParts.length > 1)
            {
                repDaysDesc = repIntervalAndDaysParts[1];
                repIntervalDays = repDaysDesc.split(",");
            }

            repIntervalParts = repIntervalDesc.split(":");
            repIntervalType = repIntervalParts[0];
            repIntervalValue = Integer.parseInt(repIntervalParts[1]);
        }
        else
        {
            repIntervalType = "DI";
            repIntervalValue = 1;
        }

        List<DateTime> taskDates = null;
        
        if (!StringUtils.isEmpty(dueDateDesc))
        {
            // get the due date - due date and end date are assumed to be in UTC time zone
            DateTime taskDueDate = FridayUtilities.parseDateDescription(dueDateDesc).withZone(DateTimeZone.UTC);

            DateTime taskEndDate = null;

            if (!StringUtils.isEmpty(endDateDesc))
            {
                taskEndDate = FridayUtilities.parseDateDescription(endDateDesc).withZone(DateTimeZone.UTC);
            }

            if (repCount > 1)
            {
                // TODO: is anything supposed to happen here?
            }

            if ((repCount == 0 && taskDueDate.equals(taskEndDate))
            || (((String) taskMap.get("repetitionType")).equalsIgnoreCase("once")))
            {
                repCount = 1;
            }

            if (repCount == 0)
            {
                if (repIntervalType.equalsIgnoreCase("DI"))
                {
                    repCount = 36500; // 100 years of days
                }
                else if (repIntervalType.equalsIgnoreCase("WI"))
                {
                    repCount = 5200; // 100 years of weeks
                }
                else if (repIntervalType.equalsIgnoreCase("MI"))
                {
                    repCount = 1200; // 100 years of months
                }
                else if (repIntervalType.equalsIgnoreCase("YI"))
                {
                    repCount = 100; // 100 years
                }
            }

            taskDates = this.generateTaskDatesFromDescription(taskDueDate, taskEndDate, repCount, repIntervalType,
                    repIntervalValue, repIntervalDays);

        }
        
        List<FridayTaskInstance> instances = new ArrayList<FridayTaskInstance>();

        if (taskDates != null)
        {
            for (DateTime taskDateTime : taskDates)
            {
                FridayTaskInstance instance = new FridayTaskInstance();
                instance.setTaskInstanceId(UUID.randomUUID().toString());
                instance.setTaskId(task.getTaskId());
                instance.setParentTaskInstanceId((String) taskMap.get("parentTaskInstanceId"));
                instance.setDescription(task.getDescription());
                instance.setNotes(task.getNotes());
                instance.setUserId(task.getUserId());
                instance.setDueDate(taskDateTime);

                if (taskMap.get("isComplete") instanceof java.lang.String)
                {
                    instance.setComplete(Boolean.getBoolean((String) taskMap.get("isComplete")));
                }
                else if (taskMap.get("isComplete") instanceof java.lang.Boolean)
                {
                    instance.setComplete((Boolean)taskMap.get("isComplete"));
                }
                else
                {
                    instance.setComplete(false);
                }

                instances.add(instance);
            }
        }
        else
        {
            FridayTaskInstance instance = new FridayTaskInstance();
            instance.setTaskInstanceId(UUID.randomUUID().toString());
            instance.setTaskId(task.getTaskId());
            instance.setParentTaskInstanceId((String) taskMap.get("parentTaskInstanceId"));
            instance.setDescription(task.getDescription());
            instance.setNotes(task.getNotes());
            instance.setUserId(task.getUserId());
            
            if (taskMap.get("isComplete") instanceof java.lang.String)
            {
                instance.setComplete(Boolean.getBoolean((String) taskMap.get("isComplete")));
            }
            else if (taskMap.get("isComplete") instanceof java.lang.Boolean)
            {
                instance.setComplete((Boolean)taskMap.get("isComplete"));
            }
            else
            {
                instance.setComplete(false);
            }
            
            instances.add(instance);            
        }
        
        return instances;
    }

    /**
     * generateTaskDatesFromDescription
     * @param taskDueDate
     * @param taskEndDate
     * @param repCount
     * @param repIntervalType
     * @param repIntervalValue
     * @param repIntervalDays
     * @return
     */
    private List<DateTime> generateTaskDatesFromDescription(DateTime taskDueDate, DateTime taskEndDate, Integer repCount,
            String repIntervalType, Integer repIntervalValue, String[] repIntervalDays)
    {
        List<DateTime> taskDates = new ArrayList<DateTime>();

        if (repIntervalType.equalsIgnoreCase("DI"))
        {
            taskDates = FridayUtilities.generateDayIntervalDatesFromDescription(taskDueDate, taskEndDate,
                    repCount, repIntervalValue, repIntervalDays);
        }
        else if (repIntervalType.equalsIgnoreCase("WI"))
        {
            taskDates = FridayUtilities.generateWeekIntervalDatesFromDescription(taskDueDate, taskEndDate,
                    repCount, repIntervalValue, repIntervalDays);
        }
        else if (repIntervalType.equalsIgnoreCase("MI"))
        {
            taskDates = FridayUtilities.generateMonthIntervalDatesFromDescription(taskDueDate, taskEndDate,
                    repCount, repIntervalValue, repIntervalDays);            
        }
        else if (repIntervalType.equalsIgnoreCase("YI"))
        {
            taskDates = FridayUtilities.generateYearIntervalDatesFromDescription(taskDueDate, taskEndDate,
                    repCount, repIntervalValue, repIntervalDays);            
        }
        else
        {
            throw new RuntimeException("Unknown event interval type");
        }

        
        return taskDates;
    }

    /**
     * deleteTaskInstance
     * @param taskInstanceId
     */
    public void deleteTaskInstance(String taskInstanceId)
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        FridayUser u = adminManager.getUserByName(currentUser);
        
        FridayTaskInstance taskInstance = taskDao.getUserTaskInstanceByTaskInstanceId(taskInstanceId);
        
        if (!taskInstance.getUserId().equalsIgnoreCase(u.getUserId()))
        {
            throw new RuntimeException("Unauthorized");
        }
        
        taskDao.deleteTaskInstance(taskInstanceId);
    }
}
