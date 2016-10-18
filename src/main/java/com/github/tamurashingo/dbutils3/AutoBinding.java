/*-
 * The MIT License (MIT)
 *
 * Copyright (c) 2014, 2016 tamura shingo
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * autobinding annotation.
 * <p>
 * When a class has {@code Autobinding} annotation,
 * searched private fields and
 * auto generated {@code Column} annotation.
 * Auto generated {@code Column} annotation has snake-cased name.
 * If set {@code Column} annotation and {@code Autobinding} annotation,
 * {@code Column} annotation's value is activated.
 * <p>
 * Here are some example of how annotations can be used:
 * <blockquote><pre>
 *     &#64;AutoBiding
 *     public class TestBean {
 *         private static final long serialVersionUID = 1L;
 *         private String testId;
 *         private String camelCaseValue;
 *         private String snake_case_value;
 *         &#64;Column("original_string")
 *         private String testValue;
 *         ...
 *     }
 * </pre></blockquote>
 * is equivalent to:
 * <blockquote><pre>
 *     public class TestBean {
 *         private static final long serialVersionUID = 1L;
 *         &#64;Column("test_id")
 *         private String testId;
 *         &#64;Column("camel_case_value")
 *         private String camelCaseValue;
 *         &#64;Column("snake_case_value")
 *         private String snake_case_value;
 *         &#64;Column("original_string")
 *         private String testValue;
 *         ...
 *     }
 * </pre></blockquote>
 *
 * @author tamura shingo (tamura.shingo at gmail.com)
 * @since 0.2.1
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoBinding {
}

