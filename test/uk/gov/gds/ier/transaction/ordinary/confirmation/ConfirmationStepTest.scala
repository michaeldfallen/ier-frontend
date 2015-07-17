package uk.gov.gds.ier.transaction.ordinary.confirmation

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.complete.CompleteCookie
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails
import uk.gov.gds.ier.controller.MockConfig
import uk.gov.gds.ier.security.{Base64EncodingService, EncryptionService}
import org.joda.time.LocalDate
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants

class ConfirmationStepTest extends ControllerTestSuite {

  val config = new MockConfig
  implicit val serialiser = jsonSerialiser
  implicit val encryptionService = new EncryptionService (new Base64EncodingService, config)

  behavior of "ConfirmationStep.post submit application and set Refnum and LocalAuthority for the next step"
    running(FakeApplication()) {
      val Some(resultFuture) = route(
        FakeRequest(POST, "/register-to-vote/confirmation")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(address = Some(PartialAddress(
            addressLine = Some("1 The Cottages, Moseley Road, Hallow, Worcestershire"),
            uprn = Some("100120595384"),
            postcode = "WR2 6NJ",
            gssCode = Some("E07000235"),
            manualAddress = None
          ))
        ))
      )

      it should "redirect to Complete page" in {
        status(resultFuture) should be(SEE_OTHER)
        redirectLocation(resultFuture) should be(Some("/register-to-vote/complete"))
      }

      val allCookies = cookies(resultFuture)

      it should "delete application inprogress data, delete main cookie but not session" in {
        allCookies.get(sessionPayloadKey) should not be (None)
        allCookies.get(sessionPayloadKey).get.value should be("")
      }

      it should "add new cookie with confirmation data for Complete page" in {
        allCookies.get(completeCookieKey) should not be (None)
        allCookies.get(completeCookieKey).get.value.trim should not be empty
      }

