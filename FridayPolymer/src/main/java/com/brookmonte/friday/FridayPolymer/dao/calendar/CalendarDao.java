/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.dao.calendar;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEvent;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEventDescription;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEventInstance;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayEventTypeEntry;
import com.brookmonte.friday.FridayPolymer.domain.calendar.FridayHoliday;
import com.mysql.jdbc.Statement;

/**
 * @author Pete
 *
 */
@Component
public class CalendarDao  extends SqlSessionDaoSupport implements ICalendarDao
{
    @Autowired
    private DataSourceTransactionManager transactionManager;

    public SqlSessionFactory sqlSessionFactory;
    
    @Autowired
    public CalendarDao(SqlSessionFactory sqlSessionFactory)
    {
        this.sqlSessionFactory = sqlSessionFactory;
    }
    
    @PostConstruct
    public void initIt() throws Exception
    {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
    
    DateTimeFormatter mysqlFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * getHolidaysForCountry
     */
    @Override
    public List<FridayHoliday> getHolidaysForCountry(String countryCode3)
    {        
        return getSqlSession()
                .selectList("com.brookmonte.friday.dao.calendar.CalendarDao.getHolidaysForCountry", countryCode3);
    }

    /**
     * getHolidaysForReligion
     */
    @Override
    public List<FridayHoliday> getHolidaysForReligion(String religionName)
    {
        return getSqlSession()
                .selectList("com.brookmonte.friday.dao.calendar.CalendarDao.getHolidaysForReligion", religionName);
    }

//    /**
//     * getEventDescriptionsForUser
//     */
//    @Override
//    public List<FridayEventDescription> getEventDescriptionsForUser(String userId)
//    {
//        return getSqlSession()
//                .selectList("com.happydog.friday.dao.calendar.CalendarDao.getEventDescriptionsForUser", userId);
//    }

    /**
     * addEventDescription
     * @throws Exception 
     */
    @Override
    public void addEventDescription(FridayEventDescription event) throws Exception
    {        
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {            
            getSqlSession().insert("com.brookmonte.friday.dao.calendar.CalendarDao.insertEventDescription", event);
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }
    }

    /**
     * getEventInstanceByEvent
     * @throws Exception
     */
    @Override
    public FridayEventInstance getEventInstanceForEvent(FridayEvent event) throws Exception
    {
        HashMap<String, Object> map = new HashMap<String, Object>(); 

        map.put("userEventId", event.getEventId());
        map.put("userId", event.getEventUserId());
        map.put("descriptionStartTime", event.getStartDateTime());
        
        return getSqlSession()
                .selectOne("com.brookmonte.friday.dao.calendar.CalendarDao.getEventInstanceForEvent", map);
    }

    /**
     * getEventForEventInstance
     * @throws Exception
     */
    @Override
    public FridayEventDescription getEventForEventInstance(FridayEventInstance eventInstance) throws Exception
    {
        HashMap<String, Object> map = new HashMap<String, Object>(); 

        map.put("userEventId", eventInstance.getUserEventId());
        map.put("userId", eventInstance.getUserId());
        
        return getSqlSession()
                .selectOne("com.brookmonte.friday.dao.calendar.CalendarDao.getEventForEventInstance", map);
    }

    
    /**
     * getEventInstanceByInstanceId
     * @throws Exception
     */
    @Override
    public FridayEventInstance getEventInstanceByInstanceId(String userId, String eventInstanceId) throws Exception
    {
        HashMap<String, Object> map = new HashMap<String, Object>(); 

        map.put("eventInstanceId", eventInstanceId);
        map.put("userId", userId);
        
        FridayEventInstance eventInstance = getSqlSession()
                .selectOne("com.brookmonte.friday.dao.calendar.CalendarDao.getEventInstanceByInstanceId", map);
        
        return eventInstance;
    }


    /**
     * getEventInstancesForUserBetweenDates
     * @param endDate 
     */
    @Override
    public List<FridayEventInstance> getEventInstancesForUserBetweenDates(String userId,  DateTime requestDateTime, DateTime instanceStartTime, DateTime instanceEndTime) throws Exception
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        
        map.put("userId", userId);
        map.put("instanceStartTime", instanceStartTime.getMillis());
        map.put("instanceEndTime", instanceEndTime.getMillis());
        
        List<FridayEventInstance> eventInstances = getSqlSession()
                .selectList("com.brookmonte.friday.dao.calendar.CalendarDao.getEventInstancesForUserBetweenDates", map);
        
        return eventInstances;
    }
    
    /**
     * insertEventInstance
     * @throws Exception 
     */
    @Override
    public void insertEventInstance(FridayEventInstance eventInstance) throws Exception
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {            
            getSqlSession().insert("com.brookmonte.friday.dao.calendar.CalendarDao.insertEventInstance", eventInstance);
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }
    }

    /**
     * updateEventInstance
     * @throws Exception
     */
    @Override
    public void updateEventInstance(FridayEventInstance eventInstance) throws Exception
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {            
            getSqlSession().update("com.brookmonte.friday.dao.calendar.CalendarDao.updateEventInstance", eventInstance);
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }        
    }

    /**
     * bulkInsertEventInstances
     * @param instances
     * @throws Exception
     */
    public void bulkInsertEventInstances(List<FridayEventInstance> instances) throws Exception
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            HashMap<String, String> eventTypes = this.getFridayEventTypes();
            
            // First create a statement from the connection and turn off unique checks and key creation
            Statement statement = (Statement) transactionManager.getDataSource().getConnection().createStatement();
            statement.execute("SET UNIQUE_CHECKS=0; ");
            statement.execute("ALTER TABLE friday.FRIDAY_EVENT_INSTANCE DISABLE KEYS");

