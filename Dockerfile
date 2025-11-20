ARG TEST_COND=with-test
ARG SONAR_TOKEN=placeholder
ARG RUNNER_SA_KEY=placeholder
ARG GOOGLE_APPLICATION_CREDENTIALS=/runner_sa_key.json

FROM asia-docker.pkg.dev/unipin-source/base-cache/base-cache:latest as base
ARG RUNNER_SA_KEY
WORKDIR /build
RUN echo $RUNNER_SA_KEY >> /runner_sa_key.json

FROM base as build-with-test
ARG SONAR_TOKEN
ARG GOOGLE_APPLICATION_CREDENTIALS
WORKDIR /build
COPY ./ /build/
RUN ./gradlew build sonarqube --info --build-cache

FROM base as build-skip-test
ARG SONAR_TOKEN
ARG GOOGLE_APPLICATION_CREDENTIALS
WORKDIR /build
COPY ./ /build/
RUN ./gradlew build -x test -x sonarqube --info --build-cache

FROM build-${TEST_COND} as build
ARG MS_NAME
ARG TEST_COND
WORKDIR /app
RUN mkdir -p "/app"
RUN cp "/build/build/libs/$MS_NAME-0.0.1.jar" "/app/app.jar"

FROM build as corretto-deps
WORKDIR /app
RUN unzip "/app/app.jar" -d temp &&  \
    jdeps  \
      --print-module-deps \
      --ignore-missing-deps \
      --recursive \
      --multi-release 17 \
      --class-path="./temp/BOOT-INF/lib/*" \
      --module-path="./temp/BOOT-INF/lib/*" \
      "/app/app.jar" > /modules.txt

FROM amazoncorretto:17-alpine as corretto-jdk
COPY --from=corretto-deps /modules.txt /modules.txt
# hadolint ignore=DL3018,SC2046
RUN apk add --no-cache binutils && \
    jlink \
     --verbose \
     --add-modules "$(cat /modules.txt),jdk.crypto.ec,jdk.crypto.cryptoki" \
     --strip-debug \
     --no-man-pages \
     --no-header-files \
     --compress=2 \
     --output /jre

# hadolint ignore=DL3007
FROM alpine:latest as final
WORKDIR /app
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV JAVA_ARGS=""
ENV JAVA_OPTS=""
COPY --from=corretto-jdk /jre $JAVA_HOME
COPY --from=build /app /app
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar $JAVA_ARGS
EXPOSE 8080