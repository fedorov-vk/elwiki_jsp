/*
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
 */
package org.apache.wiki.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.LinkCollector;
import org.apache.wiki.StringTransmutator;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.parser0.Heading;
import org.apache.wiki.parser0.HeadingListener;
import org.apache.wiki.parser0.LinkParser;
import org.apache.wiki.parser0.MarkupParser;
import org.apache.wiki.parser0.ParseException;
import org.apache.wiki.parser0.WikiDocument;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.util.XmlUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki_data.WikiPage;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.IllegalDataException;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Verifier;

/**
 *  Parses JSPWiki-style markup into a WikiDocument DOM tree.  This class is the
 *  heart and soul of JSPWiki : make sure you test properly anything that is added,
 *  or else it breaks down horribly.
 *
 *  @since  2.4
 */
public class JSPWikiMarkupParser extends MarkupParser {

    //protected static final int              READ          = 0;
    //protected static final int              EDIT          = 1;
    //protected static final int              EMPTY         = 2;  // Empty message
//    protected static final int              LOCAL         = 3;
//    protected static final int              LOCALREF      = 4;
//    protected static final int              IMAGE         = 5;
//    protected static final int              EXTERNAL      = 6;
//    protected static final int              INTERWIKI     = 7;
//    protected static final int              IMAGELINK     = 8;
//    protected static final int              IMAGEWIKILINK = 9;
//    protected static final int              ATTACHMENT    = 10;

	public enum LinkType {//@formatter:off
		EMPTY(""), // Empty message
		CMD(CLASS_WIKIPAGE),
		READ(CLASS_WIKIPAGE),
		//:FVK: READ_ID(CLASS_WIKIPAGE),
		CREATE(CLASS_CREATEPAGE),
		LOCAL(CLASS_FOOTNOTE),
		LOCALREF(CLASS_FOOTNOTE_REF),
		IMAGE(""),
		EXTERNAL(CLASS_EXTERNAL),
		INTERWIKI(CLASS_INTERWIKI),
		IMAGELINK(CLASS_EXTERNAL),
		IMAGEWIKILINK(CLASS_WIKIPAGE),
		ATTACHMENT(CLASS_ATTACHMENT);//@formatter:on

		private String classType;

		private LinkType(String classType) {
			this.classType = classType;
		}

		public String getClassType() {
			return this.classType;
		}
	};

    private static final Logger log = Logger.getLogger( JSPWikiMarkupParser.class );

    private boolean        m_isbold       = false;
    private boolean        m_isitalic     = false;
    private boolean        m_istable      = false;
    private boolean        m_isPre        = false;
    private boolean        m_isEscaping   = false;
    private boolean        m_isdefinition = false;
    private boolean        m_isPreBlock   = false;

    /** Contains style information, in multiple forms. */
    private Stack<Boolean> m_styleStack   = new Stack<>();

     // general list handling
    private int            m_genlistlevel = 0;
    private StringBuilder  m_genlistBulletBuffer = new StringBuilder(10);  // stores the # and * pattern
    private boolean        m_allowPHPWikiStyleLists = true;

    private boolean        m_isOpenParagraph = false;

    /** Parser for extended link functionality. */
    private LinkParser     m_linkParser = new LinkParser();

    /** Keeps track of any plain text that gets put in the Text nodes */
    private StringBuilder  m_plainTextBuf = new StringBuilder(20);

    private Element        m_currentElement;

    /** Keep track of duplicate header names.  */
    private Map<String, Integer>   m_titleSectionCounter = new HashMap<>();

    /** If true, then generate special output for wysiwyg editing in certain cases */
    private boolean                m_wysiwygEditorMode     = false;

    /** If true, consider URIs that have no brackets as well. */
    // FIXME: Currently reserved, but not used.
    private boolean                m_plainUris           = false;

    /** If true, all outward links use a small link image. */
    private boolean                m_useOutlinkImage     = true;

    private boolean                m_useAttachmentImage  = true;

    /** If true, allows raw HTML. */
    private boolean                m_allowHTML           = false;

    private PatternCompiler        m_compiler = new Perl5Compiler();

    private int                    m_rowNum              = 1;

    private Heading                m_lastHeading         = null;
    
    private AccountRegistry accountRegistry;
    
    private AuthorizationManager authorizationManager;

	private AttachmentManager attachmentManager;

	private VariableManager variableManager;

    /**
     *  Creates a markup parser.
     *
     *  @param context The WikiContext which controls the parsing
     *  @param in Where the data is read from.
     */
    public JSPWikiMarkupParser( final WikiContext context, final Reader in )
    {
        super( context, in );
        initialize();
        accountRegistry = this.m_engine.getManager(AccountRegistry.class);
        authorizationManager = this.m_engine.getManager(AuthorizationManager.class);
        attachmentManager = this.m_engine.getManager(AttachmentManager.class);
        variableManager = this.m_engine.getManager(VariableManager.class);
    }

    // FIXME: parsers should be pooled for better performance.
    private void initialize() {
        initInlineImagePatterns();

        final Boolean wysiwygVariable = m_context.getVariable( WikiContext.VAR_WYSIWYG_EDITOR_MODE );
        if( wysiwygVariable != null ) {
            m_wysiwygEditorMode = wysiwygVariable;
        }

        m_plainUris          = m_context.getBooleanWikiProperty( PROP_PLAINURIS, m_plainUris );
        m_useOutlinkImage    = m_context.getBooleanWikiProperty( PROP_USEOUTLINKIMAGE, m_useOutlinkImage );
        m_useAttachmentImage = m_context.getBooleanWikiProperty( PROP_USEATTACHMENTIMAGE, m_useAttachmentImage );
        m_allowHTML          = m_context.getBooleanWikiProperty( PROP_ALLOWHTML, m_allowHTML );

        if( accountRegistry == null || authorizationManager == null ) {
            disableAccessRules();
        }

        //:FVK: m_context.getPage().setHasMetadata();
    }

    /**
     *  Calls a transmutator chain.
     *
     *  @param list Chain to call
     *  @param text Text that should be passed to the mutate() method of each of the mutators in the chain.
     *  @return The result of the mutation.
     */
    protected String callMutatorChain( final Collection< StringTransmutator > list, String text ) {
        if( list == null || list.size() == 0 ) {
            return text;
        }

        for( final StringTransmutator m : list ) {
            text = m.mutate( m_context, text );
        }

        return text;
    }

    /**
     * Calls a link collectors chain.
     * 
     * @param collectors Chain to call.
     * @param text Text that should be passed to the collec() method of each of the collectors in the chain.
     */
    protected void collectLink(Collection< LinkCollector > collectors, String text) {
        if( collectors == null ) {
        	return;
        }

        for( final LinkCollector collector : collectors ) {
            text = collector.collect( text );
        }
	}
    
    /**
     * Calls the heading listeners.
     *
     * @param param A Heading object.
     */
    protected void callHeadingListenerChain( final Heading param ) {
        final List< HeadingListener > list = m_headingListenerChain;
        for( final HeadingListener h : list ) {
            h.headingAdded( m_context, param );
        }
    }

    /**
     *  Creates a JDOM anchor element.  Can be overridden to change the URL creation,
     *  if you really know what you are doing.
     *
     *  @param type One of the types above
     *  @param link URL to which to link to
     *  @param text Link text
     *  @param section If a particular section identifier is required.
     *  @return An A element.
     *  @since 2.4.78
     */
    protected Element createAnchor( final LinkType type, final String link, String text, String section)
    {
        text = escapeHTMLEntities( text );
        section = escapeHTMLEntities( section );
        final Element el = new Element("a");
        el.setAttribute("class", type.getClassType());
        el.setAttribute("href",link+section);
        el.addContent(text);
        return el;
    }

