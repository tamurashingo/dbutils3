/*-
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 tamura shingo
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

import java.util.Map.Entry;

import static org.junit.Assert.*;
import org.junit.*;

import com.github.tamurashingo.dbutils3.Mapper.AbstractSetter;

/**
 * autobidingのテスト。
 * <ul>
 *   <li>AutoBindingの指定がある場合は自動でBindする</li>
 *   <li>ただしColumnの指定がある場合はそれを優先する</li>
 * </ul>
 *
 * @author tamura shingo
 *
 */
public class MapperAutoBindingTest {

    private Mapper autoBindingMapper;
    private Mapper noAutoBindingMapper;

    @Before
    public void setUp() throws Exception {
        autoBindingMapper = new Mapper();
        autoBindingMapper.createMapper(AutoBindingBean.class);

        noAutoBindingMapper = new Mapper();
        noAutoBindingMapper.createMapper(NoAutoBindingBean.class);
    }

    @Test
    public void autoBindingTestIdTest() {
        for (Entry<String, AbstractSetter> entry: autoBindingMapper.entrySet()) {
            if (entry.getKey().equals("test_id")) {
                return ;
            }
        }
        fail("autobinding failed. no 'test_id' value");
    }

    @Test
    public void autoBindingCameCaseValueTest() {
        for (Entry<String, AbstractSetter> entry: autoBindingMapper.entrySet()) {
            if (entry.getKey().equals("camel_case_value")) {
                return ;
            }
        }
        fail("autobinding failed. no 'camel_case_value' value");
    }

    @Test
    public void autoBindingSnakeCaseValueTest() {
        for (Entry<String, AbstractSetter> entry: autoBindingMapper.entrySet()) {
            if (entry.getKey().equals("snake_case_value")) {
                return ;
            }
        }
        fail("autobinding failed. no 'snake_case_value' value");
    }


    @Test
    public void autoBindingColumnTest() {
        for (Entry<String, AbstractSetter> entry: autoBindingMapper.entrySet()) {
            if (entry.getKey().equals("original_string")) {
                return ;
            }
        }
        fail("autobinding failed. no 'original_string' value");
    }

    @Test
    public void NoAutoBindingTestIdTest() {
        for (Entry<String, AbstractSetter> entry: noAutoBindingMapper.entrySet()) {
            if (entry.getKey().equals("test_id")) {
                fail("no-autbinding failed. 'test_id' value exists");
            }
        }
    }

    @Test
    public void NoAutoBindingCameCaseValueTest() {
        for (Entry<String, AbstractSetter> entry: noAutoBindingMapper.entrySet()) {
            if (entry.getKey().equals("camel_case_value")) {
                fail("no-autobinding failed. 'camel_case_value' value exists");
            }
        }
    }

    @Test
    public void NoAutoBindingSnakeCaseValueTest() {
        for (Entry<String, AbstractSetter> entry: noAutoBindingMapper.entrySet()) {
            if (entry.getKey().equals("snake_case_value")) {
                fail("no-autobinding failed. 'snake_case_value' value exists");
            }
        }
    }


    @Test
    public void NoAutoBindingColumnTest() {
        for (Entry<String, AbstractSetter> entry: noAutoBindingMapper.entrySet()) {
            if (entry.getKey().equals("original_string")) {
                return ;
            }
        }
        fail("no-autobinding failed. no 'original_string' value");
    }


    @AutoBinding
    public static class AutoBindingBean implements java.io.Serializable{
        /** serialVersionUID */
        private static final long serialVersionUID = 1L;
        private String testId;
        private String camelCaseValue;
        private String snake_case_value;
        @Column("original_string")
        private String testValue;
        public String getTestId() {
            return testId;
        }
        public void setTestId(String testId) {
            this.testId = testId;
        }
        public String getCamelCaseValue() {
            return camelCaseValue;
        }
        public void setCamelCaseValue(String camelCaseValue) {
            this.camelCaseValue = camelCaseValue;
        }
        public String getSnake_case_value() {
            return snake_case_value;
        }
        public void setSnake_case_value(String snake_case_value) {
            this.snake_case_value = snake_case_value;
        }
        public String getTestValue() {
            return testValue;
        }
        public void setTestValue(String testValue) {
            this.testValue = testValue;
        }
    }

    public static class NoAutoBindingBean implements java.io.Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = 1L;
        private String testId;
        private String camelCaseValue;
        private String snake_case_value;
        @Column("original_string")
        private String testValue;
        public String getTestId() {
            return testId;
        }
        public void setTestId(String testId) {
            this.testId = testId;
        }
        public String getCamelCaseValue() {
            return camelCaseValue;
        }
        public void setCamelCaseValue(String camelCaseValue) {
            this.camelCaseValue = camelCaseValue;
        }
        public String getSnake_case_value() {
            return snake_case_value;
        }
        public void setSnake_case_value(String snake_case_value) {
            this.snake_case_value = snake_case_value;
        }
        public String getTestValue() {
            return testValue;
        }
        public void setTestValue(String testValue) {
            this.testValue = testValue;
        }
    }



}
