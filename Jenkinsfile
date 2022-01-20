def remote = [:]
remote.name = '192.168.172.130'
remote.host = '192.168.172.130'
remote.allowAnyHosts = true

pipeline {
    agent any
    
    tools  {
        maven 'M3.8' 
    }
    environment {
        REAL_WORKSPACE = "springboot-demo"
        JACOCO_MAXIMUM_LINE_COVERAGE="42"
    }
    
    stages {
        stage('拉取代码') {
            steps {
                git branch: 'main', url: 'https://github.com/huang-zs/cicd.git'
            }
        }
        stage('编译代码'){
            steps {
                dir(REAL_WORKSPACE){
                    sh "mvn clean compile" 
                }
            }
        }
        stage('单元测试'){
            steps {
                dir(REAL_WORKSPACE){
                    sh "mvn org.jacoco:jacoco-maven-plugin:prepare-agent test -Dmaven.test.failure.ignore=true" 
                    junit '**/target/surefire-reports/*.xml'
                    jacoco changeBuildStatus: true, maximumLineCoverage: JACOCO_MAXIMUM_LINE_COVERAGE
                }
            }
        }
        stage("打包") {
            steps {
                dir(REAL_WORKSPACE){
                    sh 'mvn clean package -Dmaven.test.skip=true'
                }
            }
        }
        stage("静态检查") {
            steps {
                dir(REAL_WORKSPACE){
                    withSonarQubeEnv('sonarQubeServer') {
                        sh 'mvn sonar:sonar'
                    }
                    script {
                        timeout(time: 1, unit: 'HOURS') {
                            def qg = waitForQualityGate()
                            if (qg.status != 'OK') {
                                error "静态检查 : ${qg.status}"
                            }
                        }
                    }
                }
            }
        }
    
        stage("部署测试环境"){
            environment {
                TEST_AUTH = credentials('test-auth')
                JAR_PATH= """${sh(returnStdout: true,script: 'find -name "*.jar"').trim()}"""
            }
            steps {
                script {
                    remote.user=TEST_AUTH_USR
                    remote.password=TEST_AUTH_PSW
                }
                sshPut remote: remote, from: JAR_PATH, into: '.'
                sshCommand remote: remote, command: "ls"
            }            
        }
    }
    post {
        failure {
            echo "send email"
            // mail body: "Something is wrong with ${env.BUILD_URL}", subject: "Failed Pipeline: ${currentBuild.fullDisplayName}", to: "${env.MAIL_TO}"
        }
    }
}