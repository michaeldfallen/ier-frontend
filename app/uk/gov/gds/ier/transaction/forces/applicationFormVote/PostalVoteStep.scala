package uk.gov.gds.ier.transaction.forces.applicationFormVote

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.model.WaysToVoteType
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PostalVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with PostalOrProxyVoteForms
  with PostalOrProxyVoteMustache {

  val wayToVote = WaysToVoteType.ByPost

  val validation = postalOrProxyVoteForm

  val routing = Routes(
    get = routes.PostalVoteStep.get,
    post = routes.PostalVoteStep.post,
    editGet = routes.PostalVoteStep.editGet,
    editPost = routes.PostalVoteStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    forces.ContactStep
  }

}

