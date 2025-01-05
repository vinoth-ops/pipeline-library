// vars/build.groovy
def call() {
    echo 'Building the application...'
    // build.groovy

    // Fetch Docker Hub credentials from Jenkins credentials store
    def dockerHubUsername = ''
    def dockerHubPassword = ''
    def dockerCredentialsId = 'docker-hub-credentials'  // ID of the credentials in Jenkins
    
    // Fetch Docker Hub credentials using Jenkins' credentials binding
    node {
        withCredentials([usernamePassword(credentialsId: dockerCredentialsId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
            dockerHubUsername = env.DOCKER_USER
            dockerHubPassword = env.DOCKER_PASS
    
            // Step 1: Extract artifactId and version from the pom.xml using grep
            def artifactId = getArtifactIdFromPom()
            def version = getVersionFromPom()
    
            if (artifactId == null || version == null) {
                println "Unable to extract artifactId or version from pom.xml"
                System.exit(1)
            }
    
            println "Artifact ID extracted from pom.xml: $artifactId"
            println "Version extracted from pom.xml: $version"
    
            // Step 2: Build Docker image
            def dockerTag = "$dockerHubUsername/$artifactId:$version"
            println "Building Docker image with tag: $dockerTag"
    
            def buildCommand = "docker build -t $dockerTag ."
            def buildResult = executeCommand(buildCommand)
    
            if (buildResult != 0) {
                println "Docker build failed"
                System.exit(1)
            }
    
            // Step 3: Log in to Docker Hub
            def loginCommand = "echo $dockerHubPassword | docker login -u $dockerHubUsername --password-stdin"
            def loginResult = executeCommand(loginCommand)
    
            if (loginResult != 0) {
                println "Docker login failed"
                System.exit(1)
            }
    
            println "Logged into Docker Hub successfully"
    
            // Step 4: Push Docker image to Docker Hub
            def pushCommand = "docker push $dockerTag"
            def pushResult = executeCommand(pushCommand)
    
            if (pushResult != 0) {
                println "Docker push failed"
                System.exit(1)
            }
    
            println "Docker image pushed successfully"
        }
    }
    
    // Function to extract artifactId from pom.xml using grep
    def getArtifactIdFromPom() {
        try {
            // Use grep to extract artifactId from pom.xml
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
    
    // Function to extract version from pom.xml using grep
    def getVersionFromPom() {
        try {
            // Use grep to extract version from pom.xml
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
    
    // Function to execute shell commands and return the exit status
    def executeCommand(command) {
        println "Executing: $command"
        def process = command.execute()
        process.waitFor()
        return process.exitValue()
    }


}

