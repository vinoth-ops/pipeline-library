// vars/build.groovy

// Import the class from the 'src/com/maven' package
import com.maven.MyLibrary

// Define the pipeline directly
pipeline {
    agent any

    stages {
        stage('Build and Push Docker Image') {
            steps {
                script {
                    def dockerHubUsername = ''
                    def dockerHubPassword = ''

                    // Fetch Docker Hub credentials using Jenkins' credentials binding
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        dockerHubUsername = env.DOCKER_USER
                        dockerHubPassword = env.DOCKER_PASS

                        // Step 1: Extract artifactId and version from the pom.xml using the MavenUtils class
                        def artifactId = MyLibrary.getArtifactIdFromPom()
                        def version = MyLibrary.getVersionFromPom()

                        if (artifactId == null || version == null) {
                            println "Unable to extract artifactId or version from pom.xml"
                            currentBuild.result = 'FAILURE'
                            return
                        }

                        println "Artifact ID extracted from pom.xml: $artifactId"
                        println "Version extracted from pom.xml: $version"

                        // Step 2: Build Docker image
                        def dockerTag = "$dockerHubUsername/$artifactId:$version"
                        println "Building Docker image with tag: $dockerTag"

                        def buildCommand = "docker build -t $dockerTag ."
                        def buildResult = MyLibrary.executeCommand(buildCommand)

                        if (buildResult != 0) {
                            println "Docker build failed"
                            currentBuild.result = 'FAILURE'
                            return
                        }

                        // Step 3: Log in to Docker Hub
                        def loginCommand = "echo $dockerHubPassword | docker login -u $dockerHubUsername --password-stdin"
                        def loginResult = MyLibrary.executeCommand(loginCommand)

                        if (loginResult != 0) {
                            println "Docker login failed"
                            currentBuild.result = 'FAILURE'
                            return
                        }

                        println "Logged into Docker Hub successfully"

                        // Step 4: Push Docker image to Docker Hub
                        def pushCommand = "docker push $dockerTag"
                        def pushResult = MyLibrary.executeCommand(pushCommand)

                        if (pushResult != 0) {
                            println "Docker push failed"
                            currentBuild.result = 'FAILURE'
                            return
                        }

                        println "Docker image pushed successfully"
                    }
                }
            }
        }
    }
}
