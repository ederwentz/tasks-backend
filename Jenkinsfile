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
      stage('Sonar Analysis') {
        environment {
            scannerHome = tool 'SONAR_SCANNER'
        }
        steps {
          withSonarQubeEnv('SONAR_LOCAL') {
              bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=Deploy-Backend -Dsonar.host.url=http://localhost:9000 -Dsonar.login=c9b7c036e443ae1ad11b00a97c2745636ab15541 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
        }
      }
    }
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
  }
}