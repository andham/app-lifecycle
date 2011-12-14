package org.sonatype.maven.plugin.app.descriptor;

import org.codehaus.plexus.util.StringUtils;

public class GAVCoordinate
{
    private static final String DEFAULT_TYPE = "jar";

    private final String groupId;

    private final String artifactId;

    private final String version;

    private final String classifier;

    private final String type;

    private final boolean shared;

    public GAVCoordinate( final String groupId, final String artifactId, final String version, final String classifier,
                          final String type, final boolean shared )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;

        // plugin in maven uses plexus-utils 1.1!!!
        // if ( StringUtils.isBlank( classifier ) )
        if ( !StringUtils.isEmpty( classifier ) )
        {
            this.classifier = classifier;
        }
        else
        {
            this.classifier = null;
        }

        // plugin in maven uses plexus-utils 1.1!!!
        // if ( StringUtils.isNotBlank( type ) )
        if ( !StringUtils.isEmpty( type ) )
        {
            this.type = type;
        }
        else
        {
            this.type = DEFAULT_TYPE;
        }

        this.shared = shared;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public String getClassifier()
    {
        return classifier;
    }

    public String getType()
    {
        return type;
    }

    public boolean isShared()
    {
        return shared;
    }

    public String toCompositeForm()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( String.valueOf( getGroupId() ) ).append( ":" ).append( String.valueOf( getArtifactId() ) ).append(
            ":" ).append( String.valueOf( getVersion() ) );

        if ( !StringUtils.isEmpty( getClassifier() ) )
        {
            sb.append( ":" ).append( getClassifier() );
        }

        if ( !StringUtils.isEmpty( getType() ) && !StringUtils.equals( DEFAULT_TYPE, getType() ) )
        {
            if ( StringUtils.isEmpty( getClassifier() ) )
            {
                sb.append( ":" );
            }

            sb.append( ":" ).append( getType() );
        }

        return sb.toString();
    }

    // ==

    public String toString()
    {
        return toCompositeForm();
    }

    public int hashCode()
    {
        int hash = 7;

        hash = 31 * hash + ( getGroupId() != null ? getGroupId().hashCode() : 0 );

        hash = 31 * hash + ( getArtifactId() != null ? getArtifactId().hashCode() : 0 );

        hash = 31 * hash + ( getVersion() != null ? getVersion().hashCode() : 0 );

        hash = 31 * hash + ( getClassifier() != null ? getClassifier().hashCode() : 0 );

        hash = 31 * hash + ( getType() != null ? getType().hashCode() : 0 );

        return hash;
    }

    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( ( obj == null ) || ( obj.getClass() != this.getClass() ) )
        {
            return false;
        }

        GAVCoordinate other = (GAVCoordinate) obj;

        return StringUtils.equals( getGroupId(), other.getGroupId() )
            && StringUtils.equals( getArtifactId(), other.getArtifactId() )
            && StringUtils.equals( getVersion(), other.getVersion() )
            && StringUtils.equals( getClassifier(), other.getClassifier() )
            && StringUtils.equals( getType(), other.getType() );
    }

}
