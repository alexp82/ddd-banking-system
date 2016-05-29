package com.keba.scala.bank.exceptions

/**
  * Exception that indicates that an attempt was made to create a
  * bank account with an account number for which there already exist a bank account.
  * Created by alexp on 28/05/16.
  */
class BankAccountAlreadyExists(message: String, cause: Throwable) extends Exception(message, cause) {
  def this() = {
    this(null, null)
  }

  def this(inMessage: String) = {
    this(inMessage, null)
  }
}
