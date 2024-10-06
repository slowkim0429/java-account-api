# default app version 
ARG APP_VERSION=0.1.0 

### Cache Stage
FROM gradle:7.4.0-jdk11 AS cache

# set workdir
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

# set cache folder
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home

# only copy dependency-related files
COPY build.gradle settings.gradle gradle.properties ./

# Only download dependencies
# Eat the expected build failure since no source code has been copied yet
RUN --mount=target=.gradle-cache,type=cache \
    gradle clean build --parallel --no-daemon > /dev/null 2>&1 || true


### Build Stage
FROM gradle:7.4.0-jdk11 AS builder

# set workdir
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

# copy all source
COPY . .

# load dependencies from cache
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle

# build soruce file with dependnecies
RUN gradle clean build --parallel -x test --console=plain
RUN java -Djarmode=layertools -jar build/libs/*.jar extract

### Runtime Stage
FROM openjdk:11-jre-slim

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

EXPOSE 8521
CMD ["java", "-Xms400M", "-Xmx400M", "org.springframework.boot.loader.JarLauncher"]

# copy build source
COPY --from=builder ${APP_HOME}/virnect.pfx ./
COPY --from=builder ${APP_HOME}/dependencies/ ./
COPY --from=builder ${APP_HOME}/spring-boot-loader/ ./
COPY --from=builder ${APP_HOME}/snapshot-dependencies/ ./
COPY --from=builder ${APP_HOME}/application/ ./
COPY --from=builder ${APP_HOME}/build.gradle ./

# change app version
RUN sed -i "/version =/ c\\version = \'${APP_VERSION}\'" build.gradle
