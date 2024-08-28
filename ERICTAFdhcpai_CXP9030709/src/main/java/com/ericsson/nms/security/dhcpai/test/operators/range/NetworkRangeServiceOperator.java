package com.ericsson.nms.security.dhcpai.test.operators.range;

import com.ericsson.nms.security.dhcpai.common.NetworkRange;

public interface NetworkRangeServiceOperator {

	String addAndCheckUUID(NetworkRange networkRange);

	String addAndCheckResponse(NetworkRange networkRange);

	String deleteAndCheckUUID(NetworkRange networkRange);

	String deleteAndCheckResponse(NetworkRange networkRange);

	String exists(NetworkRange networkRange);

	String find(NetworkRange networkRange);

}
