provider "aws" {
  region = "hrs-aws-region"
}

resource "aws_elastic_beanstalk_application" "java_app" {
  name = "JavaApp"
}

resource "aws_elastic_beanstalk_environment" "java_env" {
  name                = "JavaEnv"
  application         = aws_elastic_beanstalk_application.java_app.name
  solution_stack_name = "64bit Amazon Linux 2 v5.6.0 running Corretto 8"

  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "EnvironmentType"
    value     = "LoadBalanced"
  }

  setting {
    namespace = "aws:elasticbeanstalk:environment:process:default"
    name      = "Executor"
    value     = "tomcat"
  }

  setting {
    namespace = "aws:elasticbeanstalk:container:tomcat:jvmoptions"
    name      = "Xmx"
    value     = "512m"
  }

  setting {
    namespace = "aws:elasticbeanstalk:container:tomcat:jvmoptions"
    name      = "Xms"
    value     = "256m"
  }

  setting {
    namespace = "aws:elasticbeanstalk:container:tomcat:jvmoptions"
    name      = "XX:MaxPermSize"
    value     = "256m"
  }

  setting {
    namespace = "aws:elasticbeanstalk:container:tomcat:jvmoptions"
    name      = "XX:MaxMetaspaceSize"
    value     = "256m"
  }
}