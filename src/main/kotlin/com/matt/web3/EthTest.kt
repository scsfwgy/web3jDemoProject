package com.matt.web3

import com.matt.web3.utils.Web3Utils
import log

fun main() {
    //连接方式1：使用infura 提供的客户端
    //mainnet https://mainnet.infura.io/v3/2b86c426683f4a6095fd175fe931d799
    val web3j = Web3Utils.getWeb3Obj()

    /**
     * 查询指定地址的eth余额
     */
    val accountAddress = "0xad5d62b6900651e290035ad19219576652b61f2d" //等待查询余额的地址
    //https://bscscan.com/token/0x9767c8e438aa18f550208e6d1fdf5f43541cc2c8?a=0xad5d62b6900651e290035ad19219576652b61f2d
    val contractAddress = "0x9767c8e438aa18f550208e6d1fdf5f43541cc2c8"
    val balanceOfEth = Web3Utils.getBalanceOfContract(web3j, accountAddress)
    log.info(balanceOfEth)

    val balanceOfContract = Web3Utils.getBalanceOfContract(web3j, accountAddress, contractAddress)
    log.info(balanceOfContract)

    val apiMnemonic = Web3Utils.loadWalletByMnemonic("cherry type collect echo derive shy balcony dog concert picture kid february")
    log.info(apiMnemonic.toString())
}