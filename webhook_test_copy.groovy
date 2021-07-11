node ('windows') {
    stage ('Clone') {
        echo 'Branch name : ' + BRANCH
        echo 'URL : ' + SSH_URL
        git branch: BRANCH, credentialsId: 'jenkins-github', url: 'git@github.com:Gulivetor/jenkinsfiles.git'
    }
}