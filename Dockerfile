FROM eclipse-temurin:8-jdk
LABEL maintainer="Abhisek Datta <abhisek@appsecco.com>"

RUN apt-get update
RUN apt-get install -y default-mysql-client
RUN apt-get install -y maven

#1- Se crea un grupo llamado app y un suaurio llamado appuser sin otorgarle permisos especiales
RUN groupadd --system app && useradd --system --gid app --create-home --shell /bin/bash appuser

WORKDIR /app
COPY pom.xml pom.xml
RUN mvn dependency:resolve

COPY . .
RUN mvn clean package
RUN chmod 755 /app/scripts/start.sh

#2-cambia el propietario y el grupo de la carpeta /app 
# para otorgar el control al usuario appuser y al grupo app.
RUN chown -R appuser:app /app

EXPOSE 8080

#3- cambia el usuario de root al nuevo usuario appuser
USER appuser
CMD ["sh", "-c", "/app/scripts/start.sh"]
