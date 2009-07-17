package org.sonatype.maven.plugin.app;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

public final class ClasspathUtils
{

    private static final String CP_PROPSFILE = "classpath.properties";

    private ClasspathUtils()
    {
    }

    public static Properties read( final MavenProject project )
        throws IOException
    {
        File cpFile = new File( project.getBuild().getDirectory(), CP_PROPSFILE );
        if ( !cpFile.exists() )
        {
            throw new IOException( "Cannot find: " + cpFile + ". Did you call 'generate-metadata'?" );
        }

        Properties p = new Properties();
        FileInputStream stream = null;
        try
        {
            stream = new FileInputStream( cpFile );
            p.load( stream );
        }
        finally
        {
            IOUtil.close( stream );
        }

        return p;
    }

    public static void write( final Set<Artifact> classpathArtifacts, final MavenProject project )
        throws IOException
    {
        Properties p = new Properties();

        for ( Artifact artifact : classpathArtifacts )
        {
            File artifactFile = artifact.getFile();

            StringBuilder fname = new StringBuilder();
            fname.append( artifact.getArtifactId() ).append( '-' ).append( artifact.getVersion() );

            if ( artifact.getClassifier() != null )
            {
                fname.append( '-' ).append( artifact.getClassifier() );
            }

            fname.append( '.' ).append( artifact.getArtifactHandler().getExtension() );

            p.setProperty( fname.toString(), artifactFile.getAbsolutePath() );
        }

        File cpFile = new File( project.getBuild().getDirectory(), CP_PROPSFILE );
        FileOutputStream stream = null;
        try
        {
            cpFile.getParentFile().mkdirs();
            stream = new FileOutputStream( cpFile );

            p.store( stream, "Written on: " + new Date() );
        }
        finally
        {
            IOUtil.close( stream );
        }
    }

}
