package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.model.{HasAddressOption, LastAddress}
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
  it should "display the page with the right title if has uk address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/address").withIerSession()
        .withApplication(completeForcesApplication.copy(
          address = Some(LastAddress(
            hasAddress = Some(HasAddressOption.YesAndLivingThere),
            address = None
          ))
        ))
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What is your UK address?"
      )
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/address\"")
    }
  }

  it should "display the page with the right title if has no uk address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/address").withIerSession()
        .withApplication(completeForcesApplication.copy(
          address = Some(LastAddress(
            hasAddress = Some(HasAddressOption.No),
            address = None
          ))
        ))
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/address\"")
    }
  }

  behavior of "AddressStep.post"
  it should "redirect to the previous address step if bind successfully and 'true' to the 'has uk address'" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address/select")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(
              address = Some(LastAddress(
                hasAddress = Some(HasAddressOption.YesAndLivingThere),
                address = None
              )),
              previousAddress = None
          ))
          .withFormUrlEncodedBody(
            "address.address.uprn" -> "123456789",
            "address.address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/previous-address"))
    }
  }

  it should "redirect to the previous address step if bind successfully and 'false' to the 'has uk address'" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address/select")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(
              address = Some(LastAddress(
                hasAddress = Some(HasAddressOption.No),
                address = None
              )),
              nationality = None
          ))
          .withFormUrlEncodedBody(
            "address.address.uprn" -> "123456789",
            "address.address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/nationality"))
    }
  }

  it should "redirect to the previous address step with a manual address if 'true' to has uk address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address/manual")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(
            address = Some(LastAddress(
              hasAddress = Some(HasAddressOption.YesAndLivingThere),
              address = None
            )),
            previousAddress = None
          ))
          .withFormUrlEncodedBody(
            "address.address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.address.manualAddress.lineTwo" -> "Moseley Road",
            "address.address.manualAddress.lineThree" -> "Hallow",
            "address.address.manualAddress.city" -> "Worcester",
            "address.address.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/previous-address"))
    }
  }

  it should "redirect to the previous address step with a manual address if 'false' to has uk address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address/manual")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(
            address = Some(LastAddress(
              hasAddress = Some(HasAddressOption.No),
              address = None
            )),
            nationality = None
          ))
          .withFormUrlEncodedBody(
            "address.address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.address.manualAddress.lineTwo" -> "Moseley Road",
            "address.address.manualAddress.lineThree" -> "Hallow",
            "address.address.manualAddress.city" -> "Worcester",
            "address.address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/nationality"))
    }
  }

  it should "redirect exit page for Northern Ireland when a postcode starts with BT" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.address.postcode" -> "BT15EQ"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should behave like appWithScottishAddressWith(availableForScotlandFlag = true, andRedirectsToUrl = "/register-to-vote/forces/address/select")

  def appWithScottishAddressWith(availableForScotlandFlag: Boolean, andRedirectsToUrl: String) {
    it should s"redirect $andRedirectsToUrl for Scottish postcode with availableForScotlandFlag: $availableForScotlandFlag" in {
      running(FakeApplication(withGlobal = createGlobalConfigWith(availableForScotlandFlag = availableForScotlandFlag))) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/forces/address")
            .withIerSession()
            .withFormUrlEncodedBody(
              "address.address.postcode" -> "EH10 4AE"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some(andRedirectsToUrl))
      }
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address/manual")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "address.address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.address.manualAddress.lineTwo" -> "Moseley Road",
            "address.address.manualAddress.lineThree" -> "Hallow",
            "address.address.manualAddress.city" -> "Worcester",
            "address.address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/address\"")

    }
  }

  behavior of "AddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/edit/address\"")

    }
  }

  behavior of "AddressStep.editPost"
  it should "redirect to the previous step if bind successfully and answer 'true' to the 'has uk address'" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address/select")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(
            address = Some(LastAddress(
              hasAddress = Some(HasAddressOption.YesAndLivingThere),
              address = None
            )),
            previousAddress = None
          ))
          .withFormUrlEncodedBody(
            "address.address.uprn" -> "123456789",
            "address.address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/previous-address"))
    }
  }

  it should behave like editedAppWithScottishAddressWith(availableForScotlandFlag = true, andRedirectsToUrl = "/register-to-vote/forces/address/select")

  def editedAppWithScottishAddressWith(availableForScotlandFlag: Boolean, andRedirectsToUrl: String) {
    it should s"redirect $andRedirectsToUrl for Scottish postcode with availableForScotlandFlag: $availableForScotlandFlag" in {
      running(FakeApplication(withGlobal = createGlobalConfigWith(availableForScotlandFlag = availableForScotlandFlag))) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/forces/edit/address")
            .withIerSession()
            .withFormUrlEncodedBody(
              "address.address.postcode" -> "EH10 4AE"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some(andRedirectsToUrl))
      }
    }
  }

  it should "redirect to the nationality step if bind successfully and answer 'false' to the 'has uk address'" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address/select")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(
            address = Some(LastAddress(
              hasAddress = Some(HasAddressOption.No),
              address = None
            )),
            nationality = None
          ))
          .withFormUrlEncodedBody(
            "address.address.uprn" -> "123456789",
            "address.address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/nationality"))
    }
  }

  it should "redirect to the previous address step with a manual address if answer 'true' to 'has uk address'" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address/manual")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(
            address = Some(LastAddress(
              hasAddress = Some(HasAddressOption.YesAndLivingThere),
              address = None
            )),
            previousAddress = None
          ))
          .withFormUrlEncodedBody(
            "address.address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.address.manualAddress.lineTwo" -> "Moseley Road",
            "address.address.manualAddress.lineThree" -> "Hallow",
            "address.address.manualAddress.city" -> "Worcester",
            "address.address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/previous-address"))
    }
  }

  it should "redirect to the nationality step with a manual address if answer 'false' to 'has uk address'" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address/manual")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(
            address = Some(LastAddress(
              hasAddress = Some(HasAddressOption.No),
              address = None
            )),
            nationality = None
          ))
          .withFormUrlEncodedBody(
            "address.address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.address.manualAddress.lineTwo" -> "Moseley Road",
            "address.address.manualAddress.lineThree" -> "Hallow",
            "address.address.manualAddress.city" -> "Worcester",
            "address.address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/nationality"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address/manual")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "address.address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.address.manualAddress.lineTwo" -> "Moseley Road",
            "address.address.manualAddress.lineThree" -> "Hallow",
            "address.address.manualAddress.city" -> "Worcester",
            "address.address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  // this url is unreachable from the confirmation page
  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/edit/address\"")

    }
  }

  it should "display any errors on unsuccessful bind (select)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address/select").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("Please select your address")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/edit/address/select\"")

    }
  }

  it should "display any errors on unsuccessful bind (manual)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address/manual").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/edit/address/manual\"")

    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/statement")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(address = None))
          .withFormUrlEncodedBody(
            "statement.forcesMember" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/address/first"))
    }
  }
}
