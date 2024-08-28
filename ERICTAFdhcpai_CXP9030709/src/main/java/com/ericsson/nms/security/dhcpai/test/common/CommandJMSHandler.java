package com.ericsson.nms.security.dhcpai.test.common;

import static com.ericsson.nms.security.dhcpai.test.common.TestConstants.COMMAND_RESPONSE_TOPIC;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import javax.jms.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.JmsHandler;
import com.ericsson.nms.security.dhcpai.command.common.CommandResponse;

public class CommandJMSHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommandJMSHandler.class);

    private static JmsHandler jmsHandler;
    private static final long DEFAULT_JMS_MESSAGE_TIMEOUT = 20000L;
    private static final long DEFAULT_JMS_LISTNER_TIMEOUT = 20000L;
    private final Host server;

    public CommandJMSHandler(Host server) {
        this.server = server;
    }

    public CommandResponse getCommandResponse(UUID uuid) {
        return getCommandResponse(uuid.toString());
    }

    public BlockingQueue<Message> getAllMessages() {
        return jmsHandler.getAllMessages();
    }

    public CommandResponse getCommandResponse(String uuid) {
        CommandResponse commandResponse = null;
        try {
            Message msg = null;
            final int maxRetry = 100;
            int retryCount = 0;
            while (true) {
                try {
                    msg = jmsHandler.getMessageFromJms(uuid, DEFAULT_JMS_MESSAGE_TIMEOUT);
                    break;
                } catch (JMSException exception) {
                    retryCount++;
                    if (retryCount > maxRetry) {
                        throw exception;
                    }
                }
            }
            if (msg instanceof ObjectMessage) {
                final Object object = ((ObjectMessage) msg).getObject();
                if (object instanceof CommandResponse) {
                    commandResponse = (CommandResponse) object;
                }
            }
        } catch (Exception exception) {
            // exception.printStackTrace();
            logger.error("Error while getting command response.", exception);
        }
        return commandResponse;
    }

    public JmsHandler createJMSHandler(long timeout) throws JMSException {
        if (jmsHandler == null) {
            jmsHandler = new JmsHandler(this.server, COMMAND_RESPONSE_TOPIC, false, Session.AUTO_ACKNOWLEDGE, false, false, timeout);
        }

        return jmsHandler;
    }

    public JmsHandler createJMSHandler() throws JMSException {
        return createJMSHandler(DEFAULT_JMS_LISTNER_TIMEOUT);
    }

    public void closeJMSHandler() throws JMSException {
        if (jmsHandler != null) {
            jmsHandler.close();
            jmsHandler.removeAllListeners();
        }

    }
}
