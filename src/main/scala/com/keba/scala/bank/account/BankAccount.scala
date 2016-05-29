package com.keba.scala.bank.account

/**
  * Represents account in a bank
  * Created by alexp on 28/05/16.
  */
class BankAccount extends Cloneable {

  /* Constant(s) */

  /* Field(s) */
  var balance : BigDecimal = 0.0
  var accountNumber : String = null

  /**
    * Deposits supplied amount to the account
    * @param inAmount Amount to deposit. Must be greater than, or equal to, zero
    */
  def deposit(inAmount: BigDecimal) : Unit = {
    require(inAmount >= 0.0, "must deposit positive amounts")
    balance = balance + inAmount
  }

  /**
    * Withdraws supplied amount from the account
    * @param inAmount Amount to withdraw. Must be greater than, or equal to, zero
    */
  def withdraw(inAmount: BigDecimal) : Unit = {
    require(inAmount >= 0.0, "must withdraw positive amounts")
    assume(balance - inAmount >= 0.0, "overdrafts not allowed")
    balance = balance - inAmount
  }

  /**
    * Clones this bank account by performing a deep copy of it.
    *
    * @return Clone of this bank account.
    */
  override def clone() : BankAccount = {
    val theClone = new BankAccount()
    theClone.accountNumber = accountNumber
    theClone.balance = balance
    theClone
  }

}
