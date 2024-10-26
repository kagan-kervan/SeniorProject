# Use an official Python runtime as a parent image
FROM python:3.9-slim

# Install necessary packages
RUN apt-get update && apt-get install -y \
    chromium-driver \
    chromium \
    curl \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Install required Python packages
COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt

# Set Chrome options to be headless inside the container
ENV CHROME_BIN=/usr/bin/chromium
ENV CHROME_DRIVER=/usr/lib/chromium/chromedriver

# Set the working directory
WORKDIR /app

# Copy the Python script into the container
COPY . /app

# Run the script
CMD ["python", "FileScraping.py"]
