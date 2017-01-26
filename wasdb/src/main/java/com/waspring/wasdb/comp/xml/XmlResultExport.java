package com.waspring.wasdb.comp.xml;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

 

/**
 * xml结果导出抽象
 * @author felly
 *
 */
public abstract class XmlResultExport
{

    public XmlResultExport()
    {
    }

    public abstract int writeResultset(ResultSet resultset, int i)
        throws IOException, SQLException;

    public XmlWriter getWriter()
    {
        return writer;
    }

    public void setWriter(XmlWriter xmlwriter)
    {
        writer = xmlwriter;
        dataEncoding = xmlwriter.getEncoding();
    }

    public void close()
        throws IOException
    {
        if(writer != null)
            writer.close();
    }

    protected XmlWriter writer;
    protected String dataEncoding;
}

