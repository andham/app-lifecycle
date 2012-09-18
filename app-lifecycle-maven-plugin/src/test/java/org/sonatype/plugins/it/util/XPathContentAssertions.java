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

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class XPathContentAssertions
    implements ContentAssertions
{

    private final Set<String> assertedPaths;

    private final String entry;

    public XPathContentAssertions( final String entry, final Set<String> assertedPaths )
    {
        this.entry = entry;
        this.assertedPaths = assertedPaths;
    }

    public List<String> assertContents( final String content )
        throws IOException
    {
        Document doc;
        try
        {
            doc = new SAXBuilder().build( new StringReader( content ) );
        }
        catch ( JDOMException e )
        {
            IOException err = new IOException( e.getMessage() );
            err.initCause( e );

            throw err;
        }

        List<String> wrongContent = new ArrayList<String>();
        for ( String path : assertedPaths )
        {
            try
            {
                if ( XPath.selectSingleNode( doc.getRootElement(), path ) == null )
                {
                    wrongContent.add( path );
                }
            }
            catch ( JDOMException e )
            {
                throw new IllegalArgumentException( "Invalid XPath: '" + path + "'.", e );
            }
        }

        return wrongContent;
    }

    public String getArchivePath()
    {
        return entry;
    }

}
