package org.sonatype.maven.plugin.app;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixAwareRecursionInterceptor;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicationInformation
{

    private static final List<String> PROJECT_PREFIXES;

    static
    {
        List<String> prefixes = new ArrayList<String>();

        prefixes.add( "project." );
        prefixes.add( "pom." );

        PROJECT_PREFIXES = Collections.unmodifiableList( prefixes );
    }

    private Set<String> coreGroupIdPatterns;

    private String pluginPackaging;

    private String pluginMetadataPath;

    private String applicationId;

    private String userMimeTypesPath;

    private String applicationMinVersion;

    private String applicationMaxVersion;

    private String applicationEdition;

    public File getPluginMetadataFile( final MavenProject project )
        throws InterpolationException
    {
        return interpolateToFile( getPluginMetadataPath(), project );
    }

    public File getUserMimeTypesFile( final MavenProject project )
        throws InterpolationException
    {
        return interpolateToFile( getUserMimeTypesPath(), project );
    }

    public boolean matchesCoreGroupIds( final String groupId )
    {
        boolean matchedCoreGroupId = false;
        if ( getCoreGroupIdPatterns() != null )
        {
            for ( String pattern : getCoreGroupIdPatterns() )
            {
                if ( groupId.startsWith( pattern ) || groupId.matches( pattern ) )
                {
                    matchedCoreGroupId = true;
                    break;
                }
            }
        }

        return matchedCoreGroupId;
    }

    public void addCoreGroupIdPattern( final String coreGroupIdPattern )
    {
        if ( coreGroupIdPatterns == null )
        {
            coreGroupIdPatterns = new HashSet<String>();
        }

        coreGroupIdPatterns.add( coreGroupIdPattern );
    }

    public Set<String> getCoreGroupIdPatterns()
    {
        return coreGroupIdPatterns;
    }

    public void setCoreGroupIdPatterns( final Set<String> coreGroupIdPatterns )
    {
        this.coreGroupIdPatterns = coreGroupIdPatterns;
    }

    public String getPluginPackaging()
    {
        return pluginPackaging;
    }

    public void setPluginPackaging( final String pluginPackaging )
    {
        this.pluginPackaging = pluginPackaging;
    }

    public String getPluginMetadataPath()
    {
        return pluginMetadataPath;
    }

    public void setPluginMetadataPath( final String pluginMetadataFile )
    {
        this.pluginMetadataPath = pluginMetadataFile;
    }

    public String getApplicationId()
    {
        return applicationId;
    }

    public void setApplicationId( final String applicationId )
    {
        this.applicationId = applicationId;
    }

    public String getUserMimeTypesPath()
    {
        return userMimeTypesPath;
    }

    public void setUserMimeTypesPath( final String userMimeTypesPath )
    {
        this.userMimeTypesPath = userMimeTypesPath;
    }

    public String getApplicationMinVersion()
    {
        return applicationMinVersion;
    }

    public void setApplicationMinVersion( final String applicationMinVersion )
    {
        this.applicationMinVersion = applicationMinVersion;
    }

    public String getApplicationMaxVersion()
    {
        return applicationMaxVersion;
    }

    public void setApplicationMaxVersion( final String applicationMaxVersion )
    {
        this.applicationMaxVersion = applicationMaxVersion;
    }

    public String getApplicationEdition()
    {
        return applicationEdition;
    }

    public void setApplicationEdition( final String applicationEdition )
    {
        this.applicationEdition = applicationEdition;
    }

    private File interpolateToFile( final String pattern, final MavenProject project )
        throws InterpolationException
    {
        if ( pattern == null )
        {
            return null;
        }

        Interpolator interpolator = new StringSearchInterpolator();
        interpolator.addValueSource( new PrefixedObjectValueSource( PROJECT_PREFIXES, project, false ) );

        RecursionInterceptor ri = new PrefixAwareRecursionInterceptor( PROJECT_PREFIXES );

        return new File( interpolator.interpolate( pattern, ri ) );
    }

}
