package com.keba.scala.bank.transactions

import java.util.Date

import com.keba.scala.bank.money.Money

/**
  * Entry in transaction history of a bank account that describes
  * a deposit in the bank account's currency, that is non-foreign currency,
  * to the bank account
  *
  * @author alexp
  */
class DepositTransactionHistoryEntry(override val timeStamp: Date,
                                     override val bankAccountNumber: String, val amount: Money)
  extends TransactionHistoryEntry(timeStamp, bankAccountNumber) {
}
