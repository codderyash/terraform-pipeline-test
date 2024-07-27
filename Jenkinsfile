pipeline {
    agent any
      enviroment{
        AWS_ACCESS_KEY_ID=credentials('AWS_ACCESS_KEY_ID')
        AWS_SECRET_ACCESS_KEY=credentials('AWS_SECRET_ACCESS_KEY')
      }


    stages {
        stage('checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/yashsa-fens/terraform-pipeline.git']])
            }
        }
        stage('run bash script'){
            steps{
                 
                    bat 'script.sh all apply'
                
            }
            
        }
        
       
    }
}
