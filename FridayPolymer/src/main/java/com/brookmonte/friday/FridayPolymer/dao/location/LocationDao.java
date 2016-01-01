/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.dao.location;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import com.brookmonte.friday.FridayPolymer.domain.location.FridayCountry;

/**
 * @author Pete
 *
 */
@Component
public class LocationDao extends SqlSessionDaoSupport implements ILocationDao
{
    @Autowired
    private DataSourceTransactionManager transactionManager;

    public SqlSessionFactory sqlSessionFactory;
    
    @Autowired
    public LocationDao(SqlSessionFactory sqlSessionFactory)
    {
        this.sqlSessionFactory = sqlSessionFactory;
    }
    
    @PostConstruct
    public void initIt() throws Exception
    {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
    
    /**
     * getCountryByCountryCode2
     * @param countryCode2
     * @return
     */
    @Override
    public FridayCountry getCountryByCountryCode2(String countryCode2)
    {
      return (FridayCountry)getSqlSession().selectOne("com.brookmonte.friday.dao.location.LocationDao.getCountryByCountryCode2", countryCode2);
    }
}
