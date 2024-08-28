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
package com.ericsson.nms.security.dhcpai.test.operators.ip;

import javax.inject.Singleton;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.nms.security.dhcpai.common.IpAddress;
import com.ericsson.nms.security.dhcpai.test.common.CommandRunner;

@Operator(context = Context.API)
@Singleton
public class IpAddressServiceOperatorEjb implements IpAddressServiceOperator {

	@Override
	public String checkAvailable(IpAddress ipAddress) {
		CommandRunner<IpAddress> networkCommand = new CommandRunner<IpAddress>(
				ipAddress) {

			@Override
			public boolean executeExist() {
				return getIpAddressService().checkAvailable(
						getUnderlyingObject());
			}

		};
		return networkCommand.executeAndCheckAvailable();
	}

}
