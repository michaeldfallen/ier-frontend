package uk.gov.gds.ier.transaction.ordinary.confirmation

import uk.gov.gds.ier.controller.routes.ErrorController
import uk.gov.gds.ier.transaction.complete.routes.CompleteStep
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{WithAddressService, AddressService}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.ConfirmationStepController
import uk.gov.gds.ier.service.apiservice.IerApiService
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.transaction.complete.CompleteCookie
import uk.gov.gds.ier.session.ResultHandling
import uk.gov.gds.ier.langs.Language
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.model.PostalVoteOption
import uk.gov.gds.ier.transaction.ordinary.{WithOrdinaryControllers, OrdinaryControllers}

@Singleton
class ConfirmationStep @Inject ()(
    val serialiser: JsonSerialiser,
    ierApi: IerApiService,
    val addressService: AddressService,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
  ) extends ConfirmationStepController[InprogressOrdinary]
  with ConfirmationForms
  with ConfirmationMustache
  with ResultHandling
  with WithAddressService
  with WithOrdinaryControllers
  with WithRemoteAssets {

  def factoryOfT() = InprogressOrdinary()
  def timeoutPage() = ErrorController.ordinaryTimeout

  val routing:Routes = Routes(
    get = routes.ConfirmationStep.get,
    post = routes.ConfirmationStep.post,
    editGet = routes.ConfirmationStep.get,
    editPost = routes.ConfirmationStep.post
  )

  val validation = confirmationForm

  def get = ValidSession in Action {
    implicit request =>
      val currentAddressLine = application.address.map { addressService.fillAddressLine(_) }

      val previousAddressLine = application.previousAddress.flatMap { prev =>
        if (prev.movedRecently.exists(_.hasPreviousAddress == true)) {
          prev.previousAddress.map { addressService.fillAddressLine(_) }
        } else {
          None
        }
      }

      val appWithAddressLines = application.copy(
        address = currentAddressLine,
        previousAddress = application.previousAddress.map{ prev =>
          prev.copy(previousAddress = previousAddressLine)
        }
      )

      val filledForm = validation.fillAndValidate(application)
      val html = mustache(filledForm, routing.post, application).html
      Ok(html).refreshToken
  }

  def post = ValidSession in Action {
    implicit request =>
      validation.fillAndValidate(application).fold(
        hasErrors => {
          Ok(mustache(hasErrors, routing.post, application).html).refreshToken
        },
        validApplication => {
          val refNum = ierApi.generateOrdinaryReferenceNumber(validApplication)
          val remoteClientIP = request.headers.get("X-Real-IP")

          val response = ierApi.submitOrdinaryApplication(
            ipAddress = remoteClientIP,
            applicant = validApplication,
            referenceNumber = Some(refNum),
            timeTaken = request.getToken.map(_.timeTaken),
            language = Language.getLang(request).language
          )

          logSession()

          val isPostalVoteEmailPresent = validApplication.postalVote.exists { postalVote =>
            val isPostalOptionSelected = postalVote.postalVoteOption.exists(_ == PostalVoteOption.Yes)

            val isEmailDeliveryOptionValid = postalVote.deliveryMethod.exists{
              deliveryMethod => deliveryMethod.isEmail && deliveryMethod.emailAddress.exists(_.nonEmpty)
            }

            isPostalOptionSelected && isEmailDeliveryOptionValid
          }

          val isContactEmailPresent = validApplication.contact.exists {
            _.email.exists { emailContact => emailContact.contactMe && emailContact.detail.exists(_.nonEmpty) }
          }

          val hasOtherAddress = validApplication.otherAddress.exists(_.otherAddressOption.hasOtherAddress)

          val isBirthdayToday = validApplication.dob.exists(_.dob.exists(_.isToday))

          val completeStepData = CompleteCookie(
            refNum = refNum,
            authority = Some(response.localAuthority),
            hasOtherAddress = hasOtherAddress,
            backToStartUrl = config.ordinaryStartUrl,
            showEmailConfirmation = (isPostalVoteEmailPresent || isContactEmailPresent),
            showBirthdayBunting =  isBirthdayToday
          )

          Redirect(CompleteStep.complete())
            .emptySession()
            .storeCompleteCookie(completeStepData)
        }
      )
  }
}

