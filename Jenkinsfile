pipeline {
    agent any
      parameters {
        string(name: 'val1', defaultValue: 'all', description: 'Parameter 1')
        string(name: 'val2', defaultValue: 'plan', description: 'Parameter 2')
    }


    stages {
        stage('checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/yashsa-fens/terraform-pipeline.git']])
            }
        }
        stage('run bash script'){
            steps{
                bat 'script.sh ${params.val1} ${params.val2}'
            }
            
        }
       
    }
}
