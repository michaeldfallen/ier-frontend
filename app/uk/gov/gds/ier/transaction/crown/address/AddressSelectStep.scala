package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{HasAddressOption, LastAddress}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class AddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with AddressSelectMustache
  with AddressForms
  with WithAddressService {

  val validation = addressForm

  val routing = Routes(
    get = routes.AddressSelectStep.get,
    post = routes.AddressSelectStep.post,
    editGet = routes.AddressSelectStep.editGet,
    editPost = routes.AddressSelectStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    import HasAddressOption._

    currentState.address.flatMap(_.hasAddress) match {
      case Some(YesAndLivingThere) => crown.PreviousAddressFirstStep
      case Some(YesAndNotLivingThere) => crown.PreviousAddressFirstStep
      case _ => crown.NationalityStep
    }
  }

  override val onSuccess = TransformApplication { application =>
    val address = application.address.flatMap {_.address}
    val addressWithAddressLine =  address.map (address => addressService.fillAddressLine(address))

    application.copy(
      address = Some(LastAddress(
        hasAddress = application.address.flatMap {_.hasAddress},
        address = addressWithAddressLine
      )),
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()
}
