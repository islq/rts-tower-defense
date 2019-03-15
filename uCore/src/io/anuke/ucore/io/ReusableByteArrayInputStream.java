package io.anuke.ucore.io;

import java.io.ByteArrayInputStream;

/** A {@link java.io.ByteArrayInputStream} that can have its content bytes reset. */
public class ReusableByteArrayInputStream extends ByteArrayInputStream{

    /** {@link #setBytes} must be called before this stream can be used. */
    public ReusableByteArrayInputStream(){
        super(new byte[0]);
    }

    public int position(){
        return pos;
    }

    public void setBytes(byte[] bytes){
        pos = 0;
        count = bytes.length;
        mark = 0;
        buf = bytes;
    }

    public void setBytes(byte[] bytes, int offset, int length){
        this.buf = bytes;
        this.pos = offset;
        this.count = Math.min(offset + length, bytes.length);
        this.mark = offset;
    }
}
