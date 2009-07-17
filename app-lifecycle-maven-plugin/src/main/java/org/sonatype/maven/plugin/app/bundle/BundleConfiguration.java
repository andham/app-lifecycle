package org.sonatype.maven.plugin.app.bundle;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.assembly.AssemblerConfigurationSource;
import org.apache.maven.plugin.assembly.model.Assembly;
import org.apache.maven.plugin.assembly.utils.AssemblyFormatUtils;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BundleConfiguration
    implements AssemblerConfigurationSource
{

    private MavenProject project;

    private MavenSession session;

    private List<String> filters;

    private String finalName;

    private MavenArchiveConfiguration archiveConfiguration;

    private String tarLongFileMode = "gnu";

    public BundleConfiguration()
    {
    }

    public BundleConfiguration( final MavenProject project, final MavenSession session )
    {
        initDefaults( project, session );
    }

    public void initDefaults( final MavenProject project, final MavenSession session )
    {
        this.project = project;
        this.session = session;
        if ( finalName == null )
        {
            finalName = project.getBuild().getFinalName();
        }
    }

    public File getArchiveBaseDirectory()
    {
        return null;
    }

    public String getArchiverConfig()
    {
        return null;
    }

    public File getBasedir()
    {
        return project.getBasedir();
    }

    public String getClassifier()
    {
        return null;
    }

    public String getDescriptor()
    {
        return null;
    }

    public String getDescriptorId()
    {
        return null;
    }

    public String[] getDescriptorReferences()
    {
        return null;
    }

    public File getDescriptorSourceDirectory()
    {
        return null;
    }

    public String[] getDescriptors()
    {
        return null;
    }

    @SuppressWarnings( "unchecked" )
    public List getFilters()
    {
        return filters;
    }

    public void addFilter( final String filter )
    {
        if ( filters == null )
        {
            filters = new ArrayList<String>();
        }

        filters.add( filter );
    }

    public void setFilters( final List<String> filters )
    {
        this.filters = filters;
    }

    public String getFinalName()
    {
        return finalName;
    }

    public void setFinalName( final String finalName )
    {
        this.finalName = finalName;
    }

    public MavenArchiveConfiguration getJarArchiveConfiguration()
    {
        return archiveConfiguration;
    }

    public void setArchive( final MavenArchiveConfiguration archiveConfiguration )
    {
        this.archiveConfiguration = archiveConfiguration;
    }

    public ArtifactRepository getLocalRepository()
    {
        return session.getLocalRepository();
    }

    public MavenSession getMavenSession()
    {
        return session;
    }

    public File getOutputDirectory()
    {
        return new File( project.getBuild().getDirectory() );
    }

    public MavenProject getProject()
    {
        return project;
    }

    @SuppressWarnings( "unchecked" )
    public List getReactorProjects()
    {
        return session.getSortedProjects();
    }

    @SuppressWarnings( "unchecked" )
    public List getRemoteRepositories()
    {
        return project.getRemoteArtifactRepositories();
    }

    public File getSiteDirectory()
    {
        return new File( project.getReporting().getOutputDirectory() );
    }

    public String getTarLongFileMode()
    {
        return tarLongFileMode;
    }

    public void setTarLongFileMode( final String mode )
    {
        tarLongFileMode = mode;
    }

    public File getTemporaryRootDirectory()
    {
        return new File( project.getBuild().getDirectory(), "nexus-bundle-tmp" );
    }

    public File getWorkingDirectory()
    {
        return new File( project.getBuild().getDirectory(), "nexus-bundle/work" );
    }

    public boolean isAssemblyIdAppended()
    {
        return true;
    }

    public boolean isDryRun()
    {
        return false;
    }

    public boolean isIgnoreDirFormatExtensions()
    {
        return true;
    }

    public boolean isIgnoreMissingDescriptor()
    {
        return false;
    }

    public boolean isSiteIncluded()
    {
        return false;
    }

    public String getAssemblyFileName( final Assembly assembly )
    {
        return AssemblyFormatUtils.getDistributionName( assembly, this );
    }

}
