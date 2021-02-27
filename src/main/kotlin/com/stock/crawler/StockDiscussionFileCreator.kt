package com.stock.crawler

import com.stock.StockDiscussion
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class StockDiscussionFileCreator(itemCode: String) : StockDiscussionProcessor {

    private val directoryPath = "discussion/$itemCode"
    private val directory = File(directoryPath)

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

        println("saveFile : $fileName")
        val csv = File("$directoryPath/$fileName$fileExtension")
        val writer = BufferedWriter(FileWriter(csv, true))

        if (csv.name != "init.csv") {
            writer.write("date,title,content,url,previousDiscussionUrl,nextDiscussionUrl" + "\n")
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