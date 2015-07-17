package uk.gov.gds.ier.transaction.overseas.parentName

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.model.{OverseasParentName, PreviousName}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class ParentNameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with ParentNameForms
  with ParentNameMustache {

  val validation = parentNameForm

  val routing = Routes(
    get = routes.ParentNameStep.get,
    post = routes.ParentNameStep.post,
    editGet = routes.ParentNameStep.editGet,
    editPost = routes.ParentNameStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    overseas.ParentsAddressStep
  }

  def resetParentName = TransformApplication { currentState =>
    currentState.overseasParentName match {
      case Some(OverseasParentName(optParentName, Some(parentPreviousName)))
        if (!parentPreviousName.hasPreviousName) =>
          currentState.copy(
            overseasParentName = Some(OverseasParentName(optParentName, Some(PreviousName(false, "false", None))))
          )
      case _ => currentState
    }
  }

  override val onSuccess = resetParentName andThen GoToNextIncompleteStep()
}
