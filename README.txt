## Backend Requirements

- [Java 17+](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Git](https://git-scm.com/downloads)
- [Lombok](Install Lombok plugin in your IDE : File -> Settings -> Plugins -> Search for Lombok)

## Backend Setup

1. Create an account on https://github.com/
2. In your IDE, Open git console ( example : https://i.gyazo.com/cec0f087b60c7055a30bb3f0958822d0.png )
3. Clone the repository from github 
	git clone https://github.com/ggitp/Projectecom.git
cd Projectecom
4. Run Docker
5. In IDE Terminal, pull the needed files with docker compose : docker compose pull ( it will pull all the necessary images like postgresql,elastic,rabbit )
6. Before you run docker compose, Check if your ports are free with Powershell : netstat -ano | findstr :5050 for each port 9200,5672,5050,5433
Skip to step 7 if ports are free.

*If the ports are taken, Make a run config in your IDE ( In Intellij : Run > Edit Configurations > Press + > Choose Application.
*Fill in :  Name : Backend
			Main Class : com.shop.ecommerce.EcommerceApplication
			Use classpath of module : pick your backend module (usually named after your project folder or maven module name)
			POSTGRES_PORT=5434;RABBIT_PORT=5673;ES_URI=http://localhost:9201 Edit in your free ports.
*Click Ok*

7. Run docker : docker compose up -d ( To stop run docker compose down )
8. When the containers are up, run the backend program


## Links

RabbitMQ UI → http://localhost:15672
User: admin
Password: admin123

Elasticsearch API → http://localhost:9200

Swagger UI → http://localhost:8080/swagger-ui.html (after backend starts)
Swagger is used to check schemas and test the backend without the frontend (send mappings/jsons and see responses)

PostgreSQL UI → http://localhost:5050
User: admin@admin.com
Password: admin123

## How to setup PostgreSQL UI connection to the actual database container

1. Connect to http://localhost:5050 and enter the credentials above
2. Under object explorer, right click the server -> register -> server...
3. Enter whichever name you want, in the connections tab -> Host name/address: postgres (this is the container name from docker-compose.yml)
                                                            Port: 5432
                                                            Username: admin
                                                            Password: admin123 (check “Save Password”)

4. Once you connect the UI to the database, you can see the tables at : Expand Databases → ecommerce_db → Schemas → public → Tables
                                                                        You will see a list of all your tables (users, products, cart_items, etc.).
                                                                        Right click a table → View/Edit Data → All Rows