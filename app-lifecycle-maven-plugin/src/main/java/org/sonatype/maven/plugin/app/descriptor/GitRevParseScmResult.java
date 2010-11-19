package org.sonatype.maven.plugin.app.descriptor;

import org.apache.maven.scm.ScmResult;

public class GitRevParseScmResult
    extends ScmResult
{
    private final String revHash;

    public GitRevParseScmResult( String commandLine, String providerMessage, String commandOutput, boolean success,String revHash )
    {
        super( commandLine, providerMessage, commandOutput, success );
        
        this.revHash = revHash;
    }

    public String getRevHash()
    {
        return revHash;
    }
}
