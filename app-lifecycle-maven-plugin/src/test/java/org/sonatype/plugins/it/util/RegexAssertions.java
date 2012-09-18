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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class RegexAssertions
    implements ContentAssertions
{

    private final String entry;

    private final Set<String> patterns;

    public RegexAssertions( final String entry, final Set<String> patterns )
    {
        this.entry = entry;
        this.patterns = patterns;
    }

    public List<String> assertContents( final String content )
        throws IOException
    {
        List<String> missing = new ArrayList<String>();
        for ( String pattern : patterns )
        {
            Pattern p = Pattern.compile( pattern );
            if ( !p.matcher( content ).find() )
            {
                missing.add( pattern );
            }
        }

        return missing;
    }

    public String getArchivePath()
    {
        return entry;
    }

}
