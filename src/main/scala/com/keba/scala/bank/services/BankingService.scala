package com.keba.scala.bank.services

import java.util.Currency

import com.keba.scala.bank.account.BankAccount
import com.keba.scala.bank.exceptions.{BankAccountAlreadyExists, BankAccountNotFound, BankAccountOverdraft, NoExchangeRateRegistered}
import com.keba.scala.bank.money.Money
import com.keba.scala.bank.repositories.BankAccountRepository

/**
  * Provides services related to bank account banking.
  * Created by alexp on 28/05/16.
  */
class BankingService {
  /* Constants */
  private val ACCOUNTNUMBER_FORMAT_REGEXP =
    """[0-9]{3}\.[0-9]{3}""".r
  /* Fields */
  var exchangeRateService: ExchangeRateService = null


  /**
    * Registers the supplied bank account with the service.
    * The account number in the bank account must not have been
    * previously used to register a bank account.
    *
    * @param inNewBankAccount Bank account to register with the service.
    * @throws BankAccountAlreadyExists If a bank account with the
    *                                  same account number already exists.
    * @throws IllegalArgumentException If the supplied bank account's
    *                                  account number is not in a valid format.
    */
  def registerBankAccount(inNewBankAccount: BankAccount): Unit = {
    validateBankAccountNumberFormat(inNewBankAccount)
    /* Attempt to create the new bank account in the repository */
    try {
      BankAccountRepository.create(inNewBankAccount)
    } catch {
      case _: AssertionError =>
        throw new BankAccountAlreadyExists("Failed to register new bank account. An account with number " + inNewBankAccount.accountNumber + " has already been registered")
      case theException: IllegalArgumentException =>
        /* Just propagate the exception */
        throw theException
      case theException: Throwable =>
        throw new Error("Failed to register new bank account", theException)
    }
  }

  private def validateBankAccountNumberFormat(inBankAccount: BankAccount): Unit = {
    /*
    Make sure that the account number is the proper format
    If the format is invalid, throws an exception
     */
    inBankAccount.accountNumber match {
      case ACCOUNTNUMBER_FORMAT_REGEXP() =>
      /* Good account number, do nothing */
      case _ =>
        /* Bad account number, throw exception */
        throw new IllegalArgumentException("Failed to register new bank account. Illegal account number format: " + inBankAccount.accountNumber)
    }
  }

  /**
    * Inquires the balance of the bank account with the supplied
    * account number.
    *
    * @param inBankAccountNumber Account number of bank account for
    *                            which to inquire for balance.
    * @return Balance of the bank account.
    * @throws IllegalArgumentException If the supplied account number
    *                                  is not in a valid format.
    * @throws BankAccountNotFound      If there is no corresponding bank
    *                                  account for the supplied bank account number.
    */
  def balance(inBankAccountNumber: String): Money = {
    /*
     * This is a query-type method, so it does not have
     * any side-effects, it is idempotent.
     */
    /* Retrieve bank account with supplied account number */
    val theBankAccountOption = BankAccountRepository.findBankAccountWithAccountNumber(inBankAccountNumber)
    checkBankAccountFound(theBankAccountOption, "Bank account with account number " + inBankAccountNumber + " not found when performing balance query")
    /* Arriving here, we know that we have a bank account and can thus obtain it's balance */
    theBankAccountOption.get.balance
  }

