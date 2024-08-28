package com.ericsson.nms.security.dhcpai.test.cases.network;

import static com.ericsson.nms.security.dhcpai.test.common.TestConstants.*;

import java.util.*;

import javax.inject.Inject;

import org.testng.annotations.*;

import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.dhcpai.common.*;
import com.ericsson.nms.security.dhcpai.test.common.CommandRunner;
import com.ericsson.nms.security.dhcpai.test.common.DHCPAITestCaseHelper;
import com.ericsson.nms.security.dhcpai.test.operators.network.NetworkServiceOperator;
import com.ericsson.nms.security.dhcpai.test.operators.range.NetworkRangeServiceOperator;

public class NetworkServiceListFunctionalTest extends DHCPAITestCaseHelper {

	private static final String STANDARD_DELIMETER = "\\|";

	@BeforeTest
	public void resetDHCPAIConfig() {
		new CommandRunner<Object>(null).resetDHCPConfigFiles();
	}

	@Inject
	OperatorRegistry<NetworkServiceOperator> operatorRegistry;

	@Inject
	OperatorRegistry<NetworkRangeServiceOperator> rangeOperatorRegistry;
	
	@Context(context = { Context.API })
	@DataDriven(name = "network_list")
	@Test()
	@TestId( id = "Network_Functional_List (TORF-10130)", title = "Add Network, add Network Range to the just added Network and List the Network(s) (sync)" )
	public void shouldListNetworkSuccessfully(
			@Input("netUuid") String uuid,@Input("netSubnet") String subnet, @Input("netNetmask") String netmask,
			@Input("netDnsServers") String dnsServers,@Input("netDnsDomainName") String dnsDomainName,
			@Input("netDefaultRouter") String defaultRouter,@Output("netExpectedAddResult") String netExpectedAddResult,			
			@Output("netExpectedListCount") int expectedListCount,@Output("netExpectedListResult") String netExpectedListResult,
			
			@Input( "addNetmask" ) String addNetmask,@Input( "addDefaultRouter" ) String addDefaultRouter,@Input( "addStartIp" ) String addStartIp,
			@Input( "addEndIp" ) String addEndIp, @Input( "addLeaseTime" ) long addLeaseTime,@Input( "addSmrsServer" ) String addSmrsServer,
			@Input( "addPrimaryNtpTimeServer" ) String addPrimaryNtpTimeServer,	@Input( "addSecondaryNtpTimeServer" ) String addSecondaryNtpTimeServer,
			@Input( "addPrimaryWebServer" ) String addPrimaryWebServer,	@Input( "addSecondaryWebServer" ) String addSecondaryWebServer,
			@Output( "expectedAddResult" ) String expectedAddResult,@Input( "StartIp" ) String startIp,	@Input( "EndIp" ) String endIp,	@Output( "expectedExistsResult" ) String expectedExistsResult
			) {
		
		indicateStartOfCompositeTest("- Network List -");

		final NetworkServiceOperator networkService = operatorRegistry.provide(NetworkServiceOperator.class);
		final NetworkRangeServiceOperator rangeServiceOperator = rangeOperatorRegistry.provide(NetworkRangeServiceOperator.class);
		
		// add the network first
		final Network network = new Network(UUID.fromString(uuid));
		network.setSubnet(subnet);
		network.setNetmask(netmask);
		network.setDnsDomainName(dnsDomainName);
		network.setDefaultRouter(defaultRouter);
		network.setDnsServers(getDNSServerList(dnsServers));

		assertTest("- Network - Add Network and Check Response", networkService.addAndCheckResponse(network), netExpectedAddResult, network);

		// now add the range
		final NetworkRange networkRange = new NetworkRange();
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
		network.setRange(networkRange);
		
		assertTest( "- NetworkRange - Add networkRange before checking if it exists", rangeServiceOperator.addAndCheckResponse( networkRange ), expectedAddResult, networkRange );
		
		// retrieve the list of networks
		final List<? extends NetworkElement> networks = networkService.list();
		
		assertTest("- Network - Check number of elements in the list Test", Integer.toString(networks.size()), Integer.toString(expectedListCount), network);
				
		final Network lastNetwork = (Network) networks.get(expectedListCount - 1);
		final String actualResult = (networksEqual(network, lastNetwork) ? OPERATION_SUCCESS : OPERATION_FAILED);
				
		assertTest(	"- Network - Check if the last element added to list is the same as just added to the DHCP configuration Test",	actualResult + ": " + lastNetwork.toString(), netExpectedListResult, network);
				
		final NetworkRange actualRange = lastNetwork.getRange();
		final String actualRangeResult = (actualRange == null)?"RANGE NOT RETREIVED":actualRange.getStartAddress();
		final String expectedRangeResult = addStartIp;
		
		
		assertTest(	"- NetworkRange - Check if the network range is the same as one added to the DHCP configuration Test",	actualRangeResult, expectedRangeResult, actualRange);
				
		indicateEndOfCompositeTest("- Network List -");
	}

	/**
	 * @param network
	 * @param lastNetwork
	 * @return
	 */
	private boolean networksEqual(Network network, Network lastNetwork) {
		if (lastNetwork == null) {
			return false;
		}
		if (network.getDefaultRouter() == null) {
			if (lastNetwork.getDefaultRouter() != null) {
				return false;
			}
		} else if (!network.getDefaultRouter().equals(lastNetwork.getDefaultRouter())) {
			return false;
		}
		if (network.getDnsDomainName() == null) {
			if (lastNetwork.getDnsDomainName() != null) {
				return false;
			}
		} else if (!network.getDnsDomainName().equals(lastNetwork.getDnsDomainName())) {
			return false;
		}
		
		if (network.getDnsServers() == null) {
			if (lastNetwork.getDnsServers() != null) {
				return false;
			}
		}
		else if (!network.getDnsServers().equals(lastNetwork.getDnsServers())) {
			return false;
		}
		
		if (network.getNetmask() == null) {
			if (lastNetwork.getNetmask() != null) {
				return false;
			}
		} else if (!network.getNetmask().equals(lastNetwork.getNetmask())) {
			return false;
		}
		if (network.getRange() == null) {
			if (lastNetwork.getRange() != null) {
				return false;
			}
		} else if (!network.getRange().getStartAddress().equals(lastNetwork.getRange().getStartAddress())) {
			return false;
		}
		if (network.getSubnet() == null) {
			if (lastNetwork.getSubnet() != null) {
				return false;
			}
		} else if (!network.getSubnet().equals(lastNetwork.getSubnet())) {
			return false;
		}
		return true;
	}

	private List<String> getDNSServerList(String dnsServers) {
		if (dnsServers == null || dnsServers.isEmpty())
			return null;
		return Arrays.asList(dnsServers.split(STANDARD_DELIMETER));
	}

}
