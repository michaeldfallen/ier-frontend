package uk.gov.gds.ier.transaction.overseas.dateLeftUk

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import uk.gov.gds.ier.controller.routes.ExitController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes, GoTo}
import uk.gov.gds.ier.model._
import org.joda.time.{Months, DateTime}
import uk.gov.gds.ier.validation.DateValidator
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets


@Singleton
class DateLeftUkStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
    with DateLeftUkForms
    with DateLeftUkMustache {

  val validation = dateLeftUkForm
  val routing = Routes(
    get = routes.DateLeftUkStep.get,
    post = routes.DateLeftUkStep.post,
    editGet = routes.DateLeftUkStep.editGet,
    editPost = routes.DateLeftUkStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {

    val notRegistered = currentState.lastRegisteredToVote match {
      case Some(LastRegisteredToVote(LastRegisteredType.NotRegistered)) => true
      case _ => false
    }

    (currentState.dateLeftUk, currentState.dob, notRegistered) match {
      case (Some(dateLeftUk), Some(dateOfBirth), _)
        if DateValidator.dateLeftUkOver15Years(dateLeftUk) =>
          GoTo(ExitController.leftUkOver15Years)
      case (Some(dateLeftUk), Some(dateOfBirth), true)
        if (validateTooOldWhenLeftUk(dateLeftUk, dateOfBirth)) =>
          GoTo(ExitController.tooOldWhenLeftUk)
      case (Some(dateLeftUk), Some(dateOfBirth), true)
        if (!DateValidator.dateLeftUkOver15Years(dateLeftUk) &&
          currentState.dob.isDefined &&
          !validateTooOldWhenLeftUk(dateLeftUk, dateOfBirth)) =>
          overseas.ParentNameStep
      case _ => overseas.LastUkAddressStep
    }
  }

  def validateTooOldWhenLeftUk(dateLeftUk:DateLeft, dateOfBirth:DOB):Boolean = {
    val birthDateTime = new DateTime(dateOfBirth.year, dateOfBirth.month, dateOfBirth.day,0,0,0,0)
    val leftUk = new DateTime().withMonthOfYear(dateLeftUk.month).withYear(dateLeftUk.year)
    val monthDiff = Months.monthsBetween(birthDateTime, leftUk).getMonths()
    if (monthDiff.toFloat / 12 > 18) true
    else false
  }
}
