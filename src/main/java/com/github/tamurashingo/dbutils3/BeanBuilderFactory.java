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
 * Beanとそれに対応する{@link BeanBuilder}を保持するクラス。
 * {@link BeanBuilder}は一度生成すれば再利用が可能なため、このクラスを使ってキャッシュを保持する。
 *
 * <p>
 * 例
 * <code><pre>
 * BeanBuilderFactory.Manager factoryManager = BeanBuilderFactory.getManager();
 * BeanBuilder builder = factoryManager.getBeanBuilder(XXXXBean.class);
 * </pre></code>
 * </p>
 *
 * @author tamura shingo
 *
 */
public class BeanBuilderFactory {

    /**
     * {@link Manager}のインスタンス情報。
     * クラスロード時にインスタンス化する
     */
    private static final Manager manager = new ManagerImpl();

    /**
     * {@link Manager}を取得する。
     *
     * @return　{@link Manager}
     */
    public static Manager getManager() {
        return manager;
    }

    /**
     * コンストラクタ。
     * インスタンス化禁止。
     */
    private BeanBuilderFactory() {
    }

    /**
     * {@link BeanBuilder}を取得するためのインタフェース。
     *
     * @author tamura shingo
     *
     */
    public static interface Manager {
        /**
         * Beanに対応する{@link BeanBuilder}を取得する。
         *
         * @param cls Beanのクラス情報
         * @return {@link BeanBuilder}
         */
        <T> BeanBuilder getBeanBuilder(Class<T> cls);
    }

    /**
     * {@link BeanBuilder}を取得するための実装クラス。
     * 内部にBeanクラスと{@link BeanBuilder}のキャッシュを保持する。
     *
     * @author tamura shingo
     *
     */
    private static class ManagerImpl implements Manager {
        /** キャッシュ */
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
