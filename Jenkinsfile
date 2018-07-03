pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'echo "Hello world!"'
            }
        }
        stage('Test') {
            steps {
                sh 'echo "Testing 1, 2, 3..."'
            }
        }
        stage('master branch stage') {
            when {
                branch 'master'
            }
            steps {
                sh 'echo "Running on master branch! Testing if PR triggers new pipeline run."'
            }
        }
        stage('import-scripts stage') {
            when {
                branch 'import-scripts'
            }
            steps {
                sh 'echo "Running on import-scripts branch!"'
            }
        }
    }
}
