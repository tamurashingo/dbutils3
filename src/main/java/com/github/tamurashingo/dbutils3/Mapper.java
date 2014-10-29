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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Beanに定義されたDatabaseのカラム名と型情報を元に、{@link ResultSet}から値を取得しBeanに設定する処理を生成、保持する。
 *
 * <p>
 * 以下のBeanがあった場合
 * </p>
 * <p>
 * <code><pre>
 * public class TestBean {
 *     &#064;Column("id")
 *     private int id;
 *     &#064;Column("user_name")
 *     private String userName;
 *     &#064;Column("age")
 *     private short age;
 *     &#064:Column("valid")
 *     private boolean validFlag;
 *
 *     // setter/getterは省略
 *     ...
 * }
 * </pre></code>
 * </p>
 * <p>
 * 以下のようなセット処理を生成する。
 * </p>
 * <p>
 * <table>
 *   <thaed>
 *     <tr>
 *       <th>#</th>
 *       <th>ColumnName</th>
 *       <th>AbstractInvoker</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td>1</td>
 *       <td>id</td>
 *       <td>{@link BeanBuilder.IntInvoker}<td>
 *     <tr>
 *     </tr>
 *       <td>2</td>
 *       <td>user_name</td>
 *       <td>{@link BeanBuilder.StringInvoker}<td>
 *     </tr>
 *     </tr>
 *       <td>3</td>
 *       <td>age</td>
 *       <td>{@link BeanBuilder.ShortInvoker}<td>
 *     </tr>
 *     </tr>
 *       <td>4</td>
 *       <td>valid</td>
 *       <td>{@link BeanBuilder.BoolInvoker}<td>
 *     </tr>
 *   </tbody>
 * </table>
 * </p>
 *
 * @author tamura shingo
 *
 */
public class Mapper {

    /** Databaseカラム名とそれに対応するBeanへのsetterのMap */
    private Map<String, AbstractSetter> mapper = new HashMap<>();

