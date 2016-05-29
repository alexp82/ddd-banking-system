package com.keba.scala.bank.account

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * Created by alexp on 28/05/16.
  */
@RunWith(classOf[JUnitRunner])
class BankAccountTest extends FunSuite with BeforeAndAfterEach {
  var testedBankAccount: BankAccount = null

  override def beforeEach(): Unit = {
    testedBankAccount = new BankAccount
  }

  test("A new bank account should have a zero balance") {
    val theBalance = testedBankAccount.balance
    assert(theBalance == 0.0)
  }

  test("When money is deposited to a bank account, the balance should increase accordingly") {
    testedBankAccount.deposit(10.0)
    val theBalance = testedBankAccount.balance
    assert(theBalance == 10.0)
  }

  test("It should not be possible to deposit a negative amount of money to a bank account") {
    intercept[IllegalArgumentException] {
      testedBankAccount.deposit(-10.0)
      /* Should not arrive here */
      assert(false)
    }

    val theBalance = testedBankAccount.balance
    assert(theBalance == 0.0)
  }

  test("When money is withdrawn from a bank account, the balance should decrease accordingly") {
    testedBankAccount.deposit(100.0)
    testedBankAccount.withdraw(90.0)
    val theBalance = testedBankAccount.balance
    assert(theBalance == 10.0)
  }

  test("It should not be possible to withdraw a negative amount of money from a bank account") {
    testedBankAccount.deposit(100.0)
    intercept[IllegalArgumentException] {
      testedBankAccount.withdraw(-10.0)
      /* Should not arrive here */
      assert(false)
    }

    val theBalance = testedBankAccount.balance
    assert(theBalance == 100.0)
  }

  test("It should not be possible to overdraft a bank account") {
    testedBankAccount.deposit(20.0)

    intercept[AssertionError] {
      testedBankAccount.withdraw(30.0)
      /* Should not arrive here */
      assert(false)
    }

    val theBalance = testedBankAccount.balance
    assert(theBalance == 20.0)
  }

  test("It should be possible to clone a bank account") {
    val theAccountNumber = "123-456"
    val theBalance = 876.54
    testedBankAccount.accountNumber = theAccountNumber
    testedBankAccount.balance = theBalance
    val theBankAccountClone: BankAccount = testedBankAccount.clone
    assert(theBankAccountClone != null)
    assert(theBankAccountClone.accountNumber == theAccountNumber)
    assert(theBankAccountClone.balance == theBalance)
    assert(theBankAccountClone ne testedBankAccount)
  }
}
