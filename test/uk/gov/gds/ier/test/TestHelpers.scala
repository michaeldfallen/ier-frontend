package uk.gov.gds.ier.test

import play.api.test.FakeRequest
import play.api.test.Helpers
import org.joda.time.DateTime
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.security._
import uk.gov.gds.ier.session.{SessionKeys, CookieHandling, SessionToken}
import uk.gov.gds.ier.guice.{WithConfig, WithEncryption}
import play.api.data.FormError
import uk.gov.gds.ier.controller.MockConfig
import uk.gov.gds.ier.step.InprogressApplication
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import scala.util.Try
import play.api.mvc.Cookies
import uk.gov.gds.common.json.JsonSerializer
import uk.gov.gds.ier.transaction.complete.CompleteCookie

trait TestHelpers
  extends CustomMatchers
  with OverseasApplications
  with FakeApplicationRedefined
  with SessionKeys {

  val jsonSerialiser = new JsonSerialiser

  lazy val textTooLong = "x" * 1000

  implicit class EasyGetErrorMessageError(form: ErrorTransformForm[_]) {
    def keyedErrorsAsMap = {
      form.errors.filterNot( error =>
        error.key == ""
      ).map( error =>
        error.key -> this.errorMessages(error.key)
      ).toMap
    }
    def errorMessages(key:String) = form.errors(key).map(_.message)
    def globalErrorMessages = form.globalErrors.map(_.message)
    def prettyPrint = form.errors.map(error => s"${error.key} -> ${error.message}")
  }

  implicit class FakeRequestWithOurSessionCookies[A](request: FakeRequest[A])
    extends CookieHandling
    with WithConfig
    with WithEncryption
    with WithSerialiser
    with SessionKeys {

    val config = new MockConfig

    val serialiser = jsonSerialiser
    val encryptionService = new EncryptionService (new Base64EncodingService, config)

    def withIerSession(timeSinceInteraction:Int = 1) = {
      val token = SessionToken(DateTime.now.minusMinutes(timeSinceInteraction))
      request.withCookies(tokenCookies(token, None):_*)
    }

    def withInvalidSession() = withIerSession(6)

    def withApplication[T <: InprogressApplication[T]](application: T) = {
      request.withCookies(payloadCookies(application, None):_*)
    }

    def withCompleteCookie(payload: CompleteCookie) = {
      request.withCookies(completeCookies(payload, None):_*)
    }
  }

  lazy val completeOrdinaryApplication = InprogressOrdinary(
    name = Some(Name("John", None, "Smith")),
    previousName = Some(PreviousName(false, "false", None)),
    dob = Some(DateOfBirth(Some(DOB(1988, 1, 1)), None)),
    nationality = Some(PartialNationality(Some(true), None, None, List.empty, None)),
    nino = Some(Nino(Some("AB 12 34 56 D"), None)),
    address = Some(PartialAddress(Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None)),
    previousAddress = Some(PartialPreviousAddress(Some(MovedHouseOption.NotMoved), None)),
    otherAddress = Some(OtherAddress(OtherAddress.NoOtherAddress)),
    openRegisterOptin = Some(false),
    postalVote = Some(PostalVote(Some(PostalVoteOption.NoAndVoteInPerson),None)),
    contact = Some(Contact(true, None, None)),
    possibleAddresses = None,
    country = Some(Country("England", false))
  )



  lazy val completeForcesApplication = InprogressForces(
    statement = Some(Statement(memberForcesFlag = Some(true), None)),
    address = Some(LastAddress(
      Some(HasAddressOption.YesAndLivingThere),
      Some(PartialAddress(Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None))
    )),
    previousAddress = Some(PartialPreviousAddress(Some(MovedHouseOption.NotMoved), None)),
    nationality = Some(PartialNationality(Some(true), None, None, List.empty, None)),
    dob = Some(DateOfBirth(Some(DOB(1988, 1, 1)), None)),
    name = Some(Name("John", None, "Smith")),
    previousName = Some(PreviousName(true, "true", Some(Name("George", None, "Smith")))),
    nino = Some(Nino(Some("AB 12 34 56 D"), None)),
    service = Some(Service(Some(ServiceType.RoyalAirForce), None)),
    rank = Some(Rank(Some("1234567"), Some("rank 1"))),
    contactAddress = Some (PossibleContactAddresses(
      contactAddressType = Some("uk"),
      ukAddressLine = Some("my uk address, london"),
      bfpoContactAddress = None,
      otherContactAddress = None
    )),
    openRegisterOptin = Some(true),
    waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
    postalOrProxyVote = Some(PostalOrProxyVote(
      WaysToVoteType.ByPost,
      Some(true),
      Some(PostalVoteDeliveryMethod(Some("post"),None))
    )),
    contact = Some(Contact(true, None, None)),
    possibleAddresses = None
  )

  lazy val completeCrownApplication = InprogressCrown(
    statement = Some(CrownStatement(
      crownServant = true,
      crownPartner = false,
      councilEmployee = false,
      councilPartner = false
    )),
    address = Some(LastAddress(
      Some(HasAddressOption.YesAndLivingThere),
      Some(PartialAddress(
        Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None))
    )),
    previousAddress = Some(PartialPreviousAddress(Some(MovedHouseOption.NotMoved), None)),
    nationality = Some(PartialNationality(Some(true), None, None, List.empty, None)),
    dob = Some(DateOfBirth(Some(DOB(1988, 1, 1)), None)),
    name = Some(Name("John", None, "Smith")),
    previousName = Some(PreviousName(true, "true", Some(Name("George", None, "Smith")))),
    nino = Some(Nino(Some("AB 12 34 56 D"), None)),
    job = Some(Job(Some("job title"), Some("123456"), Some("MoJ"))),
    contactAddress = Some (PossibleContactAddresses(
      contactAddressType = Some("uk"),
      ukAddressLine = Some("my uk address, london"),
      bfpoContactAddress = None,
      otherContactAddress = None
    )),
    openRegisterOptin = Some(true),
    waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
    postalOrProxyVote = Some(PostalOrProxyVote(
      WaysToVoteType.ByPost,
      Some(true),
      Some(PostalVoteDeliveryMethod(Some("post"),None))
    )),
    contact = Some(Contact(true, None, None)),
    possibleAddresses = None
  )

  class ErrorsOps(errors: Seq[FormError], globalErrors: Seq[FormError]) {
    /**
     * Transform errors to a multi line text suitable for testing.
     * Errors order is important, test it too.
     * Filter out all items with no key, this reduces duplicities, otherwise every(?) item is there twice
     */
    def errorsAsText() = {
      errors.filter(_.key != "").map(e => e.key + " -> " + e.message).mkString("", "\n", "")
    }

    def errorsAsTextAll() = {
      errors.map(e => e.key + " -> " + e.message).mkString("", "\n", "")
    }

    /**
     * Transform errors to a multi line text suitable for testing.
     * Errors order is important, test it too.
     * Note: global errors has always empty keys, so ignore them.
     */
    def globalErrorsAsText() = {
      globalErrors.map(x => x.message).mkString("", "\n", "")
    }
  }

  implicit def formToErrorOps(form: ErrorTransformForm[InprogressOrdinary]) = {
    new ErrorsOps(form.errors, form.globalErrors)
  }

  implicit def overseasFormToErrorOps(form: ErrorTransformForm[InprogressOverseas]) = {
    new ErrorsOps(form.errors, form.globalErrors)
  }

  implicit def forcesFormToErrorOps(form: ErrorTransformForm[InprogressForces]) = {
    new ErrorsOps(form.errors, form.globalErrors)
  }

  implicit def crownFormToErrorOps(form: ErrorTransformForm[InprogressCrown]) = {
    new ErrorsOps(form.errors, form.globalErrors)
  }

  def runningApp(test: => Unit):Unit = {
    Helpers.running(FakeApplication()) {
      test
    }
  }

  /**
   * Usage:
   * {{{
   * import play.api.test.Helpers._
   * val Some(resultFuture) = route(
   *    FakeRequest(POST, "/register-to-vote/confirmation"))
   * val result = getApplication[InprogressOrdinary](cookies(resultFuture))
   * }}}
   */
  def getApplication[T <: InprogressApplication[T]](
      cookies: Cookies
    )(
      implicit manifest: Manifest[T],
      encryptionService: EncryptionService,
      serialiser: JsonSerializer): Option[T] = {
    getSessionCookie[T](cookies, sessionPayloadKey, sessionPayloadKeyIV)
  }

  /**
   * Usage:
   * {{{
   * import play.api.test.Helpers._
   * val Some(resultFuture) = route(
   *    FakeRequest(POST, "/register-to-vote/confirmation"))
   * val result = getCompleteStepCookie(cookies(resultFuture))
   * }}}
   */
  def getCompleteCookie(
      cookies: Cookies
  ) (
      implicit encryptionService: EncryptionService,
      serialiser: JsonSerializer
  ): Option[CompleteCookie] = {
    getSessionCookie[CompleteCookie](cookies, completeCookieKey, completeCookieKeyIV)
  }

  def getSessionCookie[T](
      cookies: Cookies,
      key: String,
      keyIV: String
    )(
      implicit manifest: Manifest[T],
      encryptionService: EncryptionService,
      serialiser: JsonSerializer): Option[T] = {
    val application = for {
      cookie <- cookies.get(key)
      cookieInitVec <- cookies.get(keyIV)
    } yield Try {
      val json = encryptionService.decrypt(cookie.value,  cookieInitVec.value)
      serialiser.fromJson[T](json)
    }
    application.flatMap(_.toOption)
  }
}
