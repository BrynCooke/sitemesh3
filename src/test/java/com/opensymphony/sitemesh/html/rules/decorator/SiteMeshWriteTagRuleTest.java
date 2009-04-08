package com.opensymphony.sitemesh.html.rules.decorator;

import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.InMemoryContent;
import com.opensymphony.sitemesh.ContextStub;
import com.opensymphony.sitemesh.tagprocessor.TagProcessor;
import junit.framework.TestCase;

import java.io.IOException;
import java.nio.CharBuffer;

/**
 * @author Joe Walnes
 */
public class SiteMeshWriteTagRuleTest extends TestCase {

    public void testWritesTheProperty() throws IOException {
        Content content = new InMemoryContent();
        content.addProperty("foo", "This is the <foo> property.");
        content.addProperty("bar.x", "BAR");
        ContextStub context = new ContextStub();
        context.setContentToMerge(content);

        String in = "Hello <sitemesh:write property='foo'/> <sitemesh:write property='bar.x'/>!";
        TagProcessor tagProcessor = new TagProcessor(CharBuffer.wrap(in));
        tagProcessor.addRule(new SiteMeshWriteTagRule(context));
        tagProcessor.process();
        CharSequence out = tagProcessor.getDefaultBufferContents();

        assertEquals("Hello This is the <foo> property. BAR!", out.toString());
    }

    public void testRemovesTagContents() throws IOException {
        Content content = new InMemoryContent();
        ContextStub context = new ContextStub();
        context.setContentToMerge(content);

        String in = "Hello <sitemesh:write property='notfound'/> <sitemesh:write property='found.not'/>!";
        TagProcessor tagProcessor = new TagProcessor(CharBuffer.wrap(in));
        tagProcessor.addRule(new SiteMeshWriteTagRule(context));
        tagProcessor.process();
        CharSequence out = tagProcessor.getDefaultBufferContents();

        assertEquals("Hello  !", out.toString());
    }

    public void testSkipsMissingProperties() throws IOException {
        Content content = new InMemoryContent();
        ContextStub context = new ContextStub();
        content.addProperty("found", "FOUND");
        context.setContentToMerge(content);

        String in = "Hello <sitemesh:write property='found'>BAD</sitemesh:write>" +
                " <sitemesh:write property='notfound'>BAD</sitemesh:write>!";
        TagProcessor tagProcessor = new TagProcessor(CharBuffer.wrap(in));
        tagProcessor.addRule(new SiteMeshWriteTagRule(context));
        tagProcessor.process();
        CharSequence out = tagProcessor.getDefaultBufferContents();

        assertEquals("Hello FOUND !", out.toString());
    }

}