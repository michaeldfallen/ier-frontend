package uk.gov.gds.ier.test

import play.api.test.TestBrowser
import org.joda.time.DateTime

trait BrowserHelpers {
  def goTo(url: String)(implicit browser: TestBrowser) = {
    browser.goTo(url)
  }
  def formText(selector: String, text: String)(implicit browser: TestBrowser) = {
    waitForSelector(selector)
    browser.$(selector).text(text)
  }
  def click(selector: String, index: Int = 0)(implicit browser: TestBrowser) = {
    waitForSelector(selector)
    browser.$(selector).get(index).click()
  }
  def waitForSelector(selector: String)(implicit browser: TestBrowser) = {
    waitFor(3, {
      browser => browser.$(selector).size() !=0
    })
  }
  def waitFor(timeoutSeconds: Int, predicate: TestBrowser => Boolean)(implicit browser: TestBrowser) = {
    val startTime = DateTime.now()
    while(!predicate(browser) && DateTime.now.isBefore(startTime.plusSeconds(timeoutSeconds))) {
      Thread.sleep(100)
    }
  }
}