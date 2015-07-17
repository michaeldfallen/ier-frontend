package uk.gov.gds.ier.model

class ApiResponse(body: String, timeTakenMs: Long)

case class Success(body: String, timeTakenMs: Long)
  extends ApiResponse(body, timeTakenMs)

case class Fail(body: String, timeTakenMs: Long)
  extends ApiResponse(body, timeTakenMs)
