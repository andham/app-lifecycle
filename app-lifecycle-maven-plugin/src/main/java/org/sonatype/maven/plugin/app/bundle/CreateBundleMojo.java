package org.sonatype.maven.plugin.app.bundle;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.assembly.InvalidAssemblerConfigurationException;
import org.apache.maven.plugin.assembly.archive.ArchiveCreationException;
import org.apache.maven.plugin.assembly.archive.AssemblyArchiver;
import org.apache.maven.plugin.assembly.format.AssemblyFormattingException;
import org.apache.maven.plugin.assembly.model.Assembly;
import org.apache.maven.plugin.assembly.model.FileItem;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.sonatype.maven.plugin.app.ClasspathUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

/**
 * Create a plugin bundle artifact attach it to the plugin's project.
 * 
 * @goal create-bundle
 * @phase package
 */
public class CreateBundleMojo
    extends AbstractMojo
{
    /**
     * The current plugin project being built.
     * 
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The current build session, for reference from the Assembly API.
     * 
     * @parameter default-value="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
     * Supplemental plugin assembly configuration.
     * 
     * @parameter
     */
    private BundleConfiguration bundle;

    /**
     * Assembly manager component that is responsible for creating the plugin bundle assembly and attaching it to the
     * current project.
     * 
     * @component
     */
    private AssemblyArchiver archiver;

    /**
     * Component used by the {@link AssemblyArchiver} to attach the bundle artifact to the current project.
     * 
     * @component
     */
    private MavenProjectHelper projectHelper;

    @SuppressWarnings( "unchecked" )
    public void execute()
        throws MojoExecutionException
    {
        Assembly assembly = new Assembly();
        assembly.addFormat( "zip" );
        assembly.setId( "bundle" );
        assembly.setIncludeBaseDirectory( false );

        try
        {
            Properties cpArtifacts = ClasspathUtils.read( project );
            String outputDirectory =
                project.getGroupId() + "/" + project.getArtifactId() + "/" + project.getVersion() + "/dependencies";

            for ( Iterator it = cpArtifacts.keySet().iterator(); it.hasNext(); )
            {
                String destName = (String) it.next();
                String sourcePath = cpArtifacts.getProperty( destName );

                FileItem fi = new FileItem();
                fi.setSource( sourcePath );
                fi.setOutputDirectory( outputDirectory );
                fi.setDestName( destName );

                assembly.addFile( fi );
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Failed to create plugin bundle: " + e.getMessage(), e );
        }

        FileItem fi = new FileItem();
        fi.setSource( project.getArtifact().getFile().getPath() );
        fi.setOutputDirectory( project.getGroupId() + "/" + project.getArtifactId() + "/" + project.getVersion() );

        assembly.addFile( fi );

        if ( bundle == null )
        {
            bundle = new BundleConfiguration( project, session );
        }
        else
        {
            bundle.initDefaults( project, session );
        }

        try
        {
            File assemblyFile =
                archiver.createArchive( assembly, bundle.getAssemblyFileName( assembly ), "zip", bundle );
            projectHelper.attachArtifact( project, "zip", assembly.getId(), assemblyFile );
        }
        catch ( ArchiveCreationException e )
        {
            throw new MojoExecutionException( "Failed to create plugin bundle: " + e.getMessage(), e );
        }
        catch ( AssemblyFormattingException e )
        {
            throw new MojoExecutionException( "Failed to create plugin bundle: " + e.getMessage(), e );
        }
        catch ( InvalidAssemblerConfigurationException e )
        {
            throw new MojoExecutionException( "Failed to create plugin bundle: " + e.getMessage(), e );
        }
    }
}
