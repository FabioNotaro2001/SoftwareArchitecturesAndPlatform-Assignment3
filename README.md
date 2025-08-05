# Software Architectures and Platform - Assignment 3

## Overview

This project is an implementation of a microservices-based architecture designed for a Software Architectures and Platform course. It simulates a system involving users, rides and e-bikes, with an API Gateway facilitating communication between services. The system is containerized using Docker and orchestrated with Kubernetes for deployment.

## Architecture

The system comprises the following components:

- **API Gateway** -> manages incoming requests and routes them to appropriate services
- **Users Manager** -> handles user-related operations
- **Rides Manager** -> manages ride-related functionalities
- **E-Bikes Manager** -> oversees e-bike operations
- **Admin GUI** -> provides an administrative interface for managing the system
- **User GUI** -> offers a user interface for end-users to interact with the system.

Each component is containerized and deployed using Kubernetes manifests.

## Prerequisites

Before setting up the project, ensure you have the following installed:

- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [Kubernetes](https://kubernetes.io/docs/setup/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
- [Minikube](https://minikube.sigs.k8s.io/docs/) (for local Kubernetes cluster)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/FabioNotaro2001/SoftwareArchitecturesAndPlatform-Assignment3.git
cd SoftwareArchitecturesAndPlatform-Assignment3


2. Build and Deploy with Docker Compose

docker-compose up --build

This command builds and starts all services defined in the docker-compose.yml file.
3. Deploy to Kubernetes
kubectl apply -f apigateway-deployment.yaml
kubectl apply -f ebikes-deployment.yaml
kubectl apply -f rides-service.yaml
kubectl apply -f ebikes-service.yaml
kubectl apply -f apigateway-service.yaml
kubectl apply -f configmap.yaml

These commands deploy the services to your Kubernetes cluster.
4. Access the Application

    Admin GUI: Access the administrative interface at http://<minikube-ip>:<port>/admin.

    User GUI: Access the user interface at http://<minikube-ip>:<port>/user.

Replace <minikube-ip> and <port> with the appropriate values for your setup.
Usage

Once the application is running, you can interact with the system through the provided GUIs. The Admin GUI allows for managing users, rides, and e-bikes, while the User GUI enables end-users to view and book rides.
Contributing

Contributions are welcome! Please fork the repository, make your changes, and submit a pull request.
License

This project is licensed under the MIT License - see the LICENSE file for details.
Acknowledgments

    Kubernetes

    Docker

    Minikube

    Flask

    React
