/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.subiger.openehr.codegen;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.xmlbeans.XmlException;
import org.ehrbase.client.classgenerator.ClassGenerator;
import org.ehrbase.client.classgenerator.ClassGeneratorConfig;
import org.ehrbase.client.classgenerator.OptimizerSetting;
import org.ehrbase.webtemplate.parser.OPTParser;
import org.openehr.schemas.v1.OPERATIONALTEMPLATE;
import org.openehr.schemas.v1.TemplateDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OPT to Java Mojo implementation.
 *
 * @author Renaud Subiger
 * @since 1.0.0
 */
@Mojo(name = "opt2java", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
@SuppressWarnings("unused")
public class Opt2JavaMojo extends AbstractMojo {

    /**
     * The package under which the source files will be generated.
     */
    @Parameter(required = true)
    private String packageName;

    /**
     * The directory where templates are stored.
     */
    @Parameter(defaultValue = "${basedir}/src/main/resources/opt")
    private File optRoot;

    /**
     * The directory where the generated Java source files are created.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/openehr")
    private File sourceRoot;

    /**
     * @see ClassGeneratorConfig#setOptimizerSetting(OptimizerSetting)
     */
    @Parameter(defaultValue = "NONE")
    private OptimizerSetting optimizerSetting;

    /**
     * @see ClassGeneratorConfig#setGenerateChoicesForSingleEvent(boolean)
     */
    @Parameter(defaultValue = "false")
    private boolean generateChoiceForSingleEvent;

    /**
     * @see ClassGeneratorConfig#setAddNullFlavor(boolean)
     */
    @Parameter(defaultValue = "false")
    private boolean generateNullFlavor;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException {
        var config = new ClassGeneratorConfig();
        config.setOptimizerSetting(optimizerSetting);
        config.setAddNullFlavor(generateNullFlavor);
        config.setGenerateChoicesForSingleEvent(generateChoiceForSingleEvent);

        var generator = new ClassGenerator(config);

        for (Path path : getOptFiles(optRoot)) {
            getLog().info("Processing template " + path);

            OPERATIONALTEMPLATE template;
            try {
                template = TemplateDocument.Factory.parse(path.toFile()).getTemplate();
            } catch (XmlException | IOException e) {
                throw new MojoExecutionException(e);
            }

            var webTemplate = new OPTParser(template).parse();
            var result = generator.generate(packageName, webTemplate);

            try {
                result.writeFiles(sourceRoot.toPath());
            } catch (IOException e) {
                throw new MojoExecutionException(e);
            }

            project.addCompileSourceRoot(sourceRoot.getPath());
        }
    }

    private List<Path> getOptFiles(File optRoot) throws MojoExecutionException {
        try (Stream<Path> paths = Files.list(optRoot.toPath())) {
            return paths.filter(path -> path.toString().endsWith(".opt"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
    }
}
