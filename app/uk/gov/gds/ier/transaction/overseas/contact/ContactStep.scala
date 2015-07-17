package uk.gov.gds.ier.transaction.overseas.contact

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class ContactStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with ContactForms
  with ContactMustache {

  val validation = contactForm

  val routing = Routes(
    get = routes.ContactStep.get,
    post = routes.ContactStep.post,
    editGet = routes.ContactStep.editGet,
    editPost = routes.ContactStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    overseas.ConfirmationStep
  }
}
