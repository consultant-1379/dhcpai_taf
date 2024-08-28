package com.ericsson.nms.security.dhcpai.test.cases.range;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.dhcpai.common.NetworkRange;
import com.ericsson.nms.security.dhcpai.test.common.DHCPAITestCaseHelper;
import com.ericsson.nms.security.dhcpai.test.common.TestConstants;
import com.ericsson.nms.security.dhcpai.test.common.stats.*;
import com.ericsson.nms.security.dhcpai.test.operators.range.NetworkRangeServiceOperator;

/***** Network load test must be run prior to this test ******/
public class NetworkRangeServiceLoadTest extends DHCPAITestCaseHelper {

    @Inject
    OperatorRegistry<NetworkRangeServiceOperator> networkRangeOperatorRegistry;
    private static NetworkRangeServiceOperator networkRangeServiceOperator = null;

    DhcpaiStats dhcpaiStats = new DhcpaiStats(NetworkRange.class.getSimpleName());
    final int testIterations = Integer.parseInt((String) DataHandler.getAttribute(TestConstants.NETWORKRANGE_LOAD_ITERATIONS));
    final int statInterval = testIterations / 20;

    @Context(context = { Context.API })
    @Test(groups = "acceptance")
    @DataDriven(name = "networkRange_e2e")
    public void addNetwork() {

        networkRangeServiceOperator = networkRangeOperatorRegistry.provide(NetworkRangeServiceOperator.class);
        OperationStats addStats = new OperationStats(testIterations);
        int count, thirdOctetRange;

        for (int secondOctet = 0; secondOctet <= testIterations / 255; secondOctet++) {
            thirdOctetRange = calculateFourthOctetRange(testIterations, secondOctet);
            for (int thirdOctet = 0; thirdOctet < thirdOctetRange; thirdOctet++) {
                count = secondOctet * 256 + thirdOctet;
                NetworkRange networkRangeAdd = createNetworkRange(secondOctet, thirdOctet);
                addOperation(count, networkRangeAdd, addStats);
            }
        }
        addStats.setFinishTime();
        dhcpaiStats.setAddStats(addStats);
    }

    @Context(context = { Context.API })
    @Test(groups = "acceptance", dependsOnMethods = { "addNetwork" })
    public void performExistCheck() {

        networkRangeServiceOperator = networkRangeOperatorRegistry.provide(NetworkRangeServiceOperator.class);
        OperationStats existsStats = new OperationStats(testIterations);
        int count, thirdOctetRange;

        for (int secondOctet = 0; secondOctet <= testIterations / 255; secondOctet++) {
            thirdOctetRange = calculateFourthOctetRange(testIterations, secondOctet);
            for (int thirdOctet = 0; thirdOctet < thirdOctetRange; thirdOctet++) {
                count = secondOctet * 256 + thirdOctet;
                NetworkRange networkRangeExist = createNetworkRange(secondOctet, thirdOctet);
                existsOperation(count, networkRangeExist, existsStats);

            }
        }
        existsStats.setFinishTime();
        dhcpaiStats.setExistStats(existsStats);
    }

    @Context(context = { Context.API })
    @Test(groups = "acceptance", dependsOnMethods = { "performExistCheck" })
    public void performFind() {

        networkRangeServiceOperator = networkRangeOperatorRegistry.provide(NetworkRangeServiceOperator.class);
        OperationStats findStats = new OperationStats(testIterations);
        int count, thirdOctetRange;

        for (int secondOctet = 0; secondOctet <= testIterations / 255; secondOctet++) {
            thirdOctetRange = calculateFourthOctetRange(testIterations, secondOctet);
            for (int thirdOctet = 0; thirdOctet < thirdOctetRange; thirdOctet++) {
                count = secondOctet * 256 + thirdOctet;
                NetworkRange networkRangeFind = createNetworkRange(secondOctet, thirdOctet);
                findOperation(count, networkRangeFind, findStats);

            }
        }
        findStats.setFinishTime();
        dhcpaiStats.setFindStats(findStats);
    }

    @Context(context = { Context.API })
    @Test(groups = "acceptance", dependsOnMethods = { "performFind" })
    public void performDelete() {

        networkRangeServiceOperator = networkRangeOperatorRegistry.provide(NetworkRangeServiceOperator.class);
        OperationStats deleteStats = new OperationStats(testIterations);
        int count, thirdOctetRange;

        for (int secondOctet = 0; secondOctet <= testIterations / 255; secondOctet++) {
            thirdOctetRange = calculateFourthOctetRange(testIterations, secondOctet);
            for (int thirdOctet = 0; thirdOctet < thirdOctetRange; thirdOctet++) {
                count = secondOctet * 256 + thirdOctet;
                NetworkRange networkRangeDelete = createNetworkRange(secondOctet, thirdOctet);
                deleteOperation(count, networkRangeDelete, deleteStats);

            }
        }
        deleteStats.setFinishTime();
        dhcpaiStats.setDeleteStats(deleteStats);
        writeStats();
    }

    private void addOperation(int count, NetworkRange networkRange, OperationStats addStats) {

        if (isAtSamplingPoint(count)) {
            networkRangeServiceOperator.addAndCheckResponse(networkRange);
            addStats.addOp(count);
        } else {
            networkRangeServiceOperator.addAndCheckUUID(networkRange);
        }
    }

    private void deleteOperation(int count, NetworkRange networkRange, OperationStats deleteStats) {

        if (isAtSamplingPoint(count)) {
            networkRangeServiceOperator.deleteAndCheckResponse(networkRange);
            deleteStats.addOp(count);
        } else {
            networkRangeServiceOperator.deleteAndCheckUUID(networkRange);
        }
    }

    private void findOperation(int count, NetworkRange networkRange, OperationStats findStats) {

        networkRangeServiceOperator.find(networkRange);
        if (isAtSamplingPoint(count)) {
            findStats.addOp(count);
        }
    }

    private void existsOperation(int count, NetworkRange networkRange, OperationStats existStats) {

        networkRangeServiceOperator.exists(networkRange);
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

    private NetworkRange createNetworkRange(int secondOctet, int thirdOctet) {
        NetworkRange networkRange = new NetworkRange();
        String baseIp = "192." + secondOctet + "." + thirdOctet + ".";
        networkRange.setDefaultRouter(baseIp + "1");
        networkRange.setStartAddress(baseIp + "10");
        networkRange.setEndAddress(baseIp + "20");
        networkRange.setLeaseTime(1000l);
        networkRange.setNetmask("255.255.255.0");
        networkRange.setPrimaryNTPTimeServer("4.4.4.4");
        networkRange.setPrimaryWebServer("5.5.5.5");
        networkRange.setSmrsSlaveService("10.0.2.111");

        return networkRange;
    }

    private void writeStats() {
        final StatsObjectMapper objectMapper = new StatsObjectMapper();
        final String fileName = "dhcpai-taf-testcase/test-output/loadtest/data/" + "NetworkRange" + "-stats.json";
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
}
