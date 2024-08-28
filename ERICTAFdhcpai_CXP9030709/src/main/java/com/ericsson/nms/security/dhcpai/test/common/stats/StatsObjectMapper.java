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
package com.ericsson.nms.security.dhcpai.test.common.stats;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;


public class StatsObjectMapper extends ObjectMapper {
	public StatsObjectMapper() {
		final CustomSerializerFactory sf = new CustomSerializerFactory();
		sf.addSpecificMapping(DhcpaiStats.class, new StatsSerializer(DhcpaiStats.class));
		this.setSerializerFactory(sf);
	}
}
