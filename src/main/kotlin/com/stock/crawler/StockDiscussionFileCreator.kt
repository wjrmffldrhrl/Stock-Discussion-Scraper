package com.stock.crawler

import com.stock.utils.StockDiscussion
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime

class StockDiscussionFileCreator(itemCode: String) : StockDiscussionProcessor {

    private val directoryPath = "discussion/$itemCode"
    private val directory = File(directoryPath)
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)
    init {

        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    private var processingCount = 0
    private var fileName = "init"
    private val fileExtension = ".csv"
    private var csvWriter = initOutputFileWriter(fileName)


    private fun initOutputFileWriter(fileName: String): BufferedWriter {

        logger.info("[${LocalDateTime.now()}] saveFile : $directoryPath/$fileName")
        val csv = File("$directoryPath/$fileName$fileExtension")
        val writer = BufferedWriter(FileWriter(csv, true))

        if (csv.name != "init.csv") {
            writer.write("date,url,title,content" + "\n")
        }

        return writer
    }

    override fun processing(discussion: StockDiscussion) {
        val discussionDate = discussion.date.split("T")[0].replace(".", "_")

        if (discussionDate != fileName || fileName == "init") {
            csvWriter.close()
            csvWriter = initOutputFileWriter(discussionDate)
            fileName = discussionDate

        }

        csvWriter.write(discussion.toCsv() + "\n")
        csvWriter.flush()

    }
}