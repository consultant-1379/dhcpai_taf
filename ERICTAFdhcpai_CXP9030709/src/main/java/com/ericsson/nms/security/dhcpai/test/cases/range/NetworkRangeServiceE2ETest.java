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

public class NetworkRangeServiceE2ETest extends DHCPAITestCaseHelper {

	@Inject
	OperatorRegistry<NetworkRangeServiceOperator> rangeOperatorRegistry;
	@Inject
	OperatorRegistry<NetworkServiceOperator> networkOperatorRegistry;

	@BeforeMethod
	public void resetDHCPAIConfig() {
		new CommandRunner<Object>(null).resetDHCPConfigFiles();
	}

	@Context( context = { Context.API } )
	@DataDriven( name = "networkRange_e2e" )
	@Test( groups = "acceptance" )
	@TestId( id = "Network_E2E", title = "Add, check if Exists, List and Delete Network (sync)" )	
	public void shouldAddNetworkRangeThenValidateExistThenDeleteAndValidateDoesNotExist(
			@Input("networkSubnet") String networkSubnet,
			@Input("networkNetmask") String networkNetmask,
			@Input("networkDnsServers") String networkDnsServers,
			@Input("networkDnsDomainName") String networkDnsDomainName,
			@Input("networkDefaultRouter") String networkDefaultRouter,
			@Output("networkExpectedAddResult") String networkExpectedAddResult,

			@Input("netmask") String netmask,
			@Input("defaultRouter") String defaultRouter,
			@Input("startIp") String startIp,
			@Input("endIp") String endIp,
			@Input("leaseTime") long leaseTime,
			@Input("smrsServer") String smrsServer,
			@Input("primaryNtpTimeServer") String primaryNtpTimeServer,
			@Input("secondaryNtpTimeServer") String secondaryNtpTimeServer,
			@Input("primaryWebServer") String primaryWebServer,
			@Input("secondaryWebServer") String secondaryWebServer,
			@Output("expectedAddResult") String expectedAddResult,
			@Output("expectedExistsResultAfterAdd") String expectedExistsResultAfterAdd,
			@Output("expectedDeleteResult") String expectedDeleteResult,
			@Output("expectedExistsResultAfterDelete") String expectedExistsResultAfterDelete) {

		indicateStartOfCompositeTest("- NetworkRange E2E -");

		NetworkServiceOperator networkServiceOperator = networkOperatorRegistry
				.provide(NetworkServiceOperator.class);

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

		assertTest("- NetworkRange - Add and Check Response Test",
				rangeServiceOperator.addAndCheckResponse(networkRange),
				expectedAddResult, networkRange);
		assertTest("Check DHCPD config Test", checkDHCPDConfig(), "SUCCESS",
				network);

		assertTest("- NetworkRange - Check after Add Response Test",
				rangeServiceOperator.exists(networkRange),
				expectedExistsResultAfterAdd, networkRange);
		assertTest("- NetworkRange - Delete and Check Response Test",
				rangeServiceOperator.deleteAndCheckResponse(networkRange),
				expectedDeleteResult, networkRange);
		assertTest("Check DHCPD config Test", checkDHCPDConfig(), "SUCCESS",
				network);

		assertTest("- NetworkRange - Check after Delete Response Test",
				rangeServiceOperator.exists(networkRange),
				expectedExistsResultAfterDelete, networkRange);

		indicateEndOfCompositeTest("- NetworkRange E2E -");
	} 

	private static final String STANDARD_DELIMETER = "\\|";

	private List<String> getDNSServerList(String dnsServers) {
		if (dnsServers == null || dnsServers.isEmpty())
			return null;
		return Arrays.asList(dnsServers.split(STANDARD_DELIMETER));
	}

}
