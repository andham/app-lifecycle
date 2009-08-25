package org.sonatype.plugins.it;

import static org.sonatype.plugins.it.util.TestUtils.getTestDir;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.jdom.JDOMException;
import org.junit.Test;
import org.sonatype.plugins.it.util.Bootstrap;
import org.sonatype.plugins.it.util.TestUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IT002_PluginDependencySnapshot
{

    // @SuppressWarnings( "unchecked" )
    @Test
    public void run()
        throws IOException, URISyntaxException, VerificationException, JDOMException
    {
        File dir = getTestDir( "002-pluginDependencySnapshot" );

        String version = TestUtils.getPomVersion( new File( dir, "pom.unfiltered.xml" ) );

        Verifier verifier = new Verifier( dir.getAbsolutePath() );

        Map<String, String> filterProperties = new HashMap<String, String>();
        filterProperties.put( "@test-remote-repo@", Bootstrap.installSnapshotRepositories().toURL().toExternalForm() );

        verifier.filterFile( "pom.unfiltered.xml", "pom.xml", "UTF-8", filterProperties );

        verifier.executeGoal( "package" );

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        Map<String, Map<String, TestUtils.ContentMatchType>> requiredContent =
            new HashMap<String, Map<String, TestUtils.ContentMatchType>>();

        Map<String, TestUtils.ContentMatchType> content = new HashMap<String, TestUtils.ContentMatchType>();
        content.put( "//pluginDependency[version/text()=\"1-SNAPSHOT\"]", TestUtils.ContentMatchType.XPATH );

        requiredContent.put( "META-INF/it/plugin.xml", content );

        Set<String> banned = Collections.emptySet();

        File archive = new File( verifier.getBasedir(), "target/002-pluginDependencySnapshot-" + version + ".jar" );

        TestUtils.assertZipContents( requiredContent, banned, archive );
    }

}
