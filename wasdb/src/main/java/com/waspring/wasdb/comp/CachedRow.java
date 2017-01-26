package com.waspring.wasdb.comp;

import java.io.Serializable;
import java.sql.SQLException;

import com.waspring.wasdb.comp.xml.XmlResultSet;

/**
 * 结果集行元素
 * @author felly
 *
 */
public class CachedRow
    implements Serializable
{

    public CachedRow(XmlResultSet xmlresultset, Object aobj[])
    {
        columnData = aobj;
        resultSet = xmlresultset;
    }

    public Object[] getColumnData()
    {
        return columnData;
    }

    public Object getColumn(int i)
    {
        return columnData[i - 1];
    }

    public Object getColumn(String s)
        throws SQLException
    {
        return getColumn(resultSet.findColumn(s));
    }

    public void setColumnData(Object aobj[])
    {
        columnData = aobj;
    }

    private static final long serialVersionUID = 1L;
    private Object columnData[];
    private XmlResultSet resultSet;
}
