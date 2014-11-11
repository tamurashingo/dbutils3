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

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is database utility to use query and update SQLs easily.
 *
 * @author tamura shingo (tamura.shingo at gmail.com)
 *
 */
public class DBConnectionUtil implements AutoCloseable {

    /**
     * Database connection
     */
    protected Connection conn;

    /**
     * prepared statement
     */
    protected PreparedStatement stmt;

    /**
     * constructor.
     * specify the database connection.
     *
     * @param conn database connection
     */
    public DBConnectionUtil(Connection conn) {
        this.conn = conn;
    }

    /**
     * precompile the sql.
     *
     * @param sql sql statement to precompile
     * @throws SQLException database error has occurred
     */
    public void prepare(String sql) throws SQLException {
        closeStmt();
        stmt = conn.prepareStatement(sql);
    }

    /**
     * execute query sql and return {@link List} of {@link Map}.
     * 
     * @param params parameter for precompiled sql
     * @return search result
     * @throws SQLException database error has occurred
     */
    public List<Map<String, String>> executeQuery(Object... params) throws SQLException {
        setValue(params);

        List<Map<String, String>> list = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()){
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                for (int ix = 1; ix <= columnCount; ix++) {
                    map.put(rsmd.getColumnLabel(ix), rs.getString(ix));
                }
                list.add(map);
            }
        }

        return list;
    }

    /**
     * execute query sql and return Bean of {@link List}.
     *
     * @param cls bean class information which set search result
     * @param params parameter for precompiled sql
     * @return search result
     * @throws SQLException database error or bean writer error has occurred. 
     */
    public <T> List<T> executeQuery(Class<T> cls, Object... params) throws SQLException {
        setValue(params);

        BeanBuilderFactory.Manager factoryManager = BeanBuilderFactory.getManager();
        BeanBuilder builder = factoryManager.getBeanBuilder(cls);

        List<T> list = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                T bean = builder.build(rs);
                list.add(bean);
            }
        }
        catch (BeanBuilderException ex) {
            throw new SQLException(ex);
        }

        return list;
    }


    /**
     * execute update sql.
     *
     * @param params parameter for precompiled sql
     * @return the number of update
     * @throws SQLException database error has occurred
     */
    public int executeUpdate(Object... params) throws SQLException {
        setValue(params);
        return stmt.executeUpdate();
    }

    /**
     * do commit.
     *
     * @throws SQLException database error has occurred
     */
    public void commit() throws SQLException {
        conn.commit();
    }

    /**
     * do rollback.
     *
     * @throws SQLException database error has occurred
     */
    public void rollback() throws SQLException {
        conn.rollback();
    }

    /**
     * disconnect database connection.
     */
    @Override
    public void close() {
        closeStmt();
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException ex) {
                // nothing to do
            }
            conn = null;
        }
    }

    /**
     * get {@link Connection}.
     *
     * @return database connection
     */
    public Connection getConnection() {
        return this.conn;
    }

    /**
     * set parameters for precompiled sql.
     *
     * @param params parameter for precompiled sql
     * @throws SQLException database error has occurred
     */
    private void setValue(Object... params) throws SQLException {
        int ix = 0;
        for (Object param: params) {
            ++ix;
            if (param == null) {
                ParameterMetaData pmd = stmt.getParameterMetaData();
                int type = pmd.getParameterType(ix);
                stmt.setNull(ix, type);
            }
            else {
                stmt.setObject(ix,  param);
            }
        }
    }

    /**
     * disconnect prepared statement.
     */
    private void closeStmt() {
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException ex) {
                // nothing to do
            }
            stmt = null;
        }
    }
}
