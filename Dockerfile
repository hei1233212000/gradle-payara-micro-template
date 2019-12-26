FROM payara/micro:5.194
COPY build/libs/gradle-payara-micro-template.war /opt/payara/deployments
COPY config/post-boot-command.txt /tmp/
CMD ["--deploymentDir", "/opt/payara/deployments", "--noCluster", "--postbootcommandfile", "/tmp/post-boot-command.txt"]
