package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.LastRegisteredType
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class LastRegisteredToVoteStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with LastRegisteredToVoteForms
  with LastRegisteredToVoteMustache {

  val validation = lastRegisteredToVoteForm

  val routing = Routes(
    get = routes.LastRegisteredToVoteStep.get,
    post = routes.LastRegisteredToVoteStep.post,
    editGet = routes.LastRegisteredToVoteStep.editGet,
    editPost = routes.LastRegisteredToVoteStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    import LastRegisteredType._

    currentState.lastRegisteredToVote.map(_.lastRegisteredType) match {
      case Some(Overseas) => overseas.DateLeftUkStep
      case Some(Ordinary) => overseas.DateLeftUkStep
      case Some(Forces) =>  overseas.DateLeftArmyStep
      case Some(Crown) => overseas.DateLeftCrownStep
      case Some(Council) => overseas.DateLeftCouncilStep
      case Some(NotRegistered) => overseas.DateLeftUkStep
      case _ => this
    }
  }

  override val onSuccess = TransformApplication { currentState =>
    currentState.copy (dateLeftUk = None, dateLeftSpecial = None)
  } andThen GoToNextIncompleteStep()
}
