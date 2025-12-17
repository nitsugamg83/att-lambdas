    # Base image
FROM 402712822235.dkr.ecr.mx-central-1.amazonaws.com/goldenimages/giattol9jdk21-slim:20251006211231

USER root
WORKDIR /opt/apps

# Crear carpetas y asignar permisos
RUN mkdir -p /opt/apps/ms-identity-orchestration-web /opt/apps/logs/ms-identity-orchestration-web \
    && chown -R usrapp:usrapp /opt/apps/logs/ms-identity-orchestration-web

WORKDIR /opt/apps/ms-identity-orchestration-web

# Copiar jar de la aplicación
COPY boot/target/*.jar ms-identity-orchestration-web.jar

USER usrapp
EXPOSE 16600

# No colocar credenciales en la imagen
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/opt/apps/ms-identity-orchestration-web/ms-identity-orchestration-web.jar"]
 