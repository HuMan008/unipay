FROM registry.petroun.com/base/java8:latest

LABEL maintainer="s@petroun.com"

RUN mkdir /application
RUN mkdir /application/config
RUN mkdir /application/logs

COPY ./build/libs/unipay-1.0-SNAPSHOT.jar /application/app.jar
# 增加证书目录 2019年12月23日10:29:41 苏亚江
ADD certs /application 

WORKDIR /application


CMD ["java", "-Dfile.encoding=UTF-8","-jar", "app.jar"]
