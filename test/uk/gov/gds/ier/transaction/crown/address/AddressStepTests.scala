package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.model.{LastAddress, HasAddressOption}
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.DynamicGlobal

class AddressStepTests extends ControllerTestSuite {

  private def createGlobalConfigWith(availableForScotlandFlag: Boolean) = {
    val mockConfig = new Config {
      override def availableForScotland = availableForScotlandFlag
    }

    Some(new DynamicGlobal {
      override def bindings = { binder =>
        binder bind classOf[uk.gov.gds.ier.config.Config] toInstance mockConfig
      }
    })
  }

  behavior of "AddressStep.get"
  it should "display the page with last uk address (None value)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/address\"")
    }
  }

  it should "display the page with last address (false value)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/address")
          .withApplication(completeCrownApplication.copy(
            address = Some(LastAddress(
              hasAddress = Some(HasAddressOption.No),
              address = None
            ))
          ))
          .withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/address\"")
    }
  }

  it should "display the page with current uk address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/address")
          .withApplication(completeCrownApplication.copy(
          address = Some(LastAddress(
            hasAddress = Some(HasAddressOption.YesAndLivingThere),
            address = None
          ))
        ))
        .withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What is your UK address?"
      )
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/address\"")
    }
  }

  behavior of "AddressStep.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.uprn" -> "123456789",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
    }
  }

  it should "redirect exit page for Northern Ireland when a postcode starts with BT" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/address")
          .withIerSession()
          .withFormUrlEncodedBody(
          "address.postcode" -> "BT15EQ"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should behave like appWithScottishAddressWith(availableForScotlandFlag = true, andRedirectsToUrl = "/register-to-vote/crown/address/select")

  def appWithScottishAddressWith(availableForScotlandFlag: Boolean, andRedirectsToUrl: String) {
    it should s"redirect $andRedirectsToUrl for Scottish postcode with availableForScotlandFlag: $availableForScotlandFlag" in {
      running(FakeApplication(withGlobal = createGlobalConfigWith(availableForScotlandFlag = availableForScotlandFlag))) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/crown/address")
            .withIerSession()
            .withFormUrlEncodedBody(
              "address.postcode" -> "EH10 4AE"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some(andRedirectsToUrl))
      }
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/address/manual")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.manualAddress.lineTwo" -> "Moseley Road",
            "address.manualAddress.lineThree" -> "Hallow",
            "address.manualAddress.city" -> "Worcester",
            "address.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/address/manual")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
            "address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.manualAddress.lineTwo" -> "Moseley Road",
            "address.manualAddress.lineThree" -> "Hallow",
            "address.manualAddress.city" -> "Worcester",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      implicit def defaultAwaitTimeout = Timeout(10, TimeUnit.SECONDS)
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/address\"")

    }
  }

  behavior of "AddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/edit/address\"")

    }
  }

  behavior of "AddressStep.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.uprn" -> "123456789",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
    }
  }

  it should behave like editedAppWithScottishAddressWith(availableForScotlandFlag = true, andRedirectsToUrl = "/register-to-vote/crown/address/select")

  def editedAppWithScottishAddressWith(availableForScotlandFlag: Boolean, andRedirectsToUrl: String) {
    it should s"redirect $andRedirectsToUrl for Scottish postcode with availableForScotlandFlag: $availableForScotlandFlag" in {
      running(FakeApplication(withGlobal = createGlobalConfigWith(availableForScotlandFlag = availableForScotlandFlag))) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/crown/edit/address")
            .withIerSession()
            .withFormUrlEncodedBody(
              "address.postcode" -> "EH10 4AE"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some(andRedirectsToUrl))
      }
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/address/manual")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.manualAddress.lineTwo" -> "Moseley Road",
            "address.manualAddress.lineThree" -> "Hallow",
            "address.manualAddress.city" -> "Worcester",
            "address.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/address/manual")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
            "address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.manualAddress.lineTwo" -> "Moseley Road",
            "address.manualAddress.lineThree" -> "Hallow",
            "address.manualAddress.city" -> "Worcester",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/edit/address\"")

    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/statement")
          .withIerSession()
          .withApplication(completeCrownApplication.copy(address = None))
          .withFormUrlEncodedBody(
            "statement.crownServant" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/address/first"))
    }
  }
}
