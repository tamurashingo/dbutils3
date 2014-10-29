/*-
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 tamura shingo
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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class BeanBuilderFactoryTest {

    /**
     * <ul>
     *   <li>BeanBuilderが生成されること</li>
     *   <li>同じBeanを指定した場合、同じインスタンスが渡されること</li>
     * </ul>
     */
    @Test
    public void testCache() {
        BeanBuilderFactory.Manager factoryManager = BeanBuilderFactory.getManager();
        BeanBuilder builder = factoryManager.getBeanBuilder(TestBean.class);
        
        assertThat(builder, is(notNullValue()));
        
        BeanBuilder anotherBuilder = factoryManager.getBeanBuilder(TestBean.class);
        
        assertThat(anotherBuilder, is(sameInstance(builder)));
    }
    
    public static class TestBean {
        private String value;
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }
}
