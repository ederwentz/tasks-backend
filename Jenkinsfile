
pipeline {
    agent any
    //node
    stages {
        stage ('Build Backend') {
            steps {
                //bat 'mvn clean package -DskipTests=true'
                //sh 'mvn install'
                sh 'mvn clean package -DskipTests=true'
            }
        }
        stage ('Unit Tests') {
            steps {
                //bat 'mvn test'
                sh 'mvn test'
            }
        }
        stage ('Sonar Analysis') {
            environment {
                def scannerHome = tool 'SONAR_SCANNER';
//                scannerHome = tool 'SONAR_SCANNER';
            }
            steps {
                withCredentials([string(credentialsId: 'SonarQube', variable: 'TokenSonarQube')]){
                withSonarQubeEnv('SONAR_LOCAL') {
                    //bat "\"${scannerHome}/bin/sonar-scanner\" -e -Dsonar.projectKey=DeployBackend -Dsonar.host.url=http://localhost:9000 -Dsonar.login=${TokenSonarQube} -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
                    sh "\"${scannerHome}/bin/sonar-scanner\" -e -Dsonar.projectKey=Deploy_Backend -Dsonar.host.url=http://192.168.1.118:9000 -Dsonar.login=a5d7a261967227a2f339806efb7c4dd6550424c2 -Dsonar.java.binaries=target -Dproject.build.sourceEncoding=UTF-8 -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
                    }
                }
            }
        }
        
        stage ('Quality Gate') {
            steps {
                sleep(21)
                timeout(time: 1, unit: 'MINUTES') {
                waitForQualityGate abortPipeline: true
                }
            }
        }

        stage ('Deploy Backend') {
            steps {
                deploy adapters: [tomcat9(credentialsId: 'TomcatLogin', path: '', url: 'http://192.168.1.25:8001/')], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
            }
        }
        stage ('API Test') {
            steps {
                dir('api-test') {
                    git credentialsId: 'GithubLogin', url: 'https://github.com/ederwentz/tasks-api-test'
                    //bat 'mvn test'
                    sh 'mvn test'
                }
            }
        }
        stage ('Deploy Frontend') {
            steps {
                dir('frontend') {
                    git credentialsId: 'GithubLogin', url: 'https://github.com/ederwentz/tasks-frontend'
                    //bat 'mvn clean package'
                    sh 'mvn clean package'
                    deploy adapters: [tomcat9(credentialsId: 'TomcatLogin', path: '', url: 'http://192.168.1.25:8001/')], contextPath: 'tasks', war: 'target/tasks.war'
                }
            }
        }
        stage ('Functional Test') {
            steps {
                dir('functional-test') {
                    git credentialsId: 'GithubLogin', url: 'https://github.com/ederwentz/tasks-functional-test'
                    //bat 'mvn test'
                    sh 'mvn test'
                }
            }
        }
        //stage ('Limpeza deploy Prod') {
        //    steps {
        //         bat 'powershell.exe "docker rmi $(docker images -f "reference=*build*" -q)"'
        //      }
        //    }
        stage('Deploy Prod') {
            steps {
                //bat 'docker-compose build'
                //bat 'docker-compose up -d'
                sh 'docker-compose build'
                sh 'docker-compose up -d'
            }
        }

        stage ('Health Check') {
            steps {
                sleep(5)
                dir('functional-test') {
                    //bat 'mvn verify -Dskip.surefire.tests'
                    sh 'mvn verify -Dskip.surefire.tests'
                }
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml, api-test/target/surefire-reports/*.xml, functional-test/target/surefire-reports/*.xml, functional-test/target/failsafe-reports/*.xml'
            archiveArtifacts artifacts: 'target/tasks-backend.war, frontend/target/tasks.war', onlyIfSuccessful: true
        }
        unsuccessful {
            emailext attachLog: true, body: 'See the attached log below', subject: 'Build $BUILD_NUMBER has failed', to: 'eder.wentz+jenkins@gmail.com'
        }
        fixed {
            emailext attachLog: true, body: 'See the attached log below', subject: 'Build is fine!!!', to: 'eder.wentz+jenkins@gmail.com'
        }
    }
}
