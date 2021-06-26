node ('windows') {
    def server = Artifactory.server 'Artifactory_srv'

    def uploadSpec = """{
        "files": [
            {
                "pattern": "build/",
                "target": "gradle_test/build/"
            },
            {
                "pattern": "src/",
                "target": "gradle_test/src/"
            }
        ]
    }"""

    def downloadSpec = """{
        "files": [
            {
                "pattern": "gradle_test/*",
                "target": "./artifactory_download/"
            }
        ]
    }"""

    def buildInfo1 = server.download downloadSpec
    def buildInfo2 = server.upload uploadSpec

    stage ('Clean Workspace') {
        cleanWs()
    }

    stage ('Clone') {
        git branch: 'bad-code', url: 'git@github.com:Gulivetor/sonarqube-jacoco-code-coverage.git'
    }

    stage ('Scan') {
        withSonarQubeEnv('sonarqube_srv') {
            bat "gradlew sonarqube"
        }
    }

    stage ('Quality Gate') {
        timeout (time: 10, unit: 'SECONDS'  ) {
            waitForQualityGate abortPipeline: true
        }
    }

    stage ('Upload') {
        server.upload spec: uploadSpec
    }

    stage ('Download') {
        pwsh script: "mkdir artifactory_download"
        pwsh script: "pwd"
        server.download spec: downloadSpec
    }   
        
    stage ('Build info') {
        buildInfo1.append buildInfo2
        server.publishBuildInfo buildInfo1
    }
}