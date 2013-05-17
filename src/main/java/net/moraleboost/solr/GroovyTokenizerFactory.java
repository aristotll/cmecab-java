package net.moraleboost.solr;

import groovy.lang.GroovyClassLoader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class GroovyTokenizerFactory extends TokenizerFactory
{
    private File file;
    private boolean recompile;
    private GroovyClassLoader classLoader;
    private Map<String, String> arguments;

    public GroovyTokenizerFactory(Map<String, String> args)
    {
        super(args);
        init(args);
    }

    protected void init(Map<String, String> args)
    {
        String srcFile = args.get("file");
        String recompile = args.get("recompile");
        String encoding = args.get("encoding");
        String classpath = args.get("classpath");
        this.arguments = new HashMap<String, String>(args);

        if (srcFile == null) {
            throw new RuntimeException("File not specified.");
        } else {
            this.file = new File(srcFile);
        }

        CompilerConfiguration config = new CompilerConfiguration();

        if (recompile != null) {
            this.recompile = Boolean.parseBoolean(recompile);
        }
        if (encoding != null) {
            config.setSourceEncoding(encoding);
        }
        if (classpath != null) {
            config.setClasspath(classpath);
        }

        this.classLoader = new GroovyClassLoader(getClass().getClassLoader(), config);

        try {
            classLoader.parseClass(this.file);
        } catch (IOException e) {
            throw new RuntimeException("Can't parse class.", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Tokenizer create(AttributeSource.AttributeFactory factory, Reader input)
    {
        try {
            if (recompile) {
                classLoader.clearCache();
            }
            Class cls = classLoader.parseClass(this.file);
            Constructor ctor = cls.getConstructor(AttributeSource.AttributeFactory.class, Reader.class, Map.class);
            return (Tokenizer)ctor.newInstance(factory, input, arguments);
        } catch (IOException e) {
            throw new RuntimeException("Can't parse class.", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Can't instantiate class.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't instantiate class.", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Can't instantiate class.", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Can't instantiate class.", e);
        }
    }
}