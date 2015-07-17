package uk.gov.gds.ier.transaction.complete

import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails

case class CompleteCookie(
    refNum: String = "",
    authority: Option[EroAuthorityDetails] = None,
    hasOtherAddress: Boolean = false,
    backToStartUrl: String = "",
    showEmailConfirmation: Boolean = false,
    showBirthdayBunting: Boolean = false
)
