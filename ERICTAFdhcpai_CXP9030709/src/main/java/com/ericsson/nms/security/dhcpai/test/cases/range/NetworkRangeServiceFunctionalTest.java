package com.ericsson.nms.security.dhcpai.test.cases.range;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.dhcpai.common.Network;
import com.ericsson.nms.security.dhcpai.common.NetworkRange;
import com.ericsson.nms.security.dhcpai.test.common.CommandRunner;
import com.ericsson.nms.security.dhcpai.test.common.DHCPAITestCaseHelper;
import com.ericsson.nms.security.dhcpai.test.operators.network.NetworkServiceOperator;
import com.ericsson.nms.security.dhcpai.test.operators.range.NetworkRangeServiceOperator;

public class NetworkRangeServiceFunctionalTest extends DHCPAITestCaseHelper {

	@Inject
	OperatorRegistry<NetworkRangeServiceOperator> rangeOperatorRegistry;
	
	@Inject
	OperatorRegistry<NetworkServiceOperator> networkOperatorRegistry;

	@BeforeMethod
	public void resetDHCPAIConfig() {
		new CommandRunner<Object>(null).resetDHCPConfigFiles();
	}

	@Context(context = { Context.API })
	@DataDriven(name = "networkRange_add")
	@Test
	@TestId( id = "NetworkRange_Functional_Add (TORF-7894)", title = "Add NetworkRange and check UUID (async)" )	
	public void shouldAddNetworkRangeAndCheckUUID(
			@Input("netmask") String netmask,
			@Input("defaultRouter") String defaultRouter,
			@Input("startIp") String startIp, @Input("endIp") String endIp,
			@Input("leaseTime") long leaseTime,
			@Input("smrsServer") String smrsServer,
			@Input("primaryNtpTimeServer") String primaryNtpTimeServer,
			@Input("secondaryNtpTimeServer") String secondaryNtpTimeServer,
			@Input("primaryWebServer") String primaryWebServer,
			@Input("secondaryWebServer") String secondaryWebServer,
			@Output("expected") String expected) {

		NetworkRange networkRange = new NetworkRange();
		networkRange.setNetmask(netmask);
		networkRange.setDefaultRouter(defaultRouter);
		networkRange.setStartAddress(startIp);
		networkRange.setEndAddress(endIp);
		networkRange.setLeaseTime(leaseTime);
		networkRange.setSmrsSlaveService(smrsServer);
		networkRange.setPrimaryNTPTimeServer(primaryNtpTimeServer);
		networkRange.setSecondaryNTPTimeServer(secondaryNtpTimeServer);
		networkRange.setPrimaryWebServer(primaryWebServer);
		networkRange.setSecondaryWebServer(secondaryWebServer);

		NetworkRangeServiceOperator rangeServiceOperator = rangeOperatorRegistry
				.provide(NetworkRangeServiceOperator.class);

		assertTest("- NetworkRange - Add networkRange and Check UUID",
				rangeServiceOperator.addAndCheckUUID(networkRange), expected,
				networkRange);
	}

