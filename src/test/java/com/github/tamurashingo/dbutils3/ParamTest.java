/*-
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 tamura shingo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.tamurashingo.dbutils3;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Map;

import static org.hamcrest.CoreMatchers.*;

public class ParamTest {

    @Test
    public void testNormalMapFeature() {
        Param p = new Param();
        
        p
            .put("key1", "value1")
            .put("key2", "value2")
            .put("key3", "value3")
            .put("key4", "value4")
            .put("key4", "_value4");

        assertThat(p.get("key1"), is((Object)"value1"));
        assertThat(p.get("key2"), is((Object)"value2"));
        assertThat(p.get("key3"), is((Object)"value3"));
        
        // update value
        assertThat(p.get("key4"), is((Object)"_value4"));
        
        // returns null
        assertThat(p.get("key5"), nullValue());
    }
    
    @Test
    public void testIterator() {
        Param p = new Param();
        
        p
        .put("key1", "value1")
        .put("key2", "value2")
        .put("key3", "value3");
        
        int count = 0;
        for (Map.Entry<String, Object> o: p) {
            count++;
            if (o.getKey().equals("key1")) {
                assertThat(p.get("key1"), is((Object)"value1"));
            }
            else if (o.getKey().equals("key2")) {
                assertThat(p.get("key2"), is((Object)"value2"));                
            }
            else if (o.getKey().equals("key3")) {
                assertThat(p.get("key3"), is((Object)"value3"));
            }
            else {
                fail("unknown key:" + o.getKey());
            }
        }
        assertThat(count, is(3));
    }

}
