pipeline {
    agent any
      


    stages {
        stage('checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/yashsa-fens/terraform-pipeline.git']])
            }
        }
        stage('run bash script'){
            steps{
                 withAWS(credentials: '16552b1e-b971-4018-93f4-abee8968c654', region: 'ap-south-1') {
                    bat 'script.sh all apply'
                }
            }
            
        }
        
       
    }
}
