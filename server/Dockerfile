# syntax=docker/dockerfile:1

# Build and run the mufti-saleh Spring Boot service.
#
#   docker build -t muftisaleh/api ./server

# ---- build ------------------------------------------------------------------

FROM gradle:9.6.1-jdk25-alpine AS build

ENV GRADLE_USER_HOME=/home/gradle/.gradle

WORKDIR /build

COPY settings.gradle.kts build.gradle.kts ./
RUN --mount=type=cache,target=/home/gradle/.gradle,sharing=locked \
    gradle --no-daemon --console=plain dependencies --configuration runtimeClasspath

COPY src ./src
RUN --mount=type=cache,target=/home/gradle/.gradle,sharing=locked \
    gradle --no-daemon --console=plain bootJar

RUN find build/libs -name '*.jar' ! -name '*-plain.jar' -exec cp {} application.jar \; \
 && java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# ---- runtime ----------------------------------------------------------------

FROM eclipse-temurin:25-jre-alpine AS runtime

RUN addgroup -S muftisaleh && adduser -S -G muftisaleh muftisaleh

WORKDIR /app

COPY --from=build --chown=muftisaleh:muftisaleh /build/extracted/dependencies/ ./
COPY --from=build --chown=muftisaleh:muftisaleh /build/extracted/spring-boot-loader/ ./
COPY --from=build --chown=muftisaleh:muftisaleh /build/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=muftisaleh:muftisaleh /build/extracted/application/ ./

USER muftisaleh

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseCompactObjectHeaders -XX:+ExitOnOutOfMemoryError"

HEALTHCHECK --interval=15s --timeout=3s --start-period=60s --retries=5 \
  CMD wget -qO- http://127.0.0.1:8080/actuator/health/readiness | grep -q '"status":"UP"'

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar application.jar"]
