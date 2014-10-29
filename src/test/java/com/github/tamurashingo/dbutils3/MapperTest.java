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

import java.util.Map.Entry;

import org.junit.*;

import com.github.tamurashingo.dbutils3.Mapper.AbstractSetter;
import com.github.tamurashingo.dbutils3.Mapper.BooleanSetter;
import com.github.tamurashingo.dbutils3.Mapper.ByteSetter;
import com.github.tamurashingo.dbutils3.Mapper.DateSetter;
import com.github.tamurashingo.dbutils3.Mapper.DoubleSetter;
import com.github.tamurashingo.dbutils3.Mapper.FloatSetter;
import com.github.tamurashingo.dbutils3.Mapper.IntSetter;
import com.github.tamurashingo.dbutils3.Mapper.LongSetter;
import com.github.tamurashingo.dbutils3.Mapper.ShortSetter;
import com.github.tamurashingo.dbutils3.Mapper.StringSetter;


/**
 * 各カラムの型に応じたsetterが構築されていることを確認する。
 * <p>
 * 型は以下の通りなので、それ以外の型が指定された場合はnullとなる。
 * <ul>
 *   <li>{@code boolean}</li>
 *   <li>{@code byte}</li>
 *   <li>{@code java.util.Date}</li>
 *   <li>{@code double}</li>
 *   <li>{@code float}</li>
 *   <li>{@code int}</li>
 *   <li>{@code long}</li>
 *   <li>{@code short}</li>
 *   <li>{@code java.lang.String}</li>
 * </ul>
 * </p>
 * 
 * @author tamura shingo
 *
 */
public class MapperTest {
    
