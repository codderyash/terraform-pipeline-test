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
                script {
                    def scriptOutput = bat(script: 'script.sh', returnStdout: true).trim()
                      echo "${params.val1}"
                      echo "${params.val2}"
                }
            }
            
        }
        stage('Give input'){
            steps{
                 script{
                      echo "${params.val1}"
                      echo "${params.val2}"
                  }
            }
         
            
        }
       
    }
}
