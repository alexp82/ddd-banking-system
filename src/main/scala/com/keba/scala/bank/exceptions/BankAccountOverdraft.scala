package com.keba.scala.bank.exceptions

/**
  * Exception that indicates than an attempt to overdraft a bank account was made.
  * Created by alexp on 28/05/16.
  */
class BankAccountOverdraft(message: String, cause: Throwable) extends Exception(message, cause) {
  def this() = {
    this(null, null)
  }

  def this(inMessage: String) = {
    this(inMessage, null)
  }

}
