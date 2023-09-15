FROM openjdk:21-jdk-slim-bullseye

RUN apt-get -y update && apt-get -y upgrade

RUN apt-get -y install git openjdk-17-jre

RUN cp -r /usr/local/openjdk-21 /usr/lib/jvm/java-21-openjdk-amd64