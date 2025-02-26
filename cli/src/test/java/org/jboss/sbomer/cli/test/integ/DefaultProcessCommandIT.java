/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2023 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.sbomer.cli.test.integ;

import java.nio.file.Path;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.jboss.sbomer.cli.test.integ.DefaultProcessCommandIT.CustomPncServiceProfile;
import org.jboss.sbomer.cli.test.utils.MavenCycloneDxGenerateCommandMockAlternative;
import org.jboss.sbomer.cli.test.utils.PncWireMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;

@QuarkusMainTest
@QuarkusTestResource(PncWireMock.class)
@TestProfile(CustomPncServiceProfile.class)
public class DefaultProcessCommandIT {
    public static class CustomPncServiceProfile implements QuarkusTestProfile {
        @Override
        public Set<Class<?>> getEnabledAlternatives() {
            return Set.of(MavenCycloneDxGenerateCommandMockAlternative.class);
        }
    }

    @Test
    @Disabled("Disabled, because it is too hard to mock part of the generation process")
    @DisplayName("Should successfully run default processor")
    void testSuccessfulProcessing(QuarkusMainLauncher launcher, @TempDir Path tempDir) throws Exception {

        LaunchResult result = launcher.launch(
                "-v",
                "sbom",
                "generate",
                "--workdir",
                tempDir.toAbsolutePath().toString() + "/project",
                "--output",
                tempDir.toAbsolutePath().toString() + "/bom.json",
                "--build-id",
                "ARYT3LBXDVYAC",
                "maven-cyclonedx",
                "process",
                "default");

        Assertions.assertEquals(0, result.exitCode());

        // TODO: Why output? It should be errorOutput.
        MatcherAssert.assertThat(
                result.getOutput(),
                CoreMatchers.containsString(
                        "Starting processing of Red Hat component 'pkg:maven/org.apache.logging.log4j/log4j@2.19.0.redhat-00001?type=pom' with PNC artifact '123'"));
        MatcherAssert.assertThat(result.getOutput(), CoreMatchers.containsString("DEFAULT processor finished"));
    }
}
