package com.keba.scala.bank.services

import com.keba.scala.bank.account.BankAccount
import com.keba.scala.bank.exceptions.{BankAccountAlreadyExists, BankAccountNotFound, BankAccountOverdraft}
import com.keba.scala.bank.repositories.BankAccountRepository
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * Created by alexp on 28/05/16.
  */
@RunWith(classOf[JUnitRunner])
class BankingServiceTest extends FunSuite with BeforeAndAfterEach {
  /* Constant(s) */
  val BANK_ACCOUNT_NUMBER = "123.123"
  val BANK_ACCOUNT_NUMBER_BAD_FORMAT = "123-123"

  /* Field(s) */
  protected var bankingService: BankingService = null
  protected var newBankAccount: BankAccount = null

  override def beforeEach(): Unit = {
    bankingService = new BankingService
    BankAccountRepository.clear

    newBankAccount = new BankAccount
    newBankAccount.accountNumber = BANK_ACCOUNT_NUMBER
  }

  test("It should be possible to create a new bank account with an account number that has not previously been used") {
    bankingService.registerBankAccount(newBankAccount)
  }

  test("It should not be possible to create a bank account with an account number that previously has been used") {
    bankingService.registerBankAccount(newBankAccount)
    intercept[BankAccountAlreadyExists] {
      bankingService.registerBankAccount(newBankAccount)
    }
  }

  test("It should not be possible to create a bank account with an account number that is of illegal format") {
    val theBankAccountWithBadAccountNumber = new BankAccount
    theBankAccountWithBadAccountNumber.accountNumber = BANK_ACCOUNT_NUMBER_BAD_FORMAT
    intercept[IllegalArgumentException] {
      bankingService.registerBankAccount(theBankAccountWithBadAccountNumber)
    }
  }

  test("It should be possible to perform a balance inquiry on an existing bank account") {
    bankingService.registerBankAccount(newBankAccount)
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    val theExpectedBalance : BigDecimal = 0.0
    assert(theBalance == theExpectedBalance)
  }

  test("It should not be possible to perform a balance inquiry using an account number for which there is no bank account") {
    intercept[BankAccountNotFound] {
      val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    }
  }

  test("When money is deposited to a bank account, the account balance should increase accordingly") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, 100.3)
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    val theExpectedBalance : BigDecimal = 100.3
    assert(theBalance == theExpectedBalance)
  }

  test("It should not be possible to deposit money using an account number for which there is no bank account") {
    intercept[BankAccountNotFound] {
      bankingService.deposit(BANK_ACCOUNT_NUMBER, 1.0)
    }
  }

  test("When money is withdrawn from a bank account, the account balance should decrease accordingly") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, 100.3)
    bankingService.withdraw(BANK_ACCOUNT_NUMBER, 50.1)
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    val theExpectedBalance : BigDecimal = 50.2
    assert(theBalance == theExpectedBalance)
  }

  test("It should not be possible to withdraw money using an account number for which there is no bank account") {
    intercept[BankAccountNotFound] {
      bankingService.withdraw(BANK_ACCOUNT_NUMBER, 1.0)
    }
  }

  test("It should not be possible to overdraft a bank account") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, 100.1)
    intercept[BankAccountOverdraft] {
      bankingService.withdraw(BANK_ACCOUNT_NUMBER, 200.0)
    }
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    val theExpectedBalance : BigDecimal = 100.1
    assert(theBalance == theExpectedBalance)
  }
}
