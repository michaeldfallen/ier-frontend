package uk.gov.gds.ier.transaction.forces.waysToVote

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes, Step}
import uk.gov.gds.ier.model.{WaysToVoteType}
import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.SimpleResult
import uk.gov.gds.ier.model.{WaysToVote,PostalOrProxyVote}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets


@Singleton
class WaysToVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with WaysToVoteForms
  with WaysToVoteMustache {

  val validation = waysToVoteForm

  val routing: Routes = Routes(
    get = routes.WaysToVoteStep.get,
    post = routes.WaysToVoteStep.post,
    editGet = routes.WaysToVoteStep.editGet,
    editPost = routes.WaysToVoteStep.editPost
  )

  override val onSuccess = TransformApplication { application =>
    if (application.waysToVote == Some(WaysToVote(WaysToVoteType.InPerson))) {
        application.copy(postalOrProxyVote = None)
    } else {
      application
    }
  } andThen BranchOn (_.waysToVote) {
    case Some(WaysToVote(WaysToVoteType.InPerson)) => GoToNextIncompleteStep()
    case _ => GoToNextStep()
  }

  def nextStep(currentState: InprogressForces) = {
    currentState.waysToVote.map(_.waysToVoteType) match {
      case Some(WaysToVoteType.InPerson) => forces.ContactStep
      case Some(WaysToVoteType.ByPost) => forces.PostalVoteStep
      case Some(WaysToVoteType.ByProxy) => forces.ProxyVoteStep
      case _ => throw new IllegalArgumentException("unknown next step")
    }
  }
}
