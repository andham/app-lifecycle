package org.sonatype.maven.plugin.app;


import java.util.HashSet;
import java.util.Set;

public class AppMapping
{

    private Set<String> coreGroupIdPatterns;

    private String pluginPackaging;

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

}
