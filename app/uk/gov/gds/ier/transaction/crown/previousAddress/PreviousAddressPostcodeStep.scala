package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PreviousAddressPostcodeStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val addressService: AddressService,
    val crown: CrownControllers
) extends CrownStep
  with PreviousAddressPostcodeMustache
  with PreviousAddressForms {

  val validation = postcodeStepForm

  val routing = Routes(
    get = routes.PreviousAddressPostcodeStep.get,
    post = routes.PreviousAddressPostcodeStep.post,
    editGet = routes.PreviousAddressPostcodeStep.editGet,
    editPost = routes.PreviousAddressPostcodeStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    val isPreviousAddressNI = currentState.previousAddress.exists(
      _.previousAddress.exists(prevAddr => addressService.isNothernIreland(prevAddr.postcode)))

    if (isPreviousAddressNI) {
      crown.NationalityStep
    } else {
      crown.PreviousAddressSelectStep
    }
  }

  override val onSuccess = TransformApplication { currentState =>
    val prevAddressCleaned = currentState.previousAddress.map { prev =>
      prev.copy(
        previousAddress = prev.previousAddress.map(_.copy(
          addressLine = None,
          uprn = None,
          manualAddress = None,
          gssCode = None
        ))
      )
    }

    currentState.copy(
      previousAddress = prevAddressCleaned,
      possibleAddresses = None
    )

  } andThen GoToNextIncompleteStep()
}

