package com.keba.scala.bank.services

import java.util.Date

import com.keba.scala.bank.account.BankAccount
import com.keba.scala.bank.money.Money
import com.keba.scala.bank.repositories.{BankAccountRepository, TransactionHistoryRepository}
import com.keba.scala.bank.services.BankingTestConstants._
import com.keba.scala.bank.transactions._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * Tests the <code>TransactionHistoryService</code>.
  *
  * @author alexp
  */
@RunWith(classOf[JUnitRunner])
class TransactionHistoryServiceTest extends FunSuite with BeforeAndAfterEach {
  /* Field(s): */
  protected var bankingService: BankingService = null
  protected var newBankAccount: BankAccount = null
  protected var transactionHistoryService: TransactionHistoryService = null

  override def beforeEach() {
    transactionHistoryService = new TransactionHistoryService()
    bankingService = new BankingService() with TransactionHistoryRecorder
    /*
     * Need to set a reference to the transaction history service
     * required by the transaction history recorder trait.
     * The banking service has no knowledge of this service.
     */
    bankingService.asInstanceOf[TransactionHistoryRecorder].transactionHistoryService = transactionHistoryService
    val theExchangeRateService = createExchangeRateService()
    bankingService.exchangeRateService = theExchangeRateService
    /*
     * Need to clear the repositories, as to leave no lingering
     * bank accounts or transaction history entries from earlier tests.
     */
    BankAccountRepository.clear()
    TransactionHistoryRepository.clear()
    /* Create a new bank account that has not been registered. */
    newBankAccount = new BankAccount(CURRENCY_TWD)
    newBankAccount.accountNumber = BANK_ACCOUNT_NUMBER
  }

  private def createExchangeRateService(): ExchangeRateService = {
    /*
     * Create the exchange rate service and register some
     * exchange rates for known currencies.
     */
    val theExchangeRateService = new ExchangeRateService()
    theExchangeRateService.registerExchangeRate(CURRENCY_TWD, CURRENCY_SEK, EXCHANGERATE_TWD_SEK)
    theExchangeRateService.registerExchangeRate(CURRENCY_SEK, CURRENCY_TWD, EXCHANGERATE_SEK_TWD)
    theExchangeRateService
  }

  private def changeExchangeRates(): Unit = {
    /*
     * Change the exchange rates, in order to be able to verify
     * that the exchange rates saved in the transaction history
     * are the exchange rates at the time of the transactions and
     * not the current exchange rates.
     */
    bankingService.exchangeRateService.registerExchangeRate(CURRENCY_TWD, CURRENCY_SEK, EXCHANGERATE_TWD_SEK - 1.0)
    bankingService.exchangeRateService.registerExchangeRate(CURRENCY_SEK, CURRENCY_TWD, EXCHANGERATE_SEK_TWD - 1.0)
  }

  test("A new bank account should have a registration entry in its transaction history") {
    bankingService.registerBankAccount(newBankAccount)
    val theTransactionHistory: List[TransactionHistoryEntry] = transactionHistoryService.retrieveTransactionHistory(BANK_ACCOUNT_NUMBER)
    /*
     * Verify the number of entries in the transaction history.
     * Note that the registration of the bank account will always
     * be the first entry in the transaction history.
     */
    assert(theTransactionHistory.size == 1)
    /* Verify the type of entry in the transaction history. */
    val theTransactionHistoryEntry = theTransactionHistory.last
    theTransactionHistoryEntry match {
      case _: RegistrationTransactionHistoryEntry =>
      /* Expected result, do nothing. */
      case _ =>
        assert(false, "Transaction history entry type do not match")
    }
  }

