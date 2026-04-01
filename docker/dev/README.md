# Environment: DEV

This environment is intended for Java developers working on the API.  
You must start the API locally using maven or an IDE.

```bash
keytool -genkey -alias myKeyAlias -keyalg RSA -keysize 2048 -keystore api/src/main/resources/keystore.jks -validity 3650

docker container prune -f

docker compose -f docker/dev/docker-compose.yml --env-file docker/dev/.env up

docker run --rm --env-file docker/dev/.env --env TZ=Europe/Paris --network test-network -v "$(pwd)/src/test/resources/features":/app/features vincentmoittie/e2e-test-runner:latest
```
