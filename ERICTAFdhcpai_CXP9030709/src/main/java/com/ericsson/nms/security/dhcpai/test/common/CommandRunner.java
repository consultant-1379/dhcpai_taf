/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.dhcpai.test.common;

import static com.ericsson.nms.security.dhcpai.test.common.TestConstants.*;

import java.util.*;

import javax.ejb.EJBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.AsRmiHandler;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;
import com.ericsson.nms.security.dhcpai.api.*;
import com.ericsson.nms.security.dhcpai.command.common.CommandResponse;
import com.ericsson.nms.security.dhcpai.common.NetworkElement;
import com.ericsson.nms.security.dhcpai.exception.DHCPAIServerException;
import com.ericsson.nms.security.dhcpai.exception.DHCPAIValidationException;

public class CommandRunner<T> {

	private static final Logger logger = LoggerFactory.getLogger(CommandRunner.class);
	  
    private static final String RESULT_SEPERATOR = ":";
    protected T t;
    private CommandJMSHandler commandJMSHandler;
    //private static Host server = DataHandler.getHostByName(DHCPAI_HOST_NAME_IDENTIFIER);
    private static Host server;
    String jndiRoot = (String) DataHandler.getAttribute(DHCPAI_JNDI_SERVICE_ROOT);

    // shouldnt be here but currently debugging
    static{
    	logger.debug("init host server: "+ DHCPAI_HOST_NAME_IDENTIFIER);
    	server = DataHandler.getHostByName(DHCPAI_HOST_NAME_IDENTIFIER);
    	logger.debug("host server: "+ server);
    	
    }
    
    public CommandRunner(T t) {
        this.t = t;
    }

    public T getUnderlyingObject() {
        return t;
    }

    public UUID executeAddOrDelete() {
        throw new UnsupportedOperationException("Implement execute Add or Delete method, by overriding");
    }

    public boolean executeExist() {
        throw new UnsupportedOperationException("Implement execute exist method, by overriding");
    }

    public NetworkElement executeFind() {
        throw new UnsupportedOperationException("Implement execute find method, by overriding");
    }

    public List<? extends NetworkElement> executeList() {
        throw new UnsupportedOperationException("Implement execute exist method, by overriding");
    }

    public String checkUUIDResult(UUID uuid) {
        if (uuid != null)
            return OPERATION_SUCCESS + RESULT_SEPERATOR + uuid.toString();
        else
            return OPERATION_FAILED;
    }

    public String getStringOuputFromExistsResult(boolean result) {
        if (result)
            return EXISTS;
        else
            return DOES_NOT_EXIST;
    }

    public String findResult(NetworkElement networkElement) {
        if (networkElement != null)
            return FOUND + " : " + networkElement.toString();
        else
            return NULL;
    }

    public String getStringOuputFromCheckAvailableResult(boolean result) {
        if (result)
            return AVAILABLE;
        else
            return NOT_AVAILABLE;
    }

