package uk.gov.gds.ier.transaction.crown.waysToVote

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.CrownStep
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.{WaysToVoteType}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.WaysToVote
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class WaysToVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
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

  def nextStep(currentState: InprogressCrown) = {
    import WaysToVoteType._

    currentState.waysToVote.map(_.waysToVoteType) match {
      case Some(InPerson) => crown.ContactStep
      case Some(ByPost) => crown.PostalVoteStep
      case Some(ByProxy) => crown.ProxyVoteStep
      case _ => throw new IllegalArgumentException("unknown next step")
    }
  }
}