      "content of Confirmation cookie" should "contain refnum and local ERO authority details" in {
        val result = getCompleteCookie(allCookies)
        result should not be (None)
        result.get.refNum.trim should not be ("")
        result.get.hasOtherAddress should be(false)
        result.get.backToStartUrl should be("/register-to-vote")
        result.get.authority should be(Some(EroAuthorityDetails(
          name = "Malvern Hills (test)",
          urls = List(
            "http://www.malvernhills.gov.uk/",
            "http://www.malvernhills.gov.uk/cms/council-and-democracy/elections.aspx"
          ),
          email = Some("worcestershirehub@malvernhills.gov.uk.test"),
          addressLine1 = Some("Council House"),
          addressLine2 = Some("Avenue Road"),
          addressLine3 = Some("Malvern"),
          addressLine4 = Some(""),
          postcode = Some("WR14 3AF"),
          phone = Some("01684 862151")
        )))
      }
    }


  behavior of "showEmailConfirmation flag"
    it should "submit application and set show email message flag to false for no email addresses" in runningApp {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/confirmation")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(
            postalVote = Some(PostalVote(
              postalVoteOption = Some(PostalVoteOption.NoAndVoteInPerson),
              deliveryMethod = None
            )),
            contact = Some(Contact(
              post = true,
              email = None,
              phone = None
            ))
          ))
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/complete"))
      val allCookies = cookies(result)
      val completeStepData = getCompleteCookie(allCookies)
      completeStepData should not be(None)
      completeStepData.get.showEmailConfirmation should be(false)
    }

    it should "submit application and set show email message flag to true if the contact email address is present" in runningApp {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/confirmation")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(
          postalVote = Some(PostalVote(
            postalVoteOption = Some(PostalVoteOption.NoAndVoteInPerson),
            deliveryMethod = None
          )),
          contact = Some(Contact(
            post = false,
            email = Some(ContactDetail(
              contactMe = true,
              detail = Some("test@email.com")
            )),
            phone = None
          ))
        ))
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/complete"))
      val allCookies = cookies(result)
      val completeStepData = getCompleteCookie(allCookies)
      completeStepData should not be(None)
      completeStepData.get.showEmailConfirmation should be(true)
    }

    it should "submit application and set show email message flag to true if the postal vote email is present" in runningApp {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/confirmation")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(
          postalVote = Some(PostalVote(
            postalVoteOption = Some(PostalVoteOption.Yes),
            deliveryMethod = Some(PostalVoteDeliveryMethod(
              deliveryMethod = Some("email"),
              emailAddress = Some("test@email.com")
            ))
          )),
          contact = Some(Contact(
            post = true,
            email = None,
            phone = None
          ))
        ))
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/complete"))
      val allCookies = cookies(result)
      val completeStepData = getCompleteCookie(allCookies)
      completeStepData should not be(None)
      completeStepData.get.showEmailConfirmation should be(true)
    }

    it should "submit application and set show email message flag to true if the postal vote and contact email are present" in runningApp {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/confirmation")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(
          postalVote = Some(PostalVote(
            postalVoteOption = Some(PostalVoteOption.Yes),
            deliveryMethod = Some(PostalVoteDeliveryMethod(
              deliveryMethod = Some("email"),
              emailAddress = Some("test@email.com")
            ))
          )),
          contact = Some(Contact(
            post = false,
            email = Some(ContactDetail(
              contactMe = true,
              detail = Some("test@email.com")
            )),
            phone = None
          ))
        ))
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/complete"))
      val allCookies = cookies(result)
      val completeStepData = getCompleteCookie(allCookies)
      completeStepData should not be(None)
      completeStepData.get.showEmailConfirmation should be(true)
    }

  behavior of "showBirthdayBunting flag"
  it should "submit application and set show bunting flag to true if its applicants birthday" in {
    applicationWithDateOfBirth(LocalDate.now, expectedBuntingFlagValue = true)
    applicationWithDateOfBirth(LocalDate.now.minusYears(1), expectedBuntingFlagValue = true)
    applicationWithDateOfBirth(LocalDate.now.minusYears(30), expectedBuntingFlagValue = true)
    applicationWithDateOfBirth(LocalDate.now.minusYears(60), expectedBuntingFlagValue = true)
  }

  it should "submit application and set show bunting flag to false if its not applicants birthday" in {
    applicationWithDateOfBirth(LocalDate.now.minusDays(1), expectedBuntingFlagValue = false)
    applicationWithDateOfBirth(LocalDate.now.minusDays(2), expectedBuntingFlagValue = false)
    applicationWithDateOfBirth(LocalDate.now.minusMonths(1), expectedBuntingFlagValue = false)
    applicationWithDateOfBirth(LocalDate.now.minusMonths(2), expectedBuntingFlagValue = false)
    applicationWithDateOfBirth(LocalDate.now.minusMonths(11).minusYears(80), expectedBuntingFlagValue = false)
  }

  it should "submit application and set show bunting flag to false if applicants age is not provided" in {
    applicationWithDateOfBirth(
      dateOfBirth = DateOfBirth(
        dob = None,
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.is18to70)
        ))
      ),
      expectedBuntingFlagValue = false
    )
  }

  def applicationWithDateOfBirth(localDate: LocalDate, expectedBuntingFlagValue: Boolean): Unit =
    applicationWithDateOfBirth(createDoBFrom(localDate), expectedBuntingFlagValue: Boolean)

  def applicationWithDateOfBirth(dateOfBirth: DateOfBirth, expectedBuntingFlagValue: Boolean): Unit =
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/confirmation")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(dob = Some(dateOfBirth)))
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/complete"))
      val allCookies = cookies(result)
      val completeStepData = getCompleteCookie(allCookies)
      completeStepData should not be (None)
      completeStepData.get.showBirthdayBunting should be(expectedBuntingFlagValue)
    }

  private def createDoBFrom(localDate: LocalDate) =
    DateOfBirth(
      dob = Some(DOB(
        year = localDate.year.get,
        month = localDate.monthOfYear.get,
        day = localDate.dayOfMonth.get
      )),
      noDob = None)
}
