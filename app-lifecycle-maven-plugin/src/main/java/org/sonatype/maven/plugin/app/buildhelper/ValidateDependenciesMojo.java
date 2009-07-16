package org.sonatype.maven.plugin.app.buildhelper;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.sonatype.maven.plugin.app.AppMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @goal check-dependencies
 * @requiresDependencyResolution runtime
 * @phase initialize
 */
public class ValidateDependenciesMojo
    implements Mojo
{

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     */
    private AppMapping mapping;

    private Log log;

    @SuppressWarnings( "unchecked" )
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        Set<Artifact> dependencies = project.getDependencyArtifacts();

        if ( dependencies != null )
        {
            List<String> failures = new ArrayList<String>();
            for ( Artifact dep : dependencies )
            {
                if ( Artifact.SCOPE_PROVIDED.equals( dep.getScope() ) )
                {
                    getLog().info(
                                   "Found dependency with 'provided' scope: " + dep.getDependencyConflictId()
                                       + "; ignoring" );
                    continue;
                }
                else if ( Artifact.SCOPE_TEST.equals( dep.getScope() ) )
                {
                    getLog().info(
                                   "Found dependency with 'test' scope: " + dep.getDependencyConflictId()
                                       + "; ignoring" );
                    continue;
                }

                if ( mapping.matchesCoreGroupIds( dep.getGroupId() )
                    || mapping.getPluginPackaging().equals( dep.getArtifactHandler().getPackaging() ) )
                {
                    failures.add( dep.getId() );
                }
            }

            if ( !failures.isEmpty() )
            {
                StringBuilder message = new StringBuilder();
                message.append( "The following dependencies should be changed to use 'provided' scope:\n" );

                for ( String id : failures )
                {
                    message.append( "\n  - " ).append( id );
                }

                throw new MojoExecutionException( message.toString() );
            }
            else
            {
                getLog().info( "All Nexus dependencies in this project seem to have correct scope." );
            }
        }
    }

    public Log getLog()
    {
        return log;
    }

    public void setLog( final Log log )
    {
        this.log = log;
    }

}