  test("A balance inquiry should result in an entry in the transaction history of the bank account") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.balance(BANK_ACCOUNT_NUMBER)
    val theTransactionHistory: List[TransactionHistoryEntry] =
      transactionHistoryService.retrieveTransactionHistory(BANK_ACCOUNT_NUMBER)
    assert(theTransactionHistory.size == 2)
    /*
     * Verify the type of the most recent entry in the transaction
     * history, which should be a balance inquiry.
     */
    val theTransactionHistoryEntry = theTransactionHistory.last
    theTransactionHistoryEntry match {
      case _: BalanceInquiryTransactionHistoryEntry =>
      /* Expected result, do nothing. */
      case _ =>
        assert(false, "Transaction history entry type do not match")
    }
  }

  test("A deposit should result in an entry in the transaction history of " +
    "the bank account, with the amount deposited specified") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_200_TWD)
    val theTransactionHistory: List[TransactionHistoryEntry] =
      transactionHistoryService.retrieveTransactionHistory(BANK_ACCOUNT_NUMBER)
    assert(theTransactionHistory.size == 2)
    val theTransactionHistoryEntry = theTransactionHistory.last
    theTransactionHistoryEntry match {
      case theDepositEntry: DepositTransactionHistoryEntry =>
        assert(theDepositEntry.amount == MONEY_200_TWD)
      case _ =>
        assert(false, "Transaction history entry type do not match")
    }
  }

  test("A deposit of foreign currency should result in the following " +
    "additional data being visible in the corresponding entry in the " +
    "transaction history of the bank account: " +
    "Original currency, amount in original currency, " +
    "exchange rate at the time of the deposit") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_10_SEK)
    changeExchangeRates()
    val theTransactionHistory: List[TransactionHistoryEntry] =
      transactionHistoryService.retrieveTransactionHistory(BANK_ACCOUNT_NUMBER)
    assert(theTransactionHistory.size == 2)
    val theTransactionHistoryEntry = theTransactionHistory.last
    theTransactionHistoryEntry match {
      case theDepositEntry: ForeignCurrencyDepositTransactionHistoryEntry =>
        assert(theDepositEntry.amount == MONEY_40_TWD)
        assert(theDepositEntry.foreignCurrencyAmount == MONEY_10_SEK)
        assert(theDepositEntry.exchangeRate == EXCHANGERATE_SEK_TWD)
      case _ =>
        assert(false, "Transaction history entry type do not match")
    }
  }

  test("A withdrawal should result in an entry in the transaction history " +
    "of the bank account, with the amount withdrawn specified") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_200_TWD)
    bankingService.withdraw(BANK_ACCOUNT_NUMBER, MONEY_160_TWD)
    val theTransactionHistory: List[TransactionHistoryEntry] =
      transactionHistoryService.retrieveTransactionHistory(BANK_ACCOUNT_NUMBER)
    assert(theTransactionHistory.size == 3)
    val theTransactionHistoryEntry = theTransactionHistory.last
    theTransactionHistoryEntry match {
      case theWithdrawalEntry: WithdrawalTransactionHistoryEntry =>
        assert(theWithdrawalEntry.amount == MONEY_160_TWD)
      case _ =>
        assert(false, "Transaction history entry type do not match")
    }
  }

  test("A withdrawal of foreign currency should result in the following " +
    "additional data being visible in the corresponding entry in the " +
    "transaction history of the bank account: " +
    "Original currency, amount in original currency, " +
    "exchange rate at the time of the withdrawal") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_200_TWD)
    bankingService.withdraw(BANK_ACCOUNT_NUMBER, MONEY_10_SEK)
    changeExchangeRates()
    val theTransactionHistory: List[TransactionHistoryEntry] = transactionHistoryService.retrieveTransactionHistory(BANK_ACCOUNT_NUMBER)
    assert(theTransactionHistory.size == 3)
    val theTransactionHistoryEntry = theTransactionHistory.last
    theTransactionHistoryEntry match {
      case theWithdrawalEntry: ForeignCurrencyWithdrawalTransactionHistoryEntry
      =>
        assert(theWithdrawalEntry.amount == MONEY_40_TWD)
        assert(theWithdrawalEntry.foreignCurrencyAmount == MONEY_10_SEK)
        assert(theWithdrawalEntry.exchangeRate == EXCHANGERATE_SEK_TWD)
      case _ =>
        assert(false, "Transaction history entry type do not match")
    }
  }

  test("All entries in the bank account transaction history should have a date" +
    " and time, have a bank account number and should be ordered in " +
    "reverse chronological order") {
    bankingService.registerBankAccount(newBankAccount)
    /* Local currency deposit. **/
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_200_TWD)
    /* Foreign currency deposit. */
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_10_SEK)
    /* Local currency withdrawal. */
    bankingService.withdraw(BANK_ACCOUNT_NUMBER, MONEY_50_1_TWD)
    /* Foreign currency withdrawal. */
    bankingService.withdraw(BANK_ACCOUNT_NUMBER, MONEY_10_SEK)
    /* Balance inquiry. */
    bankingService.balance(BANK_ACCOUNT_NUMBER)
    val theTransactionHistory: List[TransactionHistoryEntry] =
      transactionHistoryService.retrieveTransactionHistory(BANK_ACCOUNT_NUMBER)
    assert(theTransactionHistory.size == 6)
    val theCurrentDateTime = new Date()
    var thePreviousEntry: TransactionHistoryEntry = null
    /*
     * Closure that compares two dates. Returns true if the first date
     * is not after the second date. That is, the first date is earlier
     * or the same as the second date.
     */
    val isNotAfter = (theFirstDate: Date, theSecondDate: Date) =>
      !theFirstDate.after(theSecondDate)
    theTransactionHistory.foreach {
      theEntry =>
        /*
         * The time of this transaction history entry should be
         * before, or same, as a point in time after all transactions.
         */
        assert(isNotAfter(theEntry.timeStamp, theCurrentDateTime))
        /*
         * This transaction history entry should contain the account
         * number of the bank account involved in the transaction.
         */
        assert(theEntry.bankAccountNumber == BANK_ACCOUNT_NUMBER)
        if (thePreviousEntry != null) {
          /*
           * The previous entry should have happened later, or at
           * the same time, as this entry.
           */
          assert(isNotAfter(thePreviousEntry.timeStamp, theEntry.timeStamp))
        }
        thePreviousEntry = theEntry
    }
  }

  test("A request to the banking service that results in an exception " +
    "should not generate an entry in the transaction history") {
    bankingService.registerBankAccount(newBankAccount)
    /* Send request to banking service that will cause exception. */
    try {
      val theNegativeMoney = new Money(-1.0, CURRENCY_TWD)
      bankingService.deposit(BANK_ACCOUNT_NUMBER, theNegativeMoney)
    } catch {
      case _: Throwable =>
      /* Do nothing. */
    }
    val theTransactionHistory: List[TransactionHistoryEntry] =
      transactionHistoryService.retrieveTransactionHistory(BANK_ACCOUNT_NUMBER)
    assert(theTransactionHistory.size == 1)
  }

}