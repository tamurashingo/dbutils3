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

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

import com.github.tamurashingo.dbutils3.Mapper.AbstractSetter;

/**
 * create bean, set value from {@link ResultSet}.
 * 
 * @author tamura shingo (tamura.shingo at gmail.com)
 *
 */
public class BeanBuilder {
    
    private Class<?> cls;
    private Mapper mapper;

    /**
     * constructor.
     * 
     * @param cls bean class information what this {@code BeanBuilder} create.
     */
    public BeanBuilder(Class<?> cls) {
        this.cls = cls;
        mapper = new Mapper();
        mapper.createMapper(cls);
    }
    
    /**
     * create bean, set value from {@link ResultSet}.
     * 
     * @param rs search result
     * @param <T> bean type
     * @return bean set {@link ResultSet}
     * @throws BeanBuilderException failed to construct a bean.
     *         check the bean class does not have default constructor,
     *         or check the access qualifier.
     */
    public <T> T build(ResultSet rs) throws BeanBuilderException {
        try {
            @SuppressWarnings("unchecked")
            T bean = (T)cls.newInstance();
            
            for (Entry<String, AbstractSetter> entry: mapper.entrySet()) {
                /*-
                 * key : Database column name
                 * value : AbstractSetter
                 */
                try {
                    AbstractSetter setter = entry.getValue();
                    if (setter != null) {
                        setter.invoke(bean, rs, entry.getKey());
                    }
                }
                catch (IllegalArgumentException | InvocationTargetException | SQLException ex) {
                    // set nothing(null) when exception occurred.
                }
            }
            
            return bean;
        }
        catch (InstantiationException | IllegalAccessException ex) {
            throw new BeanBuilderException(ex);
        }
    }
}
