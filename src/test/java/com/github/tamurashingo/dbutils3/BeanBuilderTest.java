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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BeanBuilderTest {

    private Connection conn;
    
    @Before
    public void setUp() throws Exception {
        Class.forName("org.h2.Driver");
        this.conn = DriverManager.getConnection("jdbc:h2:mem:test");
        try (Statement st = conn.createStatement()) {
            st.execute("create table test(id int primary key, column_value varchar)");
            st.execute("insert into test values(1, 'this is a pen')");
            st.execute("insert into test values(2, 'that is a pen')");
        }
    }
    
    @After
    public void tearDown() throws Exception {
        if (this.conn != null) {
            conn.close();
        }
    }
    

    /**
     * Columnアノテーションを持たないBeanはインスタンス化のみが行われること。
     * int型のメンバは初期値の0。
     * String型のメンバはnull。
     */
    @Test
    public void testNoColumn() throws Exception {
        BeanBuilder builder = new BeanBuilder(NoColumnBean.class);
        try (Statement st = conn.createStatement()) {
            try (ResultSet rs = st.executeQuery("select id, column_value from test order by id")) {
                rs.next();
                NoColumnBean bean = builder.build(rs);
                assertThat(bean, is(notNullValue()));
                assertThat(bean.getId(), is(0));
                assertThat(bean.getValue(), is(nullValue()));
            }
        }
    }
    
    /**
     * Columnアノテーションを持つBeanはインスタンス化と値のセットが行われること
     * int型のメンバは1。
     * String型のメンバは"this is a pen"。
     */
    @Test
    public void testColumn() throws Exception {
        BeanBuilder builder = new BeanBuilder(ColumnBean.class);
        try (Statement st = conn.createStatement()) {
            try (ResultSet rs = st.executeQuery("select id, column_value from test order by id")) {
                rs.next();
                ColumnBean bean = builder.build(rs);
                assertThat(bean, is(notNullValue()));
                assertThat(bean.getId(), is(1));
                assertThat(bean.getValue(), is("this is a pen"));
            }
        }
    }
    
    /**
     * Columnアノテーションを持つがDatabaseカラム名が違う場合は、インスタンス化のみが行われること。 
     */
    @Test
    public void testInvalidColumn() throws Exception {
        BeanBuilder builder = new BeanBuilder(InvalidColumnBean.class);
        try (Statement st = conn.createStatement()) {
            try (ResultSet rs = st.executeQuery("select id, column_value from test order by id")) {
                rs.next();
                InvalidColumnBean bean = builder.build(rs);
                assertThat(bean, is(notNullValue()));
                assertThat(bean.getValue(), is(nullValue()));
            }
        }
    }

    public static class NoColumnBean {
        private int id;
        private String value;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }
    
    public static class ColumnBean {
        @Column("id")
        private int id;
        @Column("column_value")
        private String value;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }
    
    public static class InvalidColumnBean {
        @Column("value")
        private String value;
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }
}
