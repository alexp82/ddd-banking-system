package com.keba.scala.bank.services

import com.keba.scala.bank.account.BankAccount
import com.keba.scala.bank.exceptions.{BankAccountAlreadyExists, BankAccountNotFound, BankAccountOverdraft}

/**
  * Provides services related to bank account banking.
  * Created by alexp on 28/05/16.
  */
class BankingService {

  private val ACCOUNTNUMBER_FORMAT_REGEXP = """[0-9]{3}\.[0-9]{3}""".r

  /**
    * Registers the supplied bank account with the service.
    * The account number in the bank account must not have been
    * previously used to register a bank account.
    *
    * @param inNewBankAccount Bank account to register with the service.
    * @throws BankAccountAlreadyExists If a bank account with the
    * same account number already exists.
    * @throws IllegalArgumentException If the supplied bank account's
    * account number is not in a valid format.
    */
  def registerBankAccount(inNewBankAccount : BankAccount) : Unit = {
    validateBankAccountNumberFormat(inNewBankAccount)

  }

  /**
    * Inquires the balance of the bank account with the supplied
    * account number.
    *
    * @param inBankAccountNumber Account number of bank account for
    * which to inquire for balance.
    * @return Balance of the bank account.
    * @throws IllegalArgumentException If the supplied account number
    * is not in a valid format.
    * @throws BankAccountNotFound If there is no corresponding bank
    * account for the supplied bank account number.
    */
  def balance(inBankAccountNumber : String) : BigDecimal = {
    /*
     * This is a query-type method, so it does not have
     * any side-effects, it is idempotent.
     */
    0.0
  }

  /**
    * Deposits the supplied amount of money to the bank account with
    * the supplied account number.
    *
    * @param inBankAccountNumber Account number of bank account to
    * which to deposit money.
    * @param inAmount Amount of money to deposit to the account.
    * @throws IllegalArgumentException If the supplied account number
    * is not in a valid format.
    * @throws BankAccountNotFound If there is no corresponding bank
    * account for the supplied bank account number.
    */
  def deposit(inBankAccountNumber : String, inAmount : BigDecimal) : Unit = {
    /*
     * This is a command-type method, so we do not return a
     * result.
     * The method has side-effects in that the balance of a
     * bank account is updated.
     */
  }

  /**
    * Withdraws the supplied amount of money from the bank account with
    * the supplied account number.
    *
    * @param inBankAccountNumber Account number of bank account from
    * which to withdraw money.
    * @param inAmount Amount of money to withdraw from the account.
    * @throws IllegalArgumentException If the supplied account number
    * is not in a valid format.
    * @throws BankAccountNotFound If there is no corresponding bank
    * account for the supplied bank account number.
    * @throws BankAccountOverdraft If an attempt was made to overdraft
    * the bank account.
    */
  def withdraw(inBankAccountNumber : String, inAmount : BigDecimal) : Unit = {
    /*
     * This is a command-type method, so we do not return a
     * result.
     * The method has side-effects in that the balance of a
     * bank account is updated.
     */
  }

  private def validateBankAccountNumberFormat(inBankAccount : BankAccount) : Unit = {

  }

}
