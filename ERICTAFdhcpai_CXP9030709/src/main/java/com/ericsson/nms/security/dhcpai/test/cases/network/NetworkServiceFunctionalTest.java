package com.ericsson.nms.security.dhcpai.test.cases.network;

import java.util.*;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.dhcpai.common.Network;
import com.ericsson.nms.security.dhcpai.test.common.CommandRunner;
import com.ericsson.nms.security.dhcpai.test.common.DHCPAITestCaseHelper;
import com.ericsson.nms.security.dhcpai.test.operators.network.NetworkServiceOperator;

public class NetworkServiceFunctionalTest extends DHCPAITestCaseHelper {

	@Inject
	OperatorRegistry<NetworkServiceOperator> operatorRegistry;

	@BeforeMethod
	public void resetDHCPAIConfig() {
		new CommandRunner<Object>(null).resetDHCPConfigFiles();
	}

	@Context(context = { Context.API })
	@DataDriven(name = "network_add")
	@Test
	@TestId( id = "Network_Functional_Add (TORF-8370)", title = "Add Network and check UUID (async)" )	
	public void shouldAddNetworkAndCheckUUID(@Input("uuid") String uuid,
			@Input("subnet") String subnet, @Input("netmask") String netmask,
			@Input("dnsServers") String dnsServers,
			@Input("dnsDomainName") String dnsDomainName,
			@Input("defaultRouter") String defaultRouter,
			@Output("expectedResultType") String expectedResultType,
			@Output("expectedResultMessage") String expectedResultMessage) {

		NetworkServiceOperator networkService = operatorRegistry
				.provide(NetworkServiceOperator.class);

		Network network = new Network(UUID.fromString(uuid));
		network.setSubnet(subnet);
		network.setNetmask(netmask);
		network.setDnsDomainName(dnsDomainName);
		network.setDefaultRouter(defaultRouter);
		network.setDnsServers(getDNSServerList(dnsServers));

		assertTest("- Network - Add and Check UUID Test (Type check)",
				networkService.addAndCheckUUID(network), expectedResultType,
				network);
		assertTest("- Network - Add and Check UUID Test (Message check)",
				networkService.addAndCheckUUID(network), expectedResultMessage,
				network);
	}

	@Context(context = { Context.API })
	@DataDriven(name = "network_exists")
	@Test	
	@TestId( id = "Network_Functional_Exists (TORF-10154)", title = "Add Network and check if it Exists (sync)" )	
	public void shouldCheckNetworkExist(@Input("subnet") String subnet,
			@Input("netmask") String netmask,
			@Input("dnsServers") String dnsServers,
			@Input("dnsDomainName") String dnsDomainName,
			@Input("defaultRouter") String defaultRouter,
			@Output("expected") String expected) {

		indicateStartOfCompositeTest("- Network Exists -");

		NetworkServiceOperator networkService = operatorRegistry
				.provide(NetworkServiceOperator.class);

		Network network = new Network();
		network.setSubnet(subnet);
		network.setNetmask(netmask);
		network.setDnsDomainName(dnsDomainName);
		network.setDefaultRouter(defaultRouter);
		network.setDnsServers(getDNSServerList(dnsServers));

		assertTest("- Network - Add and Check Response before exists Test",
				networkService.addAndCheckResponse(network), "SUCCESS", network);

		assertTest("- Network - Check if network exists",
				networkService.exists(network), expected, network);

		indicateEndOfCompositeTest("- Network Exists -");
	}

	@Context(context = { Context.API })
	@DataDriven(name = "network_find")
	@Test
	@TestId( id = "Network_Functional_Find (TORF-11173)", title = "Add Network and attempt to Find it (sync)" )	
	public void shouldFindNetwork(@Input("subnet") String subnet,
			@Input("netmask") String netmask,
			@Input("dnsServers") String dnsServers,
			@Input("dnsDomainName") String dnsDomainName,
			@Input("defaultRouter") String defaultRouter,
			@Input("searchBy") String searchBy,
			@Output("expected") String expected) {

		indicateStartOfCompositeTest("- Network Find -");

		Network network = new Network();
		network.setSubnet(subnet);
		network.setNetmask(netmask);
		network.setDnsDomainName(dnsDomainName);
		network.setDefaultRouter(defaultRouter);
		network.setDnsServers(getDNSServerList(dnsServers));

		NetworkServiceOperator networkService = operatorRegistry
				.provide(NetworkServiceOperator.class);

		assertTest("- Network - Add and Check Response before find Test",
				networkService.addAndCheckResponse(network), "SUCCESS", network);
		network.setSubnet(searchBy);
		assertTest("- Network - Find Response Test",
				networkService.find(network), expected, network);

		indicateEndOfCompositeTest("- Network Find -");
	}

	@Context(context = { Context.API })
	@DataDriven(name = "network_delete")
	@Test
	@TestId( id = "Network_Functional_Find (TORF-11173)", title = "Delete Network and check UUID (async)" )	
	public void shouldDeleteNetworkAndCheckUUID(@Input("uuid") String uuid,
			@Input("subnet") String subnet, @Input("netmask") String netmask,
			@Output("expectedResultType") String expectedResultType,
			@Output("expectedResultMessage") String expectedResultMessage) {

		NetworkServiceOperator networkService = operatorRegistry
				.provide(NetworkServiceOperator.class);

		Network network = new Network(UUID.fromString(uuid));
		network.setSubnet(subnet);
		network.setNetmask(netmask);

		assertTest("- Network - Delete and Check UUID Test (Type check)",
				networkService.deleteAndCheckUUID(network), expectedResultType,
				network);
		assertTest("- Network - Delete and Check UUID Test (Message check)",
				networkService.deleteAndCheckUUID(network),
				expectedResultMessage, network);
	}

	private static final String STANDARD_DELIMETER = "\\|";

	private List<String> getDNSServerList(String dnsServers) {
		if (dnsServers == null || dnsServers.isEmpty())
			return null;
		return Arrays.asList(dnsServers.split(STANDARD_DELIMETER));
	}

}
