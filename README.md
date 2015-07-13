[![Build Status](https://travis-ci.org/ToxicBakery/BettererNaming.svg?branch=master)](https://travis-ci.org/ToxicBakery/BettererNaming)

# Betterer Naming

Betterer Naming is a Gradle plugin for making the Android release artifact naming betterer. The default naming of Android release applications and libraries with gradle is ```<module name>-<status>.<apk|aar>```. This plugin changes the default name to ```<module name>-<status>-<version>-<git branch>-<git sha1 short>.<apk|aar>``` and is configurable to support any other naming order or style. You can even include environment variables such as those delivered by your build system.

## Why

I use Jenkins to manage my builds and I have a custom web interface for users to download builds from. Before this plugin I had a Python script making API calls to Jenkins to fix my artifact names which is obviously silly.

### How to Use

Usage is simple! You simply need to add the following to your Android module build.gradle **AFTER** ```apply plugin: 'com.android.application'``` or ```apply plugin: 'com.android.library'```.

```
apply plugin: 'com.ToxicBakery.betterernaming'

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.ToxicBakery.betterernaming:betterer-naming:+'
    }
}
```

### Important Note

Betterer Naming will occur when you select to create a signed release build. This is important if you expect your debug builds to be renamed as they will not be. For ultimate clarification a release build is created by running the assembleRelease task, ```gradlew assembleRelease```. If you attempt to do this step from Android Studio by selecting 'assembleRelease' from the Gradle tasks menu, take note that is is imperative that you select the asssembleRelease task for the module and not for the root project. I can only assume this is another marvelous Android Studio ~~bug~~ **feature**.

## Dependencies

This plugin obviously depends on Gradle and Android; however, it also depends on Git being installed and available on your path. The [Git SCM](http://git-scm.com/book/en/v2/Getting-Started-Installing-Git) site should have everything you need. Make sure your build system is also properly configured.

### Windows

Ensure that your PATH points to the bin directory of your Git installation. For most common installations this means adding ```C:\Program Files (x86)\Git\bin``` to your path. After updating the path, open a **new** Command Prompt and test that the path worked by using the ```git``` command.

### Linux

Simply installing Git should be sufficient. For YUM based distributions, use ```yum install git```. For APT based distributions, use ```apt-get install git```.

### Mac

Simply installing Git should be sufficient. If you use Brew you can use ```brew install git```

## Customizations

As mentioned, Betterer Naming is betterer due to flexibility. The default name I picked is probably not whatever filename  you need so here is how you fix that. Use the following DSL samples in the module you applied the plugin and thus want to make betterer.

### A Basic Config

Simple demonstration of reverting back to the stock artifact naming.
```
rename {
    // Be weird and go back to the lame stock naming
    artifactFormat = '%name%-%status%.%ext%'
}
```

### Insert a custom parameter

Custom parameters can be easily inserted by use of dynamic variables. Below a dynamic variable is used to insert the BUILD_NUMBER provided by Jenkins.
```
rename {
    ext.buildNumber = BUILD_NUMBER
    artifactFormat = '%buildNumber%.%ext%'
}
```

For fun, generate a timestamp of the build and use it in the artifact name.
```
def buildTime() {
       def df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
       df.setTimeZone(TimeZone.getTimeZone("UTC"))
       return df.format(new Date())
}

rename {
    ext.timestamp = buildTime()
    artifactFormat = '%timestamp%.%ext%'
}
```

### Provided Git Information
A few basic pieces of information are available for use. The current list is as follows.


|Parameter|Description|Command|
|---|---|---|
|gitSha1|Full SHA1 hash of the current checkout.|```git rev-parse HEAD```|
|gitSha1Short|Short SHA1 hash of the current checkout.|```git rev-parse --short HEAD```|
|gitBranch|Branch name of the current checkout.|```git rev-parse --abbrev-ref HEAD```|

### Debug

If you are trying to use a custom parameter and you get an unexpected result or an exception, you can use debugging to explain where the failure occurred or why the parameter pulled from an unexpected source. To enable debugging use the ```--debug``` flag.
