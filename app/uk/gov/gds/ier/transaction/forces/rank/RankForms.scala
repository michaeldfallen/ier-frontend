package uk.gov.gds.ier.transaction.forces.rank

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.model._
import scala.Some
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait RankForms extends RankConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val rankMapping = mapping(
    keys.serviceNumber.key -> optional(nonEmptyText),
    keys.rank.key -> optional(nonEmptyText)
  ) (
    (serviceNumber, rank) => Rank(serviceNumber, rank)
  ) (
    rank => Some(rank.serviceNumber, rank.rank)
  ) verifying  serviceNumberAndRankRequired

  val rankForm = ErrorTransformForm(
    mapping(
      keys.rank.key -> optional(rankMapping)
    ) (
      rank => InprogressForces(rank = rank)
    ) (
      inprogressApplication => Some(inprogressApplication.rank)
    ) verifying rankObjectRequired
  )
}
trait RankConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val rankObjectRequired = Constraint[InprogressForces](keys.rank.key) {
    application => application.rank match {
      case Some(rank) => Valid
      case None => Invalid(
        "Please answer this question",
        keys.rank.rank,
        keys.rank.serviceNumber
      )
    }
  }

  lazy val serviceNumberAndRankRequired = Constraint[Rank](keys.rank.key) {
    rank => rank match {
      case Rank(Some(serviceNumber), None) =>
        Invalid("Please answer this question",keys.rank.rank)
      case Rank(None, Some(rank)) =>
        Invalid("Please answer this question",keys.rank.serviceNumber)
      case _ => Valid
    }
  }
}
