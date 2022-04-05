pipeline {
    agent any
    stages {
        stage ('Build Backend') {
            steps {
                bat 'mvn clean package -DskipTests=true'
            }
        }
        stage ('Unit Tests') {
            steps {
                bat 'mvn test'
            }
        }
 //       stage ('Sonar Analysis') {
 //           environment {
 //               scannerHome = tool 'SONAR_SCANNER'
 //           }
 //           steps {
 //               withSonarQubeEnv('SONAR_LOCAL') {
 //                   bat "${scannerHome}/bin/sonar-scanner.bat -e -Dsonar.host.url=http://localhost:9000/${JENKINS} -Dsonar.login=421277384d4bc360d08df75e1bbd49be82dbde90 -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java -Dsonar.projectKey=Deploy-Backend -Dsonar.java.binaries=target -Dsonar.projectBaseDir=C:\\Users\\Eder Wentz\\.jenkins\\workspace\\Deploy Backend"
                    //bat "${scannerHome}/bin/sonar-scanner.bat -e -Dsonar.host.url=http://192.168.1.15:9000/ -Dsonar.login=421277384d4bc360d08df75e1bbd49be82dbde90 -Dsonar.coverage.exclusions=**/.mvn/**, **/scr/test/**,**/model/**,**Application.java -Dsonar.projectKey=Deploy-Backend -Dsonar.host.url=http://192.168.1.15:9000 -Dsonar.java.binaries=target"
                    //bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=Deploy-Backend -Dsonar.host.url=http://192.168.1.15:9000 -Dsonar.login=421277384d4bc360d08df75e1bbd49be82dbde90 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/src/test/**,**/model/**,**Application.java"
 //               }
 //           }
 //       }    
 //       stage ('Quality Gate') {
 //           steps {
 //               sleep(5)
 //               timeout(time: 1, unit: 'MINUTES') {
 //                   waitForQualityGate abortPipeline: true
 //               }
 //           }
 //       }
        stage ('Deploy Backend') {
            steps {
                deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
            }
        }
        stage ('API Test') {
            steps {
                dir('api-test') {
                    git credentialsId: 'github_login', url: 'https://github.com/ederwentz/tasks-api-test'
                    bat 'mvn test'
                }
            }
        }
        stage ('Deploy Frontend') {
            steps {
                dir('frontend') {
                    git credentialsId: 'github_login', url: 'https://github.com/ederwentz/tasks-frontend'
                    bat 'mvn clean package'
                    deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks', war: 'target/tasks.war'
                }
            }
        }
        stage ('Functional Test') {
            steps {
                dir('functional-test') {
                    git credentialsId: 'github_login', url: 'https://github.com/ederwentz/tasks-functional-tests'
                    bat 'mvn test'
                }
            }
        }
        stage('Deploy Prod') {
            steps {
                bat 'docker-compose build'
                bat 'docker-compose up -d'
            }
        }
        stage ('Health Check') {
            steps {
                sleep(5)
                dir('functional-test') {
                    bat 'mvn verify -Dskip.surefire.tests'
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