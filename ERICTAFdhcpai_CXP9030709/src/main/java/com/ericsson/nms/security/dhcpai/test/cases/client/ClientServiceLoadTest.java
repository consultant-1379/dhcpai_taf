package com.ericsson.nms.security.dhcpai.test.cases.client;

import static com.ericsson.nms.security.dhcpai.test.common.TestConstants.CLIENT_LOAD_ITERATIONS;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.dhcpai.common.Client;
import com.ericsson.nms.security.dhcpai.common.ClientType;
import com.ericsson.nms.security.dhcpai.test.common.DHCPAITestCaseHelper;
import com.ericsson.nms.security.dhcpai.test.common.stats.*;
import com.ericsson.nms.security.dhcpai.test.operators.client.ClientServiceOperator;

public class ClientServiceLoadTest extends DHCPAITestCaseHelper {

    @Inject
    OperatorRegistry<ClientServiceOperator> clientOperatorRegistry;
    static ClientServiceOperator clientServiceOperator = null;

    DhcpaiStats dhcpaiStats = new DhcpaiStats(Client.class.getSimpleName());
    final int testIterations = Integer.parseInt((String) DataHandler.getAttribute(CLIENT_LOAD_ITERATIONS));
    final int statInterval = testIterations / 20;

    @Context(context = { Context.API })
    @DataDriven(name = "client_e2e")
    @Test(groups = "acceptance")
    public void shouldAddClient(@Input("ipAddress") String ipAddress, @Input("hostName") String hostName, @Input("clientIdentifier") String dhcpClientIdentifier,
            @Input("summaryFileLocation") String summaryFileLocation, @Input("smrsServer") String smrsServer, @Input("primaryNtpTimeServer") String primaryNtpTimeServer,
            @Input("secondaryNtpTimeServer") String secondaryNtpTimeServer, @Input("clientType") String clientType,
            @Input("coordinateUniversalTimeOffsetInSeconds") long coordinateUniversalTimeOffsetInSeconds) {

        clientServiceOperator = clientOperatorRegistry.provide(ClientServiceOperator.class);
        OperationStats addStats = new OperationStats(testIterations);
        int count, fourthOctetRange;

        for (int thirdOctet = 0; thirdOctet <= testIterations / 255; thirdOctet++) {
            fourthOctetRange = calculateFourthOctetRange(testIterations, thirdOctet);
            for (int fourthOctet = 0; fourthOctet < fourthOctetRange; fourthOctet++) {
                count = thirdOctet * 256 + fourthOctet;
                Client clientAdd = createClient("192.168." + thirdOctet + "." + fourthOctet, hostName, "hostNameID" + count, summaryFileLocation, smrsServer, primaryNtpTimeServer,
                        secondaryNtpTimeServer, clientType, coordinateUniversalTimeOffsetInSeconds);
                addOperation(count, clientAdd, addStats);
            }
        }
        addStats.setFinishTime();
        dhcpaiStats.setAddStats(addStats);
    }

    @Context(context = { Context.API })
    @DataDriven(name = "client_e2e")
    @Test(groups = "acceptance", dependsOnMethods = { "shouldAddClient" })
    public void shouldFindClient(@Input("ipAddress") String ipAddress, @Input("hostName") String hostName) {

        clientServiceOperator = clientOperatorRegistry.provide(ClientServiceOperator.class);
        int count, fourthOctetRange;
        OperationStats findStats = new OperationStats(testIterations);

        for (int thirdOctet = 0; thirdOctet <= testIterations / 255; thirdOctet++) {
            fourthOctetRange = calculateFourthOctetRange(testIterations, thirdOctet);
            for (int fourthOctet = 0; fourthOctet < fourthOctetRange; fourthOctet++) {
                count = thirdOctet * 256 + fourthOctet;
                Client clientFind = createClient("192.168." + thirdOctet + "." + fourthOctet, hostName, "hostNameID" + count, null, null, null, null, "LTE", 0);
                findOperation(count, clientFind, findStats);

            }
        }
        findStats.setFinishTime();
        dhcpaiStats.setFindStats(findStats);
    }

