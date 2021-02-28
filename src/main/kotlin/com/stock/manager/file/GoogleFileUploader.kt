package com.stock.manager.file

import java.io.File
import java.lang.RuntimeException
import java.time.LocalDateTime

class GoogleFileUploader(private val itemCodes: Array<String>, override val cycleTime: Int, private val uploadPriority: String = "old") : FileManager {


    private fun uploadFile(targetFileName: String) {

        val targetFile = File(targetFileName)

        if (!targetFileName.startsWith("init")) {
            println("[" + LocalDateTime.now() + "] upload file : " + targetFileName)
            GoogleDriveManager.uploadFile(targetFile)
        }

        targetFile.delete()

    }

    override fun work() {
        itemCodes.forEach {
            val targetDirectoryPath = "discussion/$it"
            val targetDirectory = File(targetDirectoryPath)
            val otherFileNames = targetDirectory.list()
                .filter { fileName -> fileName.endsWith(".csv") }
                .filter { fileName -> !fileName.contains("init") }

            if (otherFileNames.size >= 2) {
                otherFileNames.sorted()


                val targetFileName = when (uploadPriority) {
                    "old" -> {
                        otherFileNames.first()
                    }
                    "latest" -> {
                        otherFileNames.last()
                    }
                    else -> {
                        throw RuntimeException("Invalid priority : [$uploadPriority]")
                    }
                }
                uploadFile(targetDirectoryPath + "/${targetFileName}")

            }


        }
    }
}