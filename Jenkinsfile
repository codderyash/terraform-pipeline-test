pipeline {
    agent any

    stages {
        stage('checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/yashsa-fens/terraform-pipeline.git']])
            }
        }
        stage('init'){
            steps{
                sh('terraform init -reconfigure')
            }
        }
        stage('plan'){
            steps{
                sh('terraform plan')
            }
        }
       
    }
}
