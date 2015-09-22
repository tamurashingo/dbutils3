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
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class DBConnectionUtilTest {
    
    private static final String createTableSQL =
            " create table "
          + "   test "
          + " ( "
          + "   id int primary key, "
          + "   boolean_value int, "
          + "   byte_value int, "
          + "   date_value datetime, "
          + "   double_value double, "
          + "   float_value float, "
          + "   int_value int, "
          + "   long_value int, "
          + "   short_value int, "
          + "   str_value varchar"
          + ")"
          ;
  
  private static final String insertInitialValueSQL =
            " insert into "
          + "   test "
          + " ( "
          + "   id, "
          + "   boolean_value, "
          + "   byte_value, "
          + "   date_value, "
          + "   double_value, "
          + "   float_value, "
          + "   int_value, "
          + "   long_value, "
          + "   short_value, "
          + "   str_value "
          + " ) "
          + " values ( "
          + "   1, " // id
          + "   0, " // boolean
          + "   1, " // byte
          + "   '2014-10-27 12:34:56', " // date
          + "   2.3, " // double
          + "   4.5, " // float
          + "   6, " // int
          + "   7, " // long
          + "   8, " // short
          + "   'string' " /// string
          + " ) "
          ;
    

    @Before
    public void setUp() throws Exception {
        Class.forName("org.h2.Driver");
    }

    private Connection connect() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        try (Statement st = conn.createStatement()) {
            st.execute(createTableSQL);
            st.execute(insertInitialValueSQL);
        }
        
        return conn;
    }
    
    /**
     * prepare, executeQuery　の試験
     */
    @Test
    public void testExecuteQuery() throws Exception {
        try (Connection connection = connect()) {
            try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
                String selectSQL =
                          " select "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " from "
                        + "   test "
                        ;
                conn.prepare(selectSQL);
                List<Map<String, String>> result = conn.executeQuery();
                
                assertThat(result, is(notNullValue()));
                assertThat(result.size(), is(1));
                assertThat(result.get(0).get("ID"), is("1"));
                assertThat(result.get(0).get("BOOLEAN_VALUE"), is("0"));
                assertThat(result.get(0).get("BYTE_VALUE"), is("1"));
                assertThat(result.get(0).get("DATE_VALUE"), is("2014-10-27 12:34:56.0"));
                assertThat(result.get(0).get("DOUBLE_VALUE"), is("2.3"));
                assertThat(result.get(0).get("FLOAT_VALUE"), is("4.5"));
                assertThat(result.get(0).get("INT_VALUE"), is("6"));
                assertThat(result.get(0).get("LONG_VALUE"), is("7"));
                assertThat(result.get(0).get("SHORT_VALUE"), is("8"));
                assertThat(result.get(0).get("STR_VALUE"), is("string"));
            }
        }
    }

    /**
     * prepare, executeQuery　の試験
     */
    @Test
    public void testExecuteQueryWithParam() throws Exception {
        try (Connection connection = connect()) {
            try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
                String selectSQL =
                          " select "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " from "
                        + "   test "
                        + " where "
                        + "   id = :id "
                        ;
                conn.prepareWithParam(selectSQL);
                List<Map<String, String>> result = conn.executeQueryWithParam(new Param().put("id", "1"));
                
                assertThat(result, is(notNullValue()));
                assertThat(result.size(), is(1));
                assertThat(result.get(0).get("ID"), is("1"));
                assertThat(result.get(0).get("BOOLEAN_VALUE"), is("0"));
                assertThat(result.get(0).get("BYTE_VALUE"), is("1"));
                assertThat(result.get(0).get("DATE_VALUE"), is("2014-10-27 12:34:56.0"));
                assertThat(result.get(0).get("DOUBLE_VALUE"), is("2.3"));
                assertThat(result.get(0).get("FLOAT_VALUE"), is("4.5"));
                assertThat(result.get(0).get("INT_VALUE"), is("6"));
                assertThat(result.get(0).get("LONG_VALUE"), is("7"));
                assertThat(result.get(0).get("SHORT_VALUE"), is("8"));
                assertThat(result.get(0).get("STR_VALUE"), is("string"));
            }
        }
    }

    /**
     * prepare, executeQuery　の試験
     */
    @Test
    public void testExecuteQueryBean() throws Exception {
        try (Connection connection = connect()) {
            try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
                String selectSQL =
                          " select "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " from "
                        + "   test "
                        ;
                conn.prepare(selectSQL);
                List<TestBean> result = conn.executeQuery(TestBean.class);
                
                assertThat(result, is(notNullValue()));
                assertThat(result.size(), is(1));
                assertThat(result.get(0).isBooleanVal(), is(false));
                assertThat(result.get(0).getByteVal(), is((byte)1));
                
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                assertThat(df.format(result.get(0).getDateVal()), is("2014-10-27 12:34:56"));
                
                assertEquals(2.3, result.get(0).getDoubleVal(), 0.001);
                assertEquals(4.5, result.get(0).getFloatVal(), 0.001);
                assertThat(result.get(0).getIntVal(), is(6));
                assertThat(result.get(0).getLongVal(), is(7L));
                assertThat(result.get(0).getShortVal(), is((short)8));
                assertThat(result.get(0).getStrVal(), is("string"));
            }
        }
    }
    
    /**
     * prepare, executeQuery　の試験
     */
    @Test
    public void testExecuteQueryBeanWithParam() throws Exception {
        try (Connection connection = connect()) {
            try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
                String selectSQL =
                          " select "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " from "
                        + "   test "
                        + " where "
                        + "   id = :id "
                        ;
                conn.prepareWithParam(selectSQL);
                List<TestBean> result = conn.executeQueryWithParam(TestBean.class, new Param().put("id", "1"));
                
                assertThat(result, is(notNullValue()));
                assertThat(result.size(), is(1));
                assertThat(result.get(0).isBooleanVal(), is(false));
                assertThat(result.get(0).getByteVal(), is((byte)1));
                
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                assertThat(df.format(result.get(0).getDateVal()), is("2014-10-27 12:34:56"));
                
                assertEquals(2.3, result.get(0).getDoubleVal(), 0.001);
                assertEquals(4.5, result.get(0).getFloatVal(), 0.001);
                assertThat(result.get(0).getIntVal(), is(6));
                assertThat(result.get(0).getLongVal(), is(7L));
                assertThat(result.get(0).getShortVal(), is((short)8));
                assertThat(result.get(0).getStrVal(), is("string"));
            }
        }
    }

    /**
     * prepare, executeUpdate　の試験
     */
    @Test
    public void testExecuteUpdate() throws Exception {
        try (Connection connection = connect()) {
            try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
                String insertSQL =
                          " insert into "
                        + "   test "
                        + " ( "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " ) "
                        + " values ( "
                        + "   2, " // id
                        + "   true, " // boolean
                        + "   2, " // byte
                        + "   '2014-10-28 12:34:56', " // date
                        + "   4.5, " // double
                        + "   5.6, " // float
                        + "   7, " // int
                        + "   8, " // long
                        + "   ?, " // short
                        + "   ? " /// string
                        + " ) "
                        ;
                
                String selectSQL =
                          " select "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " from "
                        + "   test "
                        + " where "
                        + "   id = ? "
                        ;
                conn.prepare(insertSQL);
                int count = conn.executeUpdate(9, "this is a pen");
                
                conn.prepare(selectSQL);
                List<TestBean> result = conn.executeQuery(TestBean.class, 2);
                
                /* insert件数　*/
                assertThat(count, is(1));
                
                /* insert結果をselectして確認 */
                assertThat(result, is(notNullValue()));
                assertThat(result.size(), is(1));
                assertThat(result.get(0).isBooleanVal(), is(true));
                assertThat(result.get(0).getByteVal(), is((byte)2));
                
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                assertThat(df.format(result.get(0).getDateVal()), is("2014-10-28 12:34:56"));
                
                assertEquals(4.5, result.get(0).getDoubleVal(), 0.001);
                assertEquals(5.6, result.get(0).getFloatVal(), 0.001);
                assertThat(result.get(0).getIntVal(), is(7));
                assertThat(result.get(0).getLongVal(), is(8L));
                assertThat(result.get(0).getShortVal(), is((short)9));
                assertThat(result.get(0).getStrVal(), is("this is a pen"));
            }
        }
    }

    /**
     * prepare, executeUpdate　の試験
     */
    @Test
    public void testExecuteUpdateWithParam() throws Exception {
        try (Connection connection = connect()) {
            try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
                String insertSQL =
                          " insert into "
                        + "   test "
                        + " ( "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " ) "
                        + " values ( "
                        + "   3, " // id
                        + "   true, " // boolean
                        + "   2, " // byte
                        + "   '2014-10-28 12:34:56', " // date
                        + "   4.5, " // double
                        + "   5.6, " // float
                        + "   7, " // int
                        + "   8, " // long
                        + "   :short, " // short
                        + "   :long " /// string
                        + " ) "
                        ;
                
                String selectSQL =
                          " select "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " from "
                        + "   test "
                        + " where "
                        + "   id = :id "
                        ;
                conn.prepareWithParam(insertSQL);
                int count = conn.executeUpdateWithParam(new Param().put("short", 9).put("long", "this is a pen"));
                
                conn.prepareWithParam(selectSQL);
                List<TestBean> result = conn.executeQueryWithParam(TestBean.class, new Param().put("id", 3));
                
                /* insert件数　*/
                assertThat(count, is(1));
                
                /* insert結果をselectして確認 */
                assertThat(result, is(notNullValue()));
                assertThat(result.size(), is(1));
                assertThat(result.get(0).isBooleanVal(), is(true));
                assertThat(result.get(0).getByteVal(), is((byte)2));
                
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                assertThat(df.format(result.get(0).getDateVal()), is("2014-10-28 12:34:56"));
                
                assertEquals(4.5, result.get(0).getDoubleVal(), 0.001);
                assertEquals(5.6, result.get(0).getFloatVal(), 0.001);
                assertThat(result.get(0).getIntVal(), is(7));
                assertThat(result.get(0).getLongVal(), is(8L));
                assertThat(result.get(0).getShortVal(), is((short)9));
                assertThat(result.get(0).getStrVal(), is("this is a pen"));
            }
        }
    }

    /**
     * executeUpdate　で null を指定した際の試験
     */
    @Test
    public void testExecuteUpdateNull() throws Exception {
        try (Connection connection = connect()) {
            try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
                String insertSQL =
                          " insert into "
                        + "   test "
                        + " ( "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " ) "
                        + " values ( "
                        + "   ?, " // id
                        + "   ?, " // boolean
                        + "   ?, " // byte
                        + "   ?, " // date
                        + "   ?, " // double
                        + "   ?, " // float
                        + "   ?, " // int
                        + "   ?, " // long
                        + "   ?, " // short
                        + "   ? " /// string
                        + " ) "
                        ;
                
                String selectSQL =
                          " select "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " from "
                        + "   test "
                        + " where "
                        + "   id = ? "
                        ;
                conn.prepare(insertSQL);
                int count = conn.executeUpdate(
                        2, // id
                        null, // boolean
                        null, // byte
                        null, // date
                        null, // double
                        null, // float
                        null, // int
                        null, // long
                        null, // short
                        null  // string
                        );
                
                conn.prepare(selectSQL);
                List<TestBean> result = conn.executeQuery(TestBean.class, 2);
                
                /* insert件数　*/
                assertThat(count, is(1));
                
                /* insert結果をselectして確認 */
                assertThat(result, is(notNullValue()));
                assertThat(result.size(), is(1));
                assertThat(result.get(0).isBooleanVal(), is(false));
                assertThat(result.get(0).getByteVal(), is((byte)0));
                
                assertThat(result.get(0).getDateVal(), is(nullValue()));
                
                assertEquals(0.0, result.get(0).getDoubleVal(), 0.001);
                assertEquals(0.0, result.get(0).getFloatVal(), 0.001);
                assertThat(result.get(0).getIntVal(), is(0));
                assertThat(result.get(0).getLongVal(), is(0L));
                assertThat(result.get(0).getShortVal(), is((short)0));
                assertThat(result.get(0).getStrVal(), is(nullValue()));
            }
        }
        
    }
    
    /**
     * executeUpdate　で null を指定した際の試験
     */
    @Test
    public void testExecuteUpdateNullWithParam() throws Exception {
        try (Connection connection = connect()) {
            try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
                String insertSQL =
                          " insert into "
                        + "   test "
                        + " ( "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " ) "
                        + " values ( "
                        + "   :id, " // id
                        + "   :boolean, " // boolean
                        + "   :byte, " // byte
                        + "   :date, " // date
                        + "   :double, " // double
                        + "   :float, " // float
                        + "   :int, " // int
                        + "   :long, " // long
                        + "   :short, " // short
                        + "   :string " /// string
                        + " ) "
                        ;
                
                String selectSQL =
                          " select "
                        + "   id, "
                        + "   boolean_value, "
                        + "   byte_value, "
                        + "   date_value, "
                        + "   double_value, "
                        + "   float_value, "
                        + "   int_value, "
                        + "   long_value, "
                        + "   short_value, "
                        + "   str_value "
                        + " from "
                        + "   test "
                        + " where "
                        + "   id = :id "
                        ;
                conn.prepareWithParam(insertSQL);
                int count = conn.executeUpdateWithParam(
                        new Param()
                           .put("id", 2)
                           .put("boolean", null)
                           .put("byte", null)
                           .put("date", null)
                           .put("double", null)
                           .put("float", null)
                           .put("int", null)
                           .put("long", null)
                           .put("short", null)
                           .put("string", null)
                           );
                
                conn.prepareWithParam(selectSQL);
                List<TestBean> result = conn.executeQueryWithParam(TestBean.class, new Param().put("id", 2));
                
                /* insert件数　*/
                assertThat(count, is(1));
                
                /* insert結果をselectして確認 */
                assertThat(result, is(notNullValue()));
                assertThat(result.size(), is(1));
                assertThat(result.get(0).isBooleanVal(), is(false));
                assertThat(result.get(0).getByteVal(), is((byte)0));
                
                assertThat(result.get(0).getDateVal(), is(nullValue()));
                
                assertEquals(0.0, result.get(0).getDoubleVal(), 0.001);
                assertEquals(0.0, result.get(0).getFloatVal(), 0.001);
                assertThat(result.get(0).getIntVal(), is(0));
                assertThat(result.get(0).getLongVal(), is(0L));
                assertThat(result.get(0).getShortVal(), is((short)0));
                assertThat(result.get(0).getStrVal(), is(nullValue()));
            }
        }
        
    }

    @Test
    public void testOther() throws Exception {
        try (Connection connection = connect()) {
            try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
                Connection c = conn.getConnection();
                assertThat(c, is(sameInstance(connection)));
                
                // コミット2回
                conn.commit();
                conn.commit();
                
                // ロールバック2回
                conn.rollback();
                conn.rollback();
                
                // クローズ2回
                conn.close();
                conn.close();
            }
        }
    }

    /**
     * Beanのインスタンス化に失敗することの確認
     */
    @Test
    public void testError() throws Exception {
        try (Connection connection = connect()) {
            try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
                String selectSQL =
                        " select "
                      + "   id, "
                      + "   boolean_value, "
                      + "   byte_value, "
                      + "   date_value, "
                      + "   double_value, "
                      + "   float_value, "
                      + "   int_value, "
                      + "   long_value, "
                      + "   short_value, "
                      + "   str_value "
                      + " from "
                      + "   test "
                      ;
              conn.prepare(selectSQL);
              conn.executeQuery(NoJavaBean.class);
              fail("not reached");
            }
            catch (SQLException ex) {
                assertThat(ex.getCause(), is(instanceOf(BeanBuilderException.class)));
            }
        }
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

        public boolean isBooleanVal() {
            return booleanVal;
        }
        public void setBooleanVal(boolean booleanVal) {
            this.booleanVal = booleanVal;
        }
        public byte getByteVal() {
            return byteVal;
        }
        public void setByteVal(byte byteVal) {
            this.byteVal = byteVal;
        }
        public java.util.Date getDateVal() {
            return dateVal;
        }
        public void setDateVal(java.util.Date dateVal) {
            this.dateVal = dateVal;
        }
        public double getDoubleVal() {
            return doubleVal;
        }
        public void setDoubleVal(double doubleVal) {
            this.doubleVal = doubleVal;
        }
        public float getFloatVal() {
            return floatVal;
        }
        public void setFloatVal(float floatVal) {
            this.floatVal = floatVal;
        }
        public int getIntVal() {
            return intVal;
        }
        public void setIntVal(int intVal) {
            this.intVal = intVal;
        }
        public long getLongVal() {
            return longVal;
        }
        public void setLongVal(long longVal) {
            this.longVal = longVal;
        }
        public short getShortVal() {
            return shortVal;
        }
        public void setShortVal(short shortVal) {
            this.shortVal = shortVal;
        }
        public String getStrVal() {
            return strVal;
        }
        public void setStrVal(String strVal) {
            this.strVal = strVal;
        }
        public Object getObjVal() {
            return objVal;
        }
        public void setObjVal(Object objVal) {
            this.objVal = objVal;
        }
    }
    
    public static class NoJavaBean {
        private String message;
        public NoJavaBean(String message) {
            this.message = message;
        }
        public String getMessage() {
            return this.message;
        }
    }
}