  /**
    * Deposits the supplied amount of money to the bank account with
    * the supplied account number.
    *
    * @param inBankAccountNumber Account number of bank account to
    *                            which to deposit money.
    * @param inAmount            Amount of money to deposit to the account.
    * @throws IllegalArgumentException If the supplied account number
    *                                  is not in a valid format.
    * @throws BankAccountNotFound      If there is no corresponding bank
    *                                  account for the supplied bank account number.
    */
  def deposit(inBankAccountNumber: String, inAmount: Money): Unit = {
    /*
     * This is a command-type method, so we do not return a
     * result.
     * The method has side-effects in that the balance of a
     * bank account is updated.
     */
    /* Retrieve bank account with supplied account number */
    val theBankAccountOption = BankAccountRepository.findBankAccountWithAccountNumber(inBankAccountNumber)
    checkBankAccountFound(theBankAccountOption, "Bank account with account number " + inBankAccountNumber + " not found when performing balance query")
    /*
     * Exchange the currency to deposit to the currency of
     * the bank account. The exchange rate service will return
     * the supplied amount if it already is of the desired currency,
     * so it is safe to always perform the exchange operation.
     */
    val theBankAccount = theBankAccountOption.get
    val theExchangedAmountToDepositOption = exchangeRateService.exchange(inAmount, theBankAccount.currency)
    hasCurrencyExchangeSucceeded(theExchangedAmountToDepositOption, inAmount.currency, theBankAccount.currency)
    /*
     * Arriving here, we know that we have a bank account,
     * money to deposit in the bank account's currency and can
     * now perform the deposit and update the bank account.
     */
    theBankAccount.deposit(theExchangedAmountToDepositOption.get)
    BankAccountRepository.update(theBankAccount)
  }

  /**
    * Withdraws the supplied amount of money from the bank account with
    * the supplied account number.
    *
    * @param inBankAccountNumber Account number of bank account from
    *                            which to withdraw money.
    * @param inAmount            Amount of money to withdraw from the account.
    * @throws IllegalArgumentException If the supplied account number
    *                                  is not in a valid format.
    * @throws BankAccountNotFound      If there is no corresponding bank
    *                                  account for the supplied bank account number.
    * @throws BankAccountOverdraft     If an attempt was made to overdraft
    *                                  the bank account.
    */
  def withdraw(inBankAccountNumber: String, inAmount: Money): Unit = {
    /*
     * This is a command-type method, so we do not return a
     * result.
     * The method has side-effects in that the balance of a
     * bank account is updated.
     */
    /* Retrieve bank account with supplied account number */
    val theBankAccountOption = BankAccountRepository.findBankAccountWithAccountNumber(inBankAccountNumber)
    checkBankAccountFound(theBankAccountOption, "Bank account with account number " + inBankAccountNumber + " not found when performing balance query")
    /*
     * Exchange the currency to withdraw to the currency of
     * the bank account. The exchange rate service will do nothing if
     * the supplied amount is of the desired currency, so it is
     * safe to always perform the exchange operation.
     */
    val theBankAccount = theBankAccountOption.get
    val theExchangedAmountToWithdrawOption = exchangeRateService.exchange(inAmount, theBankAccount.currency)
    hasCurrencyExchangeSucceeded(theExchangedAmountToWithdrawOption, inAmount.currency, theBankAccount.currency)
    /* Arriving here, we know that we have a bank account and can now perform the withdraw and update the bank account */
    try {
      theBankAccount.withdraw(inAmount)
    } catch {
      case _: AssertionError =>
        throw new BankAccountOverdraft("Bank account: " + inBankAccountNumber + ", amount: " + inAmount)
      case theException: IllegalArgumentException =>
        /* Just propagate the exception */
        throw theException
      case theException: Throwable =>
        throw new Error("Failed to register new bank account.", theException)
    }
    BankAccountRepository.update(theBankAccount)
  }

  private def checkBankAccountFound(inBankAccountOption: Option[BankAccount], inExceptionMessage: String): Unit = {
    inBankAccountOption match {
      case None =>
        throw new BankAccountNotFound(inExceptionMessage)
      case _ =>
      /* Got a Some, do nothing */
    }
  }

  /**
    * Checks the supplied money option (being the result of a money
    * exchange). If it does not contain money, this is taken as an
    * indication that a money exchange has failed due to a missing
    * exchange rate and an exception is thrown.
    */
  private def hasCurrencyExchangeSucceeded(inExchangedMoneyOption: Option[Money],
                                           inFromCurrency: Currency, inToCurrency: Currency): Unit = {
    inExchangedMoneyOption match {
      case None =>
        val theErrorMsg = "failed currency exchange from " + inFromCurrency.getDisplayName() + " to " +
          inToCurrency.getDisplayName()
        throw new NoExchangeRateRegistered(theErrorMsg)
      case _ =>
      /* Exchange succeeded, just continue. */
    }
  }

}
