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

package com.ToxicBakery.betterernaming;

import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.tasks.TaskDependency;

import java.io.File;
import java.util.Date;

public class UpdatedPublishArtifact implements PublishArtifact {

    final PublishArtifact originalArtifact;
    final File newLocation;

    UpdatedPublishArtifact(PublishArtifact originalArtifact, File newLocation) {
        this.originalArtifact = originalArtifact;
        this.newLocation = newLocation;
    }

    @Override
    public String getName() {
        return originalArtifact.getName();
    }

    @Override
    public String getExtension() {
        return originalArtifact.getExtension();
    }

    @Override
    public String getType() {
        return originalArtifact.getType();
    }

    @Override
    public String getClassifier() {
        return originalArtifact.getClassifier();
    }

    @Override
    public File getFile() {
        return newLocation;
    }

    @Override
    public Date getDate() {
        return originalArtifact.getDate();
    }

    @Override
    public TaskDependency getBuildDependencies() {
        return originalArtifact.getBuildDependencies();
    }

}
