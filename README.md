<<<<<<< HEAD
## Get Started

This guide describes how to use DigitalOcean App Platform to run a Dockerised Spring Boot application.

**Note**: Following these steps may result in charges for the use of DigitalOcean services.

### Requirements

* You need a DigitalOcean account. If you do not already have one, first [sign up](https://cloud.digitalocean.com/registrations/new).

## Deploy the App

Click the following button to deploy the app to App Platform. If you are not currently logged in with your DigitalOcean account, this button prompts you to to log in.

[![Deploy to DigitalOcean](https://www.deploytodo.com/do-btn-blue.svg)](https://cloud.digitalocean.com/apps/new?repo=https://github.com/digitalocean/sample-spring-boot/tree/main)

Note that, for the purposes of this tutorial, this button deploys the app directly from DigitalOcean's GitHub repository, which disables automatic redeployment since you cannot change our template. If you want automatic redeployment or you want to change the sample app's code to your own, we instead recommend you fork [our repository](https://github.com/digitalocean/sample-spring-boot/tree/main).

To fork our repository, click the **Fork** button in the top-right of [its page on GitHub](https://github.com/digitalocean/sample-spring-boot/tree/main), then follow the on-screen instructions. To learn more about forking repos, see the [GitHub documentation](https://docs.github.com/en/github/getting-started-with-github/fork-a-repo).

After forking the repo, you can view the same README in your own GitHub org; for example, in `https://github.com/<your-org>/sample-spring-boot`. To deploy the new repo, visit the [control panel](https://cloud.digitalocean.com/apps) and click the **Create App** button. This takes you to the app creation page. Under **Service Provider**, select **GitHub**. Then, under **Repository**, select your newly-forked repo. Ensure that your branch is set to **main** and **Autodeploy** is checked on. Finally, click **Next**.

After clicking the **Deploy to DigitalOcean** button or completing the instructions above to fork the repo, follow these steps:

1. Configure the app, such as by specifying HTTP routes, declaring environment variables, or adding a database. For the purposes of this tutorial, this step is optional.
1. Provide a name for your app and select the region to deploy your app to, then click **Next**. By default, App Platform selects the region closest to you. Unless your app needs to interface with external services, your chosen region does not affect the app's performance, since to all App Platform apps are routed through a global CDN.
1. On the following screen, leave all the fields as they are and click **Next**.
1. Confirm your plan settings and how many containers you want to launch and click **Launch Basic/Pro App**.

After, you should see a "Building..." progress indicator. You can click **View Logs** to see more details of the build. It can take a few minutes for the build to finish, but you can follow the progress in the **Deployments** tab.

Once the build completes successfully, click the **Live App** link in the header and you should see your running application in a new tab, displaying the home page.


## Make Changes to Your App

If you forked our repo, you can now make changes to your copy of the sample app. Pushing a new change to the forked repo automatically redeploys the app to App Platform with zero downtime.

Here's an example code change you can make for this app:

1. Edit `main.go` and replace the "Hello!" greeting on line 31 with a different greeting
1. Commit the change to the `main` branch. Normally it's a better practice to create a new branch for your change and then merge that branch to `main` after review, but for this demo you can commit to the `main` branch directly.
1. Visit the [control panel](https://cloud.digitalocean.com/apps) and navigate to your sample app.
1. You should see a "Building..." progress indicator, just like when you first created the app.
1. Once the build completes successfully, click the **Live App** link in the header and you should see your updated application running. You may need to force refresh the page in your browser (e.g. using **Shift** + **Reload**).

## Learn More

To learn more about App Platform and how to manage and update your application, see [our App Platform documentation](https://www.digitalocean.com/docs/app-platform/).

## Delete the App

When you no longer need this sample application running live, you can delete it by following these steps:
1. Visit the [Apps control panel](https://cloud.digitalocean.com/apps).
2. Navigate to the sample app.
3. In the **Settings** tab, click **Destroy**.

**Note**: If you do not delete your app, charges for using DigitalOcean services will continue to accrue.
=======
# chatApi

[![Version](https://img.shields.io/badge/Version-4.0.0-0a58ca?style=for-the-badge&logo=semanticrelease&logoColor=white)](https://github.com/girish-kor/chatApi)
[![License](https://img.shields.io/badge/License-MIT-0a58ca?style=for-the-badge&logo=open-source-initiative&logoColor=white)](https://opensource.org/licenses/MIT)
[![Build](https://img.shields.io/badge/Build-Stable-157347?style=for-the-badge&logo=checkmarx&logoColor=white)](https://github.com/girish-kor/chatApi/actions)
[![Coverage](https://img.shields.io/badge/Coverage-95%25-157347?style=for-the-badge&logo=codecov&logoColor=white)](https://codecov.io/)
[![API Status](https://img.shields.io/badge/API-Online-a11d1d?style=for-the-badge&logo=vercel&logoColor=white)](https://chatapi-girish-kor.vercel.app)
[![Issues](https://img.shields.io/github/issues/girish-kor/chatApi?style=for-the-badge&logo=github&color=6f42c1)](https://github.com/girish-kor/chatApi/issues)
[![Pull Requests](https://img.shields.io/github/issues-pr/girish-kor/chatApi?style=for-the-badge&logo=github&color=6f42c1)](https://github.com/girish-kor/chatApi/pulls)
[![Last Commit](https://img.shields.io/github/last-commit/girish-kor/chatApi?style=for-the-badge&logo=git&color=157347)](https://github.com/girish-kor/chatApi/commits)

A scalable, real-time messaging and matchmaking platform built with Spring Boot, WebSocket (STOMP), WebRTC signaling, and MongoDB.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [WebSocket Integration](#websocket-integration)
- [Testing](#testing)
- [Monitoring](#monitoring)
- [Deployment](#deployment)
- [Security](#security)
- [Contributing](#contributing)
- [License](#license)

## Features

- Real-time messaging with WebSocket/STOMP
- Intelligent matchmaking queue system
- WebRTC signaling for peer-to-peer connections
- Session management and persistence
- Comprehensive API for chat operations

## Technology Stack

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6db33f?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java Version](https://img.shields.io/badge/Java-17-6db33f?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![MongoDB](https://img.shields.io/badge/Database-MongoDB-a11d1d?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![MongoDB Atlas](https://img.shields.io/badge/Service-Atlas-a11d1d?style=for-the-badge\&logo=mongodb\&logoColor=white)](https://www.mongodb.com/cloud/atlas)
[![CI/CD](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-157347?style=for-the-badge&logo=githubactions&logoColor=white)](https://github.com/girish-kor/chatApi/actions)
[![Deployment](https://img.shields.io/badge/Deployment-Vercel-000000?style=for-the-badge\&logo=vercel\&logoColor=white)](https://vercel.com)

## Installation

### Prerequisites

- Java 17+
- Maven 3.8+
- MongoDB (local instance or Atlas)
- Web browser with WebRTC support

### Quick Start

```bash
git clone https://github.com/girish-kor/chatApi.git
cd chatApi
mvn clean install
mvn spring-boot:run
```

## Configuration

### Required Properties

```properties
# Server configuration
server.port=8080

# MongoDB configuration
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<database>

# WebSocket configuration
websocket.enabled=true
```

### Environment Variables

```bash
export MONGODB_URI=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<database>
export JWT_SECRET=your-secure-jwt-secret
export SERVER_PORT=8080
```

## Usage

```bash
# Start the application
mvn spring-boot:run
# or
java -jar target/chatApi-4.0.0.jar
```

Access at:
- API: `http://localhost:8080/api/...`
- WebSocket: `ws://localhost:8080/ws`

## API Documentation

### Chat Endpoints

| Method | Endpoint            | Description      | Auth  |
| ------ | ------------------- | ---------------- | ----- |
| POST   | `/api/chat`         | Send a message   | Token |
| GET    | `/api/chat/{id}`    | Get a message    | Token |
| GET    | `/api/chat/history` | Get chat history | Token |
| PUT    | `/api/chat/{id}`    | Edit message     | Token |
| DELETE | `/api/chat/{id}`    | Delete message   | Token |

### Queue Endpoints

| Method | Endpoint                 | Description            | Auth  |
| ------ | ------------------------ | ---------------------- | ----- |
| POST   | `/api/queue`             | Join matchmaking queue | Token |
| GET    | `/api/queue/{sessionId}` | Check queue status     | Token |
| GET    | `/api/queue`             | View all (admin only)  | Admin |
| PUT    | `/api/queue/{sessionId}` | Update queue entry     | Token |
| DELETE | `/api/queue/{sessionId}` | Leave queue            | Token |

### Session Endpoints

| Method | Endpoint             | Description                | Auth  |
| ------ | -------------------- | -------------------------- | ----- |
| POST   | `/api/sessions`      | Start a session            | Token |
| GET    | `/api/sessions/{id}` | Get session info           | Token |
| GET    | `/api/sessions`      | List sessions (admin only) | Admin |
| PUT    | `/api/sessions/{id}` | Modify session             | Token |
| DELETE | `/api/sessions/{id}` | End session                | Token |

## WebSocket Integration

### STOMP Endpoints

| Destination           | Description       | Payload          |
| --------------------- | ----------------- | ---------------- |
| `/session.message`    | Chat message      | ChatMessage      |
| `/session.signal`     | WebRTC signaling  | SignalingMessage |
| `/session.disconnect` | Leave session     | String           |
| `/queue.join`         | Enter matchmaking | String           |
| `/queue.leave`        | Exit matchmaking  | String           |

### Data Models

#### ChatMessage

```json
{
  "_id": "UUID",
  "fromSessionId": "String",
  "toSessionId": "String",
  "content": "String",
  "sentAt": "ISODate",
  "signature": "String"
}
```

#### QueueUser

```json
{
  "_id": "String",
  "sessionId": "String",
  "joinedAt": "ISODate",
  "matched": "Boolean",
  "weight": "Float"
}
```

#### ActiveSession

```json
{
  "_id": "UUID",
  "userSessionId1": "String",
  "userSessionId2": "String",
  "startedAt": "ISODate",
  "active": "Boolean"
}
```

## Testing

```bash
# Run all tests
mvn test

# Run with coverage report
mvn verify
```

## Monitoring

Spring Boot Actuator endpoints:

- `/actuator/health`
- `/actuator/metrics`
- `/actuator/loggers`

## Deployment

### Docker

```bash
# Build Docker image
docker build -t chatapi:latest .

# Run container
docker run -p 8080:8080 -e MONGODB_URI=<connection-string> chatapi:latest
```

### Vercel

- Production URL: https://chatapi-girish-kor.vercel.app

## Security

- JWT-based authentication
- Token validation for WebSocket handshake
- CORS configuration
- Rate limiting and input validation

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

*Engineered with care using Spring Boot and WebSocket technology.*  
© 2025 Girish Kor | [MIT License](https://opensource.org/licenses/MIT)
>>>>>>> e5dc9bf (first commit)
