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
package com.ericsson.nms.security.dhcpai.test.cases.ip;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.dhcpai.common.IpAddress;
import com.ericsson.nms.security.dhcpai.test.common.CommandRunner;
import com.ericsson.nms.security.dhcpai.test.common.DHCPAITestCaseHelper;
import com.ericsson.nms.security.dhcpai.test.operators.ip.IpAddressServiceOperator;

public class IpAddressServiceFunctionalTest extends DHCPAITestCaseHelper {

    @Inject
    OperatorRegistry<IpAddressServiceOperator> operatorRegistry;

    @BeforeMethod
    public void resetDHCPAIConfig() {
        new CommandRunner<Object>(null).resetDHCPConfigFiles();
    }

    @Context(context = { Context.API })
    @DataDriven(name = "ip_checkAvailable")
    @Test
    @TestId(id = "IPAddress_Functional_CheckIp (TORF-10175)", title = "Create an IP Address and verify it is valid and available for DHCP configuration (sync)")
    public void shouldCheckIpAddressAvailable(@Input("address") String address, @Output("expected") String expected) {
        final IpAddress ipAddress = new IpAddress();
        ipAddress.setAddress(address);

        final IpAddressServiceOperator ipAddressServiceOperator = operatorRegistry.provide(IpAddressServiceOperator.class);
        assertTest("- IpAddress - Check IP Address available", ipAddressServiceOperator.checkAvailable(ipAddress), expected, ipAddress);
    }
}