//            FRIDAY_EVENT_INSTANCE.EVENT_INSTANCE_ID,
//            FRIDAY_EVENT_INSTANCE.USER_EVENT_ID,
//            FRIDAY_EVENT_INSTANCE.USER_ID,
//            FRIDAY_EVENT_INSTANCE.EVENT_LOCATION_ID,
//            FRIDAY_EVENT_INSTANCE.EVENT_TYPE_ID,
//            FRIDAY_EVENT_INSTANCE.EVENT_TITLE,
//            FRIDAY_EVENT_INSTANCE.EVENT_DESCRIPTION,
//            FRIDAY_EVENT_INSTANCE.EVENT_LOCATION,
//            FRIDAY_EVENT_INSTANCE.EVENT_START_TIME,
//            FRIDAY_EVENT_INSTANCE.EVENT_END_TIME,
//            FRIDAY_EVENT_INSTANCE.DESCRIPTION_START_TIME,
//            FRIDAY_EVENT_INSTANCE.IS_DELETED            
            
            
            // Define the query we are going to execute
            String statementText = "LOAD DATA LOCAL INFILE 'file.txt' "
                    + "INTO TABLE friday.FRIDAY_EVENT_INSTANCE "
                    + "FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\n' "
                    + "(EVENT_INSTANCE_ID, USER_EVENT_ID, USER_ID, EVENT_LOCATION_ID, EVENT_TYPE_ID, EVENT_TITLE, EVENT_DESCRIPTION, EVENT_LOCATION, EVENT_START_TIME, EVENT_END_TIME, DESCRIPTION_START_TIME, IS_DELETED) ";
 
            // Create StringBuilder to build String that will become stream
            StringBuilder builder = new StringBuilder();
 
            // Iterate over map and create tab-text string
            for (FridayEventInstance instance: instances)
            {
                builder.append("\"");
                builder.append(instance.getEventInstanceId());
                builder.append("\"");
                builder.append(",");
                
                builder.append("\"");
                builder.append(instance.getUserEventId());
                builder.append("\"");
                builder.append(",");
                
                builder.append("\"");
                builder.append(instance.getUserId());
                builder.append("\"");
                builder.append(",");               
                
                if (!StringUtils.isEmpty(instance.getEventLocationId()))
                {
                    builder.append("\"");
                    builder.append(instance.getEventLocationId());
                    builder.append("\"");
                    builder.append(",");
                }
                else
                {
                    builder.append("null");
                    builder.append(",");                    
                }
                
                builder.append("\"");
                builder.append(eventTypes.get(instance.getEventType()));
                builder.append("\"");
                builder.append(",");
                
                builder.append("\"");
                builder.append(instance.getEventTitle());
                builder.append("\"");
                builder.append(","); 
                
                builder.append("\"");
                builder.append(instance.getEventDescription());
                builder.append("\"");
                builder.append(",");
                
                builder.append("\"");
                builder.append(instance.getEventLocation());
                builder.append("\"");
                builder.append(",");
                
                builder.append("\"");
                builder.append(instance.getEventStartTime().getMillis());
                builder.append("\"");
                builder.append(",");
                
                builder.append("\"");
                builder.append(instance.getEventEndTime().getMillis());
                builder.append("\"");
                builder.append(",");
                
                builder.append("\"");
                builder.append(instance.getDescriptionStartTime().getMillis());
                builder.append("\"");
                builder.append(",");
                
                builder.append("\"");
                builder.append(instance.getDeleted());
                builder.append("\"");
                builder.append("\n");                
            }                     
 
            // Create stream from String Builder
            InputStream is = IOUtils.toInputStream(builder.toString());
 
            // Setup our input stream as the source for the local infile
            statement.setLocalInfileInputStream(is);
 
            // Execute the load infile
            statement.execute(statementText);
 
            // Turn the checks back on
            statement.execute("ALTER TABLE friday.FRIDAY_EVENT_INSTANCE ENABLE KEYS");
            statement.execute("SET UNIQUE_CHECKS=1; ");
            
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }        
    }

    /**
     * deleteEvent
     * @throws Exception
     */
    @Override
    public void deleteEvent(String userId, String eventId) throws Exception
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            HashMap<String, Object> map = new HashMap<String, Object>(); 

            map.put("userEventId", eventId);
            map.put("userId", userId);
            
            getSqlSession().delete("com.brookmonte.friday.dao.calendar.CalendarDao.deleteEventInstanceByUserIdEventId", map);
            
            getSqlSession().delete("com.brookmonte.friday.dao.calendar.CalendarDao.deleteEventByUserIdEventId", map);
            
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }        
    }

    /**
     * getFridayEventTypes
     * @return
     */
    @Override
    public HashMap<String, String> getFridayEventTypes()
    {
        List<FridayEventTypeEntry> eventTypesList = getSqlSession()
                .selectList("com.brookmonte.friday.dao.calendar.CalendarDao.getEventInstanceTypes");
        
        HashMap<String, String> eventTypes = new HashMap<String, String>();
        
        for (FridayEventTypeEntry typeEntry: eventTypesList)
        {
            eventTypes.put(typeEntry.getEventTypeName(), typeEntry.getEventTypeId());
        }
        
        return eventTypes;
    }
}