    private Mapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new Mapper();
        mapper.createMapper(TestBean.class);
    }

    @Test
    public void booleanTest() {
        for (Entry<String, AbstractSetter> entry: mapper.entrySet()) {
            if (entry.getKey().equals("boolean_value")) {
                AbstractSetter setter = entry.getValue();
                
                assertThat(setter, is(notNullValue()));
                assertThat(setter, is(instanceOf(BooleanSetter.class)));
                
                return ;
            }
        }
        fail("can't find boolean_value");
    }
    
    @Test
    public void byteTest() {
        for (Entry<String, AbstractSetter> entry: mapper.entrySet()) {
            if (entry.getKey().equals("byte_value")) {
                AbstractSetter setter = entry.getValue();
                
                assertThat(setter, is(notNullValue()));
                assertThat(setter, is(instanceOf(ByteSetter.class)));
                
                return ;
            }
        }
        fail("can't find byte_value");
    }
    
    @Test
    public void dateTest() {
        for (Entry<String, AbstractSetter> entry: mapper.entrySet()) {
            if (entry.getKey().equals("date_value")) {
                AbstractSetter setter = entry.getValue();
                
                assertThat(setter, is(notNullValue()));
                assertThat(setter, is(instanceOf(DateSetter.class)));
                
                return ;
            }
        }
        fail("can't find byte_value");
    }
    
    @Test
    public void doubleTest() {
        for (Entry<String, AbstractSetter> entry: mapper.entrySet()) {
            if (entry.getKey().equals("double_value")) {
                AbstractSetter setter = entry.getValue();
                
                assertThat(setter, is(notNullValue()));
                assertThat(setter, is(instanceOf(DoubleSetter.class)));
                
                return ;
            }
        }
        fail("can't find double_value");
    }
    
    @Test
    public void floatTest() {
        for (Entry<String, AbstractSetter> entry: mapper.entrySet()) {
            if (entry.getKey().equals("float_value")) {
                AbstractSetter setter = entry.getValue();
                
                assertThat(setter, is(notNullValue()));
                assertThat(setter, is(instanceOf(FloatSetter.class)));
                
                return ;
            }
        }
        fail("can't find float_value");
    }
    
    @Test
    public void intTest() {
        for (Entry<String, AbstractSetter> entry: mapper.entrySet()) {
            if (entry.getKey().equals("int_value")) {
                AbstractSetter setter = entry.getValue();
                
                assertThat(setter, is(notNullValue()));
                assertThat(setter, is(instanceOf(IntSetter.class)));
                
                return ;
            }
        }
        fail("can't find int_value");
    }
    
    @Test
    public void longTest() {
        for (Entry<String, AbstractSetter> entry: mapper.entrySet()) {
            if (entry.getKey().equals("long_value")) {
                AbstractSetter setter = entry.getValue();
                
                assertThat(setter, is(notNullValue()));
                assertThat(setter, is(instanceOf(LongSetter.class)));
                
                return ;
            }
        }
        fail("can't find long_value");
    }

    @Test
    public void shortTest() {
        for (Entry<String, AbstractSetter> entry: mapper.entrySet()) {
            if (entry.getKey().equals("short_value")) {
                AbstractSetter setter = entry.getValue();
                
                assertThat(setter, is(notNullValue()));
                assertThat(setter, is(instanceOf(ShortSetter.class)));
                
                return ;
            }
        }
        fail("can't find short_value");
    }
    
    @Test
    public void stringTest() {
        for (Entry<String, AbstractSetter> entry: mapper.entrySet()) {
            if (entry.getKey().equals("str_value")) {
                AbstractSetter setter = entry.getValue();
                
                assertThat(setter, is(notNullValue()));
                assertThat(setter, is(instanceOf(StringSetter.class)));
                
                return ;
            }
        }
        fail("can't find str_value");
    }
    
    @Test
    public void objectTest() {
        for (Entry<String, AbstractSetter> entry: mapper.entrySet()) {
            if (entry.getKey().equals("obect_value")) {
                fail("object_value found.");
            }
        }
        // object_valueは対応するAbstractSetterが存在しないため、mapperには入っていない
    }

    public static class TestBean {
        @Column("boolean_value")
        private boolean booleanVal;
        
        @Column("byte_value")
        private byte byteVal;
        
        @Column("date_value")
        private java.util.Date dateVal;
        
        @Column("double_value")
        private double doubleVal;
        
        @Column("float_value")
        private float floatVal;
        
        @Column("int_value")
        private int intVal;
        
        @Column("long_value")
        private long longVal;
        
        @Column("short_value")
        private short shortVal;
        
        @Column("str_value")
        private String strVal;
        
        @Column("object_value")
        private Object objVal;

        /**
         * @return the booleanVal
         */
        public boolean isBooleanVal() {
            return booleanVal;
        }

        /**
         * @param booleanVal the booleanVal to set
         */
        public void setBooleanVal(boolean booleanVal) {
            this.booleanVal = booleanVal;
        }

        /**
         * @return the byteVal
         */
        public byte getByteVal() {
            return byteVal;
        }

        /**
         * @param byteVal the byteVal to set
         */
        public void setByteVal(byte byteVal) {
            this.byteVal = byteVal;
        }

        /**
         * @return the dateVal
         */
        public java.util.Date getDateVal() {
            return dateVal;
        }

        /**
         * @param dateVal the dateVal to set
         */
        public void setDateVal(java.util.Date dateVal) {
            this.dateVal = dateVal;
        }

        /**
         * @return the doubleVal
         */
        public double getDoubleVal() {
            return doubleVal;
        }

        /**
         * @param doubleVal the doubleVal to set
         */
        public void setDoubleVal(double doubleVal) {
            this.doubleVal = doubleVal;
        }

        /**
         * @return the floatVal
         */
        public float getFloatVal() {
            return floatVal;
        }

        /**
         * @param floatVal the floatVal to set
         */
        public void setFloatVal(float floatVal) {
            this.floatVal = floatVal;
        }

        /**
         * @return the intVal
         */
        public int getIntVal() {
            return intVal;
        }

        /**
         * @param intVal the intVal to set
         */
        public void setIntVal(int intVal) {
            this.intVal = intVal;
        }

        /**
         * @return the longVal
         */
        public long getLongVal() {
            return longVal;
        }

        /**
         * @param longVal the longVal to set
         */
        public void setLongVal(long longVal) {
            this.longVal = longVal;
        }

        /**
         * @return the shortVal
         */
        public short getShortVal() {
            return shortVal;
        }

        /**
         * @param shortVal the shortVal to set
         */
        public void setShortVal(short shortVal) {
            this.shortVal = shortVal;
        }

        /**
         * @return the strVal
         */
        public String getStrVal() {
            return strVal;
        }

        /**
         * @param strVal the strVal to set
         */
        public void setStrVal(String strVal) {
            this.strVal = strVal;
        }

        /**
         * @return the objVal
         */
        public Object getObjVal() {
            return objVal;
        }

        /**
         * @param objVal the objVal to set
         */
        public void setObjVal(Object objVal) {
            this.objVal = objVal;
        }
    }
}
