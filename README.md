## Task 1

Implement the next two services:

- **Resource Service**
- **Song Service**

For a **Resource Service**, it is recommended to implement a service with CRUD operations for processing mp3 files.

When uploading a mp3 file, the **Resource Service** should process the file in this way:
- Extract file metadata. An external library can be used for this purpose.(e.g. [Apache Tika](https://www.tutorialspoint.com/tika/tika_extracting_mp3_files.htm)). 
- Store mp3 file to the underlying database of the service as Blob.
- Invoke **Song Service** to save mp3 file metadata.

For the **Song Service**, it is recommended to implement a simple CRUD service to manage the song record (metadata).
The service should provide the ability to manage some metadata about the songs (artist, album, etc.).
Make sure the service is still available over HTTP.


## Task 2

1) Package your applications as Docker images.
2) For each of your services:
 - Create a _Docker_ file that would contain instruction on how to package your project.
 - Build a docker image and run it, mapping an external port to verify that application can be started and respond to requests.
3) When all applications are successfully packaged, create a _docker-compose.yml_ file that would list all applications and 3rd party dependencies to successfully start the project.
Add init scripts for the database to run when container starts up. Once you have a compose file, you can create and start your application containers with a single command: `docker-compose up`.

Please note the following:
 - Use an _.env_ file to replace all environment variables depending on the set-up.
 - For 3rd party dependencies try to use the _–alpine_ images whenever it's possible.
 - For project applications use the build property as these images are not going to be pulled from a public hub.
 - Use logical service names to cross-reference services.


## Task 3

In this task you need to choose one of Service registry tool and inject it into your infrastructure.
Please, find the sample implementation here: [Eureka Example](https://www.javainuse.com/spring/cloud-gateway-eureka)

![image](https://github.com/irynachervinska/introduction-to-microservices/assets/56586117/bae573a6-5149-4dc2-b527-5410af4c9121)

1) Use Eureka Service Registry ([Service Registration and Discovery](https://spring.io/guides/gs/service-registration-and-discovery/)).
2) All microservices should be Eureka Clients, and they must be registered with the Eureka Server (made up of the Load Balancer and the Service Registry).
















































