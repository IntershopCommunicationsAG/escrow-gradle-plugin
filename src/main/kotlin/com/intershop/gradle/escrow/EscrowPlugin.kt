/*
 * Copyright 2022 Intershop Communications AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.intershop.gradle.escrow

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import java.util.*

open class EscrowPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            if (rootProject == this) {
                logger.info("Intershop ESCROW plugin will be initialized")

                val extension = extensions.findByType(
                    EscrowExtension::class.java
                ) ?: extensions.create(
                    EscrowExtension.ESCROW_EXTENSION_NAME,
                    EscrowExtension::class.java
                )

                project.afterEvaluate {
                    plugins.withType(MavenPublishPlugin::class.java) {
                        extensions.configure(PublishingExtension::class.java) { publishing ->
                            if (!project.version.toString().lowercase(Locale.getDefault()).endsWith("snapshot")) {
                                publishing.publications.maybeCreate(
                                    extension.mavenPublicationName.get(),
                                    MavenPublication::class.java
                                ).apply {
                                    groupId = extension.sourceGroup.get()
                                    artifact(getConfigurePackageTask(project, extension)) {
                                        it.classifier = extension.classifier.get()
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                logger.warn("Intershop ESCROW plugin will be not applied to the sub project '{}'", name)
            }
        }
    }

    private fun getConfigurePackageTask(project: Project, extension: EscrowExtension): TaskProvider<Zip> {
        val task = project.tasks.register(EscrowExtension.ESCROW_TASK_NAME, Zip::class.java) {
            it.description = "Creates an escrow source package from project"
            it.group = EscrowExtension.ESCROW_GROUP_NAME
            it.archiveBaseName.set(project.rootProject.name)

            it.includeEmptyDirs = true
            it.isZip64 = true

            it.from(project.rootProject.rootDir)
            it.into(project.rootProject.name)

            it.includeEmptyDirs = true
            it.excludes.clear()
            it.excludes.addAll(extension.excludes.get())


            it.destinationDirectory.set(project.layout.buildDirectory.dir(EscrowExtension.ESCROW_EXTENSION_NAME))
        }

        return task
    }
}