    @Context(context = { Context.API })
    @DataDriven(name = "client_e2e")
    @Test(groups = "acceptance", dependsOnMethods = { "shouldFindClient" })
    public void shouldCheckClientExists(@Input("ipAddress") String ipAddress, @Input("hostName") String hostName) {

        clientServiceOperator = clientOperatorRegistry.provide(ClientServiceOperator.class);
        OperationStats existsStats = new OperationStats(testIterations);
        int count, fourthOctetRange;

        for (int thirdOctet = 0; thirdOctet <= testIterations / 255; thirdOctet++) {
            fourthOctetRange = calculateFourthOctetRange(testIterations, thirdOctet);
            for (int fourthOctet = 0; fourthOctet < fourthOctetRange; fourthOctet++) {
                count = thirdOctet * 256 + fourthOctet;
                Client clientExist = createClient("192.168." + thirdOctet + "." + fourthOctet, hostName, "hostNameID" + count, null, null, null, null, "LTE", 0);
                existsOperation(count, clientExist, existsStats);
            }
        }
        existsStats.setFinishTime();
        dhcpaiStats.setExistStats(existsStats);
    }

    @Context(context = { Context.API })
    @DataDriven(name = "client_e2e")
    @Test(groups = "acceptance", dependsOnMethods = { "shouldCheckClientExists" })
    public void shouldDeleteClient(@Input("ipAddress") String ipAddress, @Input("hostName") String hostName) {

        clientServiceOperator = clientOperatorRegistry.provide(ClientServiceOperator.class);
        OperationStats deleteStats = new OperationStats(testIterations);
        int count, fourthOctetRange;

        for (int thirdOctet = 0; thirdOctet <= testIterations / 255; thirdOctet++) {
            fourthOctetRange = calculateFourthOctetRange(testIterations, thirdOctet);
            for (int fourthOctet = 0; fourthOctet < fourthOctetRange; fourthOctet++) {
                count = thirdOctet * 256 + fourthOctet;
                Client clientDelete = createClient("192.168." + thirdOctet + "." + fourthOctet, hostName, "hostNameID" + count, null, null, null, null, null, 0);
                deleteOperation(count, clientDelete, deleteStats);

            }
        }
        deleteStats.setFinishTime();
        dhcpaiStats.setDeleteStats(deleteStats);
        writeStats();
    }

    private void addOperation(int count, Client clientAdd, OperationStats addStats) {

        if (isAtSamplingPoint(count)) {
            clientServiceOperator.addAndCheckResponse(clientAdd);
            addStats.addOp(count);
        } else {
            clientServiceOperator.addAndCheckUUID(clientAdd);
        }
    }

    private void deleteOperation(int count, Client clientDelete, OperationStats deleteStats) {

        if (isAtSamplingPoint(count)) {
            clientServiceOperator.deleteAndCheckResponse(clientDelete);
            deleteStats.addOp(count);
        } else {
            clientServiceOperator.deleteAndCheckUUID(clientDelete);
        }
    }

    private void findOperation(int count, Client clientFind, OperationStats findStats) {

        clientServiceOperator.find(clientFind);
        if (isAtSamplingPoint(count)) {
            findStats.addOp(count);
        }
    }

    private void existsOperation(int count, Client clientExist, OperationStats existStats) {

        clientServiceOperator.exists(clientExist);
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

    private void writeStats() {
        final StatsObjectMapper objectMapper = new StatsObjectMapper();
        final String fileName = "dhcpai-taf-testcase/test-output/loadtest/data/" + "Client" + "-stats.json";
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

    private Client createClient(String ipAddress, String hostName, String dhcpClientIdentifier, String summaryFileLocation, String smrsServer, String primaryNtpTimeServer,
            String secondaryNtpTimeServer, String clientType, long coordinateUniversalTimeOffsetInSeconds) {
        Client client = new Client();
        client.setIpAddress(ipAddress);
        client.setHostName(hostName);
        client.setClientIdentifier(dhcpClientIdentifier);
        client.setSummaryFileLocation(summaryFileLocation);
        client.setSmrsServer(smrsServer);
        client.setPrimaryNTPTimeServer(primaryNtpTimeServer);
        client.setSecondaryNTPTimeServer(secondaryNtpTimeServer);
        client.setClientType(ClientType.valueOf(clientType));
        client.setCoordinateUniversalTimeOffsetInSeconds(coordinateUniversalTimeOffsetInSeconds);
        return client;
    }

}
