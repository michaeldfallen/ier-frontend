package uk.gov.gds.ier.transaction.crown.name

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class NameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with NameForms
  with NameMustache {

  val validation = nameForm

  val routing = Routes(
    get = routes.NameStep.get,
    post = routes.NameStep.post,
    editGet = routes.NameStep.editGet,
    editPost = routes.NameStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    crown.JobStep
  }
}
