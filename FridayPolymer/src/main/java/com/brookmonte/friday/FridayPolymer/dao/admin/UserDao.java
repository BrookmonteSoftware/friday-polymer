/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.dao.admin;

import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.brookmonte.friday.FridayPolymer.domain.admin.UserPreference;
import com.brookmonte.friday.FridayPolymer.domain.location.FridayLocation;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayEmailDefinition;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayPhoneDefinition;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;

/**
 * @author Pete
 *
 */
@Component
public class UserDao extends SqlSessionDaoSupport implements IUserDao
{
    @Autowired
    private DataSourceTransactionManager transactionManager;

    public SqlSessionFactory sqlSessionFactory;
    
    @Autowired
    public UserDao(SqlSessionFactory sqlSessionFactory)
    {
        this.sqlSessionFactory = sqlSessionFactory;
    }
    
    @PostConstruct
    public void initIt() throws Exception
    {
        super.setSqlSessionFactory(sqlSessionFactory);
    }    
    
    /**
     * getUserByName
     * 
     * @param userName
     */
    @Override
    public FridayUser getUserByName(String userName)
    {
      return (FridayUser)getSqlSession().selectOne("com.brookmonte.friday.maps.UserDao.getUserByName", userName);
    }
    
    /**
     * addNewUser
     * 
     * @param newUser
     * @throws Exception
     */
    @Override
    public void addNewUser(FridayUser newUser) throws Exception
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        try
        {
            // create a UUID as the userId
            newUser.setUserId(UUID.randomUUID().toString());
         
            // encode the password with the username salt
            final ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);
            newUser.setPassword(encoder.encodePassword(newUser.getPassword(), newUser.getUserName()));            
            
            getSqlSession().insert("com.brookmonte.friday.maps.UserDao.insertNewUser", newUser);
            
            // insert home locations, work locations, and favorite locations
            for (FridayLocation homeLocation : newUser.getHomeLocations())
            {
                homeLocation.setLocationId(UUID.randomUUID().toString());
                homeLocation.setUserId(newUser.getUserId());
                getSqlSession().insert("com.brookmonte.friday.maps.UserDao.insertLocation", homeLocation);
            }

            for (FridayLocation workLocation : newUser.getWorkLocations())
            {
                workLocation.setLocationId(UUID.randomUUID().toString());
                workLocation.setUserId(newUser.getUserId());
                getSqlSession().insert("com.brookmonte.friday.maps.UserDao.insertLocation", workLocation);
            }

            for (FridayLocation favoriteLocation : newUser.getFavoriteLocations())
            {
                favoriteLocation.setLocationId(UUID.randomUUID().toString());
                favoriteLocation.setUserId(newUser.getUserId());
                getSqlSession().insert("com.brookmonte.friday.maps.UserDao.insertLocation", favoriteLocation);
            }
            
            // insert work definitions
            
            // insert email addresses
            for (FridayEmailDefinition email : newUser.getEmailAddresses())
            {
                email.setUserEmailId(UUID.randomUUID().toString());
                email.setUserId(newUser.getUserId());
                
                if (email.getEmailAddress().equalsIgnoreCase(newUser.getUserName()))
                {
                    email.setPrimaryEmail(true);
                }
                
                getSqlSession().insert("com.brookmonte.friday.maps.UserDao.insertEmailDefinition", email);
            }
            
            // insert phone numbers
            for (FridayPhoneDefinition phoneNumber : newUser.getPhoneNumbers())
            {
                phoneNumber.setPhoneDefinitionId(UUID.randomUUID().toString());
                phoneNumber.setPhoneUserId(newUser.getUserId());
                
                getSqlSession().insert("com.brookmonte.friday.maps.UserDao.insertPhoneDefinition", phoneNumber);                
            }
            
            // insert interests
                        
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }
    }

    /**
     * updateUser
     * 
     * @param user
     * @throws Exception
     */
    @Override
    public void updateUser(FridayUser user) throws Exception
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            getSqlSession().update("com.brookmonte.friday.maps.UserDao.updateUser", user);
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }
        
    }

    /**
     * deleteUser
     * 
     * @param userId
     * @throws Exception
     */
    @Override
    public void deleteUser(String userId) throws Exception
    {
        getSqlSession().delete("com.brookmonte.friday.maps.UserDao.deleteUser", userId);
    }

    /**
     * getUserPreferences
     * @param user
     * @return
     */
    @Override
    public List<UserPreference> getUserPreferences(FridayUser user)
    {
        return getSqlSession().selectList("com.brookmonte.friday.maps.UserDao.getUserPreferences", user.getUserId());
    }
    
    /**
     * insertUserPreferences
     * @param preferences
     */
    @Override
    public void insertUserPreferences(FridayUser user, List<UserPreference> preferences) throws Exception
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            // assumes that the list of preferences being inserted is the complete list
            // for the user; if it isn't some preferences will be delete and therefore,
            // revert back to the default value
            this.deletePreferencesForUser(user);
            
            for (UserPreference preference : preferences)
            {
                getSqlSession().insert("com.brookmonte.friday.maps.UserDao.insertUserPreference", preference);
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
     * deletePreferencesForUser
     * @param user
     */
    @Override
    public void deletePreferencesForUser(FridayUser user)
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {            
            getSqlSession().delete("com.brookmonte.friday.maps.UserDao.deletePreferencesForUser", user.getUserId());
            
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }        
    }   
}
