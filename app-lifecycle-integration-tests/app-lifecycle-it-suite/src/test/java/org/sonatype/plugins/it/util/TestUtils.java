package org.sonatype.plugins.it.util;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.codehaus.plexus.util.IOUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class TestUtils
{
    
    public enum ContentMatchType
    {
        STRING,
        REGEX,
        XPATH
    };

    public static String getPomVersion( final File pomFile )
        throws JDOMException, IOException
    {
        Document doc = new SAXBuilder().build( pomFile );

        if ( doc.getRootElement().getChild( "version", doc.getRootElement().getNamespace() ) != null )
        {
            return doc.getRootElement().getChildTextNormalize( "version", doc.getRootElement().getNamespace() );
        }
        else if ( doc.getRootElement().getChild( "parent", doc.getRootElement().getNamespace() ) != null )
        {
            Element parent = doc.getRootElement().getChild( "parent", doc.getRootElement().getNamespace() );
            return parent.getChildTextNormalize( "version", doc.getRootElement().getNamespace() );
        }

        throw new IllegalStateException( "Cannot find version for POM: " + pomFile );
    }

    public static String archivePathFromChild( final String artifactId, final String version, final String childName,
                                               String childPath )
    {
        if ( !childPath.startsWith( "/" ) )
        {
            childPath = "/" + childPath;
        }

        return ( artifactId + "-" + version + "/" + artifactId + "-" + childName + childPath ).replace(
                                                                                                        '\\',
                                                                                                        File.separatorChar )
                                                                                              .replace(
                                                                                                        '/',
                                                                                                        File.separatorChar );
    }

    public static String archivePathFromProject( final String artifactId, final String version, String path )
    {
        if ( !path.startsWith( "/" ) )
        {
            path = "/" + path;
        }

        return ( artifactId + "-" + version + path ).replace( '\\', File.separatorChar ).replace( '/',
                                                                                                  File.separatorChar );
    }

    // @SuppressWarnings( "unchecked" )
    public static void assertZipContents( final Map<String, Map<String, TestUtils.ContentMatchType>> requiredContent,
                                          final Set<String> banned,
                                          final File assembly )
        throws ZipException, IOException, JDOMException
    {
        assertTrue( "Assembly archive missing: " + assembly, assembly.isFile() );

        ZipFile zf = new ZipFile( assembly );

        // System.out.println( "Contents of: " + assembly + ":\n\n" );
        // for( Enumeration<ZipEntry> e = (Enumeration<ZipEntry>) zf.entries(); e.hasMoreElements(); )
        // {
        // System.out.println( e.nextElement().getName() );
        // }
        // System.out.println( "\n\n" );

        Set<String> missing = new HashSet<String>();
        Set<String> wrongContent = new HashSet<String>();

        entryIteration: for ( Map.Entry<String, Map<String, ContentMatchType>> entry : requiredContent.entrySet() )
        {
            ZipEntry ze = zf.getEntry( entry.getKey() );
            if ( ze == null )
            {
                missing.add( entry.getKey() );
                continue;
            }

            StringWriter sWriter = new StringWriter();
            IOUtil.copy( zf.getInputStream( ze ), sWriter );

            String content = sWriter.toString();
            Document doc = null;
            for ( Map.Entry<String, ContentMatchType> itemEntry : entry.getValue().entrySet() )
            {
                if ( itemEntry == null || itemEntry.getKey() == null )
                {
                    continue;
                }

                if ( itemEntry.getValue() == null || itemEntry.getValue() == ContentMatchType.STRING
                    || ContentMatchType.valueOf( itemEntry.getValue().name() ) == null )
                {
                    if ( content.indexOf( itemEntry.getKey() ) < 0 )
                    {
                        wrongContent.add( entry.getKey() );
                        continue entryIteration;
                    }
                }
                else if ( itemEntry.getValue() == ContentMatchType.REGEX )
                {
                    try
                    {
                        Pattern p = Pattern.compile( itemEntry.getKey() );
                        if ( !p.matcher( content ).find() )
                        {
                            wrongContent.add( entry.getKey() );
                            continue entryIteration;
                        }
                    }
                    catch ( PatternSyntaxException e )
                    {
                        throw new IllegalArgumentException( "Invalid REGEX pattern: '" + itemEntry.getKey()
                            + "'. Reason: " + e.getMessage(), e );
                    }
                }
                else if ( itemEntry.getValue() == ContentMatchType.XPATH )
                {
                    if ( doc == null )
                    {
                        doc = new SAXBuilder().build( new StringReader( content ) );
                    }

                    try
                    {
                        if ( XPath.selectSingleNode( doc.getRootElement(), itemEntry.getKey() ) == null )
                        {
                            wrongContent.add( entry.getKey() );
                            continue entryIteration;
                        }
                    }
                    catch ( JDOMException e )
                    {
                        throw new IllegalArgumentException( "Invalid XPath pattern: '" + itemEntry.getKey()
                            + "'. Reason: " + e.getMessage(), e );
                    }
                }
            }
        }

        Set<String> banViolations = new HashSet<String>();
        for ( String name : banned )
        {
            if ( zf.getEntry( name ) != null )
            {
                banViolations.add( name );
            }
        }

        if ( !missing.isEmpty() || !wrongContent.isEmpty() || !banViolations.isEmpty() )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "The following errors were found in:\n\n" );
            msg.append( assembly );
            msg.append( "\n" );
            msg.append( "\nThe following REQUIRED entries were missing from the bundle archive:\n" );

            if ( missing.isEmpty() )
            {
                msg.append( "\nNone." );
            }
            else
            {
                for ( String name : missing )
                {
                    msg.append( "\n" ).append( name );
                }
            }

            msg.append( "\n\nThe following entries had the WRONG CONTENT in the bundle archive:\n" );

            if ( wrongContent.isEmpty() )
            {
                msg.append( "\nNone.\n" );
            }
            else
            {
                for ( String name : wrongContent )
                {
                    msg.append( "\n" ).append( name );
                }
            }

            msg.append( "\n\nThe following BANNED entries were present from the bundle archive:\n" );

            if ( banViolations.isEmpty() )
            {
                msg.append( "\nNone.\n" );
            }
            else
            {
                for ( String name : banViolations )
                {
                    msg.append( "\n" ).append( name );
                }
            }

            fail( msg.toString() );
        }
    }

    public static File getTestDir( final String name )
        throws IOException, URISyntaxException
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        URL resource = cloader.getResource( name );

        if ( resource == null )
        {
            throw new IOException( "Cannot find test directory: " + name );
        }

        return new File( new URI( resource.toExternalForm() ).normalize().getPath() );
    }

    public static File getBaseDir()
    {
        File result = new File( System.getProperty( "basedir", "." ) );
        try
        {
            return result.getCanonicalFile();
        }
        catch ( IOException e )
        {
            return result.getAbsoluteFile();
        }
    }

}
