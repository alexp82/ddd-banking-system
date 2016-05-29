package com.keba.scala.bank.exceptions

/**
  * Created by alexp on 29/05/16.
  */
class NoExchangeRateRegistered(message: String, cause: Throwable) extends Exception(message, cause) {
  def this() = {
    this(null, null)
  }

  def this(inMessage: String) = {
    this(inMessage, null)
  }

}
