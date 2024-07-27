def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {

        def JAVA_HOME_PATH = "/usr/lib/jvm/java-17-openjdk"  
        def ECR_REGISTRY = "403855341024.dkr.ecr.ap-south-1.amazonaws.com"
        def ECR_REGION = 'ap-south-1'

        stage('Checkout') {
            checkout scm
        }
        stage ("Setup Parameters") {
            script{
                properties(

                    [
                        parameters(
                            [
                                booleanParam(name: 'build_docker_image', defaultValue: false, description: 'if build_docker_image=true, docker image would be created from feature branch with branch name as a tag. for develop and main branch it will always create a docker image, irrespective of this parameter.'),
                            ]
                        )
                    ]
                )
            }
        }
        if (config.LINT_TEST == true){
            stage('Lint and test') {
            // Checkstyle and Unit tests
                if (config.GRPC_PORT && config.AWS_REGION)
                    sh "AWS_REGION=${config.AWS_REGION} QUARKUS_GRPC_SERVER_PORT=${config.GRPC_PORT} JAVA_HOME=${JAVA_HOME_PATH} mvn clean verify"
                else if (config.AWS_REGION)
                    sh "AWS_REGION=${config.AWS_REGION} JAVA_HOME=${JAVA_HOME_PATH} mvn clean verify"
                else
                    sh "JAVA_HOME=${JAVA_HOME_PATH} mvn clean verify"
            }
        }
        if (config.SONARQUBE) {
            stage('Sonarqube Analysis') {
                withSonarQubeEnv('Fens Sonarqube') {
                    if (config.XMLREPORTPATHS && config.PLFLAG)
                        sh """JAVA_HOME=${JAVA_HOME_PATH} mvn clean install -pl :${config.PLFLAG} -am sonar:sonar -Dsonar.projectKey=${config.SONAR_PROJECT_KEY} -Dsonar.coverage.exclusions=${config.COVERAGE_EXCLUSIONS} -Dsonar.coverage.jacoco.xmlReportPaths=${env.WORKSPACE}/${config.XMLREPORTPATHS} """
                    else if (config.GRPC_PORT && config.AWS_REGION)
                        sh """ AWS_REGION=${config.AWS_REGION} QUARKUS_GRPC_SERVER_PORT=${config.GRPC_PORT} JAVA_HOME=${JAVA_HOME_PATH} mvn clean install sonar:sonar -Dsonar.projectKey=${config.SONAR_PROJECT_KEY} -Dsonar.coverage.exclusions=${config.COVERAGE_EXCLUSIONS} """
                    else if (config.AWS_REGION)
                        sh """AWS_REGION=${config.AWS_REGION} JAVA_HOME=${JAVA_HOME_PATH}  mvn clean install sonar:sonar -Dsonar.projectKey=${config.SONAR_PROJECT_KEY} -Dsonar.coverage.exclusions=${config.COVERAGE_EXCLUSIONS} """
                    else 
                        sh """JAVA_HOME=${JAVA_HOME_PATH}  mvn clean install sonar:sonar -Dsonar.projectKey=${config.SONAR_PROJECT_KEY} -Dsonar.coverage.exclusions=${config.COVERAGE_EXCLUSIONS} """
                    sh """
                        sleep 5
                        echo "=============> Caution: The following SonarQube quality gate result can be wrong <============="
                        QUALITY_STATUS=\$(curl -u '${env.SONAR_AUTH_TOKEN}:' '${env.SONAR_HOST_URL}/api/qualitygates/project_status?projectKey=${config.SONAR_PROJECT_KEY}'|grep -Po '"projectStatus":{"status":".*?"'|perl -pe 's/"projectStatus":\\{"status":"//; s/"\$//')
                        echo "=========================================================="
                        echo "SONAR QUALITY GATE RESULT: \${QUALITY_STATUS}"
                        echo "=========================================================="
                        if [ "\${QUALITY_STATUS}" != "OK" ]
                        then
                        echo "SonarQube quality gate failed!" >&2
                        exit 1
                        fi
                    """
                }
            }
        }
        if (config.BUILD_PACKAGE){
            stage('Build package') {
                script {
                    if (config.LINT_TEST == false )
                        sh "JAVA_HOME=${JAVA_HOME_PATH} mvn clean package -DskipTests"
                    else if (config.GRPC_PORT && config.AWS_REGION)
                        sh "AWS_REGION=${config.AWS_REGION} QUARKUS_GRPC_SERVER_PORT=${config.GRPC_PORT} JAVA_HOME=${JAVA_HOME_PATH} mvn clean package"
                    else if (config.AWS_REGION)
                        sh "AWS_REGION=${config.AWS_REGION} JAVA_HOME=${JAVA_HOME_PATH} mvn clean package"
                    else
                        sh "JAVA_HOME=${JAVA_HOME_PATH} mvn clean package"
                }
            }
        }
        stage('Build, tag and push Docker image to AWS ECR') {
            script {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'AWS Credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]){
                    docker.withRegistry("https://${ECR_REGISTRY}", "ecr:${ECR_REGION}:AWS Credentials") {
                        config.BUILD_PARAMETERS.each{ key, value ->
                            sh """aws ecr create-repository --region ${ECR_REGION} --repository-name ${key} || true """
                            IMAGE = docker.build("${ECR_REGISTRY}/${key}", "-f ${value} .")
                            if (env.BRANCH_NAME == 'main')
                                IMAGE.push(config.SERVICE_VERSION+"-"+env.BUILD_NUMBER)
                            else if(env.BRANCH_NAME == 'develop')
                                IMAGE.push('latest')
                            else if (params.build_docker_image )
                                IMAGE.push(env.BRANCH_NAME)
                            
                        }
                    }    
                }
            }
        }
       stage("run script"){
        script{
             withCredentials([[
                    credentialsId: 'AWS Credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]){

                    bat 'script.sh all destroy'
                }
        }
       }
    
        stage("Cleanup") {
            cleanWs()
        }       
    }
}
