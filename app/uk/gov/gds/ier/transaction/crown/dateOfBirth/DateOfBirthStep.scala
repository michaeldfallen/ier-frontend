package uk.gov.gds.ier.transaction.crown.dateOfBirth

import uk.gov.gds.ier.transaction.crown.CrownControllers
import uk.gov.gds.ier.controller.routes.ExitController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model.{DateOfBirth, noDOB}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes, GoTo}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class DateOfBirthStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
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

  def nextStep(currentState: InprogressCrown) = {
    import DateOfBirthConstants._
    val tooYoung = true

    val young = currentState.dob.flatMap(_.dob).exists(
      dob => DateValidator.isTooYoungToRegister(dob)
    )
    val range = currentState.dob.flatMap(_.noDob).flatMap(_.range)

    (young, range) match {
      case (`tooYoung`, _) => GoTo(ExitController.tooYoung)
      case (_, Some(`dontKnow`)) => GoTo(ExitController.dontKnow)
      case (_, Some(`under18`)) => GoTo(ExitController.under18)
      case _ => crown.NameStep
    }
  }
}

