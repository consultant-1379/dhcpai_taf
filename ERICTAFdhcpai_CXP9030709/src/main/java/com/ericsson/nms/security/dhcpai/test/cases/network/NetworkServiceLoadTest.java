package com.ericsson.nms.security.dhcpai.test.cases.network;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.dhcpai.common.Network;
import com.ericsson.nms.security.dhcpai.test.common.*;
import com.ericsson.nms.security.dhcpai.test.common.stats.*;
import com.ericsson.nms.security.dhcpai.test.operators.network.NetworkServiceOperator;

public class NetworkServiceLoadTest extends DHCPAITestCaseHelper {

    @Inject
    OperatorRegistry<NetworkServiceOperator> networkOperatorRegistry;
    private static NetworkServiceOperator networkServiceOperator = null;

    DhcpaiStats dhcpaiStats = new DhcpaiStats(Network.class.getSimpleName());
    final int testIterations = Integer.parseInt((String) DataHandler.getAttribute(TestConstants.NETWORK_LOAD_ITERATIONS));
    final int statInterval = testIterations / 20;

    @Context(context = { Context.API })
    @Test(groups = "acceptance")
    public void setup() {
        new CommandRunner<Object>(null).setupNetworkLoadTestEnv();
    }

    @Context(context = { Context.API })
    @DataDriven(name = "network_e2e")
    @Test(groups = "acceptance", dependsOnMethods = { "setup" })
    public void addNetwork(@Input("subnet") String subnet, @Input("netmask") String netmask, @Input("dnsServers") String dnsServers, @Input("dnsDomainName") String dnsDomainName,
            @Input("defaultRouter") String defaultRouter) {

        networkServiceOperator = networkOperatorRegistry.provide(NetworkServiceOperator.class);
        OperationStats addStats = new OperationStats(testIterations);
        int count, thirdOctetRange;

        for (int secondOctet = 0; secondOctet <= testIterations / 255; secondOctet++) {
            thirdOctetRange = calculateFourthOctetRange(testIterations, secondOctet);
            for (int thirdOctet = 0; thirdOctet < thirdOctetRange; thirdOctet++) {
                count = secondOctet * 256 + thirdOctet;
                Network networkAdd = createNetwork("192." + secondOctet + "." + thirdOctet + ".1", dnsDomainName, dnsServers, subnet, "192." + secondOctet + "." + thirdOctet + ".0");
                addOperation(count, networkAdd, addStats);
            }
        }
        addStats.setFinishTime();
        dhcpaiStats.setAddStats(addStats);
    }

    @Context(context = { Context.API })
    @Test(groups = "acceptance", dependsOnMethods = { "addNetwork" })
    public void performExistCheck() {

        networkServiceOperator = networkOperatorRegistry.provide(NetworkServiceOperator.class);
        OperationStats existsStats = new OperationStats(testIterations);
        int count, thirdOctetRange;

        for (int secondOctet = 0; secondOctet <= testIterations / 255; secondOctet++) {
            thirdOctetRange = calculateFourthOctetRange(testIterations, secondOctet);
            for (int thirdOctet = 0; thirdOctet < thirdOctetRange; thirdOctet++) {
                count = secondOctet * 256 + thirdOctet;
                Network networkExist = createNetwork(null, null, null, "255.255.255.0", "192." + secondOctet + "." + thirdOctet + ".0");
                existsOperation(count, networkExist, existsStats);

            }
        }
        existsStats.setFinishTime();
        dhcpaiStats.setExistStats(existsStats);
    }

    @Context(context = { Context.API })
    @Test(groups = "acceptance", dependsOnMethods = { "performExistCheck" })
    public void performFind() {

        networkServiceOperator = networkOperatorRegistry.provide(NetworkServiceOperator.class);
        OperationStats findStats = new OperationStats(testIterations);
        int count, thirdOctetRange;

        for (int secondOctet = 0; secondOctet <= testIterations / 255; secondOctet++) {
            thirdOctetRange = calculateFourthOctetRange(testIterations, secondOctet);
            for (int thirdOctet = 0; thirdOctet < thirdOctetRange; thirdOctet++) {
                count = secondOctet * 256 + thirdOctet;
                Network networkFind = createNetwork(null, null, null, "255.255.255.0", "192." + secondOctet + "." + thirdOctet + ".0");
                findOperation(count, networkFind, findStats);
            }
        }
        findStats.setFinishTime();
        dhcpaiStats.setFindStats(findStats);
    }

    @Context(context = { Context.API })
    @Test(groups = "acceptance", dependsOnMethods = { "performFindCheck" })
    public void performDelete() {

        networkServiceOperator = networkOperatorRegistry.provide(NetworkServiceOperator.class);
        OperationStats deleteStats = new OperationStats(testIterations);
        int count, thirdOctetRange;

        for (int secondOctet = 0; secondOctet <= testIterations / 255; secondOctet++) {
            thirdOctetRange = calculateFourthOctetRange(testIterations, secondOctet);
            for (int thirdOctet = 0; thirdOctet < thirdOctetRange; thirdOctet++) {
                count = secondOctet * 256 + thirdOctet;
                Network networkDelete = createNetwork(null, null, null, "255.255.255.0", "192." + secondOctet + "." + thirdOctet + ".0");
                deleteOperation(count, networkDelete, deleteStats);

            }
        }
        deleteStats.setFinishTime();
        dhcpaiStats.setDeleteStats(deleteStats);
        writeStats();
    }

    private void addOperation(int count, Network network, OperationStats addStats) {

        if (isAtSamplingPoint(count)) {
            networkServiceOperator.addAndCheckResponse(network);
            addStats.addOp(count);
        } else {
            networkServiceOperator.addAndCheckUUID(network);
        }
    }

    private void deleteOperation(int count, Network network, OperationStats deleteStats) {

        if (isAtSamplingPoint(count)) {
            networkServiceOperator.deleteAndCheckResponse(network);
            deleteStats.addOp(count);
        } else {
            networkServiceOperator.deleteAndCheckUUID(network);
        }
    }

    private void findOperation(int count, Network network, OperationStats findStats) {

        networkServiceOperator.find(network);
        if (isAtSamplingPoint(count)) {
            findStats.addOp(count);
        }
    }

    private void existsOperation(int count, Network network, OperationStats existStats) {

        networkServiceOperator.exists(network);
        if (isAtSamplingPoint(count)) {
            existStats.addOp(count);
        }
    }

    private int calculateFourthOctetRange(int testIterations, int thirdOctet) {
        if (thirdOctet == testIterations / 255) {
            return (testIterations - (thirdOctet * 255));
        } else {
            return 256;
        }
    }

    private boolean isAtSamplingPoint(int count) {
        return (count % statInterval == 0) && (count != 0);
    }

    private Network createNetwork(String defaultRouter, String dnsDomainName, String dnsServers, String netmask, String subnet) {
        Network result = new Network();
        result.setDefaultRouter(defaultRouter);
        result.setDnsDomainName(dnsDomainName);
        result.setDnsServers(getDNSServerList(dnsServers));
        result.setNetmask(netmask);
        result.setSubnet(subnet);
        return result;
    }

    private void writeStats() {
        final StatsObjectMapper objectMapper = new StatsObjectMapper();
        final String fileName = "dhcpai-taf-testcase/test-output/loadtest/data/" + "Network" + "-stats.json";
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
