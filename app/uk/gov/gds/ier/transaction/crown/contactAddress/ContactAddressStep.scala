package uk.gov.gds.ier.transaction.crown.contactAddress

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class ContactAddressStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
    with ContactAddressForms
    with ContactAddressMustache{

  val validation = contactAddressForm

  val routing = Routes(
    get = routes.ContactAddressStep.get,
    post = routes.ContactAddressStep.post,
    editGet = routes.ContactAddressStep.editGet,
    editPost = routes.ContactAddressStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    crown.OpenRegisterStep
  }
}

