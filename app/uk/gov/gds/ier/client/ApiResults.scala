package uk.gov.gds.ier.client

import play.api.mvc.{SimpleResult, PlainResult, Results, RequestHeader}
import uk.gov.gds.ier.serialiser.WithSerialiser

trait ApiResults {
  self: WithSerialiser =>

  def okResult[A <: AnyRef, B <: RequestHeader](payload: A) = generate(Results.Ok, payload)

  def okResult[A, B <: RequestHeader](payload: (String, A)): SimpleResult = okResult[Map[String, A], B](Map(payload._1 -> payload._2))

  def badResult[A <: AnyRef, B <: RequestHeader](payload: A) = generate(Results.BadRequest, payload)

  def badResult[A <: AnyRef, B <: RequestHeader](payload: (String, A)): SimpleResult = badResult[Map[String, A], B](Map(payload._1 -> payload._2))

  def notFoundResult[A <: AnyRef, B <: RequestHeader](payload: A) = generate(Results.NotFound, payload)

  def notFoundResult[A <: AnyRef, B <: RequestHeader](payload: (String, A)): SimpleResult = notFoundResult[Map[String, A], B](Map(payload._1 -> payload._2))

  def forbiddenResult[A <: AnyRef, B <: RequestHeader](payload: A) = generate(Results.Forbidden, payload)

  def forbiddenResult[A <: AnyRef, B <: RequestHeader](payload: (String, A)): SimpleResult = forbiddenResult[Map[String, A], B](Map(payload._1 -> payload._2))

  def unauthorisedResult[A <: AnyRef, B <: RequestHeader](payload: A) = generate(Results.Unauthorized, payload)

  def unauthorisedResult[A <: AnyRef, B <: RequestHeader](payload: (String, A)): SimpleResult = unauthorisedResult[Map[String, A], B](Map(payload._1 -> payload._2))

  def serverErrorResult[A <: AnyRef, B <: RequestHeader](payload: A) = generate(Results.InternalServerError, payload)

  def serverErrorResult[A <: AnyRef, B <: RequestHeader](payload: (String, A)): SimpleResult = serverErrorResult[Map[String, A], B](Map(payload._1 -> payload._2))

  private def generate[A <: AnyRef, B <: RequestHeader](status: Results.Status, payload: A): SimpleResult =
    status(serialiser.toJson(payload)).as("application/json; charset=utf-8")
}