    public String executeAndCheckUUID() {
        try {

            return checkUUIDResult(executeAddOrDelete());
        } catch (DHCPAIServerException e) {
            return DHCPAI_SERVER_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (DHCPAIValidationException e) {
            return DHCPAI_VALIDATION_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (EJBException e) {
            return EJB_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (Exception e) {
            return OPERATION_ERROR + RESULT_SEPERATOR + e.getMessage();
        }
    }

    public String executeAndCheckExist() {
        try {

            return getStringOuputFromExistsResult(executeExist());
        } catch (DHCPAIServerException e) {
            return DHCPAI_SERVER_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (DHCPAIValidationException e) {
            return DHCPAI_VALIDATION_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (EJBException e) {
            return EJB_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (Exception e) {
            return OPERATION_ERROR + RESULT_SEPERATOR + e.getMessage();
        }
    }

    public String executeAndCheckAvailable() {
        try {

            return getStringOuputFromCheckAvailableResult(executeExist());
        } catch (DHCPAIServerException e) {
            return DHCPAI_SERVER_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (DHCPAIValidationException e) {
            return DHCPAI_VALIDATION_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (EJBException e) {
            return EJB_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (Exception e) {
            return OPERATION_ERROR + RESULT_SEPERATOR + e.getMessage();
        }
    }

    public String executeAndFind() {
        try {
            return findResult(executeFind());
        } catch (DHCPAIServerException e) {
            return DHCPAI_SERVER_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (DHCPAIValidationException e) {
            return DHCPAI_VALIDATION_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (EJBException e) {
            return EJB_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (Exception e) {
            return OPERATION_ERROR + RESULT_SEPERATOR + e.getMessage();
        }
    }

    public List<? extends NetworkElement> list() {
        try {

            return executeList();

        } catch (Exception e) {
            Assert.fail("Failed to list network element", e);
        }
        return new ArrayList<>();
    }

    public String executeAndCheckResponse() {
        try {
            commandJMSHandler = new CommandJMSHandler(server);
            commandJMSHandler.createJMSHandler();

            UUID uuid = executeAddOrDelete();
            CommandResponse commandResponse = commandJMSHandler.getCommandResponse(uuid);
            switch (commandResponse.getType()) {
            case SUCCESS:
                return OPERATION_SUCCESS + RESULT_SEPERATOR + commandResponse.getMessage();
            case ERROR:
                return OPERATION_ERROR + RESULT_SEPERATOR + commandResponse.getMessage();
            case WARNING:
                return OPERATION_WARNING + RESULT_SEPERATOR + commandResponse.getMessage();
            default:

            }
            return OPERATION_SUCCESS;
        } catch (DHCPAIServerException e) {
            return DHCPAI_SERVER_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (DHCPAIValidationException e) {
            return DHCPAI_VALIDATION_EXCEPTION + RESULT_SEPERATOR + e.getMessage();
        } catch (EJBException e) {
            return EJB_EXCEPTION;
        } catch (Exception e) {
            e.printStackTrace();
            return OPERATION_ERROR + RESULT_SEPERATOR + e.getMessage();
        }
    }

    public NetworkService getNetworkService() {
        String jndiString = jndiRoot + (String) DataHandler.getAttribute(NETWORK_SERVICE_JNDI);
        try {
            AsRmiHandler asRmiHandler = new AsRmiHandler(server);
            return (NetworkService) asRmiHandler.getServiceViaJndiLookup(jndiString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to locate DHCPAI Network service EJB.", e);
        }
    }

    public NetworkRangeService getNetworkRangeService() {
        String jndiString = jndiRoot + (String) DataHandler.getAttribute(NETWORK_RANGE_SERVICE_JNDI);

        try {
            AsRmiHandler asRmiHandler = new AsRmiHandler(server);
            return (NetworkRangeService) asRmiHandler.getServiceViaJndiLookup(jndiString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to locate DHCPAI NetworkRange service EJB.", e);
        }
    }

    public ClientService getClientService() {
        String jndiString = jndiRoot + (String) DataHandler.getAttribute(CLIENT_SERVICE_JNDI);

        try {
            AsRmiHandler asRmiHandler = new AsRmiHandler(server);
            return (ClientService) asRmiHandler.getServiceViaJndiLookup(jndiString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to locate DHCPAI Client service EJB.", e);
        }
    }

    public IpAddressService getIpAddressService() {
        String jndiString = jndiRoot + (String) DataHandler.getAttribute(IP_ADDRESS_SERVICE_JNDI);

        try {
            AsRmiHandler asRmiHandler = new AsRmiHandler(server);
            return (IpAddressService) asRmiHandler.getServiceViaJndiLookup(jndiString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to locate DHCPAI IpAddress service EJB.", e);
        }
    }

    public void resetDHCPConfigFiles() {
    	 Host server = null;
    	try {
        	
            //server = DataHandler.getHostByName("dhcpai");
    		server = DataHandler.getHostByName("ms1");
            logger.debug("resetting dhcp config files on server: {} {}",server,server.getIp());
            SshRemoteCommandExecutor executor = new SshRemoteCommandExecutor(server);

            boolean result = executor.sendCommand("mv -f /ericsson/tor/dhcp_config/dhcpai_config/dhcpd.conf /ericsson/tor/dhcp_config/dhcpai_config/dhcpd_bk.conf");
            logger.debug("mv dhcpd.conf result: {}",result);
            
            result = executor.sendCommand("mv -f /ericsson/tor/dhcp_config/dhcpai_config/dhcp_static.conf /ericsson/tor/dhcp_config/dhcpai_config/dhcp_static_bk.conf");
            logger.debug("mv dhcp_static.conf result: {}",result);
            
            executor.sendCommand("mv -f /ericsson/tor/dhcp_config/dhcpai_config/dhcp_subnet.conf /ericsson/tor/dhcp_config/dhcpai_config/dhcp_subnet_bk.conf");
            
            executor.sendCommand("touch /ericsson/tor/dhcp_config/dhcpai_config/dhcpd.conf ");
            executor.sendCommand("chmod 666 /ericsson/tor/dhcp_config/dhcpai_config/dhcpd.conf ");
                      
            executor.sendCommand("touch /ericsson/tor/dhcp_config/dhcpai_config/dhcp_static.conf ");
            executor.sendCommand("chmod 666 /ericsson/tor/dhcp_config/dhcpai_config/dhcp_static.conf ");
            
            executor.sendCommand("touch /ericsson/tor/dhcp_config/dhcpai_config/dhcp_subnet.conf ");
            executor.sendCommand("chmod 666 /ericsson/tor/dhcp_config/dhcpai_config/dhcp_subnet.conf ");
        } catch (Throwable e) {
            logger.error("Problem cleaning dhcp config files on server: {} exception: {}",server, e);
        }
    }

    public void setupNetworkLoadTestEnv() {
        try {

            final Host server = DataHandler.getHostByName("dhcpai");

            final SshRemoteCommandExecutor executor = new SshRemoteCommandExecutor(server);

            // create clients in the static conf file
            String attribute = (String) DataHandler.getAttribute(PRE_SIZE_CLIENT_COUNT);
            if (!isEmpty(attribute)) {
                final Integer clientCount = Integer.parseInt(attribute);
                final String cmd = "for nr in {1.." + clientCount + "};do /opt/ericsson/ocs/bin/add_client myClientId$nr myClientHostName$nr "
                        + "192.168.0.$nr 192.168.0.0 10.0.0.1 /my/path/to/something$nr; done > /ericsson/tor/dhcp_config/dhcpai_config/dhcp_static.conf;";
                executor.sendCommand(cmd);
            }

            // create networks in the subnet conf file
            attribute = (String) DataHandler.getAttribute(PRE_SIZE_NETWORK_COUNT);
            if (!isEmpty(attribute)) {
                final Integer networkCount = Integer.parseInt(attribute);

                int count, thirdOctetRange;
                String subnet;

                for (int secondOctet = 0; secondOctet <= networkCount / 255; secondOctet++) {
                    thirdOctetRange = calculateFourthOctetRange(networkCount, secondOctet);
                    for (int thirdOctet = 0; thirdOctet < thirdOctetRange; thirdOctet++) {
                        count = secondOctet * 256 + thirdOctet;
                        subnet = "192." + secondOctet + "." + thirdOctet + ".0";

                        String interfaceCmd = "DEVICE=\"eth0:" + count + "\" BOOTPROTO=\"static\" \n" + "IPADDR=" + subnet + "\n" + "NETMASK=255.255.255.0 \n" + "DHCP_HOSTNAME=\"ms1\" \n"
                                + "HOSTNAME=\"ms1\"\n" + "IPV6INIT=\"yes\"\n" + "IPV6_AUTOCONF=\"yes\"\n" + "MTU=\"1500\"\n" + "NM_CONTROLLED=\"yes\"\n" + "ONBOOT=\"yes\"\n" + "TYPE=\"Ethernet\"";
                        executor.sendCommand("echo \"" + interfaceCmd + "\" >>/etc/sysconfig/network-scripts/ifcfg-eth0:\"" + count + "\"");
                    }
                }
                executor.sendCommand("service network reload");
            }
        } catch (Throwable e) {
        	logger.error("Problem setting up load test env: ",e);
        }
    }

    private int calculateFourthOctetRange(int testIterations, int secondOctet) {
        if (secondOctet == testIterations / 255) {
            return (testIterations - (secondOctet * 255));
        } else {
            return 256;
        }
    }

    public String checkDHCPDConfig() {
        String staticCheck = checkDHCPDConfig("dhcpd -t -cf /ericsson/tor/dhcp_config/dhcpai_config/dhcp_static.conf");
        if (staticCheck.contains("SUCCESS")) {
            return checkDHCPDConfig("dhcpd -t -cf /ericsson/tor/dhcp_config/dhcpai_config/dhcp_subnet.conf");
        }
        return staticCheck;
    }

    public String checkDHCPDConfig(String cmd) {
        try {
            Host server = DataHandler.getHostByName("dhcpai");

            SshRemoteCommandExecutor executor = new SshRemoteCommandExecutor(server);

            boolean commandResult = executor.execute(cmd);
            if (commandResult) {

                return "SUCCESS:" + executor.getStdOut() + executor.getStdErr();
            } else {

                return "Error:" + executor.getStdErr() + executor.getStdOut();
            }

        } catch (Throwable e) {
        	logger.error("Problem checking dhcp config: ",e);
        }
        return "Error";
    }

    /**
     * @return the server
     */
    public static Host getServer() {
        return server;
    }

    public boolean isEmpty(final String string) {
        return string == null || string.trim().length() == 0;
    }

}
