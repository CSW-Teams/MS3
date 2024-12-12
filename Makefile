.PHONY: docker_gen docker_clean_images docker_clean_volumes

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
