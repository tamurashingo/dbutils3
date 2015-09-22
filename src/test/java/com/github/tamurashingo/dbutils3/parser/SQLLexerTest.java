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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.junit.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


public class SQLLexerTest {
    
    @Test
    public void testEmpty() throws Exception {
        String text = "";
        SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
        Token token = lexer.nextToken();
        
        assertThat(token.getType(), is(SQLLexer.EOF));
    }
    
    @Test
    public void testALPHA() throws Exception {
        {
            String text = "a";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.ALPHA));
            assertThat(token.getText(), is("a"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }
        
        {
            String text = "aBc_";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.ALPHA));
            assertThat(token.getText(), is("a"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.ALPHA));
            assertThat(token.getText(), is("B"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.ALPHA));
            assertThat(token.getText(), is("c"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.ALPHA));
            assertThat(token.getText(), is("_"));

            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }
    }
    
    @Test
    public void testNUMBER() throws Exception {
        {
            String text = "0";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.NUMBER));
            assertThat(token.getText(), is("0"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }

        {
            String text = "012";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.NUMBER));
            assertThat(token.getText(), is("0"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.NUMBER));
            assertThat(token.getText(), is("1"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.NUMBER));
            assertThat(token.getText(), is("2"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }
    }
    
    @Test
    public void testSYMBOL() throws Exception {
        {
            String text = "`~!";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.SYMBOL));
            assertThat(token.getText(), is("`"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.SYMBOL));
            assertThat(token.getText(), is("~"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.SYMBOL));
            assertThat(token.getText(), is("!"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }
    }

    /**
     * <table>
     *   <thead>
     *     <tr>
     *       <td>test pattern</td>
     *       <td>expect ype</td>
     *       <td>expect text</td>
     *       <td>note</td>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <td><code>:param</code></td>
     *       <td><code>NAMED_PARAM</code></td>
     *       <td><code>:param</code></td>
     *       <td></td>
     *     </tr>
     *     <tr>
     *       <td><code>:p</code></td>
     *       <td><code>NAMED_PARAM</code></td>
     *       <td><code>:p</code></td>
     *       <td></td>
     *     </tr>
     *     <tr>
     *       <td><code>:1</code></td>
     *       <td><code>COLON NUMBER</code></td>
     *       <td><code>: 1</code></td>
     *       <td></td>
     *     </tr>
     *     <tr>
     *       <td><code>:p12a</code></td>
     *       <td><code>NAMED_PARAM</code></td>
     *       <td><code>:p12a</code></td>
     *       <td></td>
     *     </tr>
     *     <tr>
     *       <td><code>:</code></td>
     *       <td><code>COLON</code></td>
     *       <td><code>:</code></td>
     *       <td></td>
     *     </tr>
     *     <tr>
     *       <td><code>::p</code></td>
     *       <td><code>COLON NAMED_PARAM</code></td>
     *       <td><code>: :p</code></td>
     *       <td></td>
     *     </tr>
     *     <tr>
     *       <td><code>: p</code></td>
     *       <td><code>COLON WS ALPHA</code></td>
     *       <td><code>: &nbsp; p</code></td>
     *       <td></td>
     *     </tr>
     *     <tr>
     *       <td><code>":p"</code></td>
     *       <td><code>STRINGLITERAL</code></td>
     *       <td><code>":p"</code></td>
     *       <td></td>
     *     </tr>
     *     <tr>
     *       <td><code>':p1'</code></td>
     *       <td><code>QUOTELITERAL</code></td>
     *       <td><code>':p1'</code></td>
     *       <td></td>
     *     </tr>
     *     <tr>
     *       <td><code>"":p</code></td>
     *       <td><code>STRINGLITERAL NAMED_PARAM</code></td>
     *       <td><code>"" :p</code></td>
     *       <td></td>
     *     </tr>
     *   </tbody>
     * </table>
     * 
     * 
     * @throws Exception
     */
    @Test
    public void testNAMEDPARAM() throws Exception {
        {
            String text = ":param";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.NAMED_PARAM));
            assertThat(token.getText(), is(":param"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }
        
        {
            String text = ":p";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.NAMED_PARAM));
            assertThat(token.getText(), is(":p"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }
        
        {
            String text = ":1";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.COLON));
            assertThat(token.getText(), is(":"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.NUMBER));
            assertThat(token.getText(), is("1"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }

        {
            String text = ":p12a";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.NAMED_PARAM));
            assertThat(token.getText(), is(":p12a"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }
        
        {
            String text = ":";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.COLON));
            assertThat(token.getText(), is(":"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }

        {
            String text = "::p";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.COLON));
            assertThat(token.getText(), is(":"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.NAMED_PARAM));
            assertThat(token.getText(), is(":p"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }
        
        {
            String text = ": p";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.COLON));
            assertThat(token.getText(), is(":"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.WS));
            assertThat(token.getText(), is(" "));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.ALPHA));
            assertThat(token.getText(), is("p"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }

        {
            String text = "\":p\"";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.STRINGLITERAL));
            assertThat(token.getText(), is("\":p\""));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }
    
        {
            String text = "':p'";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.QUOTELITERAL));
            assertThat(token.getText(), is("':p'"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }
    
        {
            String text = "\"\":p";
            SQLLexer lexer = new SQLLexer(new ANTLRInputStream(text));
            Token token = lexer.nextToken();
            
            assertThat(token.getType(), is(SQLLexer.STRINGLITERAL));
            assertThat(token.getText(), is("\"\""));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.NAMED_PARAM));
            assertThat(token.getText(), is(":p"));
            
            token = lexer.nextToken();
            assertThat(token.getType(), is(SQLLexer.EOF));
        }
    
    
    
    }

}
