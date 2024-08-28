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

import com.ericsson.nms.security.dhcpai.common.NetworkElement;

public class CommandAudit {

	public enum Service {
		Network, NetworkRange, Client, IpAddress
	};

	public enum Operation {
		Add, Delete, List, Exist, Find
	};

	private String uuid;
	private final Service service;
	private final Operation operation;
	private final NetworkElement networkElement;
	private long beforeCallingServiceCurrentTime;
	private long beforeCallingServiceAuditTime;
	private long afterCallingServiceCurrentTime;
	private long afterCallingServiceAuditTime;
	private long afterReceiveJMSReponseCurrentTime;
	private long afterReceiveJMSReponseAuditTime;
	private String status;

	public CommandAudit(NetworkElement networkElement, Service service,
			Operation operation) {
		this.service = service;
		this.operation = operation;
		this.networkElement = networkElement;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the beforeCallingServiceCurrentTime
	 */
	public long getBeforeCallingServiceCurrentTime() {
		return beforeCallingServiceCurrentTime;
	}

	/**
	 * @param beforeCallingServiceCurrentTime
	 *            the beforeCallingServiceCurrentTime to set
	 */
	public void setBeforeCallingServiceCurrentTime(
			long beforeCallingServiceCurrentTime) {
		this.beforeCallingServiceCurrentTime = beforeCallingServiceCurrentTime;
	}

	/**
	 * @return the beforeCallingServiceAuditTime
	 */
	public long getBeforeCallingServiceAuditTime() {
		return beforeCallingServiceAuditTime;
	}

	/**
	 * @param beforeCallingServiceAuditTime
	 *            the beforeCallingServiceAuditTime to set
	 */
	public void setBeforeCallingServiceAuditTime(
			long beforeCallingServiceAuditTime) {
		this.beforeCallingServiceAuditTime = beforeCallingServiceAuditTime;
	}

	/**
	 * @return the afterCallingServiceCurrentTime
	 */
	public long getAfterCallingServiceCurrentTime() {
		return afterCallingServiceCurrentTime;
	}

	/**
	 * @param afterCallingServiceCurrentTime
	 *            the afterCallingServiceCurrentTime to set
	 */
	public void setAfterCallingServiceCurrentTime(
			long afterCallingServiceCurrentTime) {
		this.afterCallingServiceCurrentTime = afterCallingServiceCurrentTime;
	}

	/**
	 * @return the afterCallingServiceAuditTime
	 */
	public long getAfterCallingServiceAuditTime() {
		return afterCallingServiceAuditTime;
	}

	/**
	 * @param afterCallingServiceAuditTime
	 *            the afterCallingServiceAuditTime to set
	 */
	public void setAfterCallingServiceAuditTime(
			long afterCallingServiceAuditTime) {
		this.afterCallingServiceAuditTime = afterCallingServiceAuditTime;
	}

	/**
	 * @return the afterReceiveJMSReponseCurrentTime
	 */
	public long getAfterReceiveJMSReponseCurrentTime() {
		return afterReceiveJMSReponseCurrentTime;
	}

	/**
	 * @param afterReceiveJMSReponseCurrentTime
	 *            the afterReceiveJMSReponseCurrentTime to set
	 */
	public void setAfterReceiveJMSReponseCurrentTime(
			long afterReceiveJMSReponseCurrentTime) {
		this.afterReceiveJMSReponseCurrentTime = afterReceiveJMSReponseCurrentTime;
	}

	/**
	 * @return the afterReceiveJMSReponseAuditTime
	 */
	public long getAfterReceiveJMSReponseAuditTime() {
		return afterReceiveJMSReponseAuditTime;
	}

	/**
	 * @param afterReceiveJMSReponseAuditTime
	 *            the afterReceiveJMSReponseAuditTime to set
	 */
	public void setAfterReceiveJMSReponseAuditTime(
			long afterReceiveJMSReponseAuditTime) {
		this.afterReceiveJMSReponseAuditTime = afterReceiveJMSReponseAuditTime;
	}

	/**
	 * @return the service
	 */
	public Service getService() {
		return service;
	}

	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		return operation;
	}

	/**
	 * @return the networkElement
	 */
	public NetworkElement getNetworkElement() {
		return networkElement;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param exists
	 */
	public void setStatus(String status) {
		this.status = status;

	}

}
