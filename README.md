# Excellentia
A Platform for sharing computer science knowledge and improving problem solving.

## 1. Introduction
Excellentia is a platform for exploring computer science knowledge, and it also hosts some probmes that help individuals to sharp their skills.

Created as a academic project, as of now, it only has backend due to time pressures.

## 2. Back-end
The back-end consists of:
1. A Compiler, that potentially compiles a solution
2. A Runner, that runs a proposed solution for a problem on some test cases and issues a Verdict (Accepted, Wrong Answer)
3. A Judge, which acts as an orchestrator, it calls the compiler to compile the proposed solution's code, and then calls the runner to run the solution and check its correctness

The judge will act as the main entry point for the project.

For now, the hosted problems are [The WinterCup 4.0 official problems](WinterCup4.0.pdf), which was hosted on **09 April 2022**, at INSAT.



The back-end is implemented using **Spring Boot 3.0**, and supports:

- Logging
- Metrics using **Prometheus**
- Health status
- Traces (Experimental)





## 3. Provisioning  

### 3.1 Database

We used a **MS-SQL** instance as a database, it is used to store:

- Problems: the information about problems
- Runs: the information about a proposed solution

Its deployment is [described at](deployment/1-sql-server)



### 3.2 Excellentia Server

We used an **Azure Kubernetes Service** instance to host the application. Its provisioning [described here](deployment/2-excellentia-server)



### 3.3 Storage

To ease the deployment, we  hosted a **Blob Storage** on which we uploaded a **7-zip** file containing the test cases of all **Winter Cup** problems.

The name of the file is *tests.7z*, and it is located at the *tests* container.



## 4. Deployment

Once the provisioning is successfully completed, the application will be deployed on 2 main phases:

### 4.1 Dockerization

On this phase, we created 4 docker images:

- `excellentia-common` which acts as a common image, It has common configurations
- `excellentia-judge` which is the image containing the Judge micro-service
- `excellentia-compiler` which is the image containing the Compiler micro-service
- `excellentia-runner` which is the image containing the Runner micro-service

To ease dockerization, we implemented a helper [bash script](backend/dockerize-all.sh) at **backend** sub-directory that creates all images

```bash
./dockerize-all.sh
```



Once dockerized, we implemented a helper [bash script](backend/push-all-docker.sh) at **backend** sub-directory that deploy all images to **Docker Hub**

```bash
./push-all-docker.sh ACCOUNT_NAME
```



### 4.2 Kubernetes Deployment

On this phase, we created a *Helm* chart, which is located at [deployment/4-kubernetes](deployment/4-kubernetes). It contains:

- The required instances, with:
  - $2$ pods for *Judge*
  - $2$ pods for *Compiler*
  - $4$ pods for *Runner*
  - $1$ Ingress pod
  - $1$ Prometheus pod
- The shared disks (Exactly, the Persistent volumes claims):
  - The disk `RUNNER_DISK` on which runs are stored
  - The disk `TESTS_DISK` on which tests are stored
  - The disk `LOGS_DISK` on which logs are stored
- An InitContainer, that runs on the compiler pod that:
  - Installs `curl` and `p7zip-full`
  - Downloads the `tests.7z` file located at the **Blob Storage**
  - Extracts it
  - Copy it to `TESTS_DISK`
- A Prometheus instance for collecting metrics
- An Ingress controller acting as a gateway