package uk.gov.gds.ier.transaction.forces.service

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class ServiceStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
    with ServiceForms
    with ServiceMustache {

  val validation = serviceForm

  val routing = Routes(
    get = routes.ServiceStep.get,
    post = routes.ServiceStep.post,
    editGet = routes.ServiceStep.editGet,
    editPost = routes.ServiceStep.editPost
  )

  override val onSuccess = TransformApplication { currentState =>
    currentState.service match {
      case Some(Service(Some(ServiceType.RoyalNavy),_))  =>
        currentState.copy(service = Some(Service(Some(ServiceType.RoyalNavy), None)))
      case Some(Service(Some(ServiceType.RoyalAirForce),_)) =>
        currentState.copy(service = Some(Service(Some(ServiceType.RoyalAirForce), None)))
      case _ =>  currentState
    }
  } andThen GoToNextIncompleteStep()

  def nextStep(currentState: InprogressForces) = {
    forces.RankStep
  }
}
