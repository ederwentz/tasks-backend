pipeline {
  agent any
  stages{
      stage('Build Backend'){
        steps{
          bat 'mvn clean package -DskipTests=true'
        }
      }
      stage('Unit Testes'){
        steps{
          bat 'mvn test'
        }
      }
  }
}