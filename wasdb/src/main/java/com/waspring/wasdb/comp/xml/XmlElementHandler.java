package com.waspring.wasdb.comp.xml;

import org.xml.sax.AttributeList;

/**
 * XML节点处理对象接口声明
 * @author felly
 *
 */
public interface XmlElementHandler
{

    public abstract void startElement(AttributeList attributelist)
        throws Exception;
}