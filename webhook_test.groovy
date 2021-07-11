@Library('pipeline-library-demo')_

node ('windows') {
    stage ('Clone') {
        cloneRepo 'jenkinsfiles'
    }
}