package org.sonatype.maven.plugin.app;


import org.apache.maven.lifecycle.mapping.DefaultLifecycleMapping;
import org.apache.maven.lifecycle.mapping.LifecycleMapping;

import java.util.List;
import java.util.Map;

/**
 * Maven {@link LifecycleMapping} implementation which delegates to another {@link LifecycleMapping} instance. This
 * allows the aliasing of one mapping by another. In our case, it allows us to specify an abstract lifecycle mapping for
 * application plugin builds, then reference the abstract case with an application-specific packaging name.
 * 
 * @author jdcasey
 * 
 */
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
