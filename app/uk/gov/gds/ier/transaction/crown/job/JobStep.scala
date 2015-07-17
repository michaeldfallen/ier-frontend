package uk.gov.gds.ier.transaction.crown.job

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.model.{WaysToVoteType, WaysToVote}
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class JobStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with JobForms
  with JobMustache {

  val validation = jobForm

  val routing = Routes(
    get = routes.JobStep.get,
    post = routes.JobStep.post,
    editGet = routes.JobStep.editGet,
    editPost = routes.JobStep.editPost
  )

  override val onSuccess = GoToNextStep()

  def nextStep(currentState: InprogressCrown) = {
    crown.NinoStep
  }
}
