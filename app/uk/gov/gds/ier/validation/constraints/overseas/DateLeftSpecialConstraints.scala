package uk.gov.gds.ier.validation.constraints.overseas

import uk.gov.gds.ier.validation._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model._
import scala.Some
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import org.joda.time.{YearMonth, DateTime}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait DateLeftSpecialConstraints extends CommonConstraints{
  self: ErrorMessages
    with FormKeys =>

  lazy val validateDateLeftSpecial = Constraint[InprogressOverseas](keys.dateLeftSpecial.key) {
    application => application.dateLeftSpecial match {
      case Some(dateLeftSpecial) => {
        if (dateLeftSpecialIsBeforeNow(dateLeftSpecial.date)) Valid
        else Invalid ("You have entered a date in the future",
              keys.dateLeftSpecial.month, keys.dateLeftSpecial.year)
      }
      case None => Invalid ("Please answer this question",
                    keys.dateLeftSpecial.month, keys.dateLeftSpecial.year)
    }
  }

  def dateLeftSpecialIsBeforeNow (dateLeftSpecial : DateLeft) : Boolean = {
    val leftSpecialDateTime = new YearMonth(dateLeftSpecial.year, dateLeftSpecial.month)
    val nowDateTime = DateTime.now()
    val nowWithoutDay = new YearMonth(nowDateTime.year().get(),nowDateTime.monthOfYear().get())
    leftSpecialDateTime.isEqual(nowWithoutDay) || leftSpecialDateTime.isBefore(nowWithoutDay)
  }
}
