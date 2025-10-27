FROM quay.io/wildfly/wildfly:latest

ENV DB_URL=jdbc:h2:file:/opt/jboss/db/autorizacoes;DB_CLOSE_ON_EXIT=FALSE;

USER root

RUN mkdir -p /opt/jboss/db

RUN chown -R jboss:jboss /opt/jboss/db
RUN chmod -R 775 /opt/jboss/db

USER jboss

VOLUME /opt/jboss/db

COPY target/*.war /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]