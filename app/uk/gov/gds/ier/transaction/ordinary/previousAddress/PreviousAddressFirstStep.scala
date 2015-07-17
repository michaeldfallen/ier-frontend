package uk.gov.gds.ier.transaction.ordinary.previousAddress

import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.model.{MovedHouseOption}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService

import uk.gov.gds.ier.step.{Routes, OrdinaryStep}
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PreviousAddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with PreviousAddressFirstMustache
  with PreviousAddressFirstForms {

  val validation = previousAddressFirstForm

  val routing = Routes(
    get = routes.PreviousAddressFirstStep.get,
    post = routes.PreviousAddressFirstStep.post,
    editGet = routes.PreviousAddressFirstStep.editGet,
    editPost = routes.PreviousAddressFirstStep.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    val nextAddressStep = currentState.previousAddress.map(_.previousAddress) match {
      case Some(address) =>
        if (address.exists(_.postcode.isEmpty)) { ordinary.PreviousAddressPostcodeStep }
        else if (address.exists(_.manualAddress.isDefined)) { ordinary.PreviousAddressManualStep }
        else if (address.exists(_.uprn.isDefined)) { ordinary.PreviousAddressSelectStep }
        else ordinary.PreviousAddressPostcodeStep
      case _ => ordinary.PreviousAddressPostcodeStep
    }

    currentState.previousAddress.flatMap(_.movedRecently) match {
      case Some(MovedHouseOption.MovedFromAbroadRegistered) => nextAddressStep
      case Some(MovedHouseOption.MovedFromAbroadNotRegistered) => ordinary.OpenRegisterStep
      case Some(MovedHouseOption.MovedFromUk) => nextAddressStep
      case Some(MovedHouseOption.NotMoved) => ordinary.OpenRegisterStep
      case _ => this
    }
  }

  override val onSuccess = TransformApplication { currentApplication =>

    val clearAddress = currentApplication.previousAddress.flatMap(
      _.movedRecently match {
        case Some(MovedHouseOption.NotMoved) => Some(true)
        case Some(MovedHouseOption.MovedFromAbroadNotRegistered) => Some(true)
        case _ => Some(false)
      }
    ).getOrElse(false)

    if(clearAddress){
      val clearedAddress = currentApplication.previousAddress.map{ _.copy(previousAddress = None) }

      currentApplication.copy(
        previousAddress = clearedAddress,
        possibleAddresses = None)
    } else {
      currentApplication
    }

  } andThen BranchOn (_.previousAddress.map(_.movedRecently)) {
    case Some(Some(MovedHouseOption.MovedFromAbroadRegistered)) => GoToNextStep()
    case Some(Some(MovedHouseOption.MovedFromUk)) => GoToNextStep()
    case _ => GoToNextIncompleteStep()
  }

}

