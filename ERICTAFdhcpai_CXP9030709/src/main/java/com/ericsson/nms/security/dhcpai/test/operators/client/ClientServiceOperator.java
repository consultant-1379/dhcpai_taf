package com.ericsson.nms.security.dhcpai.test.operators.client;

import java.util.List;

import com.ericsson.nms.security.dhcpai.common.Client;
import com.ericsson.nms.security.dhcpai.common.NetworkElement;

public interface ClientServiceOperator {

	String addAndCheckUUID(Client client);
	String addAndCheckResponse(Client client);
	String deleteAndCheckUUID(Client client);
	String deleteAndCheckResponse(Client client);
	String find(Client client);
	String exists(Client client);
	List<? extends NetworkElement> list();

}
