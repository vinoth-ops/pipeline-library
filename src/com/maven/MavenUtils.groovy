// src/com/maven/MyLibrary.groovy
package com.maven

class MavenUtils {
    // Method to extract artifactId from pom.xml
    static String getArtifactIdFromPom() {
        try {
            def artifactId = "grep -oPm1 '(?<=<artifactId>)[^<]+' pom.xml".execute().text.trim()
            if (artifactId.isEmpty()) {
                println "No artifactId found in pom.xml"
                return null
            }
            return artifactId
        } catch (Exception e) {
            println "Error extracting artifactId: ${e.message}"
            return null
        }
    }

    // Method to extract version from pom.xml
    static String getVersionFromPom() {
        try {
            def version = "grep -oPm1 '(?<=<version>)[^<]+' pom.xml".execute().text.trim()
            if (version.isEmpty()) {
                println "No version found in pom.xml"
                return null
            }
            return version
        } catch (Exception e) {
            println "Error extracting version: ${e.message}"
            return null
        }
    }

    // Method to execute shell commands and return exit status
    static int executeCommand(String command) {
        println "Executing: $command"
        def process = command.execute()
        process.waitFor()
        return process.exitValue()
    }
}

