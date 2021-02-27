package com.stock

interface Worker: Runnable {
    val cycleTime: Int

    fun work()

    override fun run() {
        while (true) {
            Thread.sleep(cycleTime.toLong())
            work()
        }
    }
}