    /**
     * Beanに定義されたDatabaseカラム名とそれに対応するフィールドへのsetterのMapを作成する。
     *
     * @param cls Bean
     */
    public void createMapper(Class<?> cls) {
        for (Field field: cls.getDeclaredFields()) {
            final Column column =  field.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }

            try {
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), cls);
                final Method setter = pd.getWriteMethod();
                if (setter == null) {
                    continue;
                }
                AbstractSetter invoker = null;
                if (field.getType().equals(boolean.class)) {
                    invoker = new BooleanSetter(setter);
                }
                else if (field.getType().equals(byte.class)) {
                    invoker = new ByteSetter(setter);
                }
                else if (field.getType().equals(Date.class)) {
                    invoker = new DateSetter(setter);
                }
                else if (field.getType().equals(double.class)) {
                    invoker = new DoubleSetter(setter);
                }
                else if (field.getType().equals(float.class)) {
                    invoker = new FloatSetter(setter);
                }
                else if (field.getType().equals(int.class)) {
                    invoker = new IntSetter(setter);
                }
                else if (field.getType().equals(long.class)) {
                    invoker = new LongSetter(setter);
                }
                else if (field.getType().equals(short.class)) {
                    invoker = new ShortSetter(setter);
                }
                else if (field.getType().equals(String.class)) {
                    invoker = new StringSetter(setter);
                }
                mapper.put(column.value(), invoker);
            }
            catch (IntrospectionException ex) {
                // setterの取得に失敗した場合は、そのフィールドへのセットを行わない
            }
        }
    }

    public Set<Entry<String, AbstractSetter>> entrySet() {
        return this.mapper.entrySet();
    }

    /**
     * {@link ResultSet}から指定したカラムの値を取得しBeanへセットを行う抽象クラス。
     *
     * @author tamura shingo
     *
     */
    static abstract class AbstractSetter {
        protected Method setter;

        /**
         * コンストラクタ。
         *
         * @param setter Beanのsetterメソッド
         */
        protected AbstractSetter(Method setter) {
            this.setter = setter;
        }

        /**
         * {@link ResultSet}から指定した型で値を取得し、Beanへセットする。
         *
         * @param beanInst　Beanのインスタンス
         * @param rs Databaseから取得したResultSet
         * @param columnName カラム名
         * @throws IllegalAccessException 例外発生時
         * @throws IllegalArgumentException 例外発生時
         * @throws InvocationTargetException 例外発生時
         * @throws SQLException 例外発生時
         */
        public abstract void invoke(Object beanInst, ResultSet rs, String columnName) throws IllegalAccessException,
        IllegalArgumentException, InvocationTargetException, SQLException; 
    }

    /**
     * Databaseからboolean型の値を取得する実装クラス。
     *
     * @author tamura shingo
     *
     */
    static class BooleanSetter extends AbstractSetter {
        public BooleanSetter(Method setter) {
            super(setter);
        }

        @Override
        public void invoke(Object beanInst, ResultSet rs, String columnName)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, SQLException {
            setter.invoke(beanInst, rs.getBoolean(columnName));
        }
    }

    /**
     * Databaseからbyte型の値を取得する実装クラス。
     *
     * @author tamura shingo
     *
     */
    static class ByteSetter extends AbstractSetter {
        public ByteSetter(Method setter) {
            super(setter);
        }

        @Override
        public void invoke(Object beanInst, ResultSet rs, String columnName)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, SQLException {
            setter.invoke(beanInst, rs.getByte(columnName));
        }
    }

    /**
     * Databaseからjava.util.Date型の値を取得する実装クラス。
     *
     * @author tamura shingo
     *
     */
    static class DateSetter extends AbstractSetter {
        public DateSetter(Method setter) {
            super(setter);
        }

        @Override
        public void invoke(Object beanInst, ResultSet rs, String columnName)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, SQLException {
            setter.invoke(beanInst, rs.getTimestamp(columnName));
        }
    }

    /**
     * Databaseからdouble型の値を取得する実装クラス。
     *
     * @author tamura shingo
     *
     */
    static class DoubleSetter extends AbstractSetter {
        public DoubleSetter(Method setter) {
            super(setter);
        }

        @Override
        public void invoke(Object beanInst, ResultSet rs, String columnName)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, SQLException {
            setter.invoke(beanInst, rs.getDouble(columnName));
        }
    }

    /**
     * Databaseからfloat型の値を取得する実装クラス。
     *
     * @author tamura shingo
     *
     */
    static class FloatSetter extends AbstractSetter {
        public FloatSetter(Method setter) {
            super(setter);
        }

        @Override
        public void invoke(Object beanInst, ResultSet rs, String columnName)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, SQLException {
            setter.invoke(beanInst, rs.getFloat(columnName));
        }
    }

    /**
     * Databaseからint型の値を取得する実装クラス。
     *
     * @author tamura shingo
     *
     */
    static class IntSetter extends AbstractSetter {
        public IntSetter(Method setter) {
            super(setter);
        }

        @Override
        public void invoke(Object beanInst, ResultSet rs, String columnName)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, SQLException {
            setter.invoke(beanInst, rs.getInt(columnName));
        }
    }

    /**
     * Databaseからlong型の値を取得する実装クラス。
     *
     * @author tamura shingo
     *
     */
    static class LongSetter extends AbstractSetter {
        public LongSetter(Method setter) {
            super(setter);
        }

        @Override
        public void invoke(Object beanInst, ResultSet rs, String columnName)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, SQLException {
            setter.invoke(beanInst, rs.getLong(columnName));
        }
    }

    /**
     * Databaseからshort型の値を取得する実装クラス。
     *
     * @author tamura shingo
     *
     */
    static class ShortSetter extends AbstractSetter {
        public ShortSetter(Method setter) {
            super(setter);
        }

        @Override
        public void invoke(Object beanInst, ResultSet rs, String columnName)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, SQLException {
            setter.invoke(beanInst, rs.getShort(columnName));
        }
    }

    /**
     * DatabaseからString型の値を取得する実装クラス。
     *
     * @author tamura shingox
     *
     */
    static class StringSetter extends AbstractSetter {
        public StringSetter(Method setter) {
            super(setter);
        }

        @Override
        public void invoke(Object beanInst, ResultSet rs, String columnName)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException, SQLException {
            setter.invoke(beanInst, rs.getString(columnName));
        }
    }
}
