package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{Routes, CrownStep}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PreviousAddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with PreviousAddressFirstMustache
  with PreviousAddressFirstForms {

  val validation = previousAddressFirstForm

  val routing = Routes(
    get = routes.PreviousAddressFirstStep.get,
    post = routes.PreviousAddressFirstStep.post,
    editGet = routes.PreviousAddressFirstStep.editGet,
    editPost = routes.PreviousAddressFirstStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    import MovedHouseOption.Yes

    val noPostcode = false
    val hasManualAddress = true
    val hasUprn = true

    val movedHouse = currentState.previousAddress.flatMap(_.movedRecently)
    val address = currentState.previousAddress.flatMap(_.previousAddress)
    val postcode = address.exists(!_.postcode.isEmpty)
    val manualAddress = address.exists(_.manualAddress.isDefined)
    val uprn = address.exists(_.uprn.isDefined)

    (movedHouse, postcode, manualAddress, uprn) match {
      case (Some(Yes), `noPostcode`, _, _) => crown.PreviousAddressPostcodeStep
      case (Some(Yes), _, `hasManualAddress`, _) => crown.PreviousAddressManualStep
      case (Some(Yes), _, _, `hasUprn`) => crown.PreviousAddressSelectStep
      case _ => crown.NationalityStep
    }
  }

  override val onSuccess = TransformApplication { currentApplication =>
    val clearAddress = currentApplication.previousAddress.exists(
      _.movedRecently.exists(_ == MovedHouseOption.NotMoved))

    if(clearAddress){
      val clearedAddress = currentApplication.previousAddress.map{ _.copy(previousAddress = None) }

      currentApplication.copy(
        previousAddress = clearedAddress,
        possibleAddresses = None)
    } else {
      currentApplication
    }

  } andThen BranchOn (_.previousAddress.map(_.movedRecently)) {
    case Some(Some(MovedHouseOption.Yes)) => GoToNextStep()
    case _ => GoToNextIncompleteStep()
  }
}

