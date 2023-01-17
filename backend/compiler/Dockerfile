FROM ramizouari/excellentia-common

RUN dnf install gcc g++ python3 -y

COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

ENV PROJECT=/project
ENV BUILD_DIR = ${PROJECT}/target
ENV SHARED=/shared
ENV APP=/compiler
WORKDIR ${PROJECT}


COPY . .
RUN rm ${PROJECT}/src/main/resources/application.yml
RUN rm -r ${PROJECT}/src/main/resources/META-INF
RUN cp -r ${SHARED}/. ${PROJECT}/src/main/resources/
RUN mkdir -p ${APP}
RUN ./mvnw install -Pdockerized

ENV DATA=/data
ENV DISK_LOGS=${DATA}/logs
ENV DISK_runs=${DATA}/runs
ENV DISK_TESTS=${DATA}/tests

RUN mkdir -p ${DATA}
RUN mkdir -p ${DISK_LOGS} ${DISK_MOUNT} ${DISK_TESTS}

CMD java -jar ${APP}/compiler.jar