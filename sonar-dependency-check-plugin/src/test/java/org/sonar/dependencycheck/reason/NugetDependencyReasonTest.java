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

package org.sonar.dependencycheck.reason;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.dependencycheck.parser.element.Confidence;
import org.sonar.dependencycheck.parser.element.Dependency;
import org.sonar.dependencycheck.parser.element.Identifier;

class NugetDependencyReasonTest extends DependencyReasonTestHelper {

    @Test
    void isReasonable() throws IOException {
        NugetDependencyReason dotnet = new NugetDependencyReason(inputFile("dotnet.csproj"));
        assertTrue(dotnet.isReasonable());
        assertNotNull(dotnet.getInputComponent());
    }

    @Test
    void isReasonableWithoutContent() throws IOException {
        DefaultInputFile dotnetproject = mock(DefaultInputFile.class, RETURNS_DEEP_STUBS);
        when(dotnetproject.contents()).thenReturn("");
        NugetDependencyReason dotnet = new NugetDependencyReason(dotnetproject);
        assertFalse(dotnet.isReasonable());
        assertNotNull(dotnet.getInputComponent());
    }

    @Test
    void constructorWithIOException() throws IOException {
        DefaultInputFile dotnetproject = mock(DefaultInputFile.class, RETURNS_DEEP_STUBS);
        when(dotnetproject.contents()).thenThrow(new IOException());
        NugetDependencyReason dotnet = new NugetDependencyReason(dotnetproject);
        assertFalse(dotnet.isReasonable());
        assertNotNull(dotnet.getInputComponent());
    }

    @Test
    void foundDependencyNugetNestedVersion() throws IOException {
        NugetDependencyReason dotnet = new NugetDependencyReason(inputFile("dotnet.csproj"));
        // Create Dependency
        Identifier identifier = new Identifier("pkg:nuget/System.Net.Security@4.3.0", Confidence.HIGHEST);
        Collection<Identifier> identifiersCollected = new ArrayList<>();
        identifiersCollected.add(identifier);
        Dependency dependency = new Dependency(null, null, null, null, Collections.emptyMap(),Collections.emptyList(), identifiersCollected, Collections.emptyList(), null);
        TextRangeConfidence textRangeConfidence = dotnet.getBestTextRange(dependency);
        assertTrue(dotnet.isReasonable());
        assertNotNull(textRangeConfidence);
        assertEquals(12, textRangeConfidence.getTextRange().start().line());
        assertEquals(0, textRangeConfidence.getTextRange().start().lineOffset());
        assertEquals(14, textRangeConfidence.getTextRange().end().line());
        assertEquals(25, textRangeConfidence.getTextRange().end().lineOffset());
        assertEquals(Confidence.HIGHEST, textRangeConfidence.getConfidence());
        // verify that same dependency points to the same TextRange, use of HashMap
        assertEquals(dotnet.getBestTextRange(dependency), dotnet.getBestTextRange(dependency));
    }

    @Test
    void foundDependencyNuget() throws IOException {
        NugetDependencyReason dotnet = new NugetDependencyReason(inputFile("dotnet.csproj"));
        // Create Dependency
        Identifier identifier = new Identifier("pkg:nuget/HtmlSanitizer@5.0.355", Confidence.HIGHEST);
        Collection<Identifier> identifiersCollected = new ArrayList<>();
        identifiersCollected.add(identifier);
        Dependency dependency = new Dependency(null, null, null, null, Collections.emptyMap(),Collections.emptyList(), identifiersCollected, Collections.emptyList(), null);
        TextRangeConfidence textRangeConfidence = dotnet.getBestTextRange(dependency);
        assertTrue(dotnet.isReasonable());
        assertNotNull(textRangeConfidence);
        assertEquals(11, textRangeConfidence.getTextRange().start().line());
        assertEquals(0, textRangeConfidence.getTextRange().start().lineOffset());
        assertEquals(11, textRangeConfidence.getTextRange().end().line());
        assertEquals(68, textRangeConfidence.getTextRange().end().lineOffset());
        assertEquals(Confidence.HIGHEST, textRangeConfidence.getConfidence());
        // verify that same dependency points to the same TextRange, use of HashMap
        assertEquals(dotnet.getBestTextRange(dependency), dotnet.getBestTextRange(dependency));
    }

