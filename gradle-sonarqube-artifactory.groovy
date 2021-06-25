node ('windows') {

    stage ('Clean Workspace') {
        cleanWs()
    }

    stage ('Clone') {
        git url: 'git@github.com:Gulivetor/sonarqube-jacoco-code-coverage.git'
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
    
}