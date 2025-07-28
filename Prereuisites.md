# Project Prerequisites & Setup Guide
 
This document outlines the necessary prerequisites and step-by-step instructions for setting up the frontend, backend, and LLM (Qdrant vector database) environments for this project. Please follow each section carefully to ensure a smooth setup process.
 
---
 
## Frontend Setup
 
### 1. Install pnpm
This project uses [pnpm] as the package manager. Ensure pnpm is installed globally:
 
```bash
npm install -g pnpm
```
 
### 2. Install Dependencies
Install all required dependencies using the following command:
 
```bash
pnpm install --legacy-peer-deps
```
 
### 3. Run the Application
Start the Angular application using the standard Angular CLI commands (e.g., `ng serve`).
 
---
 
## Backend Setup
 
After pulling the backend repository, ensure you attach the appropriate `application.properties` file to the project. This file should contain all necessary configuration values for your environment.
 
---
 
## LLM Search: Qdrant Vector Database Setup
 
For LLM search functionality, a Qdrant vector database is required. The recommended approach is to run Qdrant using Docker within a WSL 2 environment on Windows.
 
### 1. Install WSL 2
- Follow the [Microsoft WSL 2 installation guide](https://docs.microsoft.com/en-us/windows/wsl/install) to set up WSL 2 on your Windows system.
 
### 2. Install Docker Desktop
- Download and install [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/).
- Configure Docker Desktop to use the WSL 2.
 
### 3. Prepare Qdrant Docker Environment
- Open your Ubuntu/WSL terminal.
- Create a directory for Qdrant:
 
```bash
mkdir -p ~/qdrant-docker && cd ~/qdrant-docker
```
 
- Create a `docker-compose.yml` file with the following content:
 
```yaml
version: '3.8'
services:
  qdrant:
    image: qdrant/qdrant
    ports:
      - "6334:6334"
    volumes:
      - ./qdrant_storage:/qdrant/storage
    restart: unless-stopped
```
 
### 4. Start Qdrant
Run the following command to start the Qdrant service:
 
```bash
docker compose up -d
```
 
### 5. Stopping Qdrant
To stop the Qdrant service, run:
 
```bash
docker compose down
```
 
---
 
## Notes
- Ensure all environment variables and configuration files are set up as required for both frontend and backend.
- For any issues during setup, refer to the official documentation of the respective tools or contact the development team.
 
---
 
**End of Documentation**