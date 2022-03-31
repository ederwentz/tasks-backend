pipeline {
  agent any
  stages{
      stage('Build Backend'){
        steps{
          bat 'mvn clean package -DskipTests=true'
        }
      }
      stage('Unit Tests'){
        steps{
          bat 'mvn test'
        }
      }
      stage('Sonar Analysis'){
        environment {
          def scannerHome = tool 'SONAR_SCANNER'
        }
        steps{
          withSonarQubeEnv('SONAR_LOCAL'){
            bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=Deploy-Backend -Dsonar.host.url=http://localhost:9000 -Dsonar.login=c9b7c036e443ae1ad11b00a97c2745636ab15541 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/scr/test/**,**/model/**,**Application.java"
         }
       }
     }
  }
}