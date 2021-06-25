node ('windows') {
    def server = Artifactory.server 'Artifactory_srv'

    def uploadSpec = """{
        "files": [
            {
                "pattern": "ConsoleApp1/bin/Debug/*",
                "target": "result/"
            }
        ]
    }"""

    def downloadSpec = """{
        "files": [
            {
                "pattern": "result/*.exe",
                "target": "./"
            }
        ]
    }"""

    def buildInfo1 = server.download downloadSpec
    def buildInfo2 = server.upload uploadSpec

    stage ('Clean Workspace') {
        cleanWs()
    }

    stage ('Clone') {
        git url: 'git@github.com:Gulivetor/ConsoleApp1.git'
    }

    stage ('Build') {
        //bat 'dotnet restore .\\ConsoleApp1.sln'
        bat "msbuild .\\ConsoleApp1.sln"
    }

    stage ('Scan') {
        def scannerHome = tool 'sonar_msbuild'

        withSonarQubeEnv('sonarqube_srv') {
            bat script: """
                ${scannerHome}\\SonarScanner.MSBuild.exe begin /k:"Testing"
            """
            bat "MSBuild.exe .\\ConsoleApp1.sln /t:Rebuild"
            bat "${scannerHome}\\SonarScanner.MSBuild.exe end"
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
        server.download spec: downloadSpec
    }   
        
    stage ('Build info') {
        buildInfo1.append buildInfo2
        server.publishBuildInfo buildInfo1
    }

}