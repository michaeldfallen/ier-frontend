package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{Routes, CrownStep}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class AddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with AddressFirstMustache
  with AddressFirstForms {

  val validation = addressFirstForm

  val routing = Routes(
    get = routes.AddressFirstStep.get,
    post = routes.AddressFirstStep.post,
    editGet = routes.AddressFirstStep.editGet,
    editPost = routes.AddressFirstStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    val noPostcode = true
    val hasManualAddress = true
    val hasUprn = true

    val address = currentState.address.flatMap(_.address)
    val postcode = address.exists(_.postcode.isEmpty)
    val manualAddress = address.exists(_.manualAddress.isDefined)
    val uprn = address.exists(_.uprn.isDefined)

    (postcode, manualAddress, uprn) match {
      case (`noPostcode`, _, _) => crown.AddressStep
      case (_, `hasManualAddress`, _) => crown.AddressManualStep
      case (_, _, `hasUprn`) => crown.AddressSelectStep
      case _ => crown.AddressStep
    }
  }

  override val onSuccess = GoToNextStep()
}

