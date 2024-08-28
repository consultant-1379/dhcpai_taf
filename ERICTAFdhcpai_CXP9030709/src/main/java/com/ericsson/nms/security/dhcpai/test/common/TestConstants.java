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
package com.ericsson.nms.security.dhcpai.test.common;

public class TestConstants {
    public final static String OPERATION_SUCCESS = "SUCCESS";
    public final static String OPERATION_FAILED = "FAILED";
    public final static String OPERATION_ERROR = "ERROR";
    public final static String OPERATION_WARNING = "WARNING";

    public final static String EXISTS = "EXISTS";
    public final static String DOES_NOT_EXIST = "DOES_NOT_EXIST";

    public final static String FOUND = "FOUND";
    public final static String NULL = "NULL";

    public final static String AVAILABLE = "IS_AVAILABLE";
    public final static String NOT_AVAILABLE = "NOT_AVAILABLE";

    public final static String EJB_EXCEPTION = "EJB_EXCEPTION";
    public final static String DHCPAI_SERVER_EXCEPTION = "DHCPAI_SERVER_EXCEPTION";
    public final static String DHCPAI_VALIDATION_EXCEPTION = "DHCPAI_VALIDATION_EXCEPTION";
    
    public final static String DHCPAI_HOST_NAME_IDENTIFIER = "MSCM_su_0_jee_cfg";
    
    public final static String SC1_HOST_NAME_IDENTIFIER = "sc1";
    public final static String COMMAND_RESPONSE_TOPIC = "jms/topic/DHCPAIResponseTopic";
    public final static String DHCPAI_JNDI_SERVICE_ROOT = "dhcpai.jndi.root";
    public final static String NETWORK_SERVICE_JNDI = "dhcpai.network.jndi";
    public final static String CLIENT_SERVICE_JNDI = "dhcpai.client.jndi";
    public final static String NETWORK_RANGE_SERVICE_JNDI = "dhcpai.range.jndi";
    public final static String IP_ADDRESS_SERVICE_JNDI = "dhcpai.ipAddress.jndi";

    public final static String PRE_SIZE_CLIENT_COUNT = "presize.client.count";
    public final static String CLIENT_LOAD_ITERATIONS = "client.load.iterations";
    public final static String NETWORK_LOAD_ITERATIONS = "network.load.iterations";
    public final static String IPADDRESS_LOAD_ITERATIONS = "ipaddress.load.iterations";
    public final static String NETWORKRANGE_LOAD_ITERATIONS = "range.load.iterations";
    public final static String PRE_SIZE_NETWORK_COUNT = "presize.network.count";

}
