/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.testtools.FileBasedTestCase;

/**
 * Tests for {@link Tailer}.
 *
 * @version $Id: TailerTest.java 1127854 2011-05-26 10:03:42Z sebb $
 */
public class TailerTest extends FileBasedTestCase {

    private Tailer tailer;
    
    public TailerTest(String name) {
        super(name);
    }

    @Override
    protected void tearDown() throws Exception {
        if (tailer != null) {
            tailer.stop();
            Thread.sleep(1000);
        }
        FileUtils.deleteDirectory(getTestDirectory());
    }
    
    public void testTailerEof() throws Exception {
        // Create & start the Tailer
        long delay = 50;
        final File file = new File(getTestDirectory(), "tailer2-test.txt");
        createFile(file, 0);
        final TestTailerListener listener = new TestTailerListener();
        final Tailer tailer = new Tailer(file, listener, delay, false);
        final Thread thread = new Thread(tailer);
        thread.start();

        // Write some lines to the file
        FileWriter writer = null;
        try {
        	writeString(file, "Line");
            
            Thread.sleep(delay * 2);
            List<String> lines = listener.getLines();
            assertEquals("1 line count", 0, lines.size());

            writeString(file, " one\n");
            Thread.sleep(delay * 2);
            lines = listener.getLines();

            assertEquals("1 line count", 1, lines.size());
            assertEquals("1 line 1", "Line one", lines.get(0));

            listener.clear();
        } finally {
            tailer.stop();
            Thread.sleep(delay * 2);
            IOUtils.closeQuietly(writer);
        }
    }

    public void testTailer() throws Exception {

        // Create & start the Tailer
        long delay = 50;
        final File file = new File(getTestDirectory(), "tailer1-test.txt");
        createFile(file, 0);
        final TestTailerListener listener = new TestTailerListener();
        tailer = new Tailer(file, listener, delay, false);
        final Thread thread = new Thread(tailer);
        thread.start();

        // Write some lines to the file
        write(file, "Line one", "Line two");
        Thread.sleep(delay * 2);
        List<String> lines = listener.getLines();
        assertEquals("1 line count", 2, lines.size());
        assertEquals("1 line 1", "Line one", lines.get(0));
        assertEquals("1 line 2", "Line two", lines.get(1));
        listener.clear();

        // Write another line to the file
        write(file, "Line three");
        Thread.sleep(delay * 2);
        lines = listener.getLines();
        assertEquals("2 line count", 1, lines.size());
        assertEquals("2 line 3", "Line three", lines.get(0));
        listener.clear();

        // Check file does actually have all the lines
        lines = FileUtils.readLines(file);
        assertEquals("3 line count", 3, lines.size());
        assertEquals("3 line 1", "Line one", lines.get(0));
        assertEquals("3 line 2", "Line two", lines.get(1));
        assertEquals("3 line 3", "Line three", lines.get(2));

        // Delete & re-create
        file.delete();
        boolean exists = file.exists();
        String osname = System.getProperty("os.name");
        boolean isWindows = osname.startsWith("Windows");
        assertFalse("File should not exist (except on Windows)", exists && !isWindows);
        createFile(file, 0);
        Thread.sleep(delay * 2);

        // Write another line
        write(file, "Line four");
        Thread.sleep(delay * 2);
        lines = listener.getLines();
        assertEquals("4 line count", 1, lines.size());
        assertEquals("4 line 3", "Line four", lines.get(0));
        listener.clear();

        // Stop
        tailer.stop();
        tailer=null;
        thread.interrupt();
        Thread.sleep(delay * 2);
        write(file, "Line five");
        assertEquals("4 line count", 0, listener.getLines().size());
        assertNull("Should not generate Exception", listener.exception);
        assertEquals("Expected init to be called", 1 , listener.initialised);
        assertEquals("fileNotFound should not be called", 0 , listener.notFound);
        assertEquals("fileRotated should be be called", 1 , listener.rotated);
    }

    @Override
    protected void createFile(File file, long size)
        throws IOException {
        super.createFile(file, size);

        // try to make sure file is found
        // (to stop continuum occasionally failing)
        RandomAccessFile reader = null;
        try {
            while (reader == null) {
                try {
                    reader = new RandomAccessFile(file.getPath(), "r");
                } catch (FileNotFoundException e) {
                }
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /** Append some lines to a file */
    private void write(File file, String... lines) throws Exception {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
    
    /** Append a string to a file */
    private void writeString(File file, String string) throws Exception {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.write(string);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public void testStopWithNoFile() throws Exception {
        final File file = new File(getTestDirectory(),"nosuchfile");
        assertFalse("nosuchfile should not exist", file.exists());
        final TestTailerListener listener = new TestTailerListener();
        int delay = 100;
        int idle = 50; // allow time for thread to work
        tailer = Tailer.create(file, listener, delay, false);
        Thread.sleep(idle);
        tailer.stop();
        tailer=null;
        Thread.sleep(delay+idle);
        assertNull("Should not generate Exception", listener.exception);
        assertEquals("Expected init to be called", 1 , listener.initialised);
        assertTrue("fileNotFound should be called", listener.notFound > 0);
        assertEquals("fileRotated should be not be called", 0 , listener.rotated);
    }

    public void testStopWithNoFileUsingExecutor() throws Exception {
        final File file = new File(getTestDirectory(),"nosuchfile");
        assertFalse("nosuchfile should not exist", file.exists());
        TestTailerListener listener = new TestTailerListener();
        int delay = 100;
        int idle = 50; // allow time for thread to work
        tailer = new Tailer(file, listener, delay, false);
        Executor exec = new ScheduledThreadPoolExecutor(1);
        exec.execute(tailer);
        Thread.sleep(idle);
        tailer.stop();
        tailer=null;
        Thread.sleep(delay+idle);
        assertNull("Should not generate Exception", listener.exception);
        assertEquals("Expected init to be called", 1 , listener.initialised);
        assertTrue("fileNotFound should be called", listener.notFound > 0);
        assertEquals("fileRotated should be not be called", 0 , listener.rotated);
    }

    /**
     * Test {@link TailerListener} implementation.
     */
    private static class TestTailerListener implements TailerListener {

        private final List<String> lines = new ArrayList<String>();

        volatile Exception exception = null;
        
        volatile int notFound = 0;

        volatile int rotated = 0;
        
        volatile int initialised = 0;

        public void handle(String line) {
            lines.add(line);
        }
        public List<String> getLines() {
            return lines;
        }
        public void clear() {
            lines.clear();
        }
        public void handle(Exception e) {
            exception = e;
        }
        public void init(Tailer tailer) {
            initialised++; // not atomic, but OK because only updated here.
        }
        public void fileNotFound() {
            notFound++; // not atomic, but OK because only updated here.
        }
        public void fileRotated() {
            rotated++; // not atomic, but OK because only updated here.
        }
    }
}
