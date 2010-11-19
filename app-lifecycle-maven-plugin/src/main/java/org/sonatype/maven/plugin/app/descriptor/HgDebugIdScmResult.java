package org.sonatype.maven.plugin.app.descriptor;

import org.apache.maven.scm.ScmResult;

public class HgDebugIdScmResult
    extends ScmResult
{
    private final String changeSetHash;

    private final String changeSetDate;

    public HgDebugIdScmResult( String commandLine, String providerMessage, String commandOutput, boolean success,
                               final String changeSetHash, final String changeSetDate )
    {
        super( commandLine, providerMessage, commandOutput, success );
        
        this.changeSetHash = changeSetHash;
        this.changeSetDate = changeSetDate;
    }

    public String getChangeSetHash()
    {
        return changeSetHash;
    }

    public String getChangeSetDate()
    {
        return changeSetDate;
    }
}
