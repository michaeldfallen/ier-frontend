package uk.gov.gds.ier.transaction.overseas.dateOfBirth

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import uk.gov.gds.ier.controller.routes.ExitController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes, GoTo}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class DateOfBirthStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with DateOfBirthForms
  with DateOfBirthMustache {

  val validation = dateOfBirthForm

  val routing = Routes(
    get = routes.DateOfBirthStep.get,
    post = routes.DateOfBirthStep.post,
    editGet = routes.DateOfBirthStep.editGet,
    editPost = routes.DateOfBirthStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.dob match {
      case Some(dob) if DateValidator.isTooYoungToRegister(dob) => {
        GoTo(ExitController.tooYoung)
      }
      case _ => overseas.LastRegisteredToVoteStep
    }
  }
}

