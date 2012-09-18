/*
 * Sonatype Application Build Lifecycle
 * Copyright (C) 2009-2012 Sonatype, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package org.sonatype.plugins.it.util;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.codehaus.plexus.util.IOUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class TestUtils
{
    public static final String JVM_DEBUGGER_OPTIONS =
        "-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000";

    /** to use debugger on forked tests, simply change this to true and rerun */
    public static final boolean ENABLE_DEBUGGER_ON_FORKED_TEST = false;

    public static List<String> getStandardVerifierCliOptions()
    {
        List<String> cliOptions = new ArrayList<String>();
        cliOptions.add( "-V" );
        return cliOptions;
    }

    @SuppressWarnings( "unchecked" )
    public static Verifier getVerifier( final String dir )
        throws VerificationException
    {
        Verifier verifier = new Verifier( dir );
        verifier.getCliOptions().addAll( getStandardVerifierCliOptions() );
        verifier.setMavenDebug( false );
        return verifier;
    }

    public static Map<String, String> getVerifierEnvVars()
    {
        Map<String, String> envVars = new HashMap<String, String>();
        if ( TestUtils.ENABLE_DEBUGGER_ON_FORKED_TEST )
        {
            // this actually doesn't work very well
            envVars.put( "MAVEN_OPTS", JVM_DEBUGGER_OPTIONS );
        }
        return envVars;
    }

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

        return ( artifactId + "-" + version + "/" + artifactId + "-" + childName + childPath ).replace( '\\',
            File.separatorChar ).replace( '/', File.separatorChar );
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

    public static void assertZipContents( final Collection<ContentAssertions> requiredContent,
                                          final Set<String> banned, final File assembly )
        throws ZipException, IOException, JDOMException
    {
        assertTrue( "Assembly archive missing: " + assembly, assembly.isFile() );

        ZipFile zf = new ZipFile( assembly );

        Set<String> missing = new HashSet<String>();
        List<String> wrongContent = new ArrayList<String>();

        for ( ContentAssertions entry : requiredContent )
        {
            ZipEntry ze = zf.getEntry( entry.getArchivePath() );
            if ( ze == null )
            {
                missing.add( entry.getArchivePath() );
                continue;
            }

            StringWriter sWriter = new StringWriter();
            IOUtil.copy( zf.getInputStream( ze ), sWriter );

            String content = sWriter.toString();
            List<String> result = entry.assertContents( content );
            if ( result != null && !result.isEmpty() )
            {
                wrongContent.addAll( result );
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
