# Stage 1: Builder stage using OpenJDK 17 (Debian Bullseye-based) and manually installed sbt.
FROM openjdk:17-jdk-bullseye as builder
WORKDIR /app

# Install required packages.
RUN apt-get update && \
    apt-get install -y wget curl gnupg && \
    rm -rf /var/lib/apt/lists/*

# Add the sbt repository and install sbt.
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    wget -qO - "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | apt-key add - && \
    apt-get update && \
    apt-get install -y sbt

# Clear any legacy JVM options.
ENV SBT_OPTS=""
ENV _JAVA_OPTIONS=""
ENV JAVA_TOOL_OPTIONS=""

# Copy all project files into the container.
COPY . .

# Remove any local configuration files that might set unsupported options.
RUN rm -f .jvmopts .sbtopts

# Build the fat JAR using sbt-assembly.
RUN sbt clean assembly

# Stage 2: Runtime stage using OpenJDK 17 (Debian Bullseye-based).
FROM openjdk:17-jdk-bullseye
WORKDIR /app

# Copy the fat JAR from the builder stage.
# Adjust the path if your fat JAR is generated in a different location.
COPY --from=builder /app/target/scala-2.12/PriceBasket-assembly-0.1.jar /app/PriceBasket.jar

# Run the application.
CMD ["java", "-jar", "/app/PriceBasket.jar"]
