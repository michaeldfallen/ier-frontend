package uk.gov.gds.ier.transaction.ordinary.postalVote

import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.OrdinaryStep
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.PostalVote
import uk.gov.gds.ier.validation.ErrorTransformForm
import scala.Some
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PostalVoteStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with PostalVoteForms
  with PostalVoteMustache {

  val validation = postalVoteForm

  val routing = Routes(
    get = routes.PostalVoteStep.get,
    post = routes.PostalVoteStep.post,
    editGet = routes.PostalVoteStep.editGet,
    editPost = routes.PostalVoteStep.editPost
  )

  def resetPostalVote = TransformApplication { currentState =>
    val postalVoteOption = currentState.postalVote.flatMap(_.postalVoteOption)
    postalVoteOption match {
      case Some(PostalVoteOption.NoAndVoteInPerson) | Some(PostalVoteOption.NoAndAlreadyHave) =>
          currentState.copy(postalVote = Some(PostalVote(
            postalVoteOption = postalVoteOption,
            deliveryMethod = None))
          )
      case _ => currentState
    }
  }

  override val onSuccess = resetPostalVote andThen GoToNextIncompleteStep()

  def nextStep(currentState: InprogressOrdinary) = {
    ordinary.ContactStep
  }
}

