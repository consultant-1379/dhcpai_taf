package com.ericsson.nms.security.dhcpai.test.cases.client;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.dhcpai.common.Client;
import com.ericsson.nms.security.dhcpai.common.ClientType;
import com.ericsson.nms.security.dhcpai.test.common.CommandRunner;
import com.ericsson.nms.security.dhcpai.test.common.DHCPAITestCaseHelper;
import com.ericsson.nms.security.dhcpai.test.operators.client.ClientServiceOperator;

public class ClientServiceFunctionalTest extends DHCPAITestCaseHelper {

	@Inject
	OperatorRegistry<ClientServiceOperator> clientOperatorRegistry;

	@BeforeMethod
	public void resetDHCPAIConfig() {
		new CommandRunner<Object>(null).resetDHCPConfigFiles();
	}
	
	@Context(context = { Context.API })
	@DataDriven(name = "client_add")
	@Test
	@TestId( id = "Client_Functional_Add (TORF-7892)", title = "Add Client and check UUID (async)" )
	public void shouldAddClientAndCheckUUID(
			@Input("ipAddress") String ipAddress,
			@Input("hostName") String hostName,
			@Input("clientIdentifier") String dhcpClientIdentifier,
			@Input("summaryFileLocation") String summaryFileLocation,
			@Input("smrsServer") String smrsServer,
			@Input("primaryNtpTimeServer") String primaryNtpTimeServer,
			@Input("secondaryNtpTimeServer") String secondaryNtpTimeServer,
			@Input("clientType") String clientType,
			@Input("coordinateUniversalTimeOffsetInSeconds") long coordinateUniversalTimeOffsetInSeconds,
			@Output("expected") String expected) {

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

		assertTest("- Client - Add Client and Check UUID",
				clientServiceOperator.addAndCheckUUID(client), expected, client);
	}

	@Context(context = { Context.API })
	@DataDriven(name = "client_exists")
	@Test
	@TestId( id = "Client_Functional_Exists (TORF-10166)", title = "Add Client and check if it Exists (sync)" )
	public void shouldCheckClientExists(
			@Input("hostName") String hostName,
			@Input("summaryFileLocation") String summaryFileLocation,
			@Input("smrsServer") String smrsServer,
			@Input("primaryNtpTimeServer") String primaryNtpTimeServer,
			@Input("secondaryNtpTimeServer") String secondaryNtpTimeServer,
			@Input("clientType") String clientType,
			@Input("coordinateUniversalTimeOffsetInSeconds") long coordinateUniversalTimeOffsetInSeconds,
			@Input("ipAddress_add") String ipAddress_add,
			@Input("clientIdentifier_add") String dhcpClientIdentifier_add,
			@Input("ipAddress_exists") String ipAddress_exists,
			@Input("clientIdentifier_exists") String dhcpClientIdentifier_exists,
			@Output("expected_add") String expected_add,
			@Output("expected_exists") String expected_exists) {

		indicateStartOfCompositeTest("- Client Exists -");

		ClientServiceOperator clientServiceOperator = clientOperatorRegistry
				.provide(ClientServiceOperator.class);

		Client client = new Client();
		client.setHostName(hostName);
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

		client.setIpAddress(ipAddress_add);
		client.setClientIdentifier(dhcpClientIdentifier_add);
		assertTest("- Client - Add client before checking if it exists",
				clientServiceOperator.addAndCheckResponse(client),
				expected_add, client);

		client.setIpAddress(ipAddress_exists);
		client.setClientIdentifier(dhcpClientIdentifier_exists);
		assertTest("- Client - Check client exists",
				clientServiceOperator.exists(client), expected_exists, client);

		indicateEndOfCompositeTest("- Client Exists -");
	}

	@Context(context = { Context.API })
	@DataDriven(name = "client_find")
	@Test
	@TestId( id = "Client_Functional_Exists (TORF-11174)", title = "Add Client and then attempt to Find it (sync)" )
	public void shouldFindClient(
			@Input("hostName") String hostName,
			@Input("summaryFileLocation") String summaryFileLocation,
			@Input("smrsServer") String smrsServer,
			@Input("primaryNtpTimeServer") String primaryNtpTimeServer,
			@Input("secondaryNtpTimeServer") String secondaryNtpTimeServer,
			@Input("clientType") String clientType,
			@Input("coordinateUniversalTimeOffsetInSeconds") long coordinateUniversalTimeOffsetInSeconds,
			@Input("ipAddress_add") String ipAddress_add,
			@Input("clientIdentifier_add") String dhcpClientIdentifier_add,
			@Input("ipAddress_find") String ipAddress_find,
			@Input("clientIdentifier_find") String dhcpClientIdentifier_find,
			@Output("expected_add") String expected_add,
			@Output("expected_found") String expected_found)
			throws InterruptedException {

		indicateStartOfCompositeTest("- Client Find -");

		ClientServiceOperator clientServiceOperator = clientOperatorRegistry
				.provide(ClientServiceOperator.class);

		Client client = new Client();
		client.setHostName(hostName);
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

		client.setIpAddress(ipAddress_add);
		client.setClientIdentifier(dhcpClientIdentifier_add);
		assertTest("- Client - Add client bofere calling find",
				clientServiceOperator.addAndCheckResponse(client),
				expected_add, client);

		client.setIpAddress(ipAddress_find);
		client.setClientIdentifier(dhcpClientIdentifier_find);
		assertTest("- Client - Find client ",
				clientServiceOperator.find(client), expected_found, client);

		indicateEndOfCompositeTest("- Client Find -");
	}

	@Context(context = { Context.API })
	@DataDriven(name = "client_delete")
	@Test
	@TestId( id = "Client_Functional_Delete (TORF-7892)", title = "Delete Client and check UUID (async)" )
	public void shouldDeleteClientAndCheckUUID(
			@Input("clientIdentifier") String dhcpClientIdentifier,
			@Input("clientType") String clientType,
			@Output("expected") String expected) {

		ClientServiceOperator clientServiceOperator = clientOperatorRegistry
				.provide(ClientServiceOperator.class);

		Client client = new Client();
		if (clientType != null && clientType.trim() != "") {
			client.setClientType(ClientType.valueOf(clientType));
		} else {
			client.setClientType(null);
		}
		client.setClientIdentifier(dhcpClientIdentifier);

		assertTest("- Client - Delete client and Check UUID",
				clientServiceOperator.deleteAndCheckUUID(client), expected,
				client);
	}

}
