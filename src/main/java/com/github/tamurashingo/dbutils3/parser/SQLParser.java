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
 * 
 * @author tamura shingo (tamura.shingo at gmail.com)
 *
 */
public class SQLParser {
    
    private String analyzedSQL;
    private List<String> keyNames;
    
    private boolean analyzed;
    
    public SQLParser() {
        keyNames = new ArrayList<>();
        analyzed = false;
    }
    
    public boolean isAnalyzed() {
        return analyzed;
    }
    
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
    
    public String getAnalyzedSQL() {
        return analyzedSQL;
    }
    
    public Object[] createParams(Param params) {
        List<Object> p = new ArrayList<>(keyNames.size());
        for (String key: keyNames) {
            p.add(params.get(key));
        }
        
        return (Object[])p.toArray(new Object[]{});
    }

}
