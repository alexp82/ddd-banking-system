package com.keba.scala.bank.account

import java.util.Currency

import com.keba.scala.bank.money.Money
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * Created by alexp on 28/05/16.
  */
@RunWith(classOf[JUnitRunner])
class BankAccountTest extends FunSuite with BeforeAndAfterEach {

  /* Constants */
  private val CURRENCY = Currency.getInstance("TWD")
  private val MONEY_100 = new Money(100.0, CURRENCY)
  private val MONEY_10 = new Money(10.0, CURRENCY)
  private val MONEY_0 = new Money(0.0, CURRENCY)

  /* Fields */
  var testedBankAccount: BankAccount = null

  override def beforeEach(): Unit = {
    testedBankAccount = new BankAccount(CURRENCY)
  }

  test("A new bank account should have a zero balance") {
    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_0)
  }

  test("When money is deposited to a bank account, the balance should increase accordingly") {
    testedBankAccount.deposit(MONEY_10)
    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_10)
  }

  test("It should not be possible to deposit a negative amount of money to a bank account") {
    val theDepositedAmount = new Money(-10.0, CURRENCY)
    intercept[IllegalArgumentException] {
      testedBankAccount.deposit(theDepositedAmount)
      /* Should not arrive here */
      assert(false)
    }

    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_0)
  }

  test("When money is withdrawn from a bank account, the balance should decrease accordingly") {
    val the90Money = new Money(90.0, CURRENCY);
    testedBankAccount.deposit(MONEY_100)
    testedBankAccount.withdraw(the90Money)
    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_10)
  }

  test("It should not be possible to withdraw a negative amount of money from a bank account") {
    val theNegativeMoney = new Money(-10.0, CURRENCY)
    testedBankAccount.deposit(MONEY_100)
    intercept[IllegalArgumentException] {
      testedBankAccount.withdraw(theNegativeMoney)
      /* Should not arrive here */
      assert(false)
    }

    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_100)
  }

  test("It should not be possible to overdraft a bank account") {
    val the30Money = new Money(30.0, CURRENCY)
    testedBankAccount.deposit(MONEY_10)

    intercept[AssertionError] {
      testedBankAccount.withdraw(the30Money)
      /* Should not arrive here */
      assert(false)
    }

    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_10)
  }

  test("It should be possible to clone a bank account") {
    val theAccountNumber = "123-456"
    val theBalance = MONEY_10
    testedBankAccount.accountNumber = theAccountNumber
    testedBankAccount.balance = theBalance
    val theBankAccountClone: BankAccount = testedBankAccount.clone
    assert(theBankAccountClone != null)
    assert(theBankAccountClone.accountNumber == theAccountNumber)
    assert(theBankAccountClone.balance == theBalance)
    assert(theBankAccountClone.currency == CURRENCY)
    assert(theBankAccountClone ne testedBankAccount)
  }

  test("A new bank account should have a currency") {
    val theTestedAccountCurrency = testedBankAccount.currency
    /* Any currency will do */
    assert(theTestedAccountCurrency != null)
  }
}
