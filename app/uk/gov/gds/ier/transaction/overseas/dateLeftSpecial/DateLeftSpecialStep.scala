package uk.gov.gds.ier.transaction.overseas.dateLeftSpecial

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import uk.gov.gds.ier.controller.routes.ExitController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes, GoTo}
import uk.gov.gds.ier.model._
import org.joda.time.{Months, DateTime}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class DateLeftArmyStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends DateLeftSpecialStep {

  val service = "member of the armed forces"

  val routing = Routes(
    get = routes.DateLeftArmyStep.get,
    post = routes.DateLeftArmyStep.post,
    editGet = routes.DateLeftArmyStep.editGet,
    editPost = routes.DateLeftArmyStep.editPost
  )
}

@Singleton
class DateLeftCrownStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends DateLeftSpecialStep {

  val service = "Crown Servant"

  val routing = Routes(
    get = routes.DateLeftCrownStep.get,
    post = routes.DateLeftCrownStep.post,
    editGet = routes.DateLeftCrownStep.editGet,
    editPost = routes.DateLeftCrownStep.editPost
 )
}

@Singleton
class DateLeftCouncilStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends DateLeftSpecialStep {

 val service = "British Council employee"

 val routing = Routes(
    get = routes.DateLeftCouncilStep.get,
    post = routes.DateLeftCouncilStep.post,
    editGet = routes.DateLeftCouncilStep.editGet,
    editPost = routes.DateLeftCouncilStep.editPost
 )
}


abstract class DateLeftSpecialStep
  extends OverseaStep
    with DateLeftSpecialForms
    with DateLeftSpecialMustache {

  val validation = dateLeftSpecialForm

  def nextStep(currentState: InprogressOverseas) = {
    currentState.dateLeftSpecial match {
      case Some(dateLeftSpecial) if dateLeftUkOver15Years(dateLeftSpecial.date) => {
        GoTo(ExitController.leftSpecialOver15Years)
      }
      case _ => overseas.LastUkAddressStep
    }
  }

  def dateLeftUkOver15Years(dateLeftUk:DateLeft):Boolean = {
    val leftUk = new DateTime().withMonthOfYear(dateLeftUk.month).withYear(dateLeftUk.year)
    val monthDiff = Months.monthsBetween(leftUk, DateTime.now()).getMonths()
    if (monthDiff >= 15 * 12) true
    else false
  }
}
