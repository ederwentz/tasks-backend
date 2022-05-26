pipeline {
    agent any
    //node
    stages {
        stage ('Build Backend') {
            steps {
                sh 'mvn clean package -DskipTests=true'
            }
        }
        stage ('Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }
        stage ('Sonar Analysis') {
            environment {
//                target = '${SONAR_RUN_WORKSPACE}'
//                SonarToken = 'cfe8ea9cdf527feaa5aefac54460f1d7a565e9d4'
                def scannerHome = tool 'SONAR_SCANNER';
            }
            steps {
                withSonarQubeEnv('SONAR_LOCAL') {
//                    sh "mvn sonar:sonar -Dsonar.projectKey=DeployBackend -Dsonar.login=e89f6b58d5a145cd62c7c08d1ad06d07fb9ca9c0 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
//                    sh "mvn clean install sonar:sonar -Dsonar.projectKey=DeployBackend -Dsonar.login=e89f6b58d5a145cd62c7c08d1ad06d07fb9ca9c0 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
//                    sh "mvn clean package sonar:sonar -Dsonar.projectKey=DeployBackend -Dsonar.login=e89f6b58d5a145cd62c7c08d1ad06d07fb9ca9c0 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
                    sh "mvn clean verify -Dsonar.projectKey=DeployBackend -Dsonar.host.url=http://192.168.1.118:9000 -Dsonar.login=6a5d882e151292e5cec7b94d3241acb4b33a969c -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
//                    sh "${scannerHome}/bin/sonar-scanner.sh -Dproject.settings=C:\\Users\\Eder Wentz\\.jenkins\\tools\\hudson.plugins.sonar.SonarRunnerInstallation\\SONAR_SCANNER\\config\\sonar-scanner.properties" 
//                    sh "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=DeployBackend -Dsonar.host.url=http://localhost:9000 -Dsonar.login=e89f6b58d5a145cd62c7c08d1ad06d07fb9ca9c0 -Dsonar.java.binaries=target -Dsonar.language=java -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
                }
            }
        }
        
        stage ('Quality Gate') {
            steps {
                sleep(15)
                timeout(time: 1, unit: 'MINUTES') {
                waitForQualityGate abortPipeline: true
                }
            }
        }

        stage ('Deploy Backend') {
            steps {
                deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
            }
        }
        stage ('API Test') {
            steps {
                dir('api-test') {
                    git credentialsId: 'github_login', url: 'https://github.com/ederwentz/tasks-api-test'
                    sh 'mvn test'
                }
            }
        }
        stage ('Deploy Frontend') {
            steps {
                dir('frontend') {
                    git credentialsId: 'github_login', url: 'https://github.com/ederwentz/tasks-frontend'
                    sh 'mvn clean package'
                    deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks', war: 'target/tasks.war'
                }
            }
        }
        stage ('Functional Test') {
            steps {
                dir('functional-test') {
                    git credentialsId: 'github_login', url: 'https://github.com/ederwentz/tasks-functional-test'
                    sh 'mvn test'
                }
            }
        }
        //stage ('Limpeza deploy Prod') {
        //    steps {
        //         sh 'powershell.exe "docker rmi $(docker images -f "reference=*build*" -q)"'
        //      }
        //    }
        stage('Deploy Prod') {
            steps {
                sh 'docker-compose build'
                sh 'docker-compose up -d'
            }
        }

        stage ('Health Check') {
            steps {
                sleep(5)
                dir('functional-test') {
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
            emailext attachLog: true, body: 'See the attached log below', subject: 'Build $BUILD_NUMBER has failed', to: 'ederwentz+jenkins@gmail.com'
        }
        fixed {
            emailext attachLog: true, body: 'See the attached log below', subject: 'Build is fine!!!', to: 'ederwentz+jenkins@gmail.com'
        }
    }
}
