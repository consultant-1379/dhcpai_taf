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

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.nms.security.dhcpai.common.NetworkElement;

public class DHCPAITestCaseHelper extends TorTestCaseHelper implements TestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @AfterSuite
    public void closeJMSConnection() throws JMSException {
        new CommandJMSHandler(CommandRunner.getServer()).closeJMSHandler();
    }

    protected void assertTest(String testName, String actualResult, String expectedResult, NetworkElement networkElement) {
        boolean testResult = actualResult.contains(expectedResult);

        logger.debug("\n#############  Test: {}  #############", testName);
        logger.debug("--------------------");
        logger.debug("-->            GUID: {}", networkElement.getGuid());
        logger.debug("--> Network element: {}", networkElement);
        logger.debug("-->   Actual result: {}", actualResult);
        logger.debug("--> Expected result: {}", expectedResult);
        logger.debug("--------------------");
        logger.debug("-->     Test result: {}\n", (testResult ? "P A S S" : "F A I L"));

        StringBuilder testInfo = new StringBuilder();
        testInfo.append("\n#############  Test: " + testName + "  #############");
        testInfo.append("\n--------------------");
        testInfo.append("\n-->            GUID: " + networkElement.getGuid());
        testInfo.append("\n--> Network element: " + networkElement);
        testInfo.append("\n-->   Actual result: " + actualResult);
        testInfo.append("\n--> Expected result: " + expectedResult);
        testInfo.append("\n--------------------");
        testInfo.append("\n-->     Test result: " + (testResult ? "P A S S" : "F A I L") + "\n");
        System.out.println(testInfo.toString());

        Assert.assertTrue(testResult, testInfo.toString());
    }

    protected void indicateStartOfCompositeTest(final String testName) {
        logger.debug("\n#############  Composite Test started: {}  #############", testName);

        StringBuilder testInfo = new StringBuilder();

        testInfo.append("\n######################################################");
        for (int i = 0; i < testName.length(); i++) {
            testInfo.append("#");
        }
        testInfo.append("\n#############  Composite Test started: " + testName + "  #############");
        testInfo.append("\n######################################################");
        for (int i = 0; i < testName.length(); i++) {
            testInfo.append("#");
        }

        System.out.println(testInfo.toString());
    }

    protected void indicateEndOfCompositeTest(final String testName) {
        logger.debug("#############  Composite Test finished: {}  #############\n", testName);

        StringBuilder testInfo = new StringBuilder();

        testInfo.append("#######################################################");
        for (int i = 0; i < testName.length(); i++) {
            testInfo.append("#");
        }
        testInfo.append("\n#############  Composite Test finished: " + testName + "  #############");
        testInfo.append("\n#######################################################");
        for (int i = 0; i < testName.length(); i++) {
            testInfo.append("#");
        }
        testInfo.append("\n");

        System.out.println(testInfo.toString());
    }

    protected String checkDHCPDConfig() {
        return new CommandRunner<Object>(null).checkDHCPDConfig();
    }

}
