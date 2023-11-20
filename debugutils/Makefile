CONTAINER_NAME = ms3-db-1

# allow ONLY the "db" container to run locally and expose port 5432 to the host, for debug purposes only
db-debug:
	./switch-dockerfiles.sh -debug
	cd .. && docker-compose up db -d
	echo "run \"make db-verify to check the host 5432 port connection(s)\""

# stop the "db" container (and restore pre-debug configurations)
db-halt:
	cd .. && docker stop $(CONTAINER_NAME)
	./switch-dockerfiles.sh -teardown

# check if 5432 port is exposed to the host
db-verify:
	sudo lsof -i -P -n | grep 5432

frontend-local-debug:
	./frontend-debug.sh

