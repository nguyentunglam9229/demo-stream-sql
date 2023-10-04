#!/usr/bin/env groovy
pipeline {
    agent { docker { image 'maven:3.9.4-eclipse-temurin-17-alpine' } }
    stages {
        stage('build') {
            steps {
                sh 'mvn --version'
                sh 'mvn clean package -Dmaven.test.skip'
            }
        }
    }

}
