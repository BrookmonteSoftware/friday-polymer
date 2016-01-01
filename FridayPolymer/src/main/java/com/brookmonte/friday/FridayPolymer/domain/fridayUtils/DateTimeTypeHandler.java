/**
 * 
 */
package com.brookmonte.friday.FridayPolymer.domain.fridayUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * @author Pete
 *
 */
@MappedTypes(value = DateTime.class)
public class DateTimeTypeHandler implements TypeHandler<DateTime>
{

    @Override
    public DateTime getResult(ResultSet rs, String columnName) throws SQLException
    {
        Timestamp ts = rs.getTimestamp(columnName);
        if (ts != null)
        {
            return new DateTime(ts.getTime(), DateTimeZone.UTC);
        }
        else
        {
            return null;
        }

    }

    @Override
    public DateTime getResult(ResultSet rs, int columnIndex) throws SQLException
    {
        Timestamp ts = rs.getTimestamp(columnIndex);
        if (ts != null)
        {
            return new DateTime(ts.getTime(), DateTimeZone.UTC);
        }
        else
        {
            return null;
        }
    }

    @Override
    public DateTime getResult(CallableStatement cs, int columnIndex) throws SQLException
    {
        Timestamp ts = cs.getTimestamp(columnIndex);
        if (ts != null)
        {
            return new DateTime(ts.getTime(), DateTimeZone.UTC);
        }
        else
        {
            return null;
        }

    }

    @Override
    public void setParameter(PreparedStatement ps, int i, DateTime parameter, JdbcType jdbcType) throws SQLException
    {
        if (parameter != null)
        {
            ps.setTimestamp(i, new Timestamp(((DateTime) parameter).getMillis()));
        }
        else
        {
            ps.setTimestamp(i, null);
        }        
    }

}
