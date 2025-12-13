pipeline {
  agent any

  options {
    timestamps()
  }

  tools {
    maven 'Maven3'
  }

  environment {
    MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh 'mvn -B -ntp clean verify'
      }
      post {
        always {
          junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
          archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
        }
      }
    }

    stage('SonarQube Analysis') {
      when {
        expression { env.CHANGE_ID == null }
      }
      steps {
        withSonarQubeEnv('SonarQube') {
          withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
            sh 'mvn -B -ntp sonar:sonar -Dsonar.login=$SONAR_TOKEN'
          }
        }
      }
    }

    stage('Quality Gate') {
      when {
        expression { env.CHANGE_ID == null }
      }
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('Package') {
      steps {
        sh 'mvn -B -ntp -DskipTests package'
      }
      post {
        success {
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
      }
    }
  }
}
