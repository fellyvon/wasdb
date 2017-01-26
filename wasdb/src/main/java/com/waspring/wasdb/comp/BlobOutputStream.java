package com.waspring.wasdb.comp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 定义Blob输出流
 * @author felly
 *
 */
public class BlobOutputStream extends ByteArrayOutputStream
{

    public BlobOutputStream(BlobField blobfield)
    {
        field = blobfield;
    }

    public void close()
        throws IOException
    {
        field.setBytes(toByteArray());
    }

    private BlobField field;
}
