package uk.gov.gds.ier.transaction.overseas.waysToVote

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.model.{WaysToVote, WaysToVoteType}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets


@Singleton
class WaysToVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with WaysToVoteForms
  with WaysToVoteMustache {

  val validation = waysToVoteForm

  val routing = Routes(
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

  def nextStep(currentState: InprogressOverseas) = {
    currentState.waysToVote.map(_.waysToVoteType) match {
      case Some(WaysToVoteType.InPerson) => overseas.ContactStep
      case Some(WaysToVoteType.ByPost) => overseas.PostalVoteStep
      case Some(WaysToVoteType.ByProxy) => overseas.ProxyVoteStep
      case _ => throw new IllegalArgumentException("unknown next step")
    }
  }
}
