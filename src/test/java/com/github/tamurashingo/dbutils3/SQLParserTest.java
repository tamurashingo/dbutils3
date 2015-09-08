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

import com.github.tamurashingo.dbutils3.parser.SQLParser;

import static org.junit.Assert.*;


import static org.hamcrest.CoreMatchers.*;


public class SQLParserTest {
    
    @Test
    public void testEmptySQL() {
        String sql = "";
        SQLParser parser = new SQLParser();
        String analyzedSQL = parser.analyzeSQL(sql);
        Object[] params = parser.createParams(new Param());
        
        assertThat(analyzedSQL, is(""));
        assertThat(params, is(notNullValue()));
        assertThat(params.length, is(0));
    }
    
    @Test
    public void testSingleColumnSQL() {
        String sql =
                  " select "
                + "   * "
                + " from "
                + "   table "
                + " where "
                + "   column = :column "
                ;
        String testSQL =
                "select * from table where column = ? ";
        
        SQLParser parser = new SQLParser();
        String analyzedSQL = parser.analyzeSQL(sql);
        Object[] params = parser.createParams(new Param().put("column", "col1"));
        
        assertThat(analyzedSQL, is(testSQL));
        assertThat(params, is(notNullValue()));
        assertThat(params.length, is(1));
        assertThat(params[0], is((Object)"col1"));
    }
    
    
    @Test
    public void testMultiColumnSQL() {
        String sql =
                  " select "
                + "   * "
                + " from "
                + "   table "
                + " where "
                + "   column = :column "
                + " and "
                + "   last_update_date >= :last_date "
                ;
        String testSQL =
                "select * from table where column = ? and last_update_date >= ? ";
        
        SQLParser parser = new SQLParser();
        String analyzedSQL = parser.analyzeSQL(sql);
        Object[] params = parser.createParams(new Param().put("column", "col1").put("last_date", "2015-08-31"));
        
        assertThat(analyzedSQL, is(testSQL));
        assertThat(params, is(notNullValue()));
        assertThat(params.length, is(2));
        assertThat(params[0], is((Object)"col1"));
        assertThat(params[1], is((Object)"2015-08-31"));
    }
    
    @Test
    public void testSameColumnSQL() {
        String sql =
                  " select "
                + "   * "
                + " from "
                + "   m1, "
                + "   m2, "
                + "   m3 "
                + " where "
                + "   m1.id = m2.id "
                + " and "
                + "   m2.id = m3.id "
                + " and "
                + "   m1.start_ymd <= :now and :now <= m1.end_ymd "
                + " and "
                + "   m2.start_ymd <= :now and :now <= m2.end_ymd "
                + " and "
                + "   m3.start_ymd <= :now and :now <= m3.end_ymd "
                ;
        String testSQL =
                "select * from m1, m2, m3 where "
                + "m1.id = m2.id and m2.id = m3.id and "
                + "m1.start_ymd <= ? and ? <= m1.end_ymd and "
                + "m2.start_ymd <= ? and ? <= m2.end_ymd and "
                + "m3.start_ymd <= ? and ? <= m3.end_ymd ";
        
        SQLParser parser = new SQLParser();
        String analyzedSQL = parser.analyzeSQL(sql);
        Object[] params = parser.createParams(new Param().put("now", "2015-08-31 12:00:00"));
        
        assertThat(analyzedSQL, is(testSQL));
        assertThat(params, is(notNullValue()));
        assertThat(params.length, is(6));
        assertThat(params[0], is((Object)"2015-08-31 12:00:00"));
        assertThat(params[1], is((Object)"2015-08-31 12:00:00"));
        assertThat(params[2], is((Object)"2015-08-31 12:00:00"));
        assertThat(params[3], is((Object)"2015-08-31 12:00:00"));
        assertThat(params[4], is((Object)"2015-08-31 12:00:00"));
        assertThat(params[5], is((Object)"2015-08-31 12:00:00"));
    }
    
    @Test
    public void testStringSQL() {
        String sql =
                  " select "
                + "  ':id', "
                + "  \":param\" "
                + " from "
                + "  dual "
                + " where "
                + "   id = :id "
                + " and "
                + "   param = ':param' "
                ;
        String testSQL = "select ':id', \":param\" from dual where id = ? and param = ':param' ";
        
        SQLParser parser = new SQLParser();
        String analyzedSQL = parser.analyzeSQL(sql);
        Object[] params = parser.createParams(new Param().put("id", 1).put("param",  2));
        
        assertThat(analyzedSQL, is(testSQL));
        assertThat(params, is(notNullValue()));
        assertThat(params.length, is(1));
        assertThat(params[0], is((Object)1));
    }
}
