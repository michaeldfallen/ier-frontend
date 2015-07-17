package uk.gov.gds.ier.transaction.forces.applicationFormVote

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import scala.Some
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.model.WaysToVoteType
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class ProxyVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with PostalOrProxyVoteForms
  with PostalOrProxyVoteMustache {

  val wayToVote = WaysToVoteType.ByProxy

  val validation = postalOrProxyVoteForm

  val routing = Routes(
    get = routes.ProxyVoteStep.get,
    post = routes.ProxyVoteStep.post,
    editGet = routes.ProxyVoteStep.editGet,
    editPost = routes.ProxyVoteStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    forces.ContactStep
  }

}

