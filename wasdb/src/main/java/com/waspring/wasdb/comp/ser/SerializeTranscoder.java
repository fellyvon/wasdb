package com.waspring.wasdb.comp.ser;

import java.io.Closeable;

import org.apache.log4j.Logger;

/**
 * 序列化接口定义
 * 
 * @author felly
 * @date Aug 26, 2015
 * 
 */
public abstract class SerializeTranscoder {
	protected static Logger log = Logger
			.getLogger(SerializeTranscoder.class);

	public abstract byte[] serialize(Object value);

	public abstract Object deserialize(byte[] in);

	/**
	 * 
	 * @param closeable
	 */
	public void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Unable to close " + closeable, e);
			}
		}
	}
}
