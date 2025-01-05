// src/com/maven/MyLibrary.groovy
package com.maven

class MavenUtils {
    // Method to extract artifactId from pom.xml
    String getArtifactIdFromPom(String pomFilePath) {
        try {
            // Read the pom.xml file using XmlParser and extract artifactId
            def pom = new XmlParser().parse(pomFilePath)
            def artifactId = pom.artifactId?.text()

            if (artifactId == null || artifactId.isEmpty()) {
                println "Error: No artifactId found in pom.xml"
                return null
            }

            // Return the extracted artifactId
            return artifactId
        } catch (Exception e) {
            // Print any errors encountered
            println "Error extracting artifactId: ${e.message}"
            return null
        }
    }

    // Method to extract version from pom.xml
    String getVersionFromPom(String pomFilePath) {
        try {
            // Read the pom.xml file using XmlParser and extract version
            def pom = new XmlParser().parse(pomFilePath)
            def version = pom.version?.text()

            if (version == null || version.isEmpty()) {
                println "Error: No version found in pom.xml"
                return null
            }

            // Return the extracted version
            return version
        } catch (Exception e) {
            // Print any errors encountered
            println "Error extracting version: ${e.message}"
            return null
        }
    }

    // Method to execute shell commands and return exit status
    int executeCommand(String command) {
        try {
            // Logging the command with println (echo is not available in regular Groovy class)
            println "Executing: $command"

            // Execute the shell command
            def process = command.execute()

            // Wait for the process to finish
            process.waitFor()

            // Return the exit status of the process
            return process.exitValue()
        } catch (Exception e) {
            // If there are any errors, print the error message
            println "Error executing command: ${e.message}"
            return -1  // Return -1 to indicate failure
        }
    }
}

