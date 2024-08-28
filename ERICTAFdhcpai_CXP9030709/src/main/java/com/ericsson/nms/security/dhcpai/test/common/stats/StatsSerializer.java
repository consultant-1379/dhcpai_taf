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

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.SerializerBase;

@SuppressWarnings("deprecation")
public class StatsSerializer extends SerializerBase<DhcpaiStats> {

    public StatsSerializer(Class<DhcpaiStats> t) {
        super(t);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.jackson.map.ser.std.SerializerBase#serialize(java.lang.Object, org.codehaus.jackson.JsonGenerator, org.codehaus.jackson.map.SerializerProvider)
     */
    @Override
    public void serialize(final DhcpaiStats stats, final JsonGenerator gen, final SerializerProvider prov) throws IOException, JsonGenerationException {
        gen.writeStartObject();
        gen.writeNumberField("testDuration", stats.getDuration());
        gen.writeStringField("typeName", stats.getTypeName());

        if (stats.getTypeName().equals("IpAddress")) {
            writeOperationStats(gen, stats.getExistStats(), "existStats");
        } else {
            writeOperationStats(gen, stats.getAddStats(), "addStats");
            writeOperationStats(gen, stats.getDeleteStats(), "deleteStats");
            writeOperationStats(gen, stats.getFindStats(), "findStats");
            writeOperationStats(gen, stats.getExistStats(), "existStats");
        }

        gen.writeEndObject();
    }

    private void writeOperationStats(final JsonGenerator gen, final OperationStats opStats, final String opStatsName) throws JsonGenerationException, IOException {
        gen.writeFieldName(opStatsName);
        gen.writeStartObject();

        gen.writeNumberField("opCount", opStats.getTotalOps());
        gen.writeNumberField("opPerSecond", opStats.getOpsPerSecond());

        long[][] array = sortByX(opStats.getOpTimes());
        gen.writeArrayFieldStart("opsCompleted");
        for (int i = 0; i < array.length; i++) {
            final long x = array[i][0];
            final long y = array[i][1];
            gen.writeStartArray();
            gen.writeNumber(x);
            gen.writeNumber(y);
            gen.writeEndArray();
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }

    private long[][] sortByX(final long[][] array) {
        Arrays.sort(array, new Comparator<long[]>() {
            @Override
            public int compare(final long[] entry1, final long[] entry2) {
                final Long time1 = entry1[0];
                final Long time2 = entry2[0];
                return time1.compareTo(time2);
            }
        });
        return array;

    }
}
