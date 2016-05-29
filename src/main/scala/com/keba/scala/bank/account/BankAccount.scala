package com.keba.scala.bank.account

import java.util.Currency

import com.keba.scala.bank.money.Money

/**
  * Represents account in a bank
  * Created by alexp on 28/05/16.
  */
class BankAccount(val currency: Currency) extends Cloneable {
  /* Constructor code: */
  require(currency != null)
  /* Constant(s) */

  /* Field(s) */
  var balance: Money = new Money(0.0, currency)
  var accountNumber : String = null

  /**
    * Deposits supplied amount to the account
    *
    * @param inAmount Amount to deposit. Must be greater than, or equal to, zero
    */
  def deposit(inAmount: Money): Unit = {
    require(inAmount.amount >= 0.0, "must deposit positive amounts")
    require(inAmount.currency == currency, "must deposit same currency")
    balance = balance.add(inAmount)
  }

  /**
    * Withdraws supplied amount from the account
    *
    * @param inAmount Amount to withdraw. Must be greater than, or equal to, zero
    */
  def withdraw(inAmount: Money): Unit = {
    require(inAmount.amount >= 0.0, "must withdraw positive amounts")
    require(inAmount.currency == currency, "must deposit same currency")
    assume(balance.amount - inAmount.amount >= 0.0, "overdrafts not allowed")
    balance = balance.subtract(inAmount)
  }

  /**
    * Clones this bank account by performing a deep copy of it.
    *
    * @return Clone of this bank account.
    */
  override def clone() : BankAccount = {
    val theClone = new BankAccount(currency)
    theClone.accountNumber = accountNumber
    theClone.balance = balance
    theClone
  }

}
