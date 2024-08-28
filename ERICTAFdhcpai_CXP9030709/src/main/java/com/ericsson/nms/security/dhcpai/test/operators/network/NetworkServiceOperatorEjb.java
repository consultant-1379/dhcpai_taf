package com.ericsson.nms.security.dhcpai.test.operators.network;

import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.nms.security.dhcpai.common.Network;
import com.ericsson.nms.security.dhcpai.common.NetworkElement;
import com.ericsson.nms.security.dhcpai.test.common.CommandRunner;

@Operator(context = Context.API)
@Singleton
public class NetworkServiceOperatorEjb implements NetworkServiceOperator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.nms.security.dhcpai.test.operators.network.
	 * NetworkServiceOperator
	 * #addAndCheckUUID(com.ericsson.nms.security.dhcpai.common.Network)
	 */
	@Override
	public String addAndCheckUUID(Network network) {
		CommandRunner<Network> networkCommand = new CommandRunner<Network>(
				network) {
			@Override
			public UUID executeAddOrDelete() {
				return getNetworkService().add(getUnderlyingObject());
			}

		};
		return networkCommand.executeAndCheckUUID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.nms.security.dhcpai.test.operators.network.
	 * NetworkServiceOperator
	 * #addAndCheckResponse(com.ericsson.nms.security.dhcpai.common.Network)
	 */
	@Override
	public String addAndCheckResponse(Network network) {
		CommandRunner<Network> networkCommand = new CommandRunner<Network>(
				network) {

			@Override
			public UUID executeAddOrDelete() {
				return getNetworkService().add(getUnderlyingObject());
			}

		};
		return networkCommand.executeAndCheckResponse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.nms.security.dhcpai.test.operators.network.
	 * NetworkServiceOperator
	 * #deleteAndCheckUUID(com.ericsson.nms.security.dhcpai.common.Network)
	 */
	@Override
	public String deleteAndCheckUUID(Network network) {
		CommandRunner<Network> networkCommand = new CommandRunner<Network>(
				network) {

			@Override
			public UUID executeAddOrDelete() {
				return getNetworkService().delete(getUnderlyingObject());
			}

		};
		return networkCommand.executeAndCheckUUID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.nms.security.dhcpai.test.operators.network.
	 * NetworkServiceOperator
	 * #deleteAndCheckResponse(com.ericsson.nms.security.dhcpai.common.Network)
	 */
	@Override
	public String deleteAndCheckResponse(Network network) {
		CommandRunner<Network> networkCommand = new CommandRunner<Network>(
				network) {

			@Override
			public UUID executeAddOrDelete() {
				return getNetworkService().delete(getUnderlyingObject());
			}

		};
		return networkCommand.executeAndCheckResponse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.nms.security.dhcpai.test.operators.network.
	 * NetworkServiceOperator
	 * #exists(com.ericsson.nms.security.dhcpai.common.Network)
	 */
	@Override
	public String exists(Network network) {
		CommandRunner<Network> networkCommand = new CommandRunner<Network>(
				network) {

			@Override
			public boolean executeExist() {
				return getNetworkService().exists(getUnderlyingObject());
			}

		};
		return networkCommand.executeAndCheckExist();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.nms.security.dhcpai.test.operators.network.
	 * NetworkServiceOperator
	 * #find(com.ericsson.nms.security.dhcpai.common.Network)
	 */
	@Override
	public String find(Network network) {
		CommandRunner<Network> networkCommand = new CommandRunner<Network>(
				network) {

			@Override
			public Network executeFind() {
				return getNetworkService().find(getUnderlyingObject());
			}

		};
		return networkCommand.executeAndFind();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.nms.security.dhcpai.test.operators.network.
	 * NetworkServiceOperator#list()
	 */
	@Override
	public List<? extends NetworkElement> list() {
		CommandRunner<Network> networkCommand = new CommandRunner<Network>(
				new Network()) {

			@Override
			public List<? extends NetworkElement> executeList() {
				return getNetworkService().list();
			}

		};
		return networkCommand.list();
	}
}
