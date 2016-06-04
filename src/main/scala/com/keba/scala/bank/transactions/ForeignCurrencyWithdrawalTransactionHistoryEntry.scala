package com.keba.scala.bank.transactions

import java.util.Date

import com.keba.scala.bank.money.Money

/**
  * Entry in transaction history of a bank account that describes
  * a withdrawal of foreign currency from a bank account.
  *
  * @author alexp
  */
class ForeignCurrencyWithdrawalTransactionHistoryEntry(override val timeStamp: Date,
                                                       override val bankAccountNumber: String,
                                                       val foreignCurrencyAmount: Money,
                                                       override val amount: Money,
                                                       val exchangeRate: BigDecimal)
  extends WithdrawalTransactionHistoryEntry(timeStamp, bankAccountNumber, amount) {
}

