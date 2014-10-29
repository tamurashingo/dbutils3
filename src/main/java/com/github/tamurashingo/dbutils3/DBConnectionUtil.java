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
 * 参照SQLや更新SQLを実行しやすくするためのDatabase接続ユーティリティ。
 *
 * @author tamura shingo
 *
 */
public class DBConnectionUtil implements AutoCloseable {

    /**
     * Databaseコネクション
     */
    protected Connection conn;

    /**
     * プリペアドステートメント
     */
    protected PreparedStatement stmt;

    /**
     * コンストラクタ。
     * Databaseコネクションをセットする。
     *
     * @param conn Databaseコネクション
     */
    public DBConnectionUtil(Connection conn) {
        this.conn = conn;
    }

    /**
     * SQL文をプリコンパイルする。
     *
     * @param sql プリコンパイルするSQL文
     * @throws SQLException Databaseエラー発生時
     */
    public void prepare(String sql) throws SQLException {
        closeStmt();
        stmt = conn.prepareStatement(sql);
    }

    /**
     * 参照系SQLを実行し、結果を{@link Map}の{@link List}で得る。
     *
     * @param params プリコンパイルしたSQLに対するパラメータ
     * @return 参照結果
     * @throws SQLException Databaseエラー発生時
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
     * 参照系SQLを実行し、結果をBeanの{@link List}で得る。
     *
     * @param cls 結果をセットするBeanのクラス情報
     * @param params 実行パラメータ
     * @return 参照結果
     * @throws SQLException Databaseエラー発生もしくはBean設定でエラー発生
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
     * 更新系SQLを実行する。
     *
     * @param params プリコンパイルしたSQLに対するパラメータ
     * @return 更新件数
     * @throws SQLException Databaseエラー発生時
     */
    public int executeUpdate(Object... params) throws SQLException {
        setValue(params);
        return stmt.executeUpdate();
    }

    /**
     * コミットを実行する。
     *
     * @throws SQLException Databaseエラー発生時
     */
    public void commit() throws SQLException {
        conn.commit();
    }

    /**
     * ロールバックを実行する。
     *
     * @throws SQLException Databaseエラー発生時
     */
    public void rollback() throws SQLException {
        conn.rollback();
    }

    /**
     * Databaseコネクションを切断する。
     */
    @Override
    public void close() {
        closeStmt();
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException ex) {
                // クローズ時の例外は何もしない
            }
            conn = null;
        }
    }

    /**
     * {@code Connection}を取得する。
     *
     * @return Databaseコネクション
     */
    public Connection getConnection() {
        return this.conn;
    }

    /**
     * プリコンパイルしたSQLにパラメータをセットする。
     *
     * @param params プリコンパイルしたSQLに対するパラメータ
     * @throws SQLException Databaseエラー時
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
     * プリペアドステートメントをクローズする。
     */
    private void closeStmt() {
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException ex) {
                // クローズ時の例外は何もしない
            }
            stmt = null;
        }
    }
}
