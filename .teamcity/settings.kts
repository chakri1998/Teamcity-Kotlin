import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.Swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot


/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.1"

project {
    vcsRoot(CompleteDevOpsvcs)
    description = "This a sample kotlin demo project pipeline as a code edited in vcs."
    buildType(Build)
}

object Build : BuildType({
    name = "CompleteDevOps_Kotlin_Pipeline"
    description = "This a sample kotlin demo project pipeline as a code edited in vcs only CI will takes place."
    artifactRules = "target/*war"
    vcs {
        root(CompleteDevOpsvcs)
    }
    steps {
           maven {
        name = "Maven-Clean"
        goals = "clean"
        pomLocation = "devops/pom.xml"
        jdkHome = "%env.JDK_18%"
        
    }
    maven {
        name = "Maven-Install"
        goals = "install"
        pomLocation = "devops/pom.xml"
        jdkHome = "%env.JDK_18%"
        param("org.jfrog.artifactory.selectedDeployableServer.publishBuildInfo", "true")
        param("org.jfrog.artifactory.selectedDeployableServer.defaultModuleVersionConfiguration", "GLOBAL")
        param("org.jfrog.artifactory.selectedDeployableServer.urlId", "0")
        param("org.jfrog.artifactory.selectedDeployableServer.envVarsExcludePatterns", "*password*,*secret*")
        param("org.jfrog.artifactory.selectedDeployableServer.targetSnapshotRepo", "Devops")
        param("org.jfrog.artifactory.selectedDeployableServer.deployArtifacts", "true")
        param("org.jfrog.artifactory.selectedDeployableServer.targetRepo", "Devops")
    }
        script {
        name = "Download-Artifactory"
        scriptContent = "curl -u admin:AKCp5fUDpkeBGfcYv8y16nFWUqCM42d6FPnMYmG8nyK4ehRRgEkhfkmAPPmWBUjzGP35yC5Np http://localhost:8082/artifactory/Devops/com/mindtree/devops/0.0.1-SNAPSHOT/devops-0.0.1-SNAPSHOT.war -o /home/cloud_user/artifactory/ROOT.war"
        param("org.jfrog.artifactory.selectedDeployableServer.downloadSpecSource", "Job configuration")
        param("org.jfrog.artifactory.selectedDeployableServer.useSpecs", "false")
        param("org.jfrog.artifactory.selectedDeployableServer.uploadSpecSource", "Job configuration")
    }
    }
    triggers {
        vcs {
            groupCheckinsByCommitter = true
        }
    }
})
object CompleteDevOpsvcs : GitVcsRoot({
    name = "CompleteDevOpsvcs"
    url = "https://github.com/chakri1998/completedevops.git"
})
fun wrapWithFeature(buildType: BuildType, featureBlock: BuildFeatures.() -> Unit): BuildType {
    buildType.features {
        featureBlock()
    }
    return buildType
}
