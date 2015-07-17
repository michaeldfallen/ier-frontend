package uk.gov.gds.ier.transaction.overseas.passport

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.model.{DOB}
import uk.gov.gds.ier.step.{Routes, OverseaStep}
import uk.gov.gds.ier.validation.ErrorTransformForm
import org.joda.time.LocalDate
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PassportCheckStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with PassportHelperConstants
  with PassportForms
  with PassportCheckMustache {

  val validation = passportCheckForm

  val routing = Routes(
    get = routes.PassportCheckStep.get,
    post = routes.PassportCheckStep.post,
    editGet = routes.PassportCheckStep.editGet,
    editPost = routes.PassportCheckStep.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {

    val before1983 = currentState.dob map { case DOB(year, month, day) =>
      val dateOfBirth = new LocalDate()
        .withYear(year)
        .withMonthOfYear(month)
        .withDayOfMonth(day)
      dateOfBirth.isBefore(DateOfBirthConstants.jan1st1983)
    }

    val passport = currentState.passport map { passport => passport.hasPassport }

    val bornInUk = currentState.passport flatMap { passport => passport.bornInsideUk }

    (passport, bornInUk, before1983) match {
      case (`hasPassport`, _, _) => overseas.PassportDetailsStep
      case (`noPassport`, `notBornInUk`, _) => overseas.CitizenDetailsStep
      case (`noPassport`, `wasBornInUk`, `notBornBefore1983`) => overseas.CitizenDetailsStep
      case (`noPassport`, `wasBornInUk`, `wasBornBefore1983`) => overseas.NameStep
      case _ => this
    }
  }
}

private[passport] trait PassportHelperConstants {

  //Constants needed for the nextStep method
  private[passport] val hasPassport = Some(true)
  private[passport] val noPassport = Some(false)
  private[passport] val wasBornInUk = Some(true)
  private[passport] val notBornInUk = Some(false)
  private[passport] val wasBornBefore1983 = Some(true)
  private[passport] val notBornBefore1983 = Some(false)
}
