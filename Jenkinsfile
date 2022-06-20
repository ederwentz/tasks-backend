pipeline {
    agent any
    //node
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
        stage ('Sonar Analysis') {
            environment {
//                def scannerHome = tool 'SONAR_SCANNER';
                scannerHome = tool 'SONAR_SCANNER';
            }
            steps {
                withSonarQubeEnv('SONAR_LOCAL') {
                    //bat "mvn sonar:sonar -Dsonar.projectName=DeployBackend -Dsonar.projectKey=DeployBackend -Dsonar.host.url=http://localhost:9000 -Dsonar.login=adc6db71d53cd5e4873cefa492f2af4b7ba414b2 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
//                    bat "mvn clean install sonar:sonar -Dsonar.projectKey=DeployBackend -Dsonar.login=ff389cc8e5a756a370ceae9d982ac1f2044f953c -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
//                    bat "mvn clean package sonar:sonar -Dsonar.projectKey=DeployBackend -Dsonar.login=ff389cc8e5a756a370ceae9d982ac1f2044f953c -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
//                    bat "mvn clean verify -Dsonar.projectKey=DeployBackend -Dsonar.login=ff389cc8e5a756a370ceae9d982ac1f2044f953c -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
//                    bat "${scannerHome}/bin/sonar-scanner.bat -Dproject.settings=C:\\Users\\Eder Wentz\\.jenkins\\tools\\hudson.plugins.sonar.SonarRunnerInstallation\\SONAR_SCANNER\\config\\sonar-scanner.properties" 
//                    bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=DeployBackend -Dsonar.host.url=http://localhost:9000 -Dsonar.login=ff389cc8e5a756a370ceae9d982ac1f2044f953c -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
                    bat "\"${scannerHome}/bin/sonar-scanner\" -e -Dsonar.projectKey=DeployBackend -Dsonar.host.url=http://localhost:9000 -Dsonar.login=adc6db71d53cd5e4873cefa492f2af4b7ba414b2 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
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
                deploy adapters: [tomcat9(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
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
                    deploy adapters: [tomcat9(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks', war: 'target/tasks.war'
                }
            }
        }
        stage ('Functional Test') {
            steps {
                dir('functional-test') {
                    git credentialsId: 'github_login', url: 'https://github.com/ederwentz/tasks-functional-test'
                    bat 'mvn test'
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
            emailext attachLog: true, body: 'See the attached log below', subject: 'Build $BUILD_NUMBER has failed', to: 'eder.wentz+jenkins@gmail.com'
        }
        fixed {
            emailext attachLog: true, body: 'See the attached log below', subject: 'Build is fine!!!', to: 'eder.wentz+jenkins@gmail.com'
        }
    }
}
