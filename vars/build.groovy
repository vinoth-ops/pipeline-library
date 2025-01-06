// vars/build.groovy

// Import the MavenUtils class (located in src folder)
import com.maven.MavenUtils
import com.maven.Utils

// Define the build logic function (without the pipeline block)
def runBuildAndPushDockerImage(String credentialsId) {
    // Fetch Docker Hub credentials using Jenkins' credentials binding
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
        // Use methods from MavenUtils (class in src/com/maven/MavenUtils.groovy)
        println "Starting runBuildAndPushDockerImage"

        
        def artifactId = MavenUtils.getArtifactIdFromPom(this)
        def version = MavenUtils.getVersionFromPom(this)

        println "ArtifactId extracted from POM: $artifactId"
        println "Artifact version extracted from POM: $version"

        if (artifactId == null || version == null) {
            currentBuild.result = 'FAILURE'
            error("Failed to extract artifactId or version.")
        }

        // Set environment variables
        env.ARTIFACT_ID = artifactId
        env.ARTIFACT_VERSION = version

        println "updated environment variables for artifact ID and version ${env.ARTIFACT_ID} : ${env.ARTIFACT_VERSION}"

        println "Building Docker image for artifactId: $artifactId and version: $version"

        def dockerTag = "$DOCKER_USER/$artifactId:$version"
        sh "docker build -t $dockerTag ."
        
        // Log into Docker Hub and push the image
        sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
        sh "docker push $dockerTag"
    }
}
