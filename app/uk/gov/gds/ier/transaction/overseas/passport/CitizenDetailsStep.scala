package uk.gov.gds.ier.transaction.overseas.passport

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class CitizenDetailsStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with PassportForms
  with CitizenDetailsMustache {

  val validation = citizenDetailsForm

  val routing = Routes(
    get = routes.CitizenDetailsStep.get,
    post = routes.CitizenDetailsStep.post,
    editGet = routes.CitizenDetailsStep.editGet,
    editPost = routes.CitizenDetailsStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    overseas.NameStep
  }
}

