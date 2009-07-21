package org.sonatype.maven.plugin.app.nexus;

import org.sonatype.maven.plugin.app.ApplicationInformation;

/**
 * Implementation of {@link ApplicationInformation}, which supplies Nexus-specific default configurations, plugin
 * packaging, and application-core groupIds. This is simpler to maintain for now than an XML configuration.
 * 
 * @author jdcasey
 * 
 */
public class NexusApplicationInformation
    extends ApplicationInformation
{

    public NexusApplicationInformation()
    {
        addCoreGroupIdPattern( "org.sonatype.nexus" );
        addCoreGroupIdPattern( "com.sonatype.nexus" );

        setPluginPackaging( "nexus-plugin" );

        setApplicationId( "nexus" );
        setPluginMetadataPath( "${project.build.outputDirectory}/META-INF/nexus/plugin.xml" );
        setUserMimeTypesPath( "${project.build.outputDirectory}/META-INF/nexus/userMimeTypes.properties" );

        setApplicationMinVersion( "1.4.0" );

        setApplicationEdition( "OSS" );
    }
}