	@Context(context = { Context.API })
	@DataDriven(name = "networkRange_exists")
	@Test
	@TestId( id = "NetworkRange_Functional_Exists (TORF-10160)", title = "Add Network, add NetworkRange to the just added Network and check if it Exists (sync)" )
	public void shouldCheckNetworkRangeExists( 
			@Input( "networkSubnet" ) String networkSubnet,
			@Input( "networkNetmask" ) String networkNetmask,
			@Input( "networkDnsServers" ) String networkDnsServers,
			@Input( "networkDnsDomainName" ) String networkDnsDomainName,
			@Input( "networkDefaultRouter" ) String networkDefaultRouter, 
			@Output( "networkExpectedAddResult" ) String networkExpectedAddResult,
			
			@Input( "addNetmask" ) String addNetmask,
			@Input( "addDefaultRouter" ) String addDefaultRouter,
			@Input( "addStartIp" ) String addStartIp,
			@Input( "addEndIp" ) String addEndIp,
			@Input( "addLeaseTime" ) long addLeaseTime,
			@Input( "addSmrsServer" ) String addSmrsServer,
			@Input( "addPrimaryNtpTimeServer" ) String addPrimaryNtpTimeServer,
			@Input( "addSecondaryNtpTimeServer" ) String addSecondaryNtpTimeServer,
			@Input( "addPrimaryWebServer" ) String addPrimaryWebServer,
			@Input( "addSecondaryWebServer" ) String addSecondaryWebServer,
			@Output( "expectedAddResult" ) String expectedAddResult,
			
			@Input( "StartIp" ) String startIp,
			@Input( "EndIp" ) String endIp,						
			@Output( "expectedExistsResult" ) String expectedExistsResult ) {
		
		indicateStartOfCompositeTest( "- NetworkRange Exists -" );
		
		NetworkServiceOperator networkServiceOperator = networkOperatorRegistry.provide( NetworkServiceOperator.class );

		Network network = new Network();
		network.setSubnet(networkSubnet);
		network.setNetmask(networkNetmask);
		network.setDnsDomainName(networkDnsDomainName);
		network.setDefaultRouter(networkDefaultRouter);
		network.setDnsServers(getDNSServerList(networkDnsServers));

		assertTest("- NetworkRange - Add base network Test",
				networkServiceOperator.addAndCheckResponse(network),
				networkExpectedAddResult, network);

		NetworkRangeServiceOperator rangeServiceOperator = rangeOperatorRegistry
				.provide(NetworkRangeServiceOperator.class);

		NetworkRange networkRange = new NetworkRange();
		networkRange.setNetmask( addNetmask );
		networkRange.setDefaultRouter( addDefaultRouter );		
		networkRange.setLeaseTime( addLeaseTime );
		networkRange.setSmrsSlaveService( addSmrsServer );
		networkRange.setPrimaryNTPTimeServer( addPrimaryNtpTimeServer );
		networkRange.setSecondaryNTPTimeServer( addSecondaryNtpTimeServer );
		networkRange.setPrimaryWebServer( addPrimaryWebServer );
		networkRange.setSecondaryWebServer( addSecondaryWebServer );
		
		networkRange.setStartAddress( addStartIp );
		networkRange.setEndAddress( addEndIp );
		assertTest( "- NetworkRange - Add networkRange before checking if it exists", rangeServiceOperator.addAndCheckResponse( networkRange ), expectedAddResult, networkRange );
		
		networkRange.setStartAddress( startIp );
		networkRange.setEndAddress( endIp );
		assertTest( "- NetworkRange - Check networkRange exists", rangeServiceOperator.exists( networkRange ), expectedExistsResult, networkRange );
		
		indicateEndOfCompositeTest( "- NetworkRange Exists -" );
	}

	@Context(context = { Context.API })
	@DataDriven(name = "networkRange_delete")
	@Test
	@TestId( id = "NetworkRange_Functional_Delete (TORF-7894)", title = "Delete NetworkRange and check UUID (async)" )
	public void shouldDeleteNetworkRangeAndCheckUUID(
			@Input("netmask") String netmask, @Input("startIp") String startIp,
			@Output("expected") String expected) {

		NetworkRangeServiceOperator rangeServiceOperator = rangeOperatorRegistry
				.provide(NetworkRangeServiceOperator.class);

		NetworkRange networkRange = new NetworkRange();

		networkRange.setNetmask(netmask);
		networkRange.setStartAddress(startIp);

		assertTest("- NetworkRange - Delete networkRange and Check UUID",
				rangeServiceOperator.deleteAndCheckUUID(networkRange),
				expected, networkRange);
	}

	private static final String STANDARD_DELIMETER = "\\|";

	private List<String> getDNSServerList(String dnsServers) {
		if (dnsServers == null || dnsServers.isEmpty())
			return null;
		return Arrays.asList(dnsServers.split(STANDARD_DELIMETER));
	}

}
