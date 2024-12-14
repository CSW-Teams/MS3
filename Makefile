.PHONY: docker_gen docker_clean_images docker_clean_volumes
.PHONY: local_backend_gen local_frontend_gen
.PHONY: local_start_postgres local_stop_postgres

# USAGE: make <command in COMMANDS>

# COMMANDS
docker_gen:
	sudo docker compose up -d

docker_clean_images:
	sudo docker compose down 													# stop all containers
	docker images -q | xargs docker rmi -f										# remove all images

docker_clean_volumes:
	sudo docker compose down 													# stop all containers
	sudo docker volume ls -q | xargs docker volume rm 							# remove all volumes

local_backend_gen:
	# if target directory is not generated, build the project
	@if [ ! -d "target" ]; then \
        echo "Backend not built, starting ..."; \
      	mvn package -DskipTests; \
  	fi
	java -jar ./target/ms3-0.0.1-SNAPSHOT.jar									# run backend

local_frontend_gen:
	cd frontend && npm start 													# run frontend

local_start_postgres:
	sudo systemctl start postgresql												# to start postgresql
	sudo systemctl disable postgresql											# so it won't run on boot

local_stop_postgres:
	sudo systemctl stop postgresql												# to stop postgresql
	sudo systemctl disable postgresql											# so it won't run on boot