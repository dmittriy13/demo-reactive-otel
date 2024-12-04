package org.example.common.configuration

import com.rabbitmq.client.Recoverable
import com.rabbitmq.client.RecoveryListener
import com.rabbitmq.client.impl.recovery.AutorecoveringConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RabbitMQRecoveryListener : RecoveryListener {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun handleRecovery(recoverable: Recoverable?) {
        if (recoverable is AutorecoveringConnection) {
            logger.debug("Connection recovered")
        }
    }

    override fun handleRecoveryStarted(recoverable: Recoverable?) {
        if (recoverable is AutorecoveringConnection) {
            logger.debug("Start connection recovery, reason:", recoverable.closeReason)
        }
    }

}