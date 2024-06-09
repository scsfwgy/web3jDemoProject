package com.matt.web3.utils

import com.matt.web3.model.ApiMnemonic
import log
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Keys
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

fun main() {
    val web3ObjList = listOf(
        Web3Utils.getEthWeb3Obj(),
        Web3Utils.getBscWeb3Obj()
    )
    val random12Mnemonic = "cage mix correct shop shy escape broccoli rug crucial share"
    log.info("新生成的助记词：" + random12Mnemonic)
    val apiMnemonic = Web3Utils.loadWalletByMnemonic(random12Mnemonic)
    log.info("根据助记词生成钱包：" + apiMnemonic)
    val balanceList = web3ObjList.map {
        Web3Utils.getBalanceOfContract(it, apiMnemonic.address)
    }.filter { it != "0" }
    if (balanceList.isNotEmpty()) {
        log.error("查询到非空钱包！！！！！：${balanceList}===>>" + apiMnemonic)
    }
}

object Web3MnemonicUtils {
    private val MNEMONIC_LIST = ArrayList<String>()
    private val hasUsedMnemonicList = ArrayList<List<String>>()

    init {
        val populateWordList = populateWordList()
        val mnemonicListInFileCache = FileUtilsWrapper.getMnemonicListInFileCache()
        MNEMONIC_LIST.addAll(populateWordList)
        val beginTS = System.currentTimeMillis()
        hasUsedMnemonicList.add(FileUtilsWrapper.getMnemonicListInFileCache())
        log.info("原始助记词列表：大小：${populateWordList.size},单词：$populateWordList")
        log.info("已经存在的助记词列表大小：${mnemonicListInFileCache.size},加载耗时：${System.currentTimeMillis() - beginTS}ms")
    }

    fun getRandom12MnemonicListByBlank(size: Int): List<String> {
        val arrayList = ArrayList<String>(size)
        for (i in 0..<size) {
            arrayList.add(getRandom12MnemonicByBlank())
        }
        return arrayList
    }

    fun getRandom12MnemonicByBlank(): String {
        return getRandom12MnemonicList().joinToString(separator = " ")
    }

    fun getRandom12MnemonicList(): List<String> {
        val mnemonicList = getMnemonicList()
        val _12Mnemonic = mnemonicList.shuffled().take(12)
        if (hasUsedMnemonicList.contains(_12Mnemonic)) {
            log.warn("该助记词已经生成过,准备重新生成：$_12Mnemonic")
            return getMnemonicList()
        }
        hasUsedMnemonicList.add(_12Mnemonic)
        return _12Mnemonic
    }

    fun getMnemonicList(): ArrayList<String> {
        return MNEMONIC_LIST
    }

    private fun populateWordList(): List<String> {
        val inputStream: InputStream =
            Thread.currentThread().getContextClassLoader().getResourceAsStream("en-mnemonic-word-list.txt")
                ?: throw java.lang.IllegalStateException("en-mnemonic-word-list.txt load fail")
        return try {
            readAllLines(inputStream)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    @Throws(IOException::class)
    private fun readAllLines(inputStream: InputStream): List<String> {
        val br = BufferedReader(InputStreamReader(inputStream))
        val data = ArrayList<String>()
        var line: String?
        while (br.readLine().also { line = it } != null) {
            val line1 = line
            if (line1 != null) {
                data.add(line1)
            }
        }
        return data
    }
}