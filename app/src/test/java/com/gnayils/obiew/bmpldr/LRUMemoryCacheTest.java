package com.gnayils.obiew.bmpldr;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import static org.junit.Assert.*;

/**
 * Created by Gnayils on 8/28/16.
 */
public class LRUMemoryCacheTest {

    @Test
    public void testFileLength() throws IOException {
        File file = new File("/Users/Gnayils/Temp/file");
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = new byte[1234];
        fos.write(bytes);
        fos.flush();
        fos.close();
        assertTrue(file.length() == 1234);
    }

    @Test
    public void testDeleteFile() throws IOException {
        File dir = new File("/Users/Gnayils/Temp");
        File file = new File(dir, "file");
        if(!dir.exists()) {
            assertTrue(dir.mkdirs());
        }
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = new byte[1024];
        fos.write(bytes);
        fos.flush();
        fos.close();
        assertTrue(file.exists());
        file.delete();
        assertTrue(!file.exists());
    }

    @Test
    public void testLinkedHashMapRemove() {
        LinkedHashMap<String, Long> map = new LinkedHashMap<String, Long>();
        Long longValue = map.get("key");
        System.out.println(longValue);
    }
}
