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

import java.util.HashMap;
import java.util.Map;

/**
 * This class has bean class information and its {@link BeanBuilder}.
 * {@link BeanBuilder} is reusable, so this class provide cache mechanism.
 *
 * <p>
 * example
 * <code><pre>
 * BeanBuilderFactory.Manager factoryManager = BeanBuilderFactory.getManager();
 * BeanBuilder builder = factoryManager.getBeanBuilder(XXXXBean.class);
 * </pre></code>
 * </p>
 *
 * @author tamura shingo (tamura.shingo at gmail.com)
 *
 */
public class BeanBuilderFactory {

    /**
     * instance information about {@link Manager}.
     */
    private static final Manager manager = new ManagerImpl();

    /**
     * getter {@link Manager}.
     *
     * @return　{@link Manager}
     */
    public static Manager getManager() {
        return manager;
    }

    /**
     * constructor.
     * prevent instantiation.
     */
    private BeanBuilderFactory() {
    }

    /**
     * interface for getting {@link BeanBuilder}.
     *
     * @author tamura shingo (tamura.shingo at gmail.com)
     *
     */
    public static interface Manager {
        /**
         * return {@link BeanBuilder} which this manager maps the specified key (Class). 
         *
         * @param cls bean class information
         * @return {@link BeanBuilder}
         */
        <T> BeanBuilder getBeanBuilder(Class<T> cls);
    }

    /**
     * This class provides cache mechanism and implements the {@link Manager} interface.
     *
     * @author tamura shingo (tamura.shingo at gmail.com)
     *
     */
    private static class ManagerImpl implements Manager {
        /** cache */
        private Map<Class<?>, BeanBuilder> mapper = new HashMap<>();

        @Override
        public <T> BeanBuilder getBeanBuilder(Class<T> cls) {
            BeanBuilder builder = mapper.get(cls);
            if (builder == null) {
                builder = new BeanBuilder(cls);
                mapper.put(cls, builder);
            }
            return builder;
        }
    }
}
