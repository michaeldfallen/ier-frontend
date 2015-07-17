package uk.gov.gds.ier.transaction.forces.statement

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class StatementStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
    with StatementForms
    with StatementMustache {

  val validation = statementForm

  val routing = Routes(
    get = routes.StatementStep.get,
    post = routes.StatementStep.post,
    editGet = routes.StatementStep.editGet,
    editPost = routes.StatementStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    forces.AddressFirstStep
  }
}
