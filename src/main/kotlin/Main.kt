import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Keys
import org.web3j.crypto.MnemonicUtils


val log: Logger = LoggerFactory.getLogger("Web3Utils")



fun main() {
    try {
        // 助记词
        val mnemonic = "cage mix correct shop shy escape broccoli rug crucial"

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

        // 输出账户信息
        println("Address: 0x$address")
        println("Private Key: $privateKey")
        println("Public Key: $publicKey")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}