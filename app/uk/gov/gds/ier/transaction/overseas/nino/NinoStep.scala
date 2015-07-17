package uk.gov.gds.ier.transaction.overseas.nino

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class NinoStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with NinoForms
  with NinoMustache {

  val validation = ninoForm

  val routing = Routes(
    get = routes.NinoStep.get,
    post = routes.NinoStep.post,
    editGet = routes.NinoStep.editGet,
    editPost = routes.NinoStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    overseas.AddressStep
  }
}

