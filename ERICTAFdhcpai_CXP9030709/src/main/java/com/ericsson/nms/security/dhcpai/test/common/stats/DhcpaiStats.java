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

public class DhcpaiStats {

    private final long startTime;
    private String typeName;

    private OperationStats addStats;
    private OperationStats existStats;
    private OperationStats findStats;
    private OperationStats deleteStats;

    public DhcpaiStats(String typeName) {
        this.typeName = typeName;
        this.startTime = System.currentTimeMillis();
    }

    /**
     * @param addStats
     *            the addStats to set
     */
    public void setAddStats(OperationStats addStats) {
        this.addStats = addStats;
    }

    /**
     * @param existStats
     *            the existStats to set
     */
    public void setExistStats(OperationStats existStats) {
        this.existStats = existStats;
    }

    /**
     * @param findStats
     *            the findStats to set
     */
    public void setFindStats(OperationStats findStats) {
        this.findStats = findStats;
    }

    /**
     * @param deleteStats
     *            the deleteStats to set
     */
    public void setDeleteStats(OperationStats deleteStats) {
        this.deleteStats = deleteStats;
    }

    /**
     * @return the addStats
     */
    public OperationStats getAddStats() {
        return addStats;
    }

    /**
     * @return the existStats
     */
    public OperationStats getExistStats() {
        return existStats;
    }

    /**
     * @return the findStats
     */
    public OperationStats getFindStats() {
        return findStats;
    }

    /**
     * @return the deleteStats
     */
    public OperationStats getDeleteStats() {
        return deleteStats;
    }

    /**
     * @return the duration
     */
    public long getDuration() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName
     *            the typeName to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
