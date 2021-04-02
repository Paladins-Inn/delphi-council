# Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.


FROM registry.access.redhat.com/ubi8/ubi-minimal:8.3

LABEL io.k8s.description="This is a discord bot and connected webservice for supporting RPG tabletop games online without providing a VTT."
LABEL io.k8s.display-name="Delphi Council Storm Knight Information System"
LABEL io.openshift.expose-services="8080/TCP"
LABEL io.openshift.tags="spring-boot rpg"
LABEL maintainer="Kaiserpfalz EDV-Service"
LABEL summary="Provides a supporting system for Torganized Play Germany."
LABEL vendor="Kaiserpfalz EDV-Service"
LABEL version="0.1.0-SNAPSHOT"


ARG JAVA_PACKAGE=java-11-openjdk-headless
ARG RUN_JAVA_VERSION=1.3.8

ENV LANG='en_GB.UTF-8' LANGUAGE='en_GB:en'

# Install java and the run-java script
# Also set up permissions for user `1001`
RUN microdnf install curl ca-certificates ${JAVA_PACKAGE} \
    && microdnf update \
    && microdnf clean all \
    && mkdir /deployments \
    && chown 1001 /deployments \
    && chmod "g+rwX" /deployments \
    && chown 1001:root /deployments \
    && curl https://repo1.maven.org/maven2/io/fabric8/run-java-sh/${RUN_JAVA_VERSION}/run-java-sh-${RUN_JAVA_VERSION}-sh.sh -o /deployments/run-java.sh \
    && chown 1001 /deployments/run-java.sh \
    && chmod 540 /deployments/run-java.sh \
    && echo "securerandom.source=file:/dev/urandom" >> /etc/alternatives/jre/lib/security/java.security

COPY /home/runner/_work/delphi-council/delphi-council/dci-0.1.0-SNAPSHOT.jar /deployments/app.jar
RUN chown 1001 /deployments/app.jar

EXPOSE 8080
USER $UID

ENTRYPOINT [ "/deployments/run-java.sh" ]