    @Test
    void foundDependencyNugetOnlyWithName() throws IOException {
        NugetDependencyReason dotnet = new NugetDependencyReason(inputFile("dotnet.csproj"));
        // Create Dependency
        Identifier identifier = new Identifier("pkg:nuget/Newtonsoft.Json@15.0.0", Confidence.HIGHEST);
        Collection<Identifier> identifiersCollected = new ArrayList<>();
        identifiersCollected.add(identifier);
        Dependency dependency = new Dependency(null, null, null, null, Collections.emptyMap(),Collections.emptyList(), identifiersCollected, Collections.emptyList(), null);
        TextRangeConfidence textRangeConfidence = dotnet.getBestTextRange(dependency);
        assertTrue(dotnet.isReasonable());
        assertNotNull(textRangeConfidence);
        assertEquals(10, textRangeConfidence.getTextRange().start().line());
        assertEquals(0, textRangeConfidence.getTextRange().start().lineOffset());
        assertEquals(10, textRangeConfidence.getTextRange().end().line());
        assertEquals(69, textRangeConfidence.getTextRange().end().lineOffset());
        assertEquals(Confidence.HIGH, textRangeConfidence.getConfidence());
        // verify that same dependency points to the same TextRange, use of HashMap
        assertEquals(dotnet.getBestTextRange(dependency), dotnet.getBestTextRange(dependency));
    }

    @Test
    void foundDependencyNugetWithoutVersion() throws IOException {
        NugetDependencyReason dotnet = new NugetDependencyReason(inputFile("dotnet.csproj"));
        // Create Dependency
        Identifier identifier = new Identifier("pkg:nuget/Newtonsoft.Json", Confidence.HIGHEST);
        Collection<Identifier> identifiersCollected = new ArrayList<>();
        identifiersCollected.add(identifier);
        Dependency dependency = new Dependency(null, null, null, null, Collections.emptyMap(),Collections.emptyList(), identifiersCollected, Collections.emptyList(), null);
        TextRangeConfidence textRangeConfidence = dotnet.getBestTextRange(dependency);
        assertTrue(dotnet.isReasonable());
        assertNotNull(textRangeConfidence);
        assertEquals(10, textRangeConfidence.getTextRange().start().line());
        assertEquals(0, textRangeConfidence.getTextRange().start().lineOffset());
        assertEquals(10, textRangeConfidence.getTextRange().end().line());
        assertEquals(69, textRangeConfidence.getTextRange().end().lineOffset());
        assertEquals(Confidence.HIGH, textRangeConfidence.getConfidence());
        // verify that same dependency points to the same TextRange, use of HashMap
        assertEquals(dotnet.getBestTextRange(dependency), dotnet.getBestTextRange(dependency));
    }

    @Test
    void foundNoDependency() throws IOException {
        NugetDependencyReason dotnet = new NugetDependencyReason(inputFile("dotnet.csproj"));
        // Create Dependency
        Identifier identifier = new Identifier("pkg:nuget/dummyname@2.2.0", Confidence.HIGHEST);
        Collection<Identifier> identifiersCollected = new ArrayList<>();
        identifiersCollected.add(identifier);
        Dependency dependency = new Dependency(null, null, null, null, Collections.emptyMap(),Collections.emptyList(), identifiersCollected, Collections.emptyList(), null);
        TextRangeConfidence textRangeConfidence = dotnet.getBestTextRange(dependency);
        assertTrue(dotnet.isReasonable());
        assertNotNull(textRangeConfidence);
        assertEquals(LINE_NOT_FOUND, textRangeConfidence.getTextRange().start().line());
        assertEquals(Confidence.LOW, textRangeConfidence.getConfidence());
        // verify that same dependency points to the same TextRange, use of HashMap
        assertEquals(dotnet.getBestTextRange(dependency), dotnet.getBestTextRange(dependency));
    }

}
