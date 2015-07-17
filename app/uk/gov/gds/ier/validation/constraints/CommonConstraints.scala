package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{ErrorMessages, Key}
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.data.Mapping
import play.api.data.Forms._
import uk.gov.gds.ier.validation.Key

trait CommonConstraints extends ErrorMessages {

  val Invalid = play.api.data.validation.Invalid
  val Valid = play.api.data.validation.Valid
  val Constraint = play.api.data.validation.Constraint

  def required[A](mapping: Mapping[A], errorMessage:String):Mapping[A] = {
    optional(mapping).verifying(errorMessage, _.nonEmpty).transform(_.get, Option(_))
  }

  def stepRequired[A](mapping:Mapping[A]):Mapping[Option[A]] = {
    optional(mapping).verifying("Please complete this step", _.isDefined)
  }

  def default[A](mapping: Mapping[A], alternative:A):Mapping[A] = {
    optional(mapping).transform(
      opt => opt.getOrElse(alternative),
      alt => if (alt==alternative) None else Some(alt)
    )
  }

  protected def predicateHolds [T](fieldKey:Key, errorMessage:String)
                                  (predicate:T => Boolean) = {
    Constraint[T](fieldKey.key) {
      t =>
        if (predicate(t)) Valid
        else Invalid(errorMessage, fieldKey)
    }
  }

  def fieldNotTooLong [T](fieldKey:Key, errorMessage:String, maxLength: Int = maxTextFieldLength)
                         (fieldValue:T => String) = {
    predicateHolds[T](fieldKey, errorMessage){
      t => fieldValue(t).size <= maxLength
    }
  }
}
