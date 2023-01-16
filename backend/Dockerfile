FROM fedora:37

WORKDIR /shared

RUN dnf install java-17-openjdk java-17-openjdk-devel -y
RUN dnf install which -y

COPY src/main/resources/application.yml .
COPY src/main/resources/META-INF .