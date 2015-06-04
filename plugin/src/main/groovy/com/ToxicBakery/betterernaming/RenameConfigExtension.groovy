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

import java.text.SimpleDateFormat

@SuppressWarnings(["GroovyUnusedDeclaration", "GrMethodMayBeStatic"])
class RenameConfigExtension {

    String artifactFormat = '%name%-%versionName%-%gitBranch%-%gitSha1Short%-%timeStamp%.%ext%';

    String gitSha1() {
        return executeCommand("git rev-parse HEAD")
    }

    String gitSha1Short() {
        return executeCommand("git rev-parse --short HEAD")
    }

    String gitBranch() {
        return executeCommand("git rev-parse --abbrev-ref HEAD")
    }

    String timeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy-h-mm", Locale.ENGLISH)
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0000"))
        return simpleDateFormat.format(new Date())
    }

    static String executeCommand(String command) {
        return command.execute().text.trim()
    }

}