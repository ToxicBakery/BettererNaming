/*
 * Copyright 2015 Toxic Bakery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ToxicBakery.betterernaming

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.DomainObjectCollection
import org.gradle.api.DomainObjectSet
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.artifacts.PublishArtifactSet
import org.gradle.api.file.FileCollection
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskDependency

class RenameTask extends DefaultTask {

    static final String ANDROID_APP_PLUGIN = "com.android.application"
    static final String ANDROID_LIB_PLUGIN = "com.android.library"

    // Debugging messages
    boolean logDebug = false;

    // Replacement artifact name
    String artifactFormat = '%name%-%status%-%version%-%gitBranch%-%gitSha1Short%.%ext%'

    // Support dynamic properties
    def dynamicProperties = [:]

    def propertyMissing(String name, value) { dynamicProperties[name] = value }

    def propertyMissing(String name) { dynamicProperties = [name] }

    @TaskAction
    def renameArtifact() {

        final boolean hasAppPlugin = hasPlugin(ANDROID_APP_PLUGIN)
        final boolean hasLibPlugin = hasPlugin(ANDROID_LIB_PLUGIN)

        /*
         * Check the plugin state of the project before continuing
         * @author https://github.com/novoda/gradle-android-test-plugin
         */
        if (!hasAppPlugin && !hasLibPlugin) {
            throw new IllegalStateException("The '${ANDROID_APP_PLUGIN}' or '${ANDROID_LIB_PLUGIN}' plugin is required.")
        } else if (hasAppPlugin && hasLibPlugin) {
            throw new IllegalStateException("Having both '${ANDROID_APP_PLUGIN}' and '${ANDROID_LIB_PLUGIN}' plugin is not supported.")
        }

        // Locate the built artifact
        PublishArtifact originalArtifact = project.configurations.archives.artifacts[0]
        File moduleArtifact = originalArtifact.file
        if (!moduleArtifact.exists()) {
            throw new IllegalStateException("Unable to locate module in expected location! \r\n$moduleArtifact.absolutePath");
        }

        // Grab the file extension
        final String ext = originalArtifact.getExtension()

        // Parse requested artifact format. This works by looking for %...% group matches and
        // running string replace with the match as the needle and a dynamic property or method
        // result as the value.
        String outputName = artifactFormat;
        def matcher = outputName =~ /%([^%]+)%/
        def renameTaskInstance = this
        matcher.each {
            match, matchGroup ->
                String value;

                // Attempt to replace the match with the first likely match
                if ("ext".equals(matchGroup)) {
                    logD "Matched hard code ext for %$matchGroup%"
                    value = ext.toString()
                } else if (project.hasProperty(matchGroup.toString())) {
                    value = project."$matchGroup"
                    logD "Matched project property for %$matchGroup%"
                } else if (project.metaClass.respondsTo(renameTaskInstance, matchGroup.toString())) {
                    value = project."$matchGroup"()
                    logD "Matched project metaClass method for %$matchGroup%"
                } else if (dynamicProperties.get(matchGroup) != null) {
                    value = dynamicProperties.get(matchGroup)
                    logD "Matched dynamic property for %$matchGroup%"
                } else if (hasProperty(matchGroup.toString())) {
                    value = "$matchGroup"
                    logD "Matched internal property for %$matchGroup%"
                } else if (renameTaskInstance.metaClass.respondsTo(renameTaskInstance, matchGroup.toString())) {
                    value = "$matchGroup"()
                    logD "Matched project renameTaskInstance.metaClass method for %$matchGroup%"
                } else {
                    throw new IllegalArgumentException("Requested group '$matchGroup' is not a method  or property.")
                }

                outputName = outputName.replace("%$matchGroup%", value)
        }

        // Debug formatting results
        logD "Requested artifact format: $artifactFormat"
        logD "Formatted artifact name: $outputName"

        // Move the artifact to reflect the new name
        File destFile = new File(moduleArtifact.getParentFile(), outputName)
        project.configurations.archives.artifacts[0].file.renameTo(destFile)
        project.configurations.archives.artifacts.remove(originalArtifact)
        project.configurations.archives.artifacts.add(new UpdatedPublishArtifact(originalArtifact, destFile))
    }

    boolean hasPlugin(String plugin) {
        project.plugins.hasPlugin(plugin)
    }

    String gitSha1() {
        String sha1 = executeCommand("git rev-parse HEAD")
        logD "Git SHA1: $sha1"
        return sha1
    }

    public String gitSha1Short() {
        String sha1 = executeCommand("git rev-parse --short HEAD")
        logD "Git SHA1 short: $sha1"
        return sha1
    }

    String gitBranch() {
        String branch = executeCommand("git rev-parse --abbrev-ref HEAD")
        logD "Git branch: $branch"
        return branch
    }

    static String executeCommand(String command) {
        return command.execute().text.trim()
    }

    void logD(String msg) {
        if (logDebug) {
            println msg;
        }
    }

}
