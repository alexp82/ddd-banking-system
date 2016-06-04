package com.keba.scala.bank.transactions

import java.util.Date

/**
  * Entry in transaction history of a bank account that describes
  * the registration of the bank account.
  *
  * @author alexp
  */
class RegistrationTransactionHistoryEntry(override val timeStamp: Date,
                                          override val bankAccountNumber: String)
  extends TransactionHistoryEntry(timeStamp, bankAccountNumber) {
}
