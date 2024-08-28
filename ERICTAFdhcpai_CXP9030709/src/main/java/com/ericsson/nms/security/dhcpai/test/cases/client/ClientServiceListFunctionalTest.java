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
package com.ericsson.nms.security.dhcpai.test.cases.client;

import static com.ericsson.nms.security.dhcpai.test.common.TestConstants.*;

import java.util.List;

import javax.inject.Inject;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.dhcpai.common.*;
import com.ericsson.nms.security.dhcpai.test.common.CommandRunner;
import com.ericsson.nms.security.dhcpai.test.common.DHCPAITestCaseHelper;
import com.ericsson.nms.security.dhcpai.test.operators.client.ClientServiceOperator;

public class ClientServiceListFunctionalTest extends DHCPAITestCaseHelper {

	@Inject
	OperatorRegistry<ClientServiceOperator> clientOperatorRegistry;

	@BeforeTest
	public void resetDHCPAIConfig() {
		new CommandRunner<Object>(null).resetDHCPConfigFiles();
	}

	@Context(context = { Context.API })
	@DataDriven(name = "client_list")
	@Test
	@TestId( id = "Client_Functional_List (TORF-10141)", title = "Add Client and List Client(s) (sync)" )
	public void shouldListClientSuccessfully(
			@Input("ipAddress") String ipAddress,
			@Input("hostName") String hostName,
			@Input("clientIdentifier") String dhcpClientIdentifier,
			@Input("summaryFileLocation") String summaryFileLocation,
			@Input("smrsServer") String smrsServer,
			@Input("primaryNtpTimeServer") String primaryNtpTimeServer,
			@Input("secondaryNtpTimeServer") String secondaryNtpTimeServer,
			@Input("clientType") String clientType,
			@Input("coordinateUniversalTimeOffsetInSeconds") long coordinateUniversalTimeOffsetInSeconds,
			@Output("expectedAddResult") String expectedAddResult,

			@Output("expectedListCount") int expectedListCount,
			@Output("expectedListResult") String expectedListResult) {

		indicateStartOfCompositeTest("- Client List -");

		ClientServiceOperator clientServiceOperator = clientOperatorRegistry
				.provide(ClientServiceOperator.class);

		Client client = new Client();
		client.setIpAddress(ipAddress);
		client.setHostName(hostName);
		client.setClientIdentifier(dhcpClientIdentifier);
		client.setSummaryFileLocation(summaryFileLocation);
		client.setSmrsServer(smrsServer);
		client.setPrimaryNTPTimeServer(primaryNtpTimeServer);
		client.setSecondaryNTPTimeServer(secondaryNtpTimeServer);
		if (clientType != null && clientType.trim() != "") {
			client.setClientType(ClientType.valueOf(clientType));
		} else {
			client.setClientType(null);
		}
		client.setCoordinateUniversalTimeOffsetInSeconds(coordinateUniversalTimeOffsetInSeconds);

		assertTest("- Client - Add Client and Check Response",
				clientServiceOperator.addAndCheckResponse(client),
				expectedAddResult, client);

		List<? extends NetworkElement> clients = clientServiceOperator.list();

		assertTest("- Client - Check number of elements in the list Test",
				Integer.toString(clients.size()),
				Integer.toString(expectedListCount), client);

		Client lastClient = (Client) clients.get(expectedListCount - 1);
		String actualResult = (clientsEqual(client, lastClient) ? OPERATION_SUCCESS
				: OPERATION_FAILED);
		assertTest(
				"- Client - Check if the last element added to list is the same as just added to the DHCP configuration Test",
				actualResult + ": " + lastClient.toString(),
				expectedListResult, client);

		indicateEndOfCompositeTest("- Client List -");
	}

	private boolean clientsEqual(final Client c1, final Client c2) {
		if (c1 == c2) {
			return true;
		}
		if (c2 == null) {
			return false;
		}
		if (c1.getClientIdentifier() == null) {
			if (c2.getClientIdentifier() != null) {
				return false;
			}
		} else if (!c1.getClientIdentifier().equals(c2.getClientIdentifier())) {
			return false;
		}
		if (c1.getCoordinateUniversalTimeOffsetInSeconds() == null) {
			if (c2.getCoordinateUniversalTimeOffsetInSeconds() != null) {
				return false;
			}
		} else if (!c1.getCoordinateUniversalTimeOffsetInSeconds().equals(
				c2.getCoordinateUniversalTimeOffsetInSeconds())) {
			return false;
		}
		if (c1.getIpAddress() == null) {
			if (c2.getIpAddress() != null) {
				return false;
			}
		} else if (!c1.getIpAddress().equals(c2.getIpAddress())) {
			return false;
		}
		if (c1.getPrimaryNTPTimeServer() == null) {
			if (c2.getPrimaryNTPTimeServer() != null) {
				return false;
			}
		} else if (!c1.getPrimaryNTPTimeServer().equals(
				c2.getPrimaryNTPTimeServer())) {
			return false;
		}
		if (c1.getSecondaryNTPTimeServer() == null) {
			if (c2.getSecondaryNTPTimeServer() != null) {
				return false;
			}
		} else if (!c1.getSecondaryNTPTimeServer().equals(
				c2.getSecondaryNTPTimeServer())) {
			return false;
		}
		if (c1.getSmrsServer() == null) {
			if (c2.getSmrsServer() != null) {
				return false;
			}
		} else if (!c1.getSmrsServer().equals(c2.getSmrsServer())) {
			return false;
		}
		if (c1.getSummaryFileLocation() == null) {
			if (c2.getSummaryFileLocation() != null) {
				return false;
			}
		} else if (!c1.getSummaryFileLocation().equals(
				c2.getSummaryFileLocation())) {
			return false;
		}
		return true;
	}
}
