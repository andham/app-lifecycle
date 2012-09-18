/**
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
def pluginModel = new XmlSlurper().parseText(content);
def pomModel = new XmlSlurper().parse(pomFile);

java.util.List<String> result = new java.util.ArrayList<String>();

if ( pluginModel.groupId != pomModel.parent.groupId ){result.add( "Group ID" );}
if ( pluginModel.artifactId != pomModel.artifactId ){result.add( "Artifact ID" );}
if ( pluginModel.version != pomModel.version ){result.add( "Version" );}
if ( pluginModel.name != pomModel.name ){result.add( "Name" );}
if ( pluginModel.description != pomModel.description ){result.add( "Description" );}

// FIXME: Not sure this is still something that's included in the plugin descriptor.
//def components = pluginModel.components.'*'.text();
//
//if ( 3 != pluginModel.components.component.size() ){result.add( "Should contain 3 components" );}
//
//check for expected components
//[ "org.sonatype.plugin.test.ComponentExtentionPoint",
//  "org.sonatype.plugin.test.ManagedViaInterface",
//  "org.sonatype.plugin.test.ComponentManaged"
//].each {
//
//  if ( !components.contains(it) ){result.add( "components should contain: ${it}" );}
//  
//}

//dependencies
def expectedDepCount = pomModel.dependencies.dependency.findAll{
  it.scope.text().equals("") || it.scope.text().equals("compile") || it.scope.text().equals("runtime")
}.size();

if ( expectedDepCount != pluginModel.classpathDependencies.classpathDependency.size()) {
  result.add( "Found: "+ pluginModel.classpathDependencies.classpathDependency.size() +
              " dependencies, expected: "+ expectedDepCount );
}

return result;