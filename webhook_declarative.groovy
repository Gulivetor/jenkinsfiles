pipeline {
    agent {label 'windows'}
    environment {
        IS_AUTO_TRIGGERED = false;
        userInput = null;
        target_dir= null;
    }
    stages {
        stage('Checkout') {
           steps {
              script {
                 echo 'hello pipeline: '+ env.myParameter
              }
          }
        }
    }
}