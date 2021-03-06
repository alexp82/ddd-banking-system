package com.keba.scala.bank.services

import java.util.Currency

import com.keba.scala.bank.account.BankAccount
import com.keba.scala.bank.money.Money

/**
  * Trait defining the mix-in interface of the banking service.
  * This interface is to be used only when implementing traits
  * to be mixed in to the banking service, not as an interface
  * to be exposed to clients of the service.
  * Note that in order to be able to mixin non-business concerns,
  * not only do we need to include public methods in the interface
  * but also the methods with other visibilities.
  * Created by alexp on 31/05/16.
  */
trait BankingServiceMixinInterface {
  def registerBankAccount(inNewBankAccount: BankAccount): Unit

  def balance(inBankAccountNumber: String): Money

  def deposit(inBankAccountNumber: String, inAmount: Money): Unit

  def withdraw(inBankAccountNumber: String, inAmount: Money): Unit

  protected def validateBankAccountNumberFormat(inBankAccountNumber: String): Unit

  protected def retrieveBankAccount(inBankAccountNumber: String): Option[BankAccount]

  protected def updateBankAccount(inBankAccount: BankAccount): Unit

  protected def exchangeMoney(inAmount: Money, inToCurrency: Currency): Option[Money]
}
