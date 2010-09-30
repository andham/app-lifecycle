package org.sonatype.plugins.it.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.codehaus.plexus.util.FileUtils;

public class Bootstrap
{

    private static final File BASEDIR = TestUtils.getBaseDir();

    private static final File REMOTE_REPOSITORY_TARGET = new File( BASEDIR, "target/remote-repository" );

    private static final File[] SNAPSHOT_REPOS = { new File( BASEDIR,
        "../app-lifecycle-it-support/it-snapshot-plugin/remote-repository" ) };

    private static boolean snapshotRepositoriesInstalled = false;

    private static boolean installed = false;

    @SuppressWarnings( "unchecked" )
    public static void bootstrap()
        throws IOException, URISyntaxException, VerificationException
    {
        if ( !installed )
        {
            // install it-parent
            Verifier verifier = TestUtils.getVerifier( TestUtils.getTestDir( "." ).getAbsolutePath() );
            verifier.getCliOptions().add( "-N" );
            verifier.executeGoal( "deploy", TestUtils.getVerifierEnvVars() );
            verifier.verifyErrorFreeLog();
            verifier.resetStreams();

            // install it-bootstrap
            File bootstrapDir = TestUtils.getTestDir( "bootstrap" );

            verifier = TestUtils.getVerifier( bootstrapDir.getAbsolutePath() );
            verifier.executeGoal( "deploy", TestUtils.getVerifierEnvVars() );

            verifier.verifyErrorFreeLog();
            verifier.resetStreams();

            installed = true;
        }

    }

    public static File installSnapshotRepositories()
        throws IOException
    {
        if ( !snapshotRepositoriesInstalled )
        {
            for ( File repo : SNAPSHOT_REPOS )
            {
                if ( repo.isDirectory() )
                {
                    System.out.println( "Copying: " + repo.getAbsolutePath() + "\nTo: "
                        + REMOTE_REPOSITORY_TARGET.getAbsolutePath() );
                    FileUtils.copyDirectoryStructure( repo, REMOTE_REPOSITORY_TARGET );
                }
                else
                {
                    System.out.println( repo.getAbsolutePath()
                        + " not found. Maybe we're executing with deployed supporting artifacts?" );
                }
            }

            snapshotRepositoriesInstalled = true;
        }

        return REMOTE_REPOSITORY_TARGET;
    }

}
