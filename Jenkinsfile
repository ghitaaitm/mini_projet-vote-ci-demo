pipeline {
  agent any

  options {
    timestamps()
    ansiColor('xterm')
  }

  environment {
    // SonarQube project settings are also in sonar-project.properties
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
        script {
          def cmd = 'mvn -B -ntp clean verify'
          if (isUnix()) {
            sh cmd
          } else {
            bat cmd
          }
        }
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
        expression { return env.CHANGE_ID == null } // skip on PR builds by default
      }
      steps {
        script {
          // In Jenkins: configure "Manage Jenkins" -> "System" -> SonarQube servers.
          // Name below must match your Jenkins SonarQube server name.
          withSonarQubeEnv('SonarQube') {
            withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
              def sonarCmd = 'mvn -B -ntp sonar:sonar -Dsonar.login=%SONAR_TOKEN%'
              if (isUnix()) {
                sonarCmd = 'mvn -B -ntp sonar:sonar -Dsonar.login=$SONAR_TOKEN'
                sh sonarCmd
              } else {
                bat sonarCmd
              }
            }
          }
        }
      }
    }

    stage('Quality Gate') {
      when {
        expression { return env.CHANGE_ID == null }
      }
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('Package') {
      steps {
        script {
          def cmd = 'mvn -B -ntp -DskipTests package'
          if (isUnix()) {
            sh cmd
          } else {
            bat cmd
          }
        }
      }
      post {
        success {
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
      }
    }
  }
}
