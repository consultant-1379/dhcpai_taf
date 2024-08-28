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

public class ClientServiceE2ETest extends DHCPAITestCaseHelper {

	@Inject
	OperatorRegistry<ClientServiceOperator> clientOperatorRegistry;

	@BeforeMethod
	public void resetDHCPAIConfig() {
		new CommandRunner<Object>(null).resetDHCPConfigFiles();
	}
		
	@Context( context = { Context.API } )
	@DataDriven( name = "client_e2e" )
	@Test( groups = "acceptance" )
	@TestId( id = "Client_E2E", title = "Add, check if Exists, Delete Client and check if it Does Not Exist after deleting (sync)" )
	public void shouldAddClientThenValidateExistThenDeleteAndValidateDoesNotExist(			
			@Input( "ipAddress" ) String ipAddress,
			@Input( "hostName" ) String hostName,
			@Input( "clientIdentifier" ) String dhcpClientIdentifier,
			@Input( "summaryFileLocation" ) String summaryFileLocation,			
			@Input( "smrsServer" ) String smrsServer,
			@Input( "primaryNtpTimeServer" ) String primaryNtpTimeServer,
			@Input( "secondaryNtpTimeServer" ) String secondaryNtpTimeServer,
			@Input( "clientType" ) String clientType,
			@Input( "coordinateUniversalTimeOffsetInSeconds" ) long coordinateUniversalTimeOffsetInSeconds,			
			@Output( "expectedAddResult" ) String expectedAddResult,
			@Output( "expectedAddResultDetailed" ) String expectedAddResultDetailed,
			@Output( "expectedExistsResultAfterAdd" ) String expectedExistsResultAfterAdd,
			@Output( "expectedDeleteResult" ) String expectedDeleteResult,
			@Output( "expectedExistsResultAfterDelete" ) String expectedExistsResultAfterDelete ) {
	
		indicateStartOfCompositeTest( "- Client E2E -" );
		
		ClientServiceOperator clientServiceOperator = clientOperatorRegistry.provide( ClientServiceOperator.class );
		
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

		assertTest("- Client - Add and Check Response Test",
				clientServiceOperator.addAndCheckResponse(client),
				expectedAddResult, client);
		assertTest("Check DHCPD config Test", checkDHCPDConfig(), "SUCCESS",
				client);
		assertTest("- Client - Check after add Response Test",
				clientServiceOperator.exists(client),
				expectedExistsResultAfterAdd, client);
		assertTest("- Client - Delete and Check Response Test",
				clientServiceOperator.deleteAndCheckResponse(client),
				expectedDeleteResult, client);
		assertTest("Check DHCPD config Test", checkDHCPDConfig(), "SUCCESS",
				client);

		assertTest("- Client - Check after delete Response Test",
				clientServiceOperator.exists(client),
				expectedExistsResultAfterDelete, client);

		indicateEndOfCompositeTest("- Client E2E -");
	}

}
