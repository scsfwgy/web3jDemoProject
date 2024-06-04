package com.matt.web3.utils

import com.matt.web3.utils.blankj.FileUtils
import log
import java.io.File

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：2020/8/14 10:50 AM
 * 描 述 ：
 * ============================================================
 **/
fun main() {
    val logText = "log.txt"
    val findFileInResources = FileUtilsWrapper.findFileInResources(logText)
    findFileInResources.appendText("222\n")
    findFileInResources.appendText("222\n")
    findFileInResources.appendText("22\n")
    findFileInResources.appendText("22\n")

    val lineByFile = FileUtilsWrapper.getLineByFile(findFileInResources)
    lineByFile.forEach {
        log.info(it)
    }
}

object FileUtilsWrapper {
    val TAG = FileUtilsWrapper::class.java.simpleName
    const val MNEMONIC_LIST_FILE = "mnemonic_cache_list.txt"
    const val GOLD_LIST = "gold_list.txt"

    fun getMnemonicListInFileCache(): List<String> {
        val loadMnemonicFile = loadMnemonicFile()
        return loadMnemonicFile.readLines()
    }

    fun updateMnemonicList2MnemonicFile(list: List<String>) {
        val loadMnemonicFile = loadMnemonicFile()
        loadMnemonicFile.appendText(list.joinToString(separator = "\n"))
        loadMnemonicFile.appendText("\n")
    }

    fun updateGoldList2GoldFile(list: List<String>) {
        val loadMnemonicFile = loadGoldFile()
        loadMnemonicFile.appendText(list.joinToString(separator = "\n"))
        loadMnemonicFile.appendText("\n")
    }

    fun loadMnemonicFile(): File {
        return findFileInResources(MNEMONIC_LIST_FILE)
    }

    fun loadGoldFile(): File {
        return findFileInResources(GOLD_LIST)
    }

    fun getLineByFile(file: File): List<String> {
        return file.readLines()
    }

    fun findFileInResources(fileName: String): File {
        val file = loadOrCreateFileInPath("src/main/resources/$fileName")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    fun loadOrCreateFileInPath(filePath: String): File {
        return File(filePath)
    }
}