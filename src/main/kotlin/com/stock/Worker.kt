package com.stock

/**
 * Run in cycle time
 */
interface Worker: Runnable {
    val cycleTime: Int

    /**
     * Implement what want to work in cycle
     */
    fun work()

    override fun run() {
        while (true) {
            Thread.sleep(cycleTime.toLong())
            work()
        }
    }
}