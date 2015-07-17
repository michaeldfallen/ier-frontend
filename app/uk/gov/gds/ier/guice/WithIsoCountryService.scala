package uk.gov.gds.ier.guice

import uk.gov.gds.ier.service.IsoCountryService

trait WithIsoCountryService {
  val isoCountryService:IsoCountryService
}