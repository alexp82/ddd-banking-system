package com.keba.scala.bank.factories

import com.keba.scala.bank.account.BankAccount
import com.keba.scala.bank.repositories.BankAccountRepository
import com.keba.scala.bank.services.BankingServiceTest
import com.keba.scala.bank.services.BankingTestConstants._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
  * Tests an instance of the banking service, as created by
  * the <code>BankingServiceFactory</code> factory.
  *
  * @author alexp
  */
@RunWith(classOf[JUnitRunner])
class BankingServiceFactoryTest extends BankingServiceTest {

  override def beforeEach() {
    bankingService = BankingServiceFactory.createInstance()

    /*
     * Register some known exchange rates with the exchange rate
     * service of the banking service.
     */
    val theExchangeRateService = bankingService.exchangeRateService
    theExchangeRateService.registerExchangeRate(CURRENCY_TWD, CURRENCY_SEK, EXCHANGERATE_TWD_SEK)
    theExchangeRateService.registerExchangeRate(CURRENCY_SEK, CURRENCY_TWD, EXCHANGERATE_SEK_TWD)
    /*
     * Need to clear the repository, as to leave no lingering
     * bank accounts from earlier tests.
     */
    BankAccountRepository.clear()
    /* Create a new bank account that has not been registered. */
    newBankAccount = new BankAccount(CURRENCY_TWD)
    newBankAccount.accountNumber = BANK_ACCOUNT_NUMBER
  }

}
