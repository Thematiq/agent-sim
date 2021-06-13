package engine.agents

import java.util.concurrent.atomic.AtomicBoolean


class Config {
    private val closed = new AtomicBoolean(false)

    private[agents] def close(): Unit = closed.set(true)
    private[agents] def open(): Unit = closed.set(false)

}

object Config {

}