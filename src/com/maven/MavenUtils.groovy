// src/com/maven/MyLibrary.groovy
package com.maven

class MavenUtils {
    // Method to extract artifactId from pom.xml
    public static String getArtifactIdFromPom(String pomFilePath) {
        try {
            // Log current working directory to ensure correct path
            println "Current working directory: ${System.getProperty('user.dir')}"
            // Read the pom.xml file using XmlParser and extract artifactId
            def pom = new XmlParser().parse(pomFilePath)
            def artifactId = pom.artifactId?.text()

            println "${artifactId}"

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
    public static String getVersionFromPom(String pomFilePath) {
        try {
            // Log current working directory to ensure correct path
            println "Current working directory: ${System.getProperty('user.dir')}"
            
            // Read the pom.xml file using XmlParser and extract version
            def pom = new XmlParser().parse(pomFilePath)
            def version = pom.version?.text()

            println "${version}"

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

}

