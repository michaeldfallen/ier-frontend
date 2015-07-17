package uk.gov.gds.ier.validation.constraints.overseas

import uk.gov.gds.ier.validation._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model._
import scala.Some
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import org.joda.time.{YearMonth, DateTime}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait DateLeftUkConstraints extends CommonConstraints{
  self: ErrorMessages
    with FormKeys =>

  lazy val validateDateLeftUk = Constraint[InprogressOverseas](keys.dateLeftUk.key) {
    application => application.dateLeftUk match {
      case Some(dateLeftUk) => {
        if (dateLeftUkIsBeforeNow(dateLeftUk)) Valid
        else Invalid ("You have entered a date in the future",
              keys.dateLeftUk.month, keys.dateLeftUk.year)
      }
      case None => Invalid ("Please answer this question",
                    keys.dateLeftUk.month, keys.dateLeftUk.year)
    }
  }

  def dateLeftUkIsBeforeNow (dateLeftUk : DateLeft) : Boolean = {
    val leftUkDateTime = new YearMonth(dateLeftUk.year, dateLeftUk.month)
    val nowDateTime = DateTime.now()
    val nowWithoutDay = new YearMonth(nowDateTime.year().get(),nowDateTime.monthOfYear().get())
    leftUkDateTime.isEqual(nowWithoutDay) || leftUkDateTime.isBefore(nowWithoutDay)
  }
}
