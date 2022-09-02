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

import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

/**
 * Extension for Escrow package configuration.
 */
open class EscrowExtension @Inject constructor(objectFactory: ObjectFactory,
                                               layout: ProjectLayout) {

    companion object {
        // names for the plugin
        const val ESCROW_EXTENSION_NAME = "escrow"
        const val ESCROW_GROUP_NAME = "Intershop Release Plugins"
        const val ESCROW_TASK_NAME = "escrowZip"
    }


    /**
     * <p>The group configuration for this artifact.</p>
     */
    var sourceGroup: Property<String> = objectFactory.property(String::class.java)

    /**
     * <p>The classifier of this artifact.</p>
     */
    var classifier: Property<String> = objectFactory.property(String::class.java)

    /**
     * <p>The exclude list of the escrow package</p>
     */
    var excludes: SetProperty<String> = objectFactory.setProperty(String::class.java)

    /**
     * <p>Add a single exclude pattern.</p>
     * @param exclude
     */
    fun exclude(exclude: String) {
        excludes.add(exclude)
    }

    /**
     * <p>Add a list of exclude pattern.</p>
     * @param excludelist
     */
    fun excludes(excludelist: List<String>) {
        excludes.addAll(excludelist)
    }

    /**
     * <p>Publishing name of Maven of the project.</p>
     */
    val mavenPublicationName: Property<String> = objectFactory.property(String::class.java)

    init {

        sourceGroup.convention("com.intershop.source")
        classifier.convention("src")

        val buildDirName = layout.buildDirectory.asFile.get().name
        excludes.addAll(buildDirName, "*/${buildDirName}", ".gradle", ".svn",
            ".git", ".idea", ".eclipse", ".settings", "**/.settings/**")

        mavenPublicationName.convention("mvnEscrow")
    }
}
