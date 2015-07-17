package uk.gov.gds.ier.transaction.overseas.applicationFormVote

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.model.WaysToVoteType
import scala.Some
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PostalVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
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

  def nextStep(currentState: InprogressOverseas) = {
    overseas.ContactStep
  }

}