    /**
     * @param type
     * 			A Link type: IMAGEWIKILINK, ...
     * @param link
     * 			Field 'href' of tag &lt;a&gt; ... &lt;/a&gt;.
     * @param text
     * 			Text of tag &lt;a&gt;. Possibly is <code>null</code>, in this case text of linkRef is
	 *            substituted.
     * @param section
     * 			Possibly can be <code>null</code>.
     * @param attributes
     * 			An attributes of link.
     * @return
     */
    private Element makeLink(LinkType type, final String link, String text, String section, final Iterator< Attribute > attributes )
    {
        Element el = null;

        if( text == null ) text = link;

        text = callMutatorChain( m_linkMutators, text );

        section = (section != null) ? ("#"+section) : "";

        // Make sure we make a link name that can be accepted
        // as a valid URL.

        if( link.length() == 0 )
        {
            type = LinkType.EMPTY;
        }
        final ResourceBundle rb = Preferences.getBundle( m_context );

        //@formatter:off
		el = switch (type) {
    	/*:FVK: case READ_ID -> createAnchor( LinkType.READ, m_context.getURL( ContextEnum.PAGE_VIEWID.getRequestContext(), link), text, section ); */
		case CMD -> createAnchor(LinkType.CMD, m_context.getURL(link, ""), text, section);
		case READ -> createAnchor(LinkType.READ,
				m_context.getURL(ContextEnum.PAGE_VIEW.getRequestContext(), link), text, section);
		case CREATE -> createAnchor(LinkType.CREATE, m_context.getURL(WikiContext.PAGE_CREATE, link), text, "")
				.setAttribute("title", MessageFormat.format(rb.getString("markupparser.link.create"), text));
		case EMPTY -> new Element("u").addContent(text);
		//
		//  These two are for local references - footnotes and references to footnotes.
		//  We embed the page name (or whatever WikiContext gives us)
		//  to make sure the links are unique across Wiki.
		//
		case LOCALREF -> createAnchor(LinkType.LOCALREF,
				"#ref-" + m_context.getName() + "-" + link, "[" + text + "]", "");
		case LOCAL -> new Element("a").setAttribute("class", CLASS_FOOTNOTE)
				.setAttribute("name", "ref-" + m_context.getName() + "-" + link.substring(1))
				.addContent("[" + text + "]");
        //
        //  With the image, external and interwiki types we need to make sure nobody can put in Javascript
		//  or something else annoying into the links themselves.
		//  We do this by preventing a haxor from stopping the link name short with quotes in fillBuffer().
        //
		case IMAGE -> new Element("img").setAttribute("class", "inline")
				.setAttribute("src", link).setAttribute("alt", text);
		case IMAGELINK -> createAnchor(LinkType.IMAGELINK, text, "", "").addContent(
					new Element("img").setAttribute("class", "inline")
						.setAttribute("src", link).setAttribute("alt", text)
				);
		case IMAGEWIKILINK -> createAnchor(
				LinkType.IMAGEWIKILINK,
					m_context.getURL(ContextEnum.PAGE_VIEW.getRequestContext(), text),
					"", "")
				.addContent(
					new Element("img").setAttribute("class", "inline")
						.setAttribute("src", link).setAttribute("alt", text)
				);
		case EXTERNAL -> createAnchor(LinkType.EXTERNAL, link, text, section);
		case INTERWIKI -> createAnchor(LinkType.INTERWIKI, link, text, section);
		case ATTACHMENT -> {
			Element el1;
			final String attlink = m_context.getURL(ContextEnum.ATTACHMENT_DOGET.getRequestContext(), link);
			final String infolink = m_context.getURL(ContextEnum.PAGE_INFO.getRequestContext(), link);
			final String imglink = m_context.getURL(ContextEnum.PAGE_NONE.getRequestContext(), "images/attachment_small.png");
			el1 = createAnchor(LinkType.ATTACHMENT, attlink, text, "");

			if (attachmentManager.forceDownload(attlink)) {
				el1.setAttribute("download", "");
			}

			pushElement(el1);
			popElement(el1.getName());

			if (m_useAttachmentImage) {
				el1 = new Element("img").setAttribute("src", imglink);
				el1.setAttribute("border", "0");
				el1.setAttribute("alt", "(info)");

				el1 = new Element("a").setAttribute("href", infolink).addContent(el1);
				el1.setAttribute("class", "infolink");
			} else {
				el1 = null;
			}
			yield el1;
		}
		default -> throw new IllegalArgumentException("Unexpected value: " + type);
		};
		//@formatter:on
        
		if (el != null && attributes != null) {
			while (attributes.hasNext()) {
				final Attribute attr = attributes.next();
				if (attr != null) {
					el.setAttribute(attr);
				}
            }
        }

        if( el != null )
        {
            flushPlainText();
            m_currentElement.addContent( el );
        }

        return el;
    }

    /**
     *  These are all of the HTML 4.01 block-level elements.
     */
    private static final String[] BLOCK_ELEMENTS = {
        "address", "blockquote", "div", "dl", "fieldset", "form",
        "h1", "h2", "h3", "h4", "h5", "h6",
        "hr", "noscript", "ol", "p", "pre", "table", "ul"
    };

    private static boolean isBlockLevel( final String name )
    {
        return Arrays.binarySearch( BLOCK_ELEMENTS, name ) >= 0;
    }

    /**
     *  This method peeks ahead in the stream until EOL and returns the result.
     *  It will keep the buffers untouched.
     *
     *  @return The string from the current position to the end of line.
     */

    // FIXME: Always returns an empty line, even if the stream is full.
    private String peekAheadLine()
        throws IOException
    {
        final String s = readUntilEOL().toString();

        if( s.length() > PUSHBACK_BUFFER_SIZE )
        {
            log.warn("Line is longer than maximum allowed size ("+PUSHBACK_BUFFER_SIZE+" characters.  Attempting to recover...");
            pushBack( s.substring(0,PUSHBACK_BUFFER_SIZE-1) );
        }
        else
        {
            try
            {
                pushBack( s );
            }
            catch( final IOException e )
            {
                log.warn("Pushback failed: the line is probably too long.  Attempting to recover.");
            }
        }
        return s;
    }

    private int flushPlainText()
    {
        final int numChars = m_plainTextBuf.length();

        if( numChars > 0 )
        {
            String buf;

            if( !m_allowHTML )
            {
                buf = escapeHTMLEntities(m_plainTextBuf.toString());
            }
            else
            {
                buf = m_plainTextBuf.toString();
            }

            //
            //  We must first empty the buffer because the side effect, 
            //  of calling some routine is to call this routine. (for example, was makeCamelCaseLink())
            //
            m_plainTextBuf = new StringBuilder(20);

            try
            {
				// just add the elements
				m_currentElement.addContent(buf);
            }
            catch( final IllegalDataException e )
            {
                //
                // Sometimes it's possible that illegal XML chars is added to the data.
                // Here we make sure it does not stop parsing.
                //
                m_currentElement.addContent( makeError(cleanupSuspectData( e.getMessage() )) );
            }
        }

        return numChars;
    }

