package com.matt.web3

import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

class EthClient {
    private var web3j: Web3j? = null
    private val credentials: Credentials? = null
    var tempAddress: String? = null
    private val httpService: HttpService? = null

//    @Throws(IOException::class)
//    private fun connectETHClient() {
//        //连接方式1：使用infura 提供的客户端
//        //mainnet https://mainnet.infura.io/v3/2b86c426683f4a6095fd175fe931d799
//        web3j = Web3j.build(HttpService(Environment.RPC_URL)) // TODO: 2018/4/10 节点更改为自己的或者主网
//        //连接方式2：使用本地客户端
//        //web3j = Web3j.build(new HttpService("127.0.0.1:7545"));
//        //测试是否连接成功
//        val web3ClientVersion = web3j.web3ClientVersion().send().web3ClientVersion
//        println("version=$web3ClientVersion")
//    }
}