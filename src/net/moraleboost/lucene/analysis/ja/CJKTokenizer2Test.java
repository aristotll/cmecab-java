/*
 **
 **  Feb. 17, 2009
 **
 **  The author disclaims copyright to this source code.
 **  In place of a legal notice, here is a blessing:
 **
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 **
 **                                         Stolen from SQLite :-)
 **  Any feedback is welcome.
 **  Kohei TAKETA <k-tak@void.in>
 **
 */
package net.moraleboost.lucene.analysis.ja;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.lang.Character.UnicodeBlock;

import org.apache.lucene.analysis.Token;

import org.junit.Test;

public class CJKTokenizer2Test
{
    @Test
    public void testTokenizeSurrogate() throws Exception
    {
        // 好物は「ほっけ」です。
        String str = "好物は\uD867\uDE3Dです。";
        StringReader reader = new StringReader(str);
        CJKTokenizer2 tokenizer = new CJKTokenizer2(reader);

        String[] tokens = {
                "好物",
                "物は",
                "は\uD867\uDE3D",
                "\uD867\uDE3Dで",
                "です",
                "す"
        };

        int[][] offsets = {
                { 0, 2 },
                { 1, 3 },
                { 2, 5 },
                { 3, 6 },
                { 5, 7 },
                { 6, 7 }
        };

        Token token;
        int i = 0;
        while ((token = tokenizer.next()) != null) {
            assertEquals(tokens[i], token.termText());
            assertEquals("Wrong start offset", offsets[i][0], token
                    .startOffset());
            assertEquals("Wrong end offset", offsets[i][1], token.endOffset());
            ++i;
        }
        assertEquals(tokens.length, i);
    }
    
    @Test
    public void testTrigram()
    throws Exception
    {
        String str = "ルート選択があるプロセス[1]件目";
        StringReader reader = new StringReader(str);
        CJKTokenizer2 tokenizer = new CJKTokenizer2(reader, 3);
        Token token;
        
        String[] tokens = {
                "ルート",
                "ート選",
                "ト選択",
                "選択が",
                "択があ",
                "がある",
                "あるプ",
                "るプロ",
                "プロセ",
                "ロセス",
                "セス",
                "ス",
                "1",
                "件目",
                "目"
        };

        int[][] offsets = {
                { 0, 3 },   // ルート
                { 1, 4 },   // ート選
                { 2, 5 },   // ト選択
                { 3, 6 },   // 選択が
                { 4, 7 },   // 択があ
                { 5, 8 },   // がある
                { 6, 9 },   // あるプ
                { 7, 10 },  // るプロ
                { 8, 11 },  // プロセ
                { 9, 12 },  // ロセス
                { 10, 12 }, // セス
                { 11, 12 }, // ス
                { 13, 14 }, // 1
                { 15, 17 }, // 件目
                { 16, 17 }  // 目
        };

        int i = 0;
        while ((token = tokenizer.next()) != null) {
            assertEquals(tokens[i], token.termText());
            assertEquals("Wrong start offset", offsets[i][0], token
                    .startOffset());
            assertEquals("Wrong end offset", offsets[i][1], token
                    .endOffset());
            ++i;
        }
        assertEquals(tokens.length, i);
    }
    
    @Test
    public void testTokenizeHnakakuKana()
    {
        try {
            String str = "ﾎﾝｼﾞﾂﾊ､ﾊﾝﾍﾟﾝｦｼｮｸｼﾀ｡";
            StringReader reader = new StringReader(str);
            CJKTokenizer2 tokenizer = new CJKTokenizer2(reader);
            Token token;
            
            int i = 0;
            while ((token = tokenizer.next()) != null) {
                System.out.println(token.toString());
            }
            
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testTokenize()
    {
        try {
            String str = "this_ＢＯＯＫ’s落丁、乱丁  はaBCd.defお取替えします。";
            StringReader reader = new StringReader(str);
            CJKTokenizer2 tokenizer = new CJKTokenizer2(reader);
            Token token;

            String[] tokens = {
                    "this_book",
                    "s",
                    "落丁",
                    "丁",
                    "乱丁",
                    "丁",
                    "は",
                    "abcd",
                    "def",
                    "お取",
                    "取替",
                    "替え",
                    "えし",
                    "しま",
                    "ます",
                    "す"
            };

            int[][] offsets = {
                    { 0, 9 }, // this_book
                    { 10, 11 }, // s
                    { 11, 13 }, // 落丁
                    { 12, 13 }, // 丁
                    { 14, 16 }, // 乱丁
                    { 15, 16 }, // 丁
                    { 18, 19 }, // は
                    { 19, 23 }, // abcd
                    { 24, 27 }, // def
                    { 27, 29 }, // お取
                    { 28, 30 }, // 取替
                    { 29, 31 }, // 替え
                    { 30, 32 }, // えし
                    { 31, 33 }, // しま
                    { 32, 34 }, // ます
                    { 33, 34 } // す
            };

            int i = 0;
            while ((token = tokenizer.next()) != null) {
                assertEquals(tokens[i], token.termText());
                assertEquals("Wrong start offset", offsets[i][0], token
                        .startOffset());
                assertEquals("Wrong end offset", offsets[i][1], token
                        .endOffset());
                ++i;
            }
            assertEquals(tokens.length, i);
        } catch (Exception e) {
            fail(e.toString());
        }
    }
}
