package com.keba.scala.bank.services

import com.keba.scala.bank.repositories.TransactionHistoryRepository
import com.keba.scala.bank.transactions.TransactionHistoryEntry

/**
  * Allows for storage and retrieval of transaction history entries for
  * bank accounts.
  *
  * @author alexp
  */
class TransactionHistoryService {
  /**
    * Retrieves the transaction history for the bank account with
    * the supplied account number.
    *
    * @param inBankAccountNumber Account number of bank account which
    *                            transaction history to retrieve.
    * @return Chronologically ordered list containing the bank account's
    *         transaction history.
    */
  def retrieveTransactionHistory(inBankAccountNumber: String): List[TransactionHistoryEntry] = {
    TransactionHistoryRepository.read(inBankAccountNumber)
  }

  /**
    * Adds the supplied transaction history entry to the transaction
    * history entries of the bank account which account number is
    * specified in the entry.
    *
    * @param inTransactionHistoryEntry Transaction history entry to be
    *                                  added.
    */
  def addTransactionHistoryEntry(inTransactionHistoryEntry: TransactionHistoryEntry): Unit = {
    TransactionHistoryRepository.create(inTransactionHistoryEntry)
  }
}
