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

import java.util.HashMap;
import java.util.Map;

public class OperationStats {

    private final int totalOps;

    private final long startTime;
    private long finishTime;

    // time required for e2e operation to complete
    private final Map<Long, Long> opTimes = new HashMap<>();

    public OperationStats(int totalOps) {
        this.totalOps = totalOps;
        this.startTime = System.currentTimeMillis();
    }

    // Facilitate flot2 js library to plot results
    public long[][] getOpTimes() {
        return convertMapToFlot2Array(opTimes);
    }

    private long[][] convertMapToFlot2Array(final Map<Long, Long> points) {
        final long[][] arrays = new long[points.size()][];
        int counter = 0;
        for (Long key : points.keySet()) {
            final long[] array = new long[2];
            array[0] = key;
            array[1] = points.get(key);
            arrays[counter] = array;
            counter++;
        }
        return arrays;
    }

    public void addOp(final int newTotalOpsComplete) {
        long currentTime = (System.currentTimeMillis() - startTime) / 1000;
        opTimes.put(currentTime, (long) newTotalOpsComplete);
    }

    /////TODO
    public void setFinishTime() {
        finishTime = System.currentTimeMillis();
    }

    public Long getOpsPerSecond() {
        long totalTime = finishTime - startTime;
        return totalTime / totalOps;
    }

    public int getTotalOps() {
        return totalOps;
    }

}
