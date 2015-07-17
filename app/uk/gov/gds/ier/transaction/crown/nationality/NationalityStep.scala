package uk.gov.gds.ier.transaction.crown.nationality

import uk.gov.gds.ier.transaction.crown.CrownControllers
import uk.gov.gds.ier.controller.routes.ExitController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.service.IsoCountryService
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes, GoTo}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class NationalityStep @Inject ()(
    val serialiser: JsonSerialiser,
    val isoCountryService: IsoCountryService,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
    with NationalityForms
    with NationalityMustache {

  val validation = nationalityForm

  val routing = Routes(
    get = routes.NationalityStep.get,
    post = routes.NationalityStep.post,
    editGet = routes.NationalityStep.editGet,
    editPost = routes.NationalityStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {

    if (currentState.nationality.flatMap(_.noNationalityReason) == None) {
      val franchises = currentState.nationality match {
        case Some(nationality) => isoCountryService.getFranchises(nationality)
        case None => List.empty
      }

      franchises match {
        case Nil => GoTo(ExitController.noFranchise)
        case list => crown.DateOfBirthStep
      }
    }
    else crown.DateOfBirthStep
  }
}

