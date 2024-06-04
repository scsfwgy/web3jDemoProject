package com.matt.web3.utils

import com.matt.web3.model.ApiMnemonic
import log
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthCall
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.math.BigInteger
import java.util.concurrent.ExecutionException

object Web3Utils {
    const val ETH_RPC_URL = "https://mainnet.infura.io/v3/9eb78bae70c34116a2b28db3fdb96dd0"
    const val BSC_RPC_URL = "https://bsc-dataseed1.binance.org:443"
    const val emptyAddress = "0x0000000000000000000000000000000000000000"

    fun getWeb3ObjByUrl(url: String): Web3j {
        return Web3j.build(HttpService(url))
    }

    fun getWeb3Obj(): Web3j {
        //连接方式1：使用infura 提供的客户端
        //mainnet https://mainnet.infura.io/v3/2b86c426683f4a6095fd175fe931d799
        val web3j = getWeb3ObjByUrl(ETH_RPC_URL)
        //测试是否连接成功
        val web3ClientVersion: String = web3j.web3ClientVersion().send().web3ClientVersion
        log.info("web3ClientVersion:$web3ClientVersion")
        return web3j
    }

    fun getBscWeb3Obj(): Web3j {
        val web3j = Web3j.build(HttpService(BSC_RPC_URL))
        web3j.web3ClientVersion().send().web3ClientVersion
        return getWeb3ObjByUrl(BSC_RPC_URL)
    }

    fun getEthWeb3Obj(): Web3j {
        val web3j = Web3j.build(HttpService(ETH_RPC_URL))
        return getWeb3ObjByUrl(ETH_RPC_URL)
    }

    fun getBalanceOfContract(web3j: Web3j, accountAddress: String, contractAddress: String? = null): String {
        return if (contractAddress == null) {
            getEthBalanceOf(web3j, accountAddress)
        } else {
            getContractBalanceOf(web3j, accountAddress, contractAddress)
        }
    }

    fun getRandomMnemonic() {

    }

    /**
     * 根据助记词加载钱包
     */
    @Deprecated("算法不对，生成的钱包地址不对")
    fun loadWalletByMnemonic2(mnemonic: String): ApiMnemonic {
        val credentials = WalletUtils.loadBip39Credentials("", mnemonic) //no need password
        val address: String = credentials.address
        val publicKey: BigInteger = credentials.ecKeyPair.publicKey
        val privateKey: BigInteger = credentials.ecKeyPair.privateKey
        return ApiMnemonic(mnemonic, address, publicKey.toString(), privateKey.toString())
    }

    /**
     * gpt给的，这个算法是对的。
     */
    fun loadWalletByMnemonic(mnemonic:String): ApiMnemonic {
        // 生成种子
        val seed = MnemonicUtils.generateSeed(mnemonic, null)

        // 从种子生成BIP32 ECKeyPair
        val masterKeypair = Bip32ECKeyPair.generateKeyPair(seed)

        // MetaMask 使用的路径：m/44'/60'/0'/0/0
        val path = intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT, 60 or Bip32ECKeyPair.HARDENED_BIT, 0 or Bip32ECKeyPair.HARDENED_BIT, 0, 0)
        val keypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path)

        // 获取私钥和公钥
        val privateKey = keypair.privateKey.toString(16)
        val publicKey = keypair.publicKey.toString(16)

        // 生成账户地址
        val address = Keys.getAddress(keypair)
        return ApiMnemonic(mnemonic,"0x$address",privateKey,publicKey)
    }


    /**
     * 查询指定账户
     * 指定 ERC-20 余额
     */
    private fun getContractBalanceOf(web3j: Web3j, accountAddress: String, contractAddress: String): String {
        val methodName = "balanceOf"
        val fromAddr: String = emptyAddress
        var tokenBalance = BigInteger.ZERO
        val inputParameters: MutableList<Type<*>> = ArrayList()
        val userAddress = Address(accountAddress)
        inputParameters.add(userAddress)
        val outputParameters: MutableList<TypeReference<*>> = ArrayList()
        val typeReference: TypeReference<Uint256> = object : TypeReference<Uint256>() {}
        outputParameters.add(typeReference)
        val function = Function(methodName, inputParameters, outputParameters)
        val data = FunctionEncoder.encode(function)
        val transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data)
        val ethCall: EthCall
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get()
            val results = FunctionReturnDecoder.decode(ethCall.value, function.outputParameters)
            tokenBalance = results[0].value as BigInteger
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return tokenBalance.toString()
    }

    private fun getEthBalanceOf(web3j: Web3j, accountAddress: String): String {
        val balance = web3j.ethGetBalance(accountAddress, DefaultBlockParameter.valueOf("latest")).send()
        //格式转化 wei-ether
        val blanceETH = Convert.fromWei(balance.balance.toString(), Convert.Unit.ETHER).toPlainString()
        return blanceETH
    }
}