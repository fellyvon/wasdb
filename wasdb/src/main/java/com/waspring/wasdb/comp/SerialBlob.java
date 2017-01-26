package com.waspring.wasdb.comp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * 可序列化的Blob扩展实现
 * @author felly
 *
 */
public class SerialBlob
    implements Blob, Serializable
{

    public SerialBlob(Blob blob)
        throws SQLException
    {
        buffer = null;
        length = 0L;
        length = blob.length();
        buffer = new byte[(int)length];
        BufferedInputStream bufferedinputstream = new BufferedInputStream(blob.getBinaryStream());
        try
        {
            int i = 0;
            int j = 0;
            do
            {
                i = bufferedinputstream.read(buffer, j, (int)(length - (long)j));
                j += i;
            } while(i > 0 && (long)j < length);
        }
        catch(IOException ioexception)
        {
            throw new SQLException((new StringBuilder()).append("SerialBlob: ").append(ioexception.getMessage()).toString());
        }
    }

    public InputStream getBinaryStream()
        throws SQLException
    {
        return new ByteArrayInputStream(buffer);
    }

    public byte[] getBytes(long l, int i)
        throws SQLException
    {
        if(l < 0L || (long)i > length || l + (long)i > length)
        {
            throw new SQLException("Invalid Arguments");
        } else
        {
            byte abyte0[] = new byte[i];
            System.arraycopy(buffer, (int)l, abyte0, 0, i);
            return abyte0;
        }
    }

    public byte[] getBytes()
    {
        return buffer;
    }

    public long length()
        throws SQLException
    {
        return length;
    }

    public long position(Blob blob, long l)
        throws SQLException
    {
        return position(blob.getBytes(0L, (int)blob.length()), l);
    }
    public long position(byte abyte0[], long l)
    {
    	return 0;
    }
 
    public OutputStream setBinaryStream(long l)
        throws SQLException
    {
        throwUnsupportedFeatureSqlException();
        return null;
    }

    public int setBytes(long l, byte abyte0[])
        throws SQLException
    {
        throwUnsupportedFeatureSqlException();
        return -1;
    }

    public int setBytes(long l, byte abyte0[], int i, int j)
        throws SQLException
    {
        throwUnsupportedFeatureSqlException();
        return -1;
    }

    public void truncate(long l)
        throws SQLException
    {
        throwUnsupportedFeatureSqlException();
    }

    public static void throwUnsupportedFeatureSqlException()
        throws SQLException
    {
        throw new SQLException("目前还不支持该项操作！");
    }

    public void free()
        throws SQLException
    {
    }

    public InputStream getBinaryStream(long l, long l1)
        throws SQLException
    {
        return new ByteArrayInputStream(buffer, (int)l, (int)l1);
    }

    private static final long serialVersionUID = 1L;
    private byte buffer[];
    private long length;
}