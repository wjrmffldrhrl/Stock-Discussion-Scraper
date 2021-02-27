package com.stock

import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

object KoreanRequests {

    fun getScript(url: String): String {
        with(URL(url).openConnection() as HttpURLConnection) {
            setRequestProperty("referer", url)

            return String(inputStream.readBytes(), Charset.forName("EUC-KR"))
        }
    }
}