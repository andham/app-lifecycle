/*
 * Sonatype Application Build Lifecycle
 * Copyright (C) 2009-2012 Sonatype, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
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
