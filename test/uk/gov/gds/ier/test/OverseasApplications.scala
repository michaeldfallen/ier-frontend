package uk.gov.gds.ier.test

import org.joda.time.DateTime
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait OverseasApplications {

  lazy val completeOverseasApplication = InprogressOverseas(
    name = Some(Name("John", None, "Smith")),
    previousName = Some(PreviousName(false, "false", None)),
    dob = Some(DOB(year = 1970, month = 12, day = 12)),
    lastUkAddress = Some(
      PartialAddress(Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None)
    ),
    parentsAddress = Some(
      PartialAddress(Some("124 Fake Street, Fakerton"), Some("123456700"), "WR26NJ", None)
    ),
    dateLeftUk = Some(DateLeft(2000,10)),
    overseasParentName = Some(OverseasParentName(
        Some(Name("john", None, "Smith")),
        Some(PreviousName(true, "true", Some(Name("Tom", None, "Smith"))))
    )),
    nino = Some(Nino(Some("AB 12 34 56 D"), None)),
    address = Some(OverseasAddress(
      country = Some("United Kingdom"),
      addressLine1 = Some("some address line 1"),
      addressLine2 = None,
      addressLine3 = None,
      addressLine4 = None,
      addressLine5 = None)),
    lastRegisteredToVote = Some(LastRegisteredToVote(LastRegisteredType.Overseas)),
    openRegisterOptin = Some(true),
    waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
    postalOrProxyVote = Some(PostalOrProxyVote(
      WaysToVoteType.ByPost,
      Some(true),
      Some(PostalVoteDeliveryMethod(Some("post"),None))
    )),
    passport = Some(Passport(
      true, None, Some(PassportDetails("123456", "UK border office", DOB(2000, 12, 1))), None)),
    contact = Some(Contact(
      post = true,
      phone = None,
      email = None
    )),
    dateLeftSpecial = Some(DateLeftSpecial(DateLeft(1990, 1)))
  )

  lazy val incompleteYoungApplication = {
    val twentyYearsAgo = new DateTime().minusYears(20).getYear
    val fiveYearsAgo = new DateTime().minusYears(5).getYear
    InprogressOverseas(
      dob = Some(DOB(year = twentyYearsAgo, month = 12, day = 1)),
      dateLeftUk = Some(DateLeft(year = fiveYearsAgo, month = 1))
    )
  }

  lazy val incompleteNewApplication = InprogressOverseas(
    lastRegisteredToVote = Some(LastRegisteredToVote(
      lastRegisteredType = LastRegisteredType.Ordinary
    ))
  )

  val incompleteForcesApplication = InprogressOverseas(
    lastRegisteredToVote = Some(LastRegisteredToVote(
      lastRegisteredType = LastRegisteredType.Forces
    ))
  )

  val incompleteCrownApplication = InprogressOverseas(
    lastRegisteredToVote = Some(LastRegisteredToVote(
      lastRegisteredType = LastRegisteredType.Crown
    ))
  )

  val incompleteCouncilApplication = InprogressOverseas(
    lastRegisteredToVote = Some(LastRegisteredToVote(
      lastRegisteredType = LastRegisteredType.Council
    ))
  )

  val incompleteRenewerApplication = InprogressOverseas(
    lastRegisteredToVote = Some(LastRegisteredToVote(
      lastRegisteredType = LastRegisteredType.Overseas
    ))
  )
}
