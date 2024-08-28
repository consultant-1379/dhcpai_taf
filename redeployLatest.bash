#!/bin/bash
# 2014 helge
 
ME=`basename $0`
if [[ -n $1 ]]
then
    nexus=$1
else
    nexus=https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/repositories/releases/com/ericsson/nms/security/
fi

if [[ -n $2 ]]
then
    productName=$2
else
    productName=ERICdhcpai_CXP9030707
fi

if [[ -n $3 ]]
then
    jbossHome=$3
else
	if ls /home/jboss|grep jee_instance;
	then
	    jbossHome="/home/jboss/`ls -1tr /home/jboss/|grep jee_instance|tail -1`"
	else
	    jbossHome=/home/jboss
	fi
fi

if [[ -n $4 ]]
then
    jbossIp=$4
else
    hostname=`hostname`
    if echo $hostname|grep -i ms1
    then
        jbossIp=127.0.0.1
    elif echo $hostname|grep -i sc1
    then
        jbossIp=192.168.0.70
    elif echo $hostname|grep -i sc2
    then
        jbossIp=192.168.0.71
    else
        echo "$ME: ERROR cant figure out jboss controller ip"
        exit 1
    fi
fi

echo "$ME DEBUG: 
nexus=$nexus
productName=$productName
jbossHome=$jbossHome
jbossIp=$jbossIp
"
 
disableService() {
    local service=$1
    if service $service status
    then
        service $service stop
    fi
    chkconfig --del $service &>/dev/null
}

ensureLatestRpmIsInstalled(){
    # this currently does a uninstall before installing a new version as we want to test upgradablility somewhere else
    local packageName=$1
    local newVersion=$2    

    echo "$ME: if there is a older version of $packageName installed we uninstall it."
    if rpm -qi $packageName &>/dev/null
    then
        
        echo "$ME: $packageName is installed checking its version == $newVersion."
        installedRpmVersion=`rpm -qi $packageName|grep Version|awk '{print $3}'`
        if [[ $newVersion = $installedRpmVersion ]]
        then
            echo "$ME: $packageName is installed in the latest version=$installedRpmVersion."
            return 0
        fi
        installedRpm=`rpm -q $packageName`
        echo "$ME: uninstalling installed rpm=$installedRpm with version=$installedRpmVersion"
        rpm -vve $installedRpm
    fi
    echo

    echo "$ME: creating temporary dir for our new rpms:"
    rpmDir=/tmp/$packageName
    mkdir $rpmDir &>/dev/null
    echo 

    echo "$ME: clearing dir and fetching new rpm"
    cd $rpmDir
    rm -rf *.rpm
    rpmUrl=${nexus}/${packageName}/${latestVersion}/${packageName}-${latestVersion}.rpm
    curl -O $rpmUrl
    echo

	echo "$ME: installing new rpm ${packageName}-${latestVersion}.rpm"
	if ! rpm -vvi ${packageName}-${latestVersion}.rpm
	then
	    echo "$ME: error could not install new rpm ${packageName}-${latestVersion}.rpm"
	    return 1
	fi
}

ensureWeHaveTheLatestEarDeployed(){
    local jbossHome=$1
    local jbossIp=$2
    local earInstallSource=$3
    local earName=`basename $earInstallSource`
    local earSearch=`echo $earName|awk -F\- '{print $1 "-" $2}'`

    echo "$ME: test if jboss can be connected"
    if ! ${jbossHome}/bin/jboss-cli.sh --controller=$jbossIp -c --command="ls deployment" 1>/dev/null
    then 
        echo "$ME ERROR: can not connnect to jboss via controller ip $jbossIp"
        return 1
    fi
    echo 
    
    echo "$ME: exit if we already have the latest ear deployed"
    if ${jbossHome}/bin/jboss-cli.sh --controller=$jbossIp -c --command="ls deployment"|grep $earName
    then 
        echo "$ME: ear $earName is already in the latest version deployed"
        return 0
    fi
    echo 
    
	echo "$ME: undeploy any existing ear or war matching $earSearch:"
    ${jbossHome}/bin/jboss-cli.sh --controller=$jbossIp -c --command="ls deployment"|grep $earSearch|while read ear
    do
        echo "$ME: undeploying $ear"
        ${jbossHome}/bin/jboss-cli.sh --controller=$jbossIp -c --command="undeploy $ear";
	done
	echo 
	 
	echo "$ME: deploy $earInstallSource"
	$jbossHome/bin/jboss-cli.sh --controller=$jbossIp -c --command="deploy $earInstallSource";
	 
	$jbossHome/bin/jboss-cli.sh --controller=$jbossIp -c --command="ls deployment"|grep $earName
   
}    
echo "$ME:put node into a testing state disabling puppet and iptables:"
disableService puppet
disableService iptables
disableService landscaped
echo

echo "$ME:fetching latest released version from nexus"
mavenXml=${nexus}/${productName}/maven-metadata.xml
latestVersion=`curl $mavenXml 2>/dev/null|grep release|tr "<" " "|tr ">" " "|awk '{print $2}'`
echo  

echo "$ME: ensure we have the latest version of $productName installed"
if ! ensureLatestRpmIsInstalled $productName $latestVersion
then
    echo "$ME: error could not install new rpm ${productName}-${latestVersion}.rpm"
    exit 1
fi

echo "$ME: get install source and earname for later"
earInstallSource=`rpm -ql $productName |grep ear|head -1`

if ! ensureWeHaveTheLatestEarDeployed $jbossHome $jbossIp $earInstallSource
then
    echo "$ME ERROR: unable to deploy $earName"
    exit 1
fi

#make sure to end on this string for expect to catch:
echo 
echo "successfully deployed new ear file"


