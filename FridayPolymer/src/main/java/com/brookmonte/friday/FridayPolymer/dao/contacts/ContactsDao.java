/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.dao.contacts;

import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.brookmonte.friday.FridayPolymer.domain.contact.FridayUserContact;
import com.brookmonte.friday.FridayPolymer.domain.contact.FridayUserContactDetails;
import com.brookmonte.friday.FridayPolymer.domain.user.FridayUser;

/**
 * @author Pete
 *
 */
@Component
public class ContactsDao  extends SqlSessionDaoSupport implements IContactsDao
{
    @Autowired
    private DataSourceTransactionManager transactionManager;

    public SqlSessionFactory sqlSessionFactory;
    
    @Autowired
    public ContactsDao(SqlSessionFactory sqlSessionFactory)
    {
        this.sqlSessionFactory = sqlSessionFactory;
    }
    
    @PostConstruct
    public void initIt() throws Exception
    {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
    
    /**
     * getUserContactsByUserId
     * 
     * Get the contacts for the user
     * 
     * @param user
     * @return
     */
    @Override
    public List<FridayUserContact> getUserContactsByUserId(FridayUser user)
    {
        return getSqlSession().selectList("com.brookmonte.friday.dao.contacts.ContactsDao.getUserContactsByUserId", user.getUserId());
    }

    /**
     * getUserContactByUserContactId
     */
    @Override
    public FridayUserContact getUserContactByUserContactId(String userContactId)
    {
        return getSqlSession().selectOne("com.brookmonte.friday.dao.contacts.ContactsDao.getUserContactByUserContactId", userContactId);
    }
    
    /**
     * insertContact
     */
    @Override
    public void insertContact(FridayUserContact contact)
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            contact.setUserContactId(UUID.randomUUID().toString());
            getSqlSession().insert("com.brookmonte.friday.dao.contacts.ContactsDao.insertContact", contact);
            
            for (FridayUserContactDetails contactDetail : contact.getContactDetails())
            {
                contactDetail.setContactDetailsId(UUID.randomUUID().toString());
                contactDetail.setUserContactId(contact.getUserContactId());
                getSqlSession().insert("com.brookmonte.friday.dao.contacts.ContactsDao.insertContactDetail", contactDetail);
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
     * updateContact
     */
    @Override
    public void updateContact(FridayUserContact contact)
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            getSqlSession().update("com.brookmonte.friday.dao.contacts.ContactsDao.updateContact", contact);
            
            getSqlSession().delete("com.brookmonte.friday.dao.contacts.ContactsDao.deleteContactDetailsForUserContact", contact.getUserContactId());
            
            for (FridayUserContactDetails contactDetail : contact.getContactDetails())
            {
                contactDetail.setContactDetailsId(UUID.randomUUID().toString());
                contactDetail.setUserContactId(contact.getUserContactId());
                getSqlSession().insert("com.brookmonte.friday.dao.contacts.ContactsDao.insertContactDetail", contactDetail);
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
     * deleteContact
     */
    @Override
    public void deleteContact(FridayUserContact contact)
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try
        {
            getSqlSession().delete("com.brookmonte.friday.dao.contacts.ContactsDao.deleteContactDetailsForUserContact", contact.getUserContactId());

            getSqlSession().delete("com.brookmonte.friday.dao.contacts.ContactsDao.deleteContact", contact);
                        
            transactionManager.commit(status);
        }
        catch (Exception ex)
        {
            transactionManager.rollback(status);
            throw ex;
        }
    }
}
