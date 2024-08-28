package com.ericsson.nms.security.dhcpai.test.cases.network;

import java.util.*;

import javax.inject.Inject;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.dhcpai.common.Network;
import com.ericsson.nms.security.dhcpai.common.NetworkElement;
import com.ericsson.nms.security.dhcpai.test.common.CommandRunner;
import com.ericsson.nms.security.dhcpai.test.common.DHCPAITestCaseHelper;
import com.ericsson.nms.security.dhcpai.test.operators.network.NetworkServiceOperator;

public class NetworkServiceE2ETest extends DHCPAITestCaseHelper {

	@Inject
	OperatorRegistry<NetworkServiceOperator> operatorRegistry;

	@BeforeMethod
	public void resetDHCPAIConfig() {
		new CommandRunner<Object>(null).resetDHCPConfigFiles();
	}

	@Context(context = { Context.API })
	@DataDriven(name = "network_e2e")
	@Test(groups = "acceptance")
	@TestId( id = "NetworkRange_E2E", title = "Add, check if Exists, List and Delete NetworkRange (sync)" )	
	public void shouldAddNetworkThenValidateExistThenDeleteAndValidateDoesNotExist(
			@Input("subnet") String subnet,
			@Input("netmask") String netmask,
			@Input("dnsServers") String dnsServers,
			@Input("dnsDomainName") String dnsDomainName,
			@Input("defaultRouter") String defaultRouter,
			@Output("expectedAddResult") String expectedAddResult,
			@Output("expectedAddResultDetailed") String expectedAddResultDetailed,
			@Output("expectedValidateResultAfterAdd") String expectedValidateResultAfterAdd,
			@Output("expectedDeleteResult") String expectedDeleteResult,
			@Output("expectedDeleteResultDetailed") String expectedDeleteResultDetailed,
			@Output("expectedValidateResultAfterDelete") String expectedValidateResultAfterDelete) {

		indicateStartOfCompositeTest("- Network E2E -");

		Network network = new Network(UUID.randomUUID());
		network.setSubnet(subnet);
		network.setNetmask(netmask);
		network.setDnsDomainName(dnsDomainName);
		network.setDefaultRouter(defaultRouter);
		network.setDnsServers(getDNSServerList(dnsServers));

		NetworkServiceOperator networkService = operatorRegistry
				.provide(NetworkServiceOperator.class);

		assertTest("Add and Check Response Test", networkService.addAndCheckResponse(network), expectedAddResult, network);

		assertTest("Check DHCPD config Test", checkDHCPDConfig(), "SUCCESS", network);
                assertTest("Check if network exists", networkService.exists(network), expectedValidateResultAfterAdd, network); 

		List<? extends NetworkElement> networks = networkService.list();
		Assert.assertEquals(networks.size(), 1);
		Network networkResult = (Network) networks.get(0);
		Assert.assertEquals(subnet, networkResult.getSubnet());
		Assert.assertEquals(netmask, networkResult.getNetmask());

		assertTest("Delete and Check Response Test", networkService.deleteAndCheckResponse(network), expectedDeleteResult, network);

		assertTest("Check DHCPD config Test", checkDHCPDConfig(), "SUCCESS",
				network);

		networks = networkService.list();
		Assert.assertEquals(networks.size(), 0);
		
		assertTest("Add and Check Detailed Response Test", networkService.addAndCheckResponse(network), expectedAddResultDetailed, network);

		assertTest("Delete and Check Detailed Response Test", networkService.deleteAndCheckResponse(network), expectedDeleteResultDetailed, network);

		assertEquals(expectedValidateResultAfterDelete, networkService.exists(network));

		indicateEndOfCompositeTest("- Network E2E -");
	}

	private static final String STANDARD_DELIMETER = "\\|";

	private List<String> getDNSServerList(String dnsServers) {
		if (dnsServers == null || dnsServers.isEmpty())
			return null;
		return Arrays.asList(dnsServers.split(STANDARD_DELIMETER));
	}

}
