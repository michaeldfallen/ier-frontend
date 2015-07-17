package uk.gov.gds.ier.transaction.forces.service

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.model._
import scala.Some
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait ServiceForms extends ServiceConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val serviceMapping = mapping(
    keys.serviceName.key -> optional(nonEmptyText).verifying("Please answer this question", _.isDefined),
    keys.regiment.key -> optional(nonEmptyText)
  ) (
    (serviceName, regiment) => Service(Some(ServiceType.parse(serviceName.get)), regiment)
  ) (
    service => Some(Some(service.serviceName.get.name), service.regiment)
  ) verifying (isValidServiceConstraint, isValidRegiment)

  val serviceForm = ErrorTransformForm(
    mapping(
      keys.service.key -> optional(serviceMapping)
    ) (
      service => InprogressForces(service = service)
    ) (
      inprogressApplication => Some(inprogressApplication.service)
    ) verifying serviceIsFilledConstraint
  )
}

trait ServiceConstraints {
  self: FormKeys =>
  lazy val validServices = List(
    "Royal Navy",
    "British Army",
    "Royal Air Force")

  lazy val isValidServiceConstraint = Constraint[Service](keys.service.serviceName.key) {
    service =>
      if (ServiceType.isValid(service.serviceName.get.name)) Valid
      else Invalid("This is not a valid service", keys.service.serviceName)
  }

  lazy val isValidRegiment = Constraint[Service](keys.service.regiment.key) {
    service =>
      service.serviceName match {
        case Some(ServiceType.BritishArmy) if (!service.regiment.isDefined)
            => Invalid("Please enter the regiment", keys.service.regiment)
        case _ => Valid
      }
  }

  lazy val serviceIsFilledConstraint = Constraint[InprogressForces](keys.service.key) {
    application =>
      if (application.service.isDefined) Valid
      else Invalid("Please answer this question", keys.service.serviceName)
  }
}
