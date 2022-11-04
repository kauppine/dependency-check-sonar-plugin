/*
 * Dependency-Check Plugin for SonarQube
 * Copyright (C) 2015-2021 dependency-check
 * philipp.dallig@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.dependencycheck.parser.deserializer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sonar.dependencycheck.reason.nuget.NugetDependency;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import edu.umd.cs.findbugs.annotations.Nullable;

public class NugetDependencyDeserializer extends StdDeserializer<List<NugetDependency>>{

    /**
     *
     */

    protected NugetDependencyDeserializer() {
        this(null);
    }

    protected NugetDependencyDeserializer(@Nullable Class<?> vc) {
        super(vc);
    }

    @Override
    public List<NugetDependency> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        List<NugetDependency> nugetDependencies = new LinkedList<>();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            if (StringUtils.equalsIgnoreCase("PackageReference", jsonParser.getCurrentName())) {
                // We found a dependency
                String name = "";
                String version = "";
                int startLineNr = jsonParser.getCurrentLocation().getLineNr();
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    if (StringUtils.equalsIgnoreCase("Include", jsonParser.getCurrentName()) && StringUtils.equalsIgnoreCase("VALUE_STRING", jsonParser.getCurrentToken().toString())) {
                        name = jsonParser.getText();
                    }
                    if (StringUtils.equalsIgnoreCase("Version", jsonParser.getCurrentName()) && StringUtils.equalsIgnoreCase("VALUE_STRING", jsonParser.getCurrentToken().toString())) {
                        version = jsonParser.getText();
                    }
                }
                int endLineNr = jsonParser.getCurrentLocation().getLineNr();
                nugetDependencies.add(new NugetDependency(name, version, startLineNr, endLineNr));
            }

        }
        return nugetDependencies;
    }
}
