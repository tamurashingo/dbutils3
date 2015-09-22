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
package com.github.tamurashingo.dbutils3.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;

import com.github.tamurashingo.dbutils3.Param;

/**
 * analyse sql with named parameter and convert to parametarised SQL.
 * 
 * <p>example.</p>
 * <pre><code>
 * SQLParser parser = new SQLParser();
 * String sql = parser.analyzeSQL("select * from table where id = :id");
 * Param param = new Param().put("id", 123);
 * Object[] params = parser.createParams(param);
 * 
 * conn.prepare(sql);
 * List&lt;TableBean&gt; result = conn.executeQuery(TableBean.class, params);
 * </code></pre>
 * 
 * @author tamura shingo (tamura.shingo at gmail.com)
 * @since 0.2.0
 */
public class SQLParser {
    
    private String analyzedSQL;
    private List<String> keyNames;
    
    private boolean analyzed;
    
    /**
     * constructor
     */
    public SQLParser() {
        keyNames = new ArrayList<>();
        analyzed = false;
    }
    
    /**
     * @return true when {@link #analyzeSQL(String)} called.
     */
    public boolean isAnalyzed() {
        return analyzed;
    }
    
    /**
     * analyse sql with named parameter and convert to parametarised SQL.
     * 
     * @param sql sql string
     * @return converted sql string
     */
    public String analyzeSQL(String sql) {
        SQLLexer l = new SQLLexer(new ANTLRInputStream(sql));
        StringBuilder buf = new StringBuilder();
        
        Token token = l.nextToken();
        int prevType = SQLLexer.WS;
        while (token.getType() != SQLLexer.EOF) {
            switch (token.getType()) {
            case SQLLexer.NAMED_PARAM:
                keyNames.add(token.getText().substring(1));
                buf.append("?");
                break;
            case SQLLexer.WS:
                if (prevType != SQLLexer.WS) {
                    buf.append(" ");
                }
                break;
            default:
                buf.append(token.getText());
            }
            prevType = token.getType();
            token = l.nextToken();
        }
        
        this.analyzedSQL = buf.toString();
        this.analyzed = true;
        return analyzedSQL;
    }
    
    /**
     * get converted sql string
     * @return converted sql string
     */
    public String getAnalyzedSQL() {
        return analyzedSQL;
    }
    
    /**
     * convert named parameters to array of parameter values.
     * 
     * @param params named parameters and values
     * @return array of parameter values
     */
    public Object[] createParams(Param params) {
        List<Object> p = new ArrayList<>(keyNames.size());
        for (String key: keyNames) {
            p.add(params.get(key));
        }
        
        return (Object[])p.toArray(new Object[]{});
    }

}
