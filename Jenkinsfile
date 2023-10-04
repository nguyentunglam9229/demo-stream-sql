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

        stage('deploy') {
            steps {
                sh 'java -jar target/demo-stream-sql-data-0.0.1-SNAPSHOT.jar'
            }
        }
    }

}
