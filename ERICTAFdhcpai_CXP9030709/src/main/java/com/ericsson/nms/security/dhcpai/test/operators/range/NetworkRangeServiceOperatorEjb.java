package com.ericsson.nms.security.dhcpai.test.operators.range;

import java.util.UUID;

import javax.inject.Singleton;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.nms.security.dhcpai.common.NetworkRange;
import com.ericsson.nms.security.dhcpai.test.common.CommandRunner;

@Operator(context = Context.API)
@Singleton
public class NetworkRangeServiceOperatorEjb implements
		NetworkRangeServiceOperator {

	@Override
	public String addAndCheckUUID(NetworkRange networkRange) {
		CommandRunner<NetworkRange> networkCommand = new CommandRunner<NetworkRange>(
				networkRange) {
			@Override
			public UUID executeAddOrDelete() {
				return getNetworkRangeService().add(getUnderlyingObject());
			}
		};

		return networkCommand.executeAndCheckUUID();
	}

	@Override
	public String addAndCheckResponse(NetworkRange networkRange) {
		CommandRunner<NetworkRange> networkCommand = new CommandRunner<NetworkRange>(
				networkRange) {
			@Override
			public UUID executeAddOrDelete() {
				return getNetworkRangeService().add(getUnderlyingObject());
			}
		};

		return networkCommand.executeAndCheckResponse();
	}

	@Override
	public String deleteAndCheckUUID(NetworkRange networkRange) {
		CommandRunner<NetworkRange> networkCommand = new CommandRunner<NetworkRange>(
				networkRange) {
			@Override
			public UUID executeAddOrDelete() {
				return getNetworkRangeService().delete(getUnderlyingObject());
			}
		};

		return networkCommand.executeAndCheckUUID();
	}

	@Override
	public String deleteAndCheckResponse(NetworkRange networkRange) {
		CommandRunner<NetworkRange> networkCommand = new CommandRunner<NetworkRange>(
				networkRange) {
			@Override
			public UUID executeAddOrDelete() {
				return getNetworkRangeService().delete(getUnderlyingObject());
			}
		};

		return networkCommand.executeAndCheckResponse();
	}

	@Override
	public String exists(NetworkRange networkRange) {
		CommandRunner<NetworkRange> networkCommand = new CommandRunner<NetworkRange>(
				networkRange) {
			@Override
			public boolean executeExist() {
				return getNetworkRangeService().exists(getUnderlyingObject());
			}
		};

		return networkCommand.executeAndCheckExist();
	}

	@Override
	public String find(NetworkRange networkRange) {
		CommandRunner<NetworkRange> networkCommand = new CommandRunner<NetworkRange>(
				networkRange) {
			@Override
			public NetworkRange executeFind() {
				return getNetworkRangeService().find(getUnderlyingObject());
			}
		};

		return networkCommand.executeAndFind();
	}

}
