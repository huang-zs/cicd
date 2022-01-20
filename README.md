# cicd
> 基于github，jenkins，sonarqube的CI-CD的DEMO

## 0. pipeline 常用指令
```
@Library('cicd') _

pipeline {
    agent any

    environment {
        // env from shell
        WHO="""${sh(
        returnStdout: true,
        script: 'whoami'
        )}"""
        // env from credentials
        DB=credentials('db')
    }
    // pipeline input params
    parameters {
        string defaultValue: 'PARAMETER_DEFAULT_VALUE', name: 'PARAMETER_KEY'
    }


    stages {
        stage('Hello') {
            steps {
                echo "${WHO} hello world!"
                echo "${params.PARAMETER_KEY}"
                echo "${env.MAIL_TO}"
                // use share library
                script {
                    test.call('jenkins')
                }
            }
        }
        stage('Test Check') {
            steps {
                // input block
                input  'test succeed?'
            }
        }
        stage('When Test') {
            steps {
               echo 'when'
            }
            when {
                expression { params.PARAMETER_KEY == 'when' }
            }
        }

        stage('Deploy') {
            steps {
            //  sh "curl post 'https://web.comprame.com/public/oauth2callback' -d '{\"db\"=\"${DB}\",\"db_usr\"=\"${DB_USR}\",\"db_pwd\"=\"${DB_PSW}\"}' "
            sh (script:"""
            curl post 'https://web.comprame.com/public/oauth2callback'  \
            --header 'Content-Type: application/json' \
            -d '{"db"="${DB}","db_usr"="${DB_USR}","db_pwd"="${DB_PSW}"}'
            """)
            }
        }
    }
    post {
        always {
            echo "${currentBuild.fullDisplayName} build ${currentBuild.currentResult}"
        }
        failure {
            echo "build fail"
            mail body: "Something is wrong with ${env.BUILD_URL}", subject: "Failed Pipeline: ${currentBuild.fullDisplayName}", to: "${env.MAIL_TO}"
        }
    }
}

```

## 1. sonarqube 代码检查 [link](https://www.jenkins.io/doc/pipeline/steps/sonar/#sonarqube-scanner-for-jenkins)
- sonarqube安装
> 略

- sonarqube关于jenkins的配置
1. 获取sonarqube的token
> 当前用户 -> My Account -> Security -> Generate
2. 配置jenkins的webhook
> Administrator -> Configuration -> Webhook -> Create -> http://host:port/sonarqube-webhook/

- jenkins关于sonarqube的配置
1. 安装插件
> `SonarQube Scanner for Jenkins` 和 `Sonar Quality Gates Plugin`
2. 配置sonarqube的token
> 当前用户 -> 凭据 -> global -> Add Credentials -> Kind -> Secret text
3. SonarQube Scanner配置
> Manage Jenkins -> Global Tool Configuration -> SonarQube Scanner
4. sonarqube配置
> Manage Jenkins -> Configure system -> SonarQube servers 和 Quality Gates - Sonarqube

- pipeline配置
```
pipeline {
    agent any
    
    tools  {
        maven 'M3.8' 
    }
    
    stages {
        stage('Get Code') {
            steps {
                git branch: 'main', url: 'https://github.com/huang-zs/cicd.git'
            }
        }
        stage("Build & SonarQube analysis") {
            steps {
                withSonarQubeEnv('sonarQubeServer') {
                    sh 'mvn -f springboot-demo/pom.xml clean package sonar:sonar'
                }
            }
        }
        stage("Quality Gate"){
            steps {
                timeout(time: 1, unit: 'HOURS') {
                     waitForQualityGate abortPipeline: true
                }
                // script {
                //     timeout(time: 1, unit: 'HOURS') {
                //           def qg = waitForQualityGate()
                //           if (qg.status != 'OK') {
                //               error "Pipeline aborted due to quality gate failure: ${qg.status}"
                //           }
                //       }
                // }
            }
        }
    }
}
```

- sonarqube质量阀配置
> Quantity Gate -> Create -> Add Condition -> Projects with


## 2. jacoco 代码覆盖率 [doc](https://www.jenkins.io/doc/pipeline/steps/jacoco/#jacoco-plugin)
- jenkins关于jacoco的配置
1. 安装插件
> `	JaCoCo plugin`
- pipeline配置
```
pipeline {
    agent any
    
    tools  {
        maven 'M3.8' 
    }
    
    stages {
        stage('Get Code') {
            steps {
                git branch: 'main', url: 'https://github.com/huang-zs/cicd.git'
            }
        }
        stage("Jacoco") {
            steps {
                // use jacoco test code
                sh "mvn org.jacoco:jacoco-maven-plugin:prepare-agent -f springboot-demo/pom.xml clean test -Dautoconfig.skip=true -Dmaven.test.skip=false -Dmaven.test.failure.ignore=true"
                // show test result by junit
                junit '**/target/surefire-reports/*.xml'
                // set jacoco gateway
                jacoco changeBuildStatus: true, maximumLineCoverage:"42"
            }
        }
    }
}

```
- JaCoCo Coverage Report

|instruction|branch|complexity|line|method|class|
|-|-|-|-|-|-|
|指令覆盖|分支覆盖,if/switch||代码行覆盖|方法覆盖,方法中有代码执行就覆盖|类覆盖，类中有方法执行就覆盖|


## 3. 远程部署 [link](https://github.com/jenkinsci/ssh-steps-plugin)
- 配置远程用户密码
> 当前用户 -> 凭据 -> global -> Add Credentials -> Kind -> Username with password
- pipeline配置
```
def remote = [:]
remote.name = '192.168.172.130'
remote.host = '192.168.172.130'
remote.allowAnyHosts = true

pipeline {
    agent any
    
    environment {
        TEST_AUTH=credentials('test-auth')
    }

    stages {
        stage('Remote SSH') {
            steps {
                script {
                    remote.user=TEST_AUTH_USR
                    remote.password=TEST_AUTH_PSW
                }
                sh "touch ssh-test"
                sshPut remote: remote, from: 'ssh-test', into: '.'
                sshCommand remote: remote, command: "ls -lrt"
            }
          }
    }
}

```

# 完整pipeline
```
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

```
 