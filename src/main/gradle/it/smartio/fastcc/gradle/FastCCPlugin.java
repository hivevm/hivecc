/*
 * Copyright (c) 2001-2021 Territorium Online Srl / TOL GmbH. All Rights Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as defined in and that are
 * subject to the Territorium Online License Version 1.0. You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at http://www.tol.info/license/
 * and read it before using this file.
 *
 * The Original Code and all software distributed under the License are distributed on an 'AS IS'
 * basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND TERRITORIUM ONLINE HEREBY
 * DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT. Please see the License for
 * the specific language governing rights and limitations under the License.
 */

package it.smartio.fastcc.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;

import java.io.File;

import it.smartio.fastcc.FastCCBuilder;
import it.smartio.fastcc.FastCCBuilder.Language;

/**
 * The {@link FastCCPlugin} defines the different tasks required for a smart.IO build management.
 */
public class FastCCPlugin implements Plugin<Project> {

  public static final String NAME_CONFIG = "fastcc";


  @Override
  public void apply(Project project) {
    ExtensionContainer extension = project.getExtensions();
    FastCCConfig config = extension.create(FastCCPlugin.NAME_CONFIG, FastCCConfig.class);

    // Tasks
    project.task("javacc").doLast(t -> {
      FastCCBuilder builder = FastCCBuilder.of(Language.Java);
      builder.setJJTreeFile(new File(project.getProjectDir(), config.sourceJava));
      builder.setOutputDirectory(new File(project.getProjectDir(), config.targetJava));
      builder.build();

    }).setDescription("FastCC for Java!");

    project.task("cppcc").doLast(t -> {
      FastCCBuilder builder = FastCCBuilder.of(Language.Cpp);
      builder.setJJTreeFile(new File(project.getProjectDir(), config.sourceCpp));
      builder.setOutputDirectory(new File(project.getProjectDir(), config.targetCpp));
      builder.build();

    }).setDescription("FastCC for Cpp!");
  }
}
