package com.gnayils.obiew.bmpldr;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Gnayils on 9/3/16.
 */
public class ContentLengthInputStream extends InputStream{

    private InputStream inputStream;
    private int length;

    public ContentLengthInputStream(InputStream inputStream, int length) {
        this.inputStream = inputStream;
        this.length = length;
        if(this.length <= 0) {
            throw new IllegalStateException("length cannot be less than 1");
        }
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public int available() throws IOException {
        return length;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public void mark(int readLimit) {
        inputStream.mark(readLimit);
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return inputStream.read(buffer);
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        return inputStream.read(buffer, byteOffset, byteCount);
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public long skip(long byteCount) throws IOException {
        return inputStream.skip(byteCount);
    }
}
