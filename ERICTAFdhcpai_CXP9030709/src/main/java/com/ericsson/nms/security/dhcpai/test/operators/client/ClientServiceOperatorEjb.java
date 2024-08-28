package com.ericsson.nms.security.dhcpai.test.operators.client;

import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.nms.security.dhcpai.common.*;
import com.ericsson.nms.security.dhcpai.test.common.CommandRunner;

@Operator(context = Context.API)
@Singleton
public class ClientServiceOperatorEjb implements ClientServiceOperator {

	@Override
	public String addAndCheckUUID(Client client) {
		CommandRunner<Client> commandRunner = new CommandRunner<Client>(client) {
			@Override
			public UUID executeAddOrDelete() {
				return getClientService().add(getUnderlyingObject());
			}
		};

		return commandRunner.executeAndCheckUUID();
	}

	@Override
	public String addAndCheckResponse(Client client) {
		CommandRunner<Client> commandRunner = new CommandRunner<Client>(client) {
			@Override
			public UUID executeAddOrDelete() {
				return getClientService().add(getUnderlyingObject());
			}
		};

		return commandRunner.executeAndCheckResponse();
	}

	@Override
	public String deleteAndCheckUUID(Client client) {
		CommandRunner<Client> commandRunner = new CommandRunner<Client>(client) {
			@Override
			public UUID executeAddOrDelete() {
				return getClientService().delete(getUnderlyingObject());
			}
		};

		return commandRunner.executeAndCheckUUID();
	}

	@Override
	public String deleteAndCheckResponse(Client client) {
		CommandRunner<Client> commandRunner = new CommandRunner<Client>(client) {
			@Override
			public UUID executeAddOrDelete() {
				return getClientService().delete(getUnderlyingObject());
			}
		};

		return commandRunner.executeAndCheckResponse();
	}

	@Override
	public String exists(Client client) {
		CommandRunner<Client> commandRunner = new CommandRunner<Client>(client) {
			@Override
			public boolean executeExist() {
				return getClientService().exists(getUnderlyingObject());
			}
		};

		return commandRunner.executeAndCheckExist();
	}

	@Override
	public String find(Client client) {
		CommandRunner<Client> commandRunner = new CommandRunner<Client>(client) {
			@Override
			public NetworkElement executeFind() {
				return getClientService().find(getUnderlyingObject());
			}
		};

		return commandRunner.executeAndFind();
	}
	
	@Override
	public List<? extends NetworkElement> list() {
		CommandRunner<Client> commandRunner = new CommandRunner<Client>(new Client()) {
			@Override
			public List<? extends NetworkElement> executeList() {
				return getClientService().list();
			}
		};
		
		return commandRunner.list();
	}

}
