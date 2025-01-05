// src/com/maven/MyLibrary.groovy
package com.maven

class MavenUtils {
    // Method to extract artifactId from pom.xml
    static String getArtifactIdFromPom() {
        try {
            echo "executing getArtifactIdFromPom method"

            sh 'ls -l'  // List files in the current directory
            
            def artifactId = sh(script: "grep -oPm1 '(?<=<artifactId>)[^<]+' pom.xml", returnStdout: true).trim()
            
            if (artifactId.isEmpty()) {
                echo "No artifactId found in pom.xml"
                return null
            }
            return artifactId
        } catch (Exception e) {
            echo "Error extracting artifactId: ${e.message}"
            return null
        }
    }

    // Method to extract version from pom.xml
    static String getVersionFromPom() {
        try {
            echo " executing getVersionFromPom"
            def artifactId = sh(script: "grep -oPm1 '(?<=<version>)[^<]+' pom.xml", returnStdout: true).trim()
            if (version.isEmpty()) {
                echo "No version found in pom.xml"
                return null
            }
            return version
        } catch (Exception e) {
            echo "Error extracting version: ${e.message}"
            return null
        }
    }

    // Method to execute shell commands and return exit status
    static int executeCommand(String command) {
        echo "Executing: $command"
        def process = command.execute()
        process.waitFor()
        return process.exitValue()
    }
}

