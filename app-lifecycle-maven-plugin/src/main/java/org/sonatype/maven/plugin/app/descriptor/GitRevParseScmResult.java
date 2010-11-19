package org.sonatype.maven.plugin.app.descriptor;

import org.apache.maven.scm.ScmResult;

public class GitRevParseScmResult
    extends ScmResult
{
    private final String changeSetHash;

    private final String changeSetDate;

    public GitRevParseScmResult( String commandLine, String providerMessage, String commandOutput, boolean success,
                                 String changeSetHash, String changeSetDate )
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
