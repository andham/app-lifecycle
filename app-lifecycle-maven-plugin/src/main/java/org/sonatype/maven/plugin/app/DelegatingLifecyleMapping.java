package org.sonatype.maven.plugin.app;

import org.apache.maven.lifecycle.mapping.DefaultLifecycleMapping;
import org.apache.maven.lifecycle.mapping.LifecycleMapping;

import java.util.List;
import java.util.Map;

public class DelegatingLifecyleMapping
    extends DefaultLifecycleMapping
{

    private LifecycleMapping delegate;

    @SuppressWarnings( "unchecked" )
    @Override
    public List getOptionalMojos( final String lifecycle )
    {
        return delegate.getOptionalMojos( lifecycle );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Map getPhases( final String lifecycle )
    {
        return delegate.getPhases( lifecycle );
    }

}
