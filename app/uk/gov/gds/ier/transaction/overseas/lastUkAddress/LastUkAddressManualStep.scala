package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.ApplicationType
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class LastUkAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with LastUkAddressManualMustache
  with LastUkAddressForms
  with OverseasFormImplicits {

  val validation = manualAddressForm

  val routing = Routes(
    get = routes.LastUkAddressManualStep.get,
    post = routes.LastUkAddressManualStep.post,
    editGet = routes.LastUkAddressManualStep.editGet,
    editPost = routes.LastUkAddressManualStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.identifyApplication match {
      case ApplicationType.RenewerVoter => overseas.NameStep
      case ApplicationType.DontKnow => this
      case _ => overseas.PassportCheckStep
    }
  }
}
