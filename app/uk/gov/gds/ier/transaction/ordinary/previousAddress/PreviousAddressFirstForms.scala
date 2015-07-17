package uk.gov.gds.ier.transaction.ordinary.previousAddress

import play.api.data.Forms._
import uk.gov.gds.ier.model.{MovedHouseOption, PartialAddress, PartialPreviousAddress}
import uk.gov.gds.ier.validation.{PostcodeValidator, ErrorMessages, FormKeys, ErrorTransformForm}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PreviousAddressFirstForms
    extends PreviousAddressFirstConstraints {
  self: FormKeys
    with ErrorMessages
    with WithSerialiser =>

  lazy val movedHouseRegisteredAbroadMapping = mapping(
    keys.movedRecently.key -> optional(movedHouseOptionmapping),
    keys.wasRegisteredWhenAbroad.key -> optional(boolean)
  ) (
      (movedHouse, registered) => movedHouse match {
        case Some(MovedHouseOption.MovedFromAbroad) =>
          registered match {
            case Some(true) => Some(MovedHouseOption.MovedFromAbroadRegistered)
            case Some(false) => Some(MovedHouseOption.MovedFromAbroadNotRegistered)
            case _ => movedHouse
          }
        case _ => movedHouse
      }
    ) (
      (movedHouse) => movedHouse match {
        case Some(MovedHouseOption.MovedFromAbroadRegistered) => Some((Some(MovedHouseOption.MovedFromAbroad), Some(true)))
        case Some(MovedHouseOption.MovedFromAbroadNotRegistered) => Some((Some(MovedHouseOption.MovedFromAbroad), Some(false)))
        case _ => Some((movedHouse, None))
      }
    )

  lazy val previousAddressRegisteredAbroadMapping = mapping(
    keys.movedRecently.key -> movedHouseRegisteredAbroadMapping,
    keys.previousAddress.key -> optional(PartialAddress.mapping)
  ) (
    PartialPreviousAddress.apply
  ) (
    PartialPreviousAddress.unapply
  )

  val previousAddressFirstForm = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(previousAddressRegisteredAbroadMapping)
    ) (
      previousAddressYesNo => InprogressOrdinary(
        previousAddress = previousAddressYesNo
      )
    ) (
      inprogress => Some(inprogress.previousAddress)
    ).verifying(
      previousAddressYesNoIsNotEmpty,
      previouslyRegisteredAbroad)
  )

  lazy val movedHouseOptionmapping = text.verifying(
    str => MovedHouseOption.isValid(str)
  ).transform[MovedHouseOption](
      str => MovedHouseOption.parse(str),
      option => option.name
    ).verifying(
      allPossibleMoveOptions
    )

  lazy val allPossibleMoveOptions = Constraint[MovedHouseOption]("movedHouse") {
    case MovedHouseOption.Yes => Valid

    case MovedHouseOption.MovedFromUk => Valid
    case MovedHouseOption.MovedFromAbroad => Valid
    case MovedHouseOption.MovedFromAbroadRegistered => Valid
    case MovedHouseOption.MovedFromAbroadNotRegistered => Valid

    case MovedHouseOption.NotMoved => Valid

    case _ => Invalid("ordinary_previousAddress_error_invalidOption")
  }
}

trait PreviousAddressFirstConstraints extends CommonConstraints {
  self: FormKeys
    with ErrorMessages =>

  lazy val previousAddressYesNoIsNotEmpty = Constraint[InprogressOrdinary](keys.previousAddress.movedRecently.key) {
    inprogress => inprogress.previousAddress match {
      case Some(PartialPreviousAddress(Some(_), _)) => Valid
      case _ => Invalid("ordinary_previousAddress_error_answerThis",keys.previousAddress.movedRecently)
    }
  }

  lazy val previouslyRegisteredAbroad = Constraint[InprogressOrdinary](keys.previousAddress.wasRegisteredWhenAbroad.key) {
    inprogress => inprogress.previousAddress.map( _.movedRecently ) match {
      case Some(Some(MovedHouseOption.MovedFromAbroad)) => Invalid("ordinary_previousAddress_error_answerThis",keys.previousAddress.wasRegisteredWhenAbroad)
      case _ => Valid
    }
  }
}
