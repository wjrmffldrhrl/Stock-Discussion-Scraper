package com.stock.manager.file

import org.junit.jupiter.api.Test

class GoogleFileUploaderTest {

    @Test
    fun testFileUploader() {

        Thread(GoogleFileUploader(arrayOf("005930"), 10000)).start()

        Thread.sleep(1000000000)
    }
}