    /**
     *  Escapes XML entities in a HTML-compatible way (i.e. does not escape
     *  entities that are already escaped).
     *
     *  @param buf
     *  @return An escaped string.
     */
    private String escapeHTMLEntities( final String buf)
    {
        final StringBuilder tmpBuf = new StringBuilder( buf.length() + 20 );

        for( int i = 0; i < buf.length(); i++ )
        {
            final char ch = buf.charAt(i);

            if( ch == '<' )
            {
                tmpBuf.append("&lt;");
            }
            else if( ch == '>' )
            {
                tmpBuf.append("&gt;");
            }
            else if( ch == '\"' )
            {
                tmpBuf.append("&quot;");
            }
            else if( ch == '&' )
            {
                //
                //  If the following is an XML entity reference (&#.*;) we'll
                //  leave it as it is; otherwise we'll replace it with an &amp;
                //

                boolean isEntity = false;
                final StringBuilder entityBuf = new StringBuilder();

                if( i < buf.length() -1 )
                {
                    for( int j = i; j < buf.length(); j++ )
                    {
                        final char ch2 = buf.charAt(j);

                        if( Character.isLetterOrDigit( ch2 ) || (ch2 == '#' && j == i+1) || ch2 == ';' || ch2 == '&' )
                        {
                            entityBuf.append(ch2);

                            if( ch2 == ';' )
                            {
                                isEntity = true;
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                }

                if( isEntity )
                {
                    tmpBuf.append( entityBuf );
                    i = i + entityBuf.length() - 1;
                }
                else
                {
                    tmpBuf.append("&amp;");
                }

            }
            else
            {
                tmpBuf.append( ch );
            }
        }

        return tmpBuf.toString();
    }

    private Element pushElement( final Element e )
    {
        flushPlainText();
        m_currentElement.addContent( e );
        m_currentElement = e;

        return e;
    }

    private Element addElement( final Content e )
    {
        if( e != null )
        {
            flushPlainText();
            m_currentElement.addContent( e );
        }
        return m_currentElement;
    }

    /**
     *  All elements that can be empty by the HTML DTD.
     */
    //  Keep sorted.
    private static final String[] EMPTY_ELEMENTS = {
        "area", "base", "br", "col", "hr", "img", "input", "link", "meta", "p", "param"
    };

    /**
     *  Goes through the current element stack and pops all elements until this
     *  element is found - this essentially "closes" and element.
     *
     *  @param s
     *  @return The new current element, or null, if there was no such element in the entire stack.
     */
    private Element popElement( final String s )
    {
        final int flushedBytes = flushPlainText();

        Element currEl = m_currentElement;

        while( currEl.getParentElement() != null )
        {
            if( currEl.getName().equals(s) && !currEl.isRootElement() )
            {
                m_currentElement = currEl.getParentElement();

                //
                //  Check if it's okay for this element to be empty.  Then we will
                //  trick the JDOM generator into not generating an empty element,
                //  by putting an empty string between the tags.  Yes, it's a kludge
                //  but what'cha gonna do about it. :-)
                //

                if( flushedBytes == 0 && Arrays.binarySearch( EMPTY_ELEMENTS, s ) < 0 )
                {
                    currEl.addContent("");
                }

                return m_currentElement;
            }

            currEl = currEl.getParentElement();
        }

        return null;
    }


    /**
     *  Reads the stream until it meets one of the specified
     *  ending characters, or stream end.  The ending character will be left
     *  in the stream.
     */
    private String readUntil( final String endChars )
        throws IOException
    {
        final StringBuilder sb = new StringBuilder( 80 );
        int ch = nextToken();

        while( ch != -1 )
        {
            if( ch == '\\' )
            {
                ch = nextToken();
                if( ch == -1 )
                {
                    break;
                }
            }
            else
            {
                if( endChars.indexOf((char)ch) != -1 )
                {
                    pushBack( ch );
                    break;
                }
            }
            sb.append( (char) ch );
            ch = nextToken();
        }

        return sb.toString();
    }

    /**
     *  Reads the stream while the characters that have been specified are
     *  in the stream, returning then the result as a String.
     */
    private String readWhile( final String endChars )
        throws IOException
    {
        final StringBuilder sb = new StringBuilder( 80 );
        int ch = nextToken();

        while( ch != -1 )
        {
            if( endChars.indexOf((char)ch) == -1 )
            {
                pushBack( ch );
                break;
            }

            sb.append( (char) ch );
            ch = nextToken();
        }

        return sb.toString();
    }

    private JSPWikiMarkupParser m_cleanTranslator;

    /**
     *  Does a lazy init.  Otherwise, we would get into a situation where HTMLRenderer would try and boot a TranslatorReader before
     *  the TranslatorReader it is contained by is up.
     */
    private JSPWikiMarkupParser getCleanTranslator() {
        if( m_cleanTranslator == null ) {
        	HttpServletRequest servletRequest = m_context.getHttpRequest();
            final WikiContext dummyContext = Wiki.context().create( m_engine, servletRequest, m_context.getPage() );
            m_cleanTranslator = new JSPWikiMarkupParser( dummyContext, null );
            m_cleanTranslator.m_allowHTML = true;
        }

        return m_cleanTranslator;
    }
    /**
     *  Modifies the "hd" parameter to contain proper values.  Because
     *  an "id" tag may only contain [a-zA-Z0-9:_-], we'll replace the
     *  % after url encoding with '_'.
     *  <p>
     *  Counts also duplicate headings (= headings with similar name), and
     *  attaches a counter.
     * @throws IOException 
     */
    private String makeHeadingAnchor( final String baseName, String title, final Heading hd ) throws IOException {
        hd.m_titleText = title;
        title = MarkupParser.wikifyLink( title );
			hd.m_titleSection = this.m_engine.encodeName(title);

        if( m_titleSectionCounter.containsKey( hd.m_titleSection ) ) {
            final Integer count = m_titleSectionCounter.get( hd.m_titleSection ) + 1;
            m_titleSectionCounter.put( hd.m_titleSection, count );
            hd.m_titleSection += "-" + count;
        } else {
            m_titleSectionCounter.put( hd.m_titleSection, 1 );
        }

        hd.m_titleAnchor = "section-" + this.m_engine.encodeName( baseName ) + "-" + hd.m_titleSection;
        hd.m_titleAnchor = hd.m_titleAnchor.replace( '%', '_' );
        hd.m_titleAnchor = hd.m_titleAnchor.replace( '/', '_' );

        return hd.m_titleAnchor;
    }

    private String makeSectionTitle( String title ) {
        title = title.trim();
        try {
            final JSPWikiMarkupParser dtr = getCleanTranslator();
            dtr.setInputReader( new StringReader( title ) );
            final WikiDocument doc = dtr.parse();
            doc.setContext( m_context );

            return XmlUtil.extractTextFromDocument( doc );
        } catch( final IOException e ) {
            log.fatal("Title parsing not working", e );
            throw new InternalWikiException( "Xml text extraction not working as expected when cleaning title" + e.getMessage() , e );
        }
    }

    /**
     *  Returns XHTML for the heading.
     *
     *  @param level The level of the heading.  @see Heading
     *  @param title the title for the heading
     *  @param hd a List to which heading should be added
     *  @return An Element containing the heading
     * @throws IOException 
     */
    public Element makeHeading( final int level, final String title, final Heading hd ) throws IOException {
        final Element el;
        final String pageName = m_context.getPage().getName();
        final String outTitle = makeSectionTitle( title );
        hd.m_level = level;

        switch( level ) {
          case Heading.HEADING_SMALL:
            el = new Element( "h4" ).setAttribute("id",makeHeadingAnchor( pageName, outTitle, hd ) );
            break;

          case Heading.HEADING_MEDIUM:
            el = new Element( "h3" ).setAttribute("id",makeHeadingAnchor( pageName, outTitle, hd ) );
            break;

          case Heading.HEADING_LARGE:
            el = new Element( "h2" ).setAttribute("id",makeHeadingAnchor( pageName, outTitle, hd ) );
            break;

          default:
            throw new InternalWikiException( "Illegal heading type " + level );
        }

        return el;
    }

    /** Holds the image URL for the duration of this parser */
    private String m_outlinkImageURL = null;

    /**
     *  Returns an element for the external link image (out.png).  However,
     *  this method caches the URL for the lifetime of this MarkupParser,
     *  because it's commonly used, and we'll end up with possibly hundreds
     *  our thousands of references to it...  It's a lot faster, too.
     *
     *  @return  An element containing the HTML for the outlink image.
     */
    private Element outlinkImage()
    {
        Element el = null;

        if( m_useOutlinkImage )
        {
            if( m_outlinkImageURL == null )
            {
                m_outlinkImageURL = m_context.getURL( ContextEnum.PAGE_NONE.getRequestContext(), OUTLINK_IMAGE );
            }

            el = new Element( "img" ).setAttribute( "class", OUTLINK );
            el.setAttribute( "src", m_outlinkImageURL );
            el.setAttribute( "alt","" );
        }

        return el;
    }

    /**
     *  Image links are handled differently:
     *  1. If the text is a WikiName of an existing page,
     *     it gets linked.
     *  2. If the text is an external link, then it is inlined.
     *  3. Otherwise it becomes an ALT text.
     *
     *  @param reallink The link to the image.
     *  @param link     Link text portion, may be a link to somewhere else.
     *  @param hasLinkText If true, then the defined link had a link text available.
     *                  This means that the link text may be a link to a wiki page,
     *                  or an external resource.
     */

    // FIXME: isExternalLink() is called twice.
    private Element handleImageLink( final String reallink, final String link, final boolean hasLinkText )
    {
        final String possiblePage = MarkupParser.cleanLink( link );

        if( m_linkParsingOperations.isExternalLink( link ) && hasLinkText )
        {
            return makeLink( LinkType.IMAGELINK, reallink, link, null, null );
        }
        else if( m_linkParsingOperations.linkExists( possiblePage ) && hasLinkText )
        {
            // System.out.println("Orig="+link+", Matched: "+matchedLink);
        	collectLink( m_localLinkCollectors, possiblePage );

            return makeLink( LinkType.IMAGEWIKILINK, reallink, link, null, null );
        }
        else
        {
            return makeLink( LinkType.IMAGE, reallink, link, null, null );
        }
    }

    /**
     *  Handles metadata setting [{SET foo=bar}]
     */
    private Element handleMetadata( final String link ) {
        if( m_wysiwygEditorMode ) {
            m_currentElement.addContent( "[" + link + "]" );
        }

        try {
            final String args = link.substring( link.indexOf(' '), link.length()-1 );
            final String name = args.substring( 0, args.indexOf('=') ).trim();
            String val  = args.substring( args.indexOf('=')+1 ).trim();

            if( val.startsWith("'") ) {
                val = val.substring( 1 );
            }
            if( val.endsWith("'") ) {
                val = val.substring( 0, val.length()-1 );
            }

            // log.debug("SET name='"+name+"', value='"+val+"'.");

            if( name.length() > 0 && val.length() > 0 ) {
                val = variableManager.expandVariables( m_context, val );
                m_context.getPage().setAttribute(name, val );
            }
        } catch( final Exception e ) {
            final ResourceBundle rb = Preferences.getBundle( m_context );
            return makeError( MessageFormat.format( rb.getString( "markupparser.error.invalidset" ), link ) );
        }

        return m_currentElement;
    }

    /**
     *  Emits a processing instruction that will disable markup escaping. This is
     *  very useful if you want to emit HTML directly into the stream.
     *
     */
    private void disableOutputEscaping() {
        addElement( new ProcessingInstruction( Result.PI_DISABLE_OUTPUT_ESCAPING, "" ) );
    }

    /**
     *  Gobbles up all hyperlinks that are encased in square brackets.
     */
    private Element handleHyperlinks( String linkText, final int pos ) {
        final ResourceBundle rb = Preferences.getBundle( m_context );
        final StringBuilder sb = new StringBuilder( linkText.length() + 80 );

        if( m_linkParsingOperations.isMetadata( linkText ) ) {
            return handleMetadata( linkText );
        }

        if( m_linkParsingOperations.isPluginLink( linkText ) ) {
        	PluginContent pluginContent = null;
            try {
            	pluginContent = PluginContent.parsePluginLine( m_context, linkText, pos );

                // This might sometimes fail, especially if there is something which looks like a plugin invocation but is really not.
                if( pluginContent != null ) {
                    addElement( pluginContent );
                    pluginContent.executeParse( m_context );
                }
            } catch( final PluginException e ) {
				log.info(m_context.getRealPage().getWiki() + " : " + m_context.getRealPage().getName()
						+ " - Failed to insert plugin: " + e.getMessage());

                if (pluginContent != null) {
					Element parentEl = pluginContent.getParent();
					if (parentEl != null) {
						// Removing the PluginContent element so it is not cause error. 
						parentEl.removeContent(pluginContent);
						if (!m_wysiwygEditorMode) {
							// Adding 'red' SPAN that will contatin an error message,
							// that will be made by PluginContent itself.
							parentEl.addContent(makeError(pluginContent));
						}
					}
				} else {
					// The problem was not detected. it is not recognized at this level.
	                if( !m_wysiwygEditorMode ) {
	   					String pattern = Preferences.getBundle(m_context).getString("markupparser.error.plugininsertion");
						return addElement(makeError(MessageFormat.format(pattern, //
								m_context.getRealPage().getWiki(), //
								m_context.getRealPage().getName(), //
								e.getMessage())));
					}
				}
			}

            return m_currentElement;
        }

		try {
			final LinkParser.Link link = m_linkParser.parse(linkText);
			linkText = link.getText();
			String linkRef = link.getReference();

			//
			// Yes, we now have the components separated.
			// linktext = the text the link should have
			// linkref = the url or @pageID, name of page attachment.
			//
			// In many cases these are the same. [linktext|linkref].
			//
			if (m_linkParsingOperations.isVariableLink(linkText)) {
				final Content el = new VariableContent(linkText);

				addElement(el);
			} else if (m_linkParsingOperations.isExternalLink(linkRef)) {
				// It's an external link, out of this Wiki

				collectLink(m_externalLinkCollectors, linkRef);

				if (m_linkParsingOperations.isImageLink(linkRef, isImageInlining(), getInlineImagePatterns())) {
					handleImageLink(linkRef, linkText, link.hasReference());
				} else {
					makeLink(LinkType.EXTERNAL, linkRef, linkText, null, link.getAttributes());
					addElement(outlinkImage());
				}
			} else if (link.isInterwikiLink()) {
				// It's an interwiki link; InterWiki links also get added to external link chain after the links
				// have been resolved.

				// FIXME: There is an interesting issue here: We probably should
				// URLEncode the wikiPage, but we can't since some of the
				// Wikis use slashes (/), which won't survive URLEncoding.
				// Besides, we don't know which character set the other Wiki
				// is using, so you'll have to write the entire name as it appears
				// in the URL. Bugger.

				final String extWiki = link.getExternalWiki();
				final String wikiPage = link.getExternalWikiPage();

				if (m_wysiwygEditorMode) {
					makeLink(LinkType.INTERWIKI, extWiki + ":" + wikiPage, linkText, null, link.getAttributes());
				} else {
					String urlReference = m_engine.getWikiConfiguration().getInterWikiURL(extWiki);

					if (urlReference != null) {
						urlReference = TextUtil.replaceString(urlReference, "%s", wikiPage);
						collectLink(m_externalLinkCollectors, urlReference);

						if (m_linkParsingOperations.isImageLink(urlReference, isImageInlining(),
								getInlineImagePatterns())) {
							handleImageLink(urlReference, linkText, link.hasReference());
						} else {
							makeLink(LinkType.INTERWIKI, urlReference, linkText, null, link.getAttributes());
						}

						if (m_linkParsingOperations.isExternalLink(urlReference)) {
							addElement(outlinkImage());
						}
					} else {
						final Object[] args = { escapeHTMLEntities(extWiki) };

						addElement(makeError(
								MessageFormat.format(rb.getString("markupparser.error.nointerwikiref"), args)));
					}
				}
			} else if (linkRef.startsWith("#")) {
				// It defines a local footnote
				makeLink(LinkType.LOCAL, linkRef, linkText, null, link.getAttributes());
			} else if (TextUtil.isNumber(linkRef)) {
				// It defines a reference to a local footnote
				makeLink(LinkType.LOCALREF, linkRef, linkText, null, link.getAttributes());
			} else if (linkRef.matches("@cmd\\..+")) {
				// Internal wiki link (by wiki command).
				// Working up link of ElWiki format.

				// LinkType linkType = LinkType.EMPTY;

				String cmdName = linkRef.substring(5); // :FVK: Workaround - length of prefix '@cmd.'
				String nameWikiContext = ContextEnum.getWikiContextName("cmd." + cmdName);
				makeLink(LinkType.CMD, nameWikiContext, linkText, null, link.getAttributes());
			} else if (linkRef.matches("^\s*?@.+")) {
				// Internal wiki link (by pageId, which can be unknown).
				// Working up link of ElWiki format.
				LinkType linkType = LinkType.EMPTY;
				String pageId = linkRef.trim().substring(1); // :FVK: Workaround: =1 is length of prefix '@'
				String pageName = this.m_context.getPageName(pageId);
				if (pageName != null) {
					if (!link.hasReference()) {
						linkText = pageName;
					}
					linkRef = pageId;
					linkType = LinkType.READ;
					collectLink(this.m_localLinkCollectors, linkRef);
				} else {
					// page creation required.
					WikiPage basePage = m_context.getRealPage();
					linkRef = (basePage != null) ? basePage.getId() : "";
					linkType = LinkType.CREATE;
					collectLink(this.unknownPagesCollectors, linkText);
				}
				makeLink(linkType, linkRef, linkText, null, link.getAttributes());
			} else {
				final int hashMark;

				// Internal wiki link, but is it an attachment link?
				String attachment = attachmentManager.getAttachmentName(m_context, linkRef);
				if (attachment != null) {
					collectLink(m_attachmentLinkCollectors, attachment);

					if (m_linkParsingOperations.isImageLink(linkRef, isImageInlining(), getInlineImagePatterns())) {
						attachment = m_context.getURL(ContextEnum.ATTACHMENT_DOGET.getRequestContext(), attachment);
						attachment += "?pageId=" + m_context.getPageId();
						sb.append(handleImageLink(attachment, linkText, link.hasReference()));
					} else {
						attachment += "?pageId=" + m_context.getPageId();
						makeLink(LinkType.ATTACHMENT, attachment, linkText, null, link.getAttributes());
					}
				} else if ((hashMark = linkRef.indexOf('#')) != -1) {
					// It's an internal Wiki link, but to a named section

					final String namedSection = linkRef.substring(hashMark + 1);
					linkRef = linkRef.substring(0, hashMark);

					linkRef = MarkupParser.cleanLink(linkRef);

					collectLink(m_localLinkCollectors, linkRef);

					final String matchedLink = m_linkParsingOperations.linkIfExists(linkRef);
					if (matchedLink != null) {
						String sectref = "section-"
								+ this.m_engine.encodeName(matchedLink + "-" + wikifyLink(namedSection));
						sectref = sectref.replace('%', '_');
						makeLink(LinkType.READ, matchedLink, linkText, sectref, link.getAttributes());
					} else {
						makeLink(LinkType.CREATE, linkRef, linkText, null, link.getAttributes());
					}
				} else {
					// It's an internal Wiki link
					linkRef = MarkupParser.cleanLink(linkRef);

					collectLink(m_localLinkCollectors, linkRef);

					final String matchedLink = m_linkParsingOperations.linkIfExists(linkRef);
					if (matchedLink != null) {
						makeLink(LinkType.READ, matchedLink, linkText, null, link.getAttributes());
					} else {
						makeLink(LinkType.CREATE, linkRef, linkText, null, link.getAttributes());
					}
				}
			}
		} catch (final ParseException | IOException e) {
			log.info("Parser failure: ", e);
			final Object[] args = { e.getMessage() };
			addElement(makeError(MessageFormat.format(rb.getString("markupparser.error.parserfailure"), args)));
		}
		return m_currentElement;
	}

	/**
     *  Pushes back any string that has been read.  It will obviously
     *  be pushed back in a reverse order.
     *
     *  @since 2.1.77
     */
    private void pushBack( final String s )
        throws IOException
    {
        for( int i = s.length()-1; i >= 0; i-- )
        {
            pushBack( s.charAt(i) );
        }
    }

    private Element handleBackslash()
        throws IOException
    {
        final int ch = nextToken();

        if( ch == '\\' )
        {
            final int ch2 = nextToken();

            if( ch2 == '\\' )
            {
                pushElement( new Element("br").setAttribute("clear","all"));
                return popElement("br");
            }

            pushBack( ch2 );

            pushElement( new Element("br") );
            return popElement("br");
        }

        pushBack( ch );

        return null;
    }

    private Element handleUnderscore()
        throws IOException
    {
        final int ch = nextToken();
        Element el = null;

        if( ch == '_' )
        {
            if( m_isbold )
            {
                el = popElement("b");
            }
            else
            {
                el = pushElement( new Element("b") );
            }
            m_isbold = !m_isbold;
        }
        else
        {
            pushBack( ch );
        }

        return el;
    }


    /**
     *  For example: italics.
     */
    private Element handleApostrophe()
        throws IOException
    {
        final int ch = nextToken();
        Element el = null;

        if( ch == '\'' )
        {
            if( m_isitalic )
            {
                el = popElement("i");
            }
            else
            {
                el = pushElement( new Element("i") );
            }
            m_isitalic = !m_isitalic;
        }
        else
        {
            pushBack( ch );
        }

        return el;
    }

    private Element handleOpenbrace( final boolean isBlock )
        throws IOException
    {
        final int ch = nextToken();

        if( ch == '{' )
        {
            final int ch2 = nextToken();

            if( ch2 == '{' )
            {
                m_isPre = true;
                m_isEscaping = true;
                m_isPreBlock = isBlock;

                if( isBlock )
                {
                    startBlockLevel();
                    return pushElement( new Element("pre") );
                }

                return pushElement( new Element("span").setAttribute("class","inline-code") );
            }

            pushBack( ch2 );

            return pushElement( new Element("tt") );
        }

        pushBack( ch );

        return null;
    }

    /**
     *  Handles both }} and }}}
     */
    private Element handleClosebrace()
        throws IOException
    {
        final int ch2 = nextToken();

        if( ch2 == '}' )
        {
            final int ch3 = nextToken();

            if( ch3 == '}' )
            {
                if( m_isPre )
                {
                    if( m_isPreBlock )
                    {
                        popElement( "pre" );
                    }
                    else
                    {
                        popElement( "span" );
                    }

                    m_isPre = false;
                    m_isEscaping = false;
                    return m_currentElement;
                }

                m_plainTextBuf.append("}}}");
                return m_currentElement;
            }

            pushBack( ch3 );

            if( !m_isEscaping )
            {
                return popElement("tt");
            }
        }

        pushBack( ch2 );

        return null;
    }

    private Element handleDash()
        throws IOException
    {
        int ch = nextToken();

        if( ch == '-' )
        {
            final int ch2 = nextToken();

            if( ch2 == '-' )
            {
                final int ch3 = nextToken();

                if( ch3 == '-' )
                {
                    // Empty away all the rest of the dashes.
                    // Do not forget to return the first non-match back.
                    do
                    {
                        ch = nextToken();
                    }
                    while ( ch == '-' );

                    pushBack(ch);
                    startBlockLevel();
                    pushElement( new Element("hr") );
                    return popElement( "hr" );
                }

                pushBack( ch3 );
            }
            pushBack( ch2 );
        }

        pushBack( ch );

        return null;
    }

    private Element handleHeading()
        throws IOException
    {
        Element el = null;

        final int ch  = nextToken();

        final Heading hd = new Heading();

        if( ch == '!' )
        {
            final int ch2 = nextToken();

            if( ch2 == '!' )
            {
                final String title = peekAheadLine();

                el = makeHeading( Heading.HEADING_LARGE, title, hd);
            }
            else
            {
                pushBack( ch2 );
                final String title = peekAheadLine();
                el = makeHeading( Heading.HEADING_MEDIUM, title, hd );
            }
        }
        else
        {
            pushBack( ch );
            final String title = peekAheadLine();
            el = makeHeading( Heading.HEADING_SMALL, title, hd );
        }

        callHeadingListenerChain( hd );

        m_lastHeading = hd;

        if( el != null ) pushElement(el);

        return el;
    }

    /**
     *  Reads the stream until the next EOL or EOF.  Note that it will also read the
     *  EOL from the stream.
     */
    private StringBuilder readUntilEOL()
        throws IOException
    {
        int ch;
        final StringBuilder buf = new StringBuilder( 256 );

        while( true )
        {
            ch = nextToken();

            if( ch == -1 )
                break;

            buf.append( (char) ch );

            if( ch == '\n' )
                break;
        }
        return buf;
    }

    /** Controls whether italic is restarted after a paragraph shift */

    private boolean m_restartitalic = false;
    private boolean m_restartbold   = false;

    private boolean m_newLine;

    /**
     *  Starts a block level element, therefore closing
     *  a potential open paragraph tag.
     */
    private void startBlockLevel()
    {
        // These may not continue over block level limits in XHTML

        popElement("i");
        popElement("b");
        popElement("tt");

        if( m_isOpenParagraph )
        {
            m_isOpenParagraph = false;
            popElement("p");
            m_plainTextBuf.append("\n"); // Just small beautification
        }

        m_restartitalic = m_isitalic;
        m_restartbold   = m_isbold;

        m_isitalic = false;
        m_isbold   = false;
    }

    private static String getListType( final char c )
    {
        if( c == '*' )
        {
            return "ul";
        }
        else if( c == '#' )
        {
            return "ol";
        }
        throw new InternalWikiException("Parser got faulty list type: "+c);
    }
    /**
     *  Like original handleOrderedList() and handleUnorderedList()
     *  however handles both ordered ('#') and unordered ('*') mixed together.
     */

    // FIXME: Refactor this; it's a bit messy.

    private Element handleGeneralList()
        throws IOException
    {
         startBlockLevel();

         String strBullets = readWhile( "*#" );
         // String strBulletsRaw = strBullets;      // to know what was original before phpwiki style substitution
         final int numBullets = strBullets.length();

         // override the beginning portion of bullet pattern to be like the previous
         // to simulate PHPWiki style lists

         if(m_allowPHPWikiStyleLists)
         {
             // only substitute if different
             if(!( strBullets.substring(0,Math.min(numBullets,m_genlistlevel)).equals
                   (m_genlistBulletBuffer.substring(0,Math.min(numBullets,m_genlistlevel)) ) ) )
             {
                 if(numBullets <= m_genlistlevel)
                 {
                     // Substitute all but the last character (keep the expressed bullet preference)
                     strBullets  = (numBullets > 1 ? m_genlistBulletBuffer.substring(0, numBullets-1) : "")
                                   + strBullets.substring(numBullets-1, numBullets);
                 }
                 else
                 {
                     strBullets = m_genlistBulletBuffer + strBullets.substring(m_genlistlevel, numBullets);
                 }
             }
         }

         //
         //  Check if this is still of the same type
         //
         if( strBullets.substring(0,Math.min(numBullets,m_genlistlevel)).equals
            (m_genlistBulletBuffer.substring(0,Math.min(numBullets,m_genlistlevel)) ) )
         {
             if( numBullets > m_genlistlevel )
             {
                 pushElement( new Element( getListType(strBullets.charAt(m_genlistlevel++) ) ) );

                 for( ; m_genlistlevel < numBullets; m_genlistlevel++ )
                 {
                     // bullets are growing, get from new bullet list
                     pushElement( new Element("li") );
                     pushElement( new Element( getListType(strBullets.charAt(m_genlistlevel)) ));
                 }
             }
             else if( numBullets < m_genlistlevel )
             {
                 //  Close the previous list item.
                 // buf.append( m_renderer.closeListItem() );
                 popElement( "li" );

                 for( ; m_genlistlevel > numBullets; m_genlistlevel-- )
                 {
                     // bullets are shrinking, get from old bullet list

                     popElement( getListType(m_genlistBulletBuffer.charAt(m_genlistlevel-1)) );
                     if( m_genlistlevel > 0 )
                     {
                         popElement( "li" );
                     }

                 }
             }
             else
             {
                 if( m_genlistlevel > 0 )
                 {
                     popElement( "li" );
                 }
             }
         }
         else
         {
             //
             //  The pattern has changed, unwind and restart
             //
             int  numEqualBullets;
             final int  numCheckBullets;

             // find out how much is the same
             numEqualBullets = 0;
             numCheckBullets = Math.min(numBullets,m_genlistlevel);

             while( numEqualBullets < numCheckBullets )
             {
                 // if the bullets are equal so far, keep going
                 if( strBullets.charAt(numEqualBullets) == m_genlistBulletBuffer.charAt(numEqualBullets))
                     numEqualBullets++;
                 // otherwise giveup, we have found how many are equal
                 else
                     break;
             }

             //unwind
             for( ; m_genlistlevel > numEqualBullets; m_genlistlevel-- )
             {
                 popElement( getListType( m_genlistBulletBuffer.charAt(m_genlistlevel-1) ) );
                 if( m_genlistlevel > numBullets )
                 {
                     popElement("li");
                 }
             }

             //rewind

             pushElement( new Element(getListType( strBullets.charAt(numEqualBullets++) ) ) );
             for(int i = numEqualBullets; i < numBullets; i++)
             {
                 pushElement( new Element("li") );
                 pushElement( new Element( getListType( strBullets.charAt(i) ) ) );
             }
             m_genlistlevel = numBullets;
         }

         //
         //  Push a new list item, and eat away any extra whitespace
         //
         pushElement( new Element("li") );
         readWhile(" ");

         // work done, remember the new bullet list (in place of old one)
         m_genlistBulletBuffer.setLength(0);
         m_genlistBulletBuffer.append(strBullets);

         return m_currentElement;
    }

    private Element unwindGeneralList()
    {
        //unwind
        for( ; m_genlistlevel > 0; m_genlistlevel-- )
        {
            popElement( "li" );
            popElement( getListType(m_genlistBulletBuffer.charAt(m_genlistlevel-1)) );
        }

        m_genlistBulletBuffer.setLength(0);

        return null;
    }


    private Element handleDefinitionList()
        throws IOException
    {
        if( !m_isdefinition )
        {
            m_isdefinition = true;

            startBlockLevel();

            pushElement( new Element("dl") );
            return pushElement( new Element("dt") );
        }

        return null;
    }

    private Element handleOpenbracket()
        throws IOException
    {
        final StringBuilder sb = new StringBuilder(40);
        final int pos = getPosition();
        int ch = nextToken();
        boolean isPlugin = false;

        if( ch == '[' )
        {
            if( m_wysiwygEditorMode )
            {
                sb.append( '[' );
            }

            sb.append( (char)ch );

            while( (ch = nextToken()) == '[' )
            {
                sb.append( (char)ch );
            }
        }


        if( ch == '{' )
        {
            isPlugin = true;
        }

        pushBack( ch );

        if( sb.length() > 0 )
        {
            m_plainTextBuf.append( sb );
            return m_currentElement;
        }

        //
        //  Find end of hyperlink
        //

        ch = nextToken();
        int nesting = 1;    // Check for nested plugins

        while( ch != -1 )
        {
            final int ch2 = nextToken(); pushBack(ch2);

            if( isPlugin )
            {
                if( ch == '[' && ch2 == '{' )
                {
                    nesting++;
                }
                else if( nesting == 0 && ch == ']' && sb.charAt(sb.length()-1) == '}' )
                {
                    break;
                }
                else if( ch == '}' && ch2 == ']' )
                {
                    // NB: This will be decremented once at the end
                    nesting--;
                }
            }
            else
            {
                if( ch == ']' )
                {
                    break;
                }
            }

            sb.append( (char) ch );

            ch = nextToken();
        }

        //
        //  If the link is never finished, do some tricks to display the rest of the line
        //  unchanged.
        //
        if( ch == -1 )
        {
            log.debug("Warning: unterminated link detected!");
            m_isEscaping = true;
            m_plainTextBuf.append( sb );
            flushPlainText();
            m_isEscaping = false;
            return m_currentElement;
        }

        return handleHyperlinks( sb.toString(), pos );
    }

    /**
     *  Reads the stream until the current brace is closed or stream end.
     */
    private String readBraceContent( final char opening, final char closing )
        throws IOException
    {
        final StringBuilder sb = new StringBuilder(40);
        int braceLevel = 1;
        int ch;
        while(( ch = nextToken() ) != -1 )
        {
            if( ch == '\\' )
            {
                continue;
            }
            else if ( ch == opening )
            {
                braceLevel++;
            }
            else if ( ch == closing )
            {
                braceLevel--;
                if (braceLevel==0)
                {
                  break;
                }
            }
            sb.append( (char)ch );
        }
        return sb.toString();
    }


    /**
     *  Handles constructs of type %%(style) and %%class
     * @param newLine
     * @return An Element containing the div or span, depending on the situation.
     * @throws IOException
     */
    private Element handleDiv( final boolean newLine )
        throws IOException
    {
        int ch = nextToken();
        Element el = null;

        if( ch == '%' )
        {
            String style = null;
            String clazz = null;

            ch = nextToken();

            //
            //  Style or class?
            //
            if( ch == '(' )
            {
                style = readBraceContent('(',')');
            }
            else if( Character.isLetter( (char) ch ) )
            {
                pushBack( ch );
                clazz = readUntil( "( \t\n\r" );
                //Note: ref.https://www.w3.org/TR/CSS21/syndata.html#characters
                //CSS Classnames can contain only the characters [a-zA-Z0-9] and
                //ISO 10646 characters U+00A0 and higher, plus the "-" and the "_".
                //They cannot start with a digit, two hyphens, or a hyphen followed by a digit.

                //(1) replace '.' by spaces, allowing multiple classnames on a div or span
                //(2) remove any invalid character
                if( clazz != null){

                    clazz = clazz.replace('.', ' ')
                                 .replaceAll("[^\\s-_\\w\\x200-\\x377]+","");

                }
                ch = nextToken();

                //check for %%class1.class2( style information )
                if( ch == '(' )
                {
                    style = readBraceContent('(',')');
                }
                //
                //  Pop out only spaces, so that the upcoming EOL check does not check the
                //  next line.
                //
                else if( ch == '\n' || ch == '\r' )
                {
                    pushBack(ch);
                }
            }
            else
            {
                //
                // Anything else stops.
                //

                pushBack(ch);

                try
                {
                    final Boolean isSpan = m_styleStack.pop();

                    if( isSpan == null )
                    {
                        // Fail quietly
                    }
                    else if( isSpan.booleanValue() )
                    {
                        el = popElement( "span" );
                    }
                    else
                    {
                        el = popElement( "div" );
                    }
                }
                catch( final EmptyStackException e )
                {
                    log.debug("Page '"+m_context.getName()+"' closes a %%-block that has not been opened.");
                    return m_currentElement;
                }

                return el;
            }

            //
            //  Check if there is an attempt to do something nasty
            //

            try
            {
                style = StringEscapeUtils.unescapeHtml4(style);
                if( style != null && style.indexOf("javascript:") != -1 )
                {
                    log.debug("Attempt to output javascript within CSS:"+style);
                    final ResourceBundle rb = Preferences.getBundle( m_context );
                    return addElement( makeError( rb.getString( "markupparser.error.javascriptattempt" ) ) );
                }
            }
            catch( final NumberFormatException e )
            {
                //
                //  If there are unknown entities, we don't want the parser to stop.
                //
                final ResourceBundle rb = Preferences.getBundle( m_context );
                final String msg = MessageFormat.format( rb.getString( "markupparser.error.parserfailure"), e.getMessage() );
                return addElement( makeError( msg ) );
            }

            //
            //  Decide if we should open a div or a span?
            //
            final String eol = peekAheadLine();

            if( eol.trim().length() > 0 )
            {
                // There is stuff after the class

                el = new Element("span");

                m_styleStack.push( Boolean.TRUE );
            }
            else
            {
                startBlockLevel();
                el = new Element("div");
                m_styleStack.push( Boolean.FALSE );
            }

            if( style != null ) el.setAttribute("style", style);
            if( clazz != null ) el.setAttribute("class", clazz);
            el = pushElement( el );

            return el;
        }

        pushBack(ch);

        return el;
    }

    private Element handleSlash( final boolean newLine )
        throws IOException
    {
        final int ch = nextToken();

        pushBack(ch);
        if( ch == '%' && !m_styleStack.isEmpty() )
        {
            return handleDiv( newLine );
        }

        return null;
    }

    private Element handleBar( final boolean newLine )
        throws IOException
    {
        Element el = null;

        if( !m_istable && !newLine )
        {
            return null;
        }

        //
        //  If the bar is in the first column, we will either start
        //  a new table or continue the old one.
        //

        if( newLine )
        {
            if( !m_istable )
            {
                startBlockLevel();
                el = pushElement( new Element("table").setAttribute("class","wikitable").setAttribute("border","1") );
                m_istable = true;
                m_rowNum = 0;
            }

            m_rowNum++;
            final Element tr = ( m_rowNum % 2 != 0 )
                       ? new Element("tr").setAttribute("class", "odd")
                       : new Element("tr");
            el = pushElement( tr );
        }

        //
        //  Check out which table cell element to start;
        //  a header element (th) or a regular element (td).
        //
        final int ch = nextToken();

        if( ch == '|' )
        {
            if( !newLine )
            {
                el = popElement("th");
                if( el == null ) popElement("td");
            }
            el = pushElement( new Element("th") );
        }
        else
        {
            if( !newLine )
            {
                el = popElement("td");
                if( el == null ) popElement("th");
            }

            el = pushElement( new Element("td") );

            pushBack( ch );
        }

        return el;
    }

    /**
     *  Generic escape of next character or entity.
     */
    private Element handleTilde()
        throws IOException
    {
        final int ch = nextToken();

        if( ch == ' ' )
        {
            if( m_wysiwygEditorMode )
            {
                m_plainTextBuf.append( "~ " );
            }
            return m_currentElement;
        }

        if( ch == '|' || ch == '~' || ch == '\\' || ch == '*' || ch == '#' ||
            ch == '-' || ch == '!' || ch == '\'' || ch == '_' || ch == '[' ||
            ch == '{' || ch == ']' || ch == '}' || ch == '%' )
        {
            if( m_wysiwygEditorMode )
            {
                m_plainTextBuf.append( '~' );
            }

            m_plainTextBuf.append( (char)ch );
            m_plainTextBuf.append(readWhile( ""+(char)ch ));
            return m_currentElement;
        }

        // No escape.
        pushBack( ch );

        return null;
    }

    private void fillBuffer( final Element startElement )
        throws IOException
    {
        m_currentElement = startElement;

        boolean quitReading = false;
        m_newLine = true;
        disableOutputEscaping();

        while(!quitReading)
        {
            final int ch = nextToken();

            if( ch == -1 ) break;

            //
            //  Check if we're actually ending the preformatted mode.
            //  We still must do an entity transformation here.
            //
            if( m_isEscaping )
            {
                if( ch == '}' )
                {
                    if( handleClosebrace() == null ) m_plainTextBuf.append( (char) ch );
                }
                else if( ch == -1 )
                {
                    quitReading = true;
                }
                else if( ch == '\r' )
                {
                    // DOS line feeds we ignore.
                }
                else if( ch == '<' )
                {
                    m_plainTextBuf.append( "&lt;" );
                }
                else if( ch == '>' )
                {
                    m_plainTextBuf.append( "&gt;" );
                }
                else if( ch == '&' )
                {
                    m_plainTextBuf.append( "&amp;" );
                }
                else if( ch == '~' )
                {
                    String braces = readWhile("}");
                    if( braces.length() >= 3 )
                    {
                        m_plainTextBuf.append("}}}");

                        braces = braces.substring(3);
                    }
                    else
                    {
                        m_plainTextBuf.append( (char) ch );
                    }

                    for( int i = braces.length()-1; i >= 0; i-- )
                    {
                        pushBack(braces.charAt(i));
                    }
                }
                else
                {
                    m_plainTextBuf.append( (char) ch );
                }

                continue;
            }

            //
            //  An empty line stops a list
            //
            if( m_newLine && ch != '*' && ch != '#' && ch != ' ' && m_genlistlevel > 0 )
            {
                m_plainTextBuf.append(unwindGeneralList());
            }

            if( m_newLine && ch != '|' && m_istable )
            {
                popElement("table");
                m_istable = false;
            }

            int skip = IGNORE;

            //
            //  Do the actual parsing and catch any errors.
            //
            try
            {
                skip = parseToken( ch );
            }
            catch( final IllegalDataException e )
            {
                log.info("Page "+m_context.getPage().getName()+" contains data which cannot be added to DOM tree: "+e.getMessage());

                makeError("Error: "+cleanupSuspectData(e.getMessage()) );
            }

            //
            //   The idea is as follows:  If the handler method returns
            //   an element (el != null), it is assumed that it has been
            //   added in the stack.  Otherwise the character is added
            //   as is to the plaintext buffer.
            //
            //   For the transition phase, if s != null, it also gets
            //   added in the plaintext buffer.
            //

            switch( skip )
            {
                case ELEMENT:
                    m_newLine = false;
                    break;

                case CHARACTER:
                    m_plainTextBuf.append( (char) ch );
                    m_newLine = false;
                    break;

                case IGNORE:
                default:
                    break;
            }
        }

        closeHeadings();
        popElement("domroot");
    }

    private String cleanupSuspectData( final String s )
    {
        final StringBuilder sb = new StringBuilder( s.length() );

        for( int i = 0; i < s.length(); i++ )
        {
            final char c = s.charAt(i);

            if( Verifier.isXMLCharacter( c ) ) sb.append( c );
            else sb.append( "0x"+Integer.toString(c,16).toUpperCase() );
        }

        return sb.toString();
    }

    /** The token is a plain character. */
    protected static final int CHARACTER = 0;

    /** The token is a wikimarkup element. */
    protected static final int ELEMENT   = 1;

    /** The token is to be ignored. */
    protected static final int IGNORE    = 2;

    /**
     *  Return CHARACTER, if you think this was a plain character; ELEMENT, if
     *  you think this was a wiki markup element, and IGNORE, if you think
     *  we should ignore this altogether.
     *  <p>
     *  To add your own MarkupParser, you can override this method, but it
     *  is recommended that you call super.parseToken() as well to gain advantage
     *  of JSPWiki's own markup.  You can call it at the start of your own
     *  parseToken() or end - it does not matter.
     *
     * @param ch The character under investigation
     * @return {@link #ELEMENT}, {@link #CHARACTER} or {@link #IGNORE}.
     * @throws IOException If parsing fails.
     */
    protected int parseToken( final int ch )
        throws IOException
    {
        Element el = null;

        //
        //  Now, check the incoming token.
        //
        switch( ch )
        {
          case '\r':
            // DOS linefeeds we forget
            return IGNORE;

          case '\n':
            //
            //  Close things like headings, etc.
            //

            // FIXME: This is not really very fast

            closeHeadings();

            popElement("dl"); // Close definition lists.
            if( m_istable )
            {
                popElement("tr");
            }

            m_isdefinition = false;

            if( m_newLine )
            {
                // Paragraph change.
                startBlockLevel();

                //
                //  Figure out which elements cannot be enclosed inside
                //  a <p></p> pair according to XHTML rules.
                //
                final String nextLine = peekAheadLine();
                if( nextLine.length() == 0 ||
                    (nextLine.length() > 0 &&
                     !nextLine.startsWith("{{{") &&
                     !nextLine.startsWith("----") &&
                     !nextLine.startsWith("%%") &&
                     "*#!;".indexOf( nextLine.charAt(0) ) == -1) )
                {
                    pushElement( new Element("p") );
                    m_isOpenParagraph = true;

                    if( m_restartitalic )
                    {
                        pushElement( new Element("i") );
                        m_isitalic = true;
                        m_restartitalic = false;
                    }
                    if( m_restartbold )
                    {
                        pushElement( new Element("b") );
                        m_isbold = true;
                        m_restartbold = false;
                    }
                }
            }
            else
            {
                m_plainTextBuf.append("\n");
                m_newLine = true;
            }
            return IGNORE;


          case '\\':
            el = handleBackslash();
            break;

          case '_':
            el = handleUnderscore();
            break;

          case '\'':
            el = handleApostrophe();
            break;

          case '{':
            el = handleOpenbrace( m_newLine );
            break;

          case '}':
            el = handleClosebrace();
            break;

          case '-':
            if( m_newLine )
                el = handleDash();

            break;

          case '!':
            if( m_newLine )
            {
                el = handleHeading();
            }
            break;

          case ';':
            if( m_newLine )
            {
                el = handleDefinitionList();
            }
            break;

          case ':':
            if( m_isdefinition )
            {
                popElement("dt");
                el = pushElement( new Element("dd") );
                m_isdefinition = false;
            }
            break;

          case '[':
            el = handleOpenbracket();
            break;

          case '*':
            if( m_newLine )
            {
                pushBack('*');
                el = handleGeneralList();
            }
            break;

          case '#':
            if( m_newLine )
            {
                pushBack('#');
                el = handleGeneralList();
            }
            break;

          case '|':
            el = handleBar( m_newLine );
            break;

          case '~':
            el = handleTilde();
            break;

          case '%':
            el = handleDiv( m_newLine );
            break;

          case '/':
            el = handleSlash( m_newLine );
            break;

          default:
            break;
        }

        return el != null ? ELEMENT : CHARACTER;
    }

    private void closeHeadings()
    {
        if( m_lastHeading != null && !m_wysiwygEditorMode )
        {
            // Add the hash anchor element at the end of the heading
            addElement( new Element("a").setAttribute( "class",HASHLINK ).setAttribute( "href","#"+m_lastHeading.m_titleAnchor ).setText( "#" ) );
            m_lastHeading = null;
        }
        popElement("h2");
        popElement("h3");
        popElement("h4");
    }

    /**
     *  Parses the entire document from the Reader given in the constructor or
     *  set by {@link #setInputReader(Reader)}.
     *
     *  @return A WikiDocument, ready to be passed to the renderer.
     *  @throws IOException If parsing cannot be accomplished.
     */
    @Override
    public WikiDocument parse()
        throws IOException
    {
        final WikiDocument d = new WikiDocument( m_context.getPage() );
        d.setContext( m_context );

        final Element rootElement = new Element("domroot");

        d.setRootElement( rootElement );

        fillBuffer( rootElement );

        paragraphify(rootElement);

        return d;
    }

    /**
     *  Checks out that the first paragraph is correctly installed.
     *
     *  @param rootElement
     */
    private void paragraphify( final Element rootElement)
    {
        //
        //  Add the paragraph tag to the first paragraph
        //
        final List< Content > kids = rootElement.getContent();

        if( rootElement.getChild("p") != null )
        {
            final ArrayList<Content> ls = new ArrayList<>();
            int idxOfFirstContent = 0;
            int count = 0;

            for( final Iterator< Content > i = kids.iterator(); i.hasNext(); count++ )
            {
                final Content c = i.next();
                if( c instanceof Element element)
                {
                    final String name = element.getName();
                    if( isBlockLevel( name ) ) break;
                }

                if( !(c instanceof ProcessingInstruction) )
                {
                    ls.add( c );
                    if( idxOfFirstContent == 0 ) idxOfFirstContent = count;
                }
            }

            //
            //  If there were any elements, then add a new <p> (unless it would
            //  be an empty one)
            //
            if( ls.size() > 0 )
            {
                final Element newel = new Element("p");

                for( final Iterator< Content > i = ls.iterator(); i.hasNext(); )
                {
                    final Content c = i.next();

                    c.detach();
                    newel.addContent(c);
                }

                //
                // Make sure there are no empty <p/> tags added.
                //
                if( newel.getTextTrim().length() > 0 || !newel.getChildren().isEmpty() )
                    rootElement.addContent(idxOfFirstContent, newel);
            }
        }
    }

}
