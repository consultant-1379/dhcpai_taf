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
package com.ericsson.nms.security.dhcpai.test.cases.ip;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.dhcpai.common.*;
import com.ericsson.nms.security.dhcpai.test.common.DHCPAITestCaseHelper;
import com.ericsson.nms.security.dhcpai.test.common.TestConstants;
import com.ericsson.nms.security.dhcpai.test.common.stats.*;
import com.ericsson.nms.security.dhcpai.test.operators.ip.IpAddressServiceOperator;
import com.ericsson.nms.security.dhcpai.test.operators.network.NetworkServiceOperator;
import com.ericsson.nms.security.dhcpai.test.operators.range.NetworkRangeServiceOperator;

public class IpAddressServiceLoadTest extends DHCPAITestCaseHelper {

    @Inject
    OperatorRegistry<NetworkRangeServiceOperator> networkRangeOperatorRegistry;
    static NetworkRangeServiceOperator networkRangeServiceOperator = null;

    @Inject
    OperatorRegistry<NetworkServiceOperator> networkOperatorRegistry;
    static NetworkServiceOperator networkServiceOperator = null;

    @Inject
    OperatorRegistry<IpAddressServiceOperator> operatorRegistry;
    static IpAddressServiceOperator ipAddressServiceOperator = null;

    private final DhcpaiStats dhcpaiStats = new DhcpaiStats(IpAddress.class.getSimpleName());

    @Context(context = { Context.API })
    @BeforeTest
    public void setup() {

        ipAddressServiceOperator = operatorRegistry.provide(IpAddressServiceOperator.class);
        networkServiceOperator = networkOperatorRegistry.provide(NetworkServiceOperator.class);
        networkRangeServiceOperator = networkRangeOperatorRegistry.provide(NetworkRangeServiceOperator.class);

        String subnet = "192.168.0.1";
        Network network = setupNetwork(subnet);
        setupRange(network);

    }

    @Context(context = { Context.API })
    @Test(groups = "acceptance")
    public void performExistCheck() {

        final int testIterations = Integer.parseInt((String) DataHandler.getAttribute(TestConstants.IPADDRESS_LOAD_ITERATIONS));
        OperationStats checkStats = new OperationStats(testIterations);
        int count, fourthOctetRange;

        for (int thirdOctet = 0; thirdOctet <= testIterations / 255; thirdOctet++) {
            fourthOctetRange = calculateFourthOctetRange(testIterations, thirdOctet);
            for (int fourthOctet = 0; fourthOctet < fourthOctetRange; fourthOctet++) {
                count = thirdOctet * 256 + fourthOctet;
                final IpAddress ipAddress = new IpAddress();
                ipAddress.setAddress("192.168." + thirdOctet + "." + fourthOctet);
                ipAddressServiceOperator.checkAvailable(ipAddress);
                if (count % 250 == 0) {
                    checkStats.addOp(count);
                }
            }
        }
        checkStats.setFinishTime();
        dhcpaiStats.setExistStats(checkStats);
        writeStats();
    }

    private Network setupNetwork(String subnet) {

        Network network = new Network();
        String defaultRouter = subnet.substring(0, subnet.length() - 2) + "1";
        network.setDefaultRouter(defaultRouter);
        network.setDnsDomainName("my.domain.com");
        network.setDnsServers(getDNSServerList("222.222.222.222"));
        network.setNetmask("255.255.255.0");
        network.setSubnet(subnet);

        networkServiceOperator.addAndCheckResponse(network);
        return network;
    }

    private void setupRange(Network network) {

        NetworkRange networkRange = new NetworkRange();
        networkRange.setDefaultRouter(network.getDefaultRouter());
        final String firstTwoOctets = network.getSubnet().substring(0, 8);
        networkRange.setStartAddress(firstTwoOctets + "10.0");
        networkRange.setEndAddress(firstTwoOctets + "20.0");
        networkRange.setLeaseTime(1000l);
        networkRange.setNetmask(networkRange.getNetmask());
        networkRange.setPrimaryNTPTimeServer("4.4.4.4");
        networkRange.setPrimaryWebServer("5.5.5.5");
        networkRange.setSmrsSlaveService("10.0.2.111");

        networkRangeServiceOperator.addAndCheckResponse(networkRange);
    }

    private int calculateFourthOctetRange(int testIterations, int thirdOctet) {
        if (thirdOctet == testIterations / 255) {
            return (testIterations - (thirdOctet * 255));
        } else {
            return 256;
        }
    }

    private void writeStats() {

        final StatsObjectMapper objectMapper = new StatsObjectMapper();
        final String fileName = "dhcpai-taf-testcase/test-output/loadtest/data/" + "IpAddress" + "-stats.json";
        System.out.println("--> as json file: " + fileName);

        final File file = new File(fileName);
        try {
            new File("dhcpai-taf-testcase/test-output/loadtest/data").mkdirs();
            objectMapper.writeValue(file, dhcpaiStats);
        } catch (IOException e) {
            //logger.error("--> problem writing stats to file: '{}'  error: '{}' ", file, e);
            e.printStackTrace();
        }
    }

    private static final String STANDARD_DELIMETER = "\\|";

    private List<String> getDNSServerList(String dnsServers) {
        if (dnsServers == null || dnsServers.isEmpty())
            return null;
        return Arrays.asList(dnsServers.split(STANDARD_DELIMETER));
    }

}
