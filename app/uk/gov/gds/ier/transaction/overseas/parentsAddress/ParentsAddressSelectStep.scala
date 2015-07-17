package uk.gov.gds.ier.transaction.overseas.parentsAddress

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class ParentsAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with ParentsAddressSelectMustache
  with ParentsAddressForms
  with WithSerialiser
  with WithAddressService {

  val validation = parentsAddressForm

  val routing = Routes(
    get = routes.ParentsAddressSelectStep.get,
    post = routes.ParentsAddressSelectStep.post,
    editGet = routes.ParentsAddressSelectStep.editGet,
    editPost = routes.ParentsAddressSelectStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    overseas.PassportCheckStep
  }

  override val onSuccess = TransformApplication { currentState =>
    val addressWithAddressLine = currentState.parentsAddress.map {
      addressService.fillAddressLine(_)
    }

    currentState.copy(
      parentsAddress = addressWithAddressLine,
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()
}
