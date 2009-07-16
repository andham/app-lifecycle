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
import org.apache.maven.plugin.assembly.model.FileSet;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;

/**
 * Create a plugin bundle.
 * 
 * @goal create-bundle
 * @phase package
 */
public class CreateBundleMojo
    extends AbstractMojo
{
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter default-value="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
     * @parameter
     */
    private BundleConfiguration bundle;

    /**
     * @component
     */
    private AssemblyArchiver archiver;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     * The temporary working directory for storing classpath artifacts that should be bundled with the plugin.
     * 
     * @parameter default-value="${project.build.directory}/bundle-classpath"
     */
    private File classpathWorkdir;

    public void execute()
        throws MojoExecutionException
    {
        Assembly assembly = new Assembly();
        assembly.addFormat( "zip" );
        assembly.setId( "bundle" );
        assembly.setIncludeBaseDirectory( false );

        FileSet fs = new FileSet();
        fs.setDirectory( classpathWorkdir.getAbsolutePath() );
        fs.setOutputDirectory( project.getGroupId() + "/" + project.getArtifactId() + "/" + project.getVersion()
            + "/dependencies" );

        assembly.addFileSet( fs );

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

        bundle.configureAssembly( assembly );

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
