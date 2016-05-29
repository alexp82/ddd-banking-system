package com.keba.scala.bank.exceptions

/**
  * Exception that indicates that an attempt was made to perform an
  * operation on a bank account that does not exist.
  * Created by alexp on 28/05/16.
  */
class BankAccountNotFound(message: String, cause: Throwable) extends Exception(message, cause) {
  def this() = {
    this(null, null)
  }

  def this(inMessage: String) = {
    this(inMessage, null)
  }

}
