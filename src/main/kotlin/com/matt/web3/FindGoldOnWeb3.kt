package com.matt.web3

import com.matt.web3.utils.FileUtilsWrapper
import com.matt.web3.utils.Web3MnemonicUtils
import com.matt.web3.utils.Web3Utils
import com.matt.web3.utils.XFormatUtil
import log
import org.web3j.protocol.Web3j


fun main(args: Array<String>) {
    log.info(args.toString())
    FindGoldOnWeb3().run(10000)
}

class FindGoldOnWeb3 {
    fun run(size: Int, web3j: List<Web3j>? = null) {
        val beginTS=System.currentTimeMillis()
        //助记词列表
        val random12MnemonicListByBlank = Web3MnemonicUtils.getRandom12MnemonicListByBlank(size)
        log.info("生成不重复助记词耗时(${size}个)："+(System.currentTimeMillis()-beginTS)+"ms")
        //web3实例
        val web3ObjList = web3j ?: listOf(
            Web3Utils.getEthWeb3Obj(),
            Web3Utils.getBscWeb3Obj()
        )
        var findGoldSize = 0
        val totalSize = random12MnemonicListByBlank.size
        random12MnemonicListByBlank.forEachIndexed { index, it ->
            val currentCount = index + 1
            val percent = currentCount * 1.0 / totalSize * 100
            val show = "$currentCount/${totalSize}"
            val percentFormat = XFormatUtil.globalFormat(percent, 2) + "%"
            val apiMnemonic = Web3Utils.loadWalletByMnemonic(it)
            log.info("根据助记词生成钱包（进度${show}=${percentFormat},gold:${findGoldSize}）：助记词：$it，对应钱包：${apiMnemonic.address}")
            val balanceList = web3ObjList.map {
                Web3Utils.getBalanceOfContract(it, apiMnemonic.address)
            }.filter { it != "0" }
            if (balanceList.isNotEmpty()) {
                val data = "查询到非空钱包！！！！！：${balanceList}===>>" + apiMnemonic
                log.error(data)
                //持久化有价值的账号
                FileUtilsWrapper.updateGoldList2GoldFile(listOf(data))
                findGoldSize++
            }
            //将查询过的助记词持久化
            FileUtilsWrapper.updateMnemonicList2MnemonicFile(listOf(it))
        }
    }
}