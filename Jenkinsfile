pipeline {
  agent any
  stages{
      stage('Build Backend') {
        steps {
          bat 'mvn clean package -DskipTests=true'
        }
      }
      stage('Unit Tests') {
        steps {
          bat 'mvn test'
        }
      }
      stage ('Sonar Analysis') {
            environment {
                def scannerHome = tool 'SONAR_SCANNER'
            }
            steps {
                withSonarQubeEnv('SONAR_LOCAL') {
                    bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=DeployBack -Dsonar.host.url=http://localhost:9000 -Dsonar.login=421277384d4bc360d08df75e1bbd49be82dbde90 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/src/test/**,**/model/**,**Application.java"
                }
            }
        }
//      stage('Sonar Analysis') {
//        environment {
//            scannerHome = tool 'SONAR_SCANNER'
//        }
//        steps {
//          withCredentials([string(credentialsId: 'SonarToken', variable: 'jenkins')]) {
//          withSonarQubeEnv('SONAR_LOCAL') {
//              bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=Deploy-Backend -Dsonar.host.url=http://localhost:9000/${JENKINS} -Dsonar.login=421277384d4bc360d08df75e1bbd49be82dbde90 -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java -Dsonar.java.binaries=target -Dsonar.projectBaseDir=/Users/Eder Wentz/.jenkins/workspace/Deploy Backend"
//              //bat "${scannerHome}/bin/sonar-scanner.bat -e -Dsonar.projectKey=Deploy-Backend -Dsonar.host.url=http://localhost:9000 -Dsonar.login=421277384d4bc360d08df75e1bbd49be82dbde90 -Dsonar.java.binaries=target -Dsonar.projectBaseDir=C:/Users/Eder Wentz/.jenkins/workspace/Deploy-Backend -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
//              //bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.host.url=http://localhost:9000/${JENKINS} -Dsonar.login=421277384d4bc360d08df75e1bbd49be82dbde90 -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java -Dsonar.projectKey=Deploy-Backend -Dsonar.java.binaries=target -Dsonar.projectBaseDir=C:\\Users\\Eder Wentz\\.jenkins\\workspace\\Deploy Backend"
//          }
//        }
//      }
//    }
    stage ('Quality Gate'){
        steps {
          sleep(5)
          timeout(time: 1, unit:'MINUTES'){
              waitForQualityGate abortPipeline: true
        }
      }
    }
    stage('Deploy Backend') {
        steps {
          deploy adapters: [tomcat9(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001/')], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
        }
    }
    stage('API Tests') {
        steps {
          git credentialsId: 'github_login', url: 'https://github.com/ederwentz/tasks-api-test'
          bat 'mvn test'
      }
    }
  }
}