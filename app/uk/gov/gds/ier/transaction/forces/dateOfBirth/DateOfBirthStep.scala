package uk.gov.gds.ier.transaction.forces.dateOfBirth

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import uk.gov.gds.ier.controller.routes.ExitController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model.{DateOfBirth, noDOB}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes, GoTo}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class DateOfBirthStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with DateOfBirthForms
  with DateOfBirthMustache{

  val validation = dateOfBirthForm

  val routing = Routes(
    get = routes.DateOfBirthStep.get,
    post = routes.DateOfBirthStep.post,
    editGet = routes.DateOfBirthStep.editGet,
    editPost = routes.DateOfBirthStep.editPost
  )

  override val onSuccess = TransformApplication { currentState =>
    val dateOfBirth = currentState.dob.map { currentDob =>
      if (currentDob.dob.isDefined) currentDob.copy(noDob = None)
      else {
        currentDob.copy(dob = None)
      }
    }
    currentState.copy(dob = dateOfBirth)
  } andThen GoToNextIncompleteStep()

  def nextStep(currentState: InprogressForces) = {
    currentState.dob match {
      case Some(DateOfBirth(Some(dob), _)) if DateValidator.isTooYoungToRegister(dob) => {
        GoTo(ExitController.tooYoung)
      }
      case Some(DateOfBirth(_, Some(noDOB(Some(reason), Some(range)))))
        if range == DateOfBirthConstants.under18 => {
          GoTo(ExitController.under18)
      }
      case Some(DateOfBirth(_, Some(noDOB(Some(reason), Some(range)))))
        if range == DateOfBirthConstants.dontKnow => {
          GoTo(ExitController.dontKnow)
      }
      case _ => forces.NameStep
    }
  }
}

