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
package org.sonatype.plugins.it;

import static org.sonatype.plugins.it.util.Bootstrap.bootstrap;
import static org.sonatype.plugins.it.util.TestUtils.getTestDir;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.jdom.JDOMException;
import org.junit.Test;
import org.sonatype.plugins.it.util.ContentAssertions;
import org.sonatype.plugins.it.util.TestUtils;
import org.sonatype.plugins.it.util.XPathContentAssertions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

public class IT002_PluginDependencySnapshot
{

    @Test
    public void run()
        throws IOException, URISyntaxException, VerificationException, JDOMException
    {
        bootstrap();

        File dir = getTestDir( "002-pluginDependencySnapshot" );

        String version = TestUtils.getPomVersion( new File( dir, "pom.xml" ) );

        Verifier verifier = TestUtils.getVerifier(dir.getAbsolutePath());
        verifier.executeGoal( "package", TestUtils.getVerifierEnvVars() );

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        Set<String> xpaths = Collections.singleton( "//pluginDependency[version/text()=\"1-SNAPSHOT\"]" );
        ContentAssertions assertions = new XPathContentAssertions( "META-INF/it/plugin.xml", xpaths );

        Set<String> banned = Collections.emptySet();

        File archive = new File( verifier.getBasedir(), "target/002-pluginDependencySnapshot-" + version + ".jar" );

        TestUtils.assertZipContents( Collections.singleton( assertions ), banned, archive );
    }

}
