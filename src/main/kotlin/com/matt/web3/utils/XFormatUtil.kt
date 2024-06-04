package com.matt.web3.utils

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 测试用例：[XFormatUtilTest],修改这里的方法注意跑一下测试用例，因为修改会影响整个应用
 */
object XFormatUtil {
    val TAG = XFormatUtil::class.java.simpleName

    const val DEF_GLOBAL_FORMAT = "0.00"

    /**
     * 账户资金相关计算显示
     */
    private fun double2BigDecimalByScale(
        num: String,
        scale: Int,
        stripTrailingZeros: Boolean? = null,
        roundDown: Boolean? = null,
    ): BigDecimal {
        //默认舍去
        val roundDownLocal = roundDown ?: true
        //默认保留0
        val stripTrailingZerosLocal = stripTrailingZeros ?: false
        val roundType = if (roundDownLocal) {
            RoundingMode.DOWN
        } else {
            RoundingMode.UP
        }
        val decimal = BigDecimal(num)
        val scaled =
            decimal.setScale(scale, roundType)
        return if (stripTrailingZerosLocal) {
            scaled.stripTrailingZeros()
        } else {
            scaled
        }
    }

    private fun double2BigDecimalByScaleToString(
        num: String,
        scale: Int,
        stripTrailingZeros: Boolean? = null,
        roundDown: Boolean? = null,
    ): String {
        return try {
            double2BigDecimalByScale(num, scale, stripTrailingZeros, roundDown).toPlainString()
                .toString()
        } catch (e: Exception) {
            DEF_GLOBAL_FORMAT
        }
    }

    /**
     * @param addPrefix 是否添加正负前缀，一般用于收益
     */
    @JvmOverloads
    @JvmStatic
    fun globalFormat(
        number: Any?,
        scale: Int? = 2,
        stripTrailingZeros: Boolean? = null,
        roundDown: Boolean? = null,
    ): String {
        val finalParam = when (number) {
            is String -> {
                number
            }

            is Number -> {
                number.toString()
            }

            else -> {
                DEF_GLOBAL_FORMAT
            }
        }
        return double2BigDecimalByScaleToString(
            finalParam,
            scale ?: 2,
            stripTrailingZeros,
            roundDown
        )
    }
}