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
package org.apache.commons.io.output;


import java.io.IOException;

import junit.framework.TestCase;


/**
 * Really not a lot to do here, but checking that no 
 * Exceptions are thrown. 
 *
 * @version $Revision: 618694 $ $Date: 2008-02-05 11:34:21 -0500 (Tue, 05 Feb 2008) $
 */

public class NullOutputStreamTest extends TestCase {

    public NullOutputStreamTest(String name) {
        super(name);
    }

    public void testNull() throws IOException {
        NullOutputStream nos = new NullOutputStream();
        nos.write("string".getBytes());
        nos.write("some string".getBytes(), 3, 5);
        nos.write(1);
        nos.write(0x0f);
        nos.flush();
        nos.close();
        nos.write("allowed".getBytes());
        nos.write(255);
    }

}
