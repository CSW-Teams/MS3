# PowerShell script to replicate Makefile functionality on Windows

# USAGE: .\DockerTasks.ps1 -Task <function_name in COMMANDS>

# COMMANDS
function docker_gen {
    # Start Docker Compose in detached mode
    docker-compose up -d
}

function docker_clean_images {
    # Stop all containers
    docker-compose down

    # Remove all images
    docker images -q| ForEach-Object {
        docker rmi -f $_
    }
}

function docker_clean_volumes {
    # Stop all containers
    docker-compose down

    # Remove all Docker volumes
    docker volume ls -q | ForEach-Object {
        docker volume rm $_
    }
}

# Main execution logic
param (
    [Parameter(Mandatory=$true)]
    [ValidateSet("docker_gen", "docker_clean_images", "docker_clean_volumes")]
    $Task
)

switch ($Task) {
    "docker_gen" { docker_gen }
    "docker_clean_images" { docker_clean_images }
    "docker_clean_volumes" { docker_clean_volumes }
}
