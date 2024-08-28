	to get tests executing on a cloud:

1.  need to deploy the dhcpai ear on the jboss first :(

2.  need to create files & folders as well, set the permissions!!

	mkdir /opt/ericsson/ocs/lib/log
	chmod 666 /opt/ericsson/ocs/log/ai_manager_bsim_inst_log
	
	# want all read/write to keep it simple
	touch /ericsson/tor/dhcp_config/dhcpai_config/dhcpd.conf
	touch /ericsson/tor/dhcp_config/dhcpai_config/dhcp_static.conf 
	touch /ericsson/tor/dhcp_config/dhcpai_config/dhcp_subnet.conf 
	chmod 666 /ericsson/tor/dhcp_config/dhcpai_config/dhcpd.conf
	chmod 666  /ericsson/tor/dhcp_config/dhcpai_config/dhcp_static.conf
	chmod 666 /ericsson/tor/dhcp_config/dhcpai_config/dhcp_subnet.conf
	
	touch /etc/netmasks
	chmod 666 /etc/netmasks
	
	# horse into netmasks
	192.168.0.0  255.255.255.0
	
	# bleedin /etc folder needs write permission for litp_jboss
	chmod 777 /etc
	
	# original jenkins job has a lot of 'shell execute' block, as running against a static config
	# these dont make sense e.g. as env.properties basically has ear version static here no point pulling latest ear from nexus
	# listing all four original blocks below so not to loose them
	
	# execute shell 1

	echo
	echo "-----------------------------------------"
	
	hostProperties=ERICTAFdhcpai_CXP9030709/src/main/resources/taf_properties/host.properties
	rm -rf ERICTAFdhcpai_CXP9030709/src/main/resources/taf_properties/host*
	rm -rf ERICTAFdhcpai_CXP9030709/src/main/resources/taf_properties_cloud/
	
	echo "jenkins: creating new host.properties file $hostProperties"
	
	echo "#This parameter is used by TafHosts class to determine the deployment type
	deployment.type=cloud
	
	host.dhcpai.ip = $vCloudGateway
	
	host.dhcpai.user.root.pass = shroot
	host.dhcpai.user.root.type = ADMIN
	host.dhcpai.port.ssh=22
	
	#tunnelling to sc1 which is behind the dhcpai
	host.dhcpai.node.sc1.tunnel = 1000
	host.dhcpai.node.sc1.type = SC1
	host.dhcpai.node.sc1.ip = 192.168.0.43
	host.dhcpai.node.sc1.port.ssh=22
	host.dhcpai.node.sc1.port.http = 8080
	host.dhcpai.node.sc1.port.jmx = 9999
	host.dhcpai.node.sc1.port.rmi=4447
	host.dhcpai.node.sc1.user.root.pass=litpc0b6lEr
	host.dhcpai.node.sc1.user.root.type=ADMIN
	
	#tunnelling to sc1jbossinstance which is behind the dhcpai
	host.dhcpai.node.sc1jbi.tunnel = 2000
	host.dhcpai.node.sc1jbi.type = jboss
	host.dhcpai.node.sc1jbi.ip = 192.168.0.70
	host.dhcpai.node.sc1jbi.port.ssh=22
	host.dhcpai.node.sc1jbi.port.http = 8080
	host.dhcpai.node.sc1jbi.port.rmi = 4447
	host.dhcpai.node.sc1jbi.port.jmx = 9999
	host.dhcpai.node.sc1jbi.port.jms = 5445
	host.dhcpai.node.sc1jbi.user.guest.pass=guestp
	host.dhcpai.node.sc1jbi.user.guest.type=OPER 
	
	#tunnelling to sc2 which is behind the dhcpai
	host.dhcpai.node.sc2.tunnel = 3000
	host.dhcpai.node.sc2.type = SC2
	host.dhcpai.node.sc2.ip = 192.168.0.44
	host.dhcpai.node.sc2.port.ssh=22
	host.dhcpai.node.sc2.port.http = 8080
	host.dhcpai.node.sc2.port.jmx = 9999
	host.dhcpai.node.sc2.port.jms=9999
	host.dhcpai.node.sc2.user.root.pass=litpc0b6lEr
	host.dhcpai.node.sc2.user.root.type=ADMIN
	
	#tunnelling to sc2jbossinstance which is behind the dhcpai
	host.dhcpai.node.sc2jbi.tunnel = 4000
	host.dhcpai.node.sc2jbi.type = jboss
	host.dhcpai.node.sc2jbi.ip = 192.168.0.71
	host.dhcpai.node.sc2jbi.port.ssh=22
	host.dhcpai.node.sc2jbi.port.http = 8080
	host.dhcpai.node.sc2jbi.port.rmi = 4447
	host.dhcpai.node.sc2jbi.port.jmx = 9999
	host.dhcpai.node.sc2jbi.port.jms = 4447
	host.dhcpai.node.sc2jbi.user.guest.pass=guestp
	host.dhcpai.node.sc2jbi.user.guest.type=OPER
	
	" > $hostProperties
	echo "-----------------------------------------"
	echo 
	
	********************************************************************************************************************************************************************************************************
	# execute shell 2
	
	echo 
	echo "-----------------------------------------"
	ME="Dhcpai_TAF_unit jenkins"
	hostProperties=./ERICTAFdhcpai_CXP9030709/src/main/resources/taf_properties/host.properties
	sc1RootPass=`grep sc1.user.root.pass $hostProperties|awk -F= '{print $2}'`
	sc1JbossHome=/home/jboss/Serv_su_0_jee_instance/
	sc1JbossIp=192.168.0.70
	
	echo "$ME: sc1 root password=\"$sc1RootPass\""
	
	./scpCommand.exp root@$vCloudGateway $sc1RootPass redeployLatest.bash 2243 /root/
	
	if ! ./sshCommand.exp root@$vCloudGateway $sc1RootPass "ls -1 /root/redeployLatest.bash" 2243 "/root/redeployLatest.bash"
	then 
	   echo "$ME ERROR: failed to copy over  redeployLatest.bash file on remote vCloud $vCloudGateway"
	   exit 1
	fi
	echo "$ME DEBUG: ./sshCommand.exp root@$vCloudGateway $sc1RootPass \"/root/redeployLatest.bash $nexus $productName $sc1JbossHome $sc1JbossIp\" 2243 \"successfully deployed new ear file\""
	
	if ! ./sshCommand.exp root@$vCloudGateway $sc1RootPass "/root/redeployLatest.bash $nexus $productName $sc1JbossHome $sc1JbossIp" 2243 "successfully deployed new ear file"
	then 
	   echo "$ME ERROR: failed to redeploy ear file on remote vCloud $vCloudGateway"
	   exit 1
	fi
	echo "-----------------------------------------"
	echo
	
	
	********************************************************************************************************************************************************************************************************
	# execute shell 3
	
	echo
	echo "-----------------------------------------"
	fileName=ERICTAFdhcpai_CXP9030709/pom.xml
	echo "JENKINS: updating product version in pom.xml hack"
	
	mavenXml=${nexus}/${productName}/maven-metadata.xml
	latestVersion=`curl $mavenXml 2>/dev/null|grep release|tr "<" " "|tr ">" " "|awk '{print $2}'`
	sed -i "s/dhcpai_version>.*</dhcpai_version>$latestVersion</g" $fileName
	echo "-----------------------------------------"
	echo
	
	********************************************************************************************************************************************************************************************************
	# execute shell 4
	
	echo
	echo "-----------------------------------------"
	fileName=ERICTAFdhcpai_CXP9030709/src/main/resources/taf_properties/env.properties 
	echo "JENKINS: updating product version in pom.xml hack"
	
	mavenXml=${nexus}/${productName}/maven-metadata.xml
	latestVersion=`curl $mavenXml 2>/dev/null|grep release|tr "<" " "|tr ">" " "|awk '{print $2}'`
	sed -i "s/dhcpai.jndi.root=.*/dhcpai.jndi.root=\/dhcpai-ear-$latestVersion\/dhcpai-ejb-$latestVersion/g" $fileName
	
	********************************************************************************************************************************************************************************************************
	
	
	
	
	