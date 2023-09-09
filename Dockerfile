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


FROM quay.io/klenkes74/delphi-council-is:latest


# Install java and the run-java script
# Also set up permissions for user `1001`
RUN microdnf install curl ca-certificates ${JAVA_PACKAGE} \
    && microdnf update \
    && microdnf clean all \
    && mkdir -p /deployments/config \
    && chmod "g+rwX" -R /deployments \
    && chown 1001:root -R /deployments \
    && chown 1001 /deployments/run-java.sh \
    && chmod 750 /deployments/run-java.sh \
    && echo "securerandom.source=file:/dev/urandom" >> /etc/alternatives/jre/lib/security/java.security

EXPOSE 8080
USER 1001

ENTRYPOINT [ "/deployments/run-java.sh" ]
