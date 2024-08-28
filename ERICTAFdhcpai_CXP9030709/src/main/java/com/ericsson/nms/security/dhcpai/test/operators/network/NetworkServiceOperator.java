package com.ericsson.nms.security.dhcpai.test.operators.network;

import java.util.List;

import com.ericsson.nms.security.dhcpai.common.Network;
import com.ericsson.nms.security.dhcpai.common.NetworkElement;

public interface NetworkServiceOperator {

	String addAndCheckUUID(Network network);

	String addAndCheckResponse(Network network);

	String deleteAndCheckUUID(Network network);

	String deleteAndCheckResponse(Network network);

	String exists(Network network);

	String find(Network network);

	List<? extends NetworkElement> list();
}
