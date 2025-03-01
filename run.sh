#!/bin/bash
# run.sh

# Generate Maven Wrapper if it doesn't exist
if [ ! -f "mvnw" ]; then
    echo "Generating Maven Wrapper..."
    mvn wrapper:wrapper
fi

# Make the wrapper executable
chmod +x mvnw

# Clean and install
echo "Cleaning and installing..."
./mvnw clean install

# Run tests with coverage
echo "Running tests with coverage..."
./mvnw verify

# Open test coverage report if on macOS
if [[ "$OSTYPE" == "darwin"* ]]; then
    open target/site/jacoco/index.html
fi

# Run the application
echo "Starting the application..."
./mvnw spring-boot:run