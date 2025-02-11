# ------------------------------
# Stage 1: Build (Compilation) Stage
# ------------------------------
    FROM openjdk:17-jdk-bullseye as builder
    WORKDIR /app
    
    # Install required packages with minimal extras.
    RUN apt-get update && \
        apt-get install -y --no-install-recommends wget curl gnupg && \
        rm -rf /var/lib/apt/lists/*
    
    # Add the sbt repository, import its GPG key, and install sbt.
    RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" > /etc/apt/sources.list.d/sbt.list && \
        wget -qO - "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | apt-key add - && \
        apt-get update && \
        apt-get install -y --no-install-recommends sbt && \
        rm -rf /var/lib/apt/lists/*
    
    # Clear any legacy JVM options.
    ENV SBT_OPTS=""
    ENV _JAVA_OPTIONS=""
    ENV JAVA_TOOL_OPTIONS=""
    
    # Copy build configuration first to leverage caching.
    COPY build.sbt .
    COPY project project
    
    # Copy the rest of the source code.
    COPY . .
    
    # Remove local configuration files that may interfere with the build.
    RUN rm -f .jvmopts .sbtopts
    
    # Build the fat JAR (adjust the sbt command if your assembly settings differ).
    RUN sbt clean assembly
    
    # ------------------------------
    # Stage 2: Runtime Stage
    # ------------------------------
    # Use a slimmer runtime image to reduce final image size.
    FROM openjdk:17-jdk-slim
    WORKDIR /app
    
    # Copy the fat JAR from the builder stage. Adjust the path if needed.
    COPY --from=builder /app/target/scala-2.12/PriceBasket-assembly-0.1.jar /app/PriceBasket.jar
    
    # (Optional) Switch to a non-root user for improved security.
    USER nobody
    
    # Run the application.
    CMD ["java", "-jar", "/app/PriceBasket.jar"]
    