package net.moraleboost.lucene.analysis.ja;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

public class CJKAnalyzer2 extends Analyzer
{
    private int ngram = CJKTokenizer2.DEFAULT_NGRAM;

    public CJKAnalyzer2()
    {
        super();
    }

    public CJKAnalyzer2(int ngram)
    {
        super();
        this.ngram = ngram;
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new CJKTokenizer2(reader, ngram);
    }
}