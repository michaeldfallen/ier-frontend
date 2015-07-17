package uk.gov.gds.ier.service.apiservice

import uk.gov.gds.ier.model.{HasAddressOption, LastAddress, PartialNationality, IsoNationality}

trait SubmissionHacks {

  implicit class CrownApplicationHacked(application: CrownApplication) {
    /**
     * For most cases it returns unchanged application but for users with 'last' UK address but not
     * living there, because there is currently no special flag for that in output data,
     * do (mis)use nationality excuse description and erase nationality to filter out user on ERO side
     */
    def hackNoUkAddressToNonat(
        originalListOfUserNationalities: Option[PartialNationality],
        address: Option[LastAddress]
      ): CrownApplication = {
      val originalNonat = application.nationality.flatMap(_.noNationalityReason)
      address.flatMap(_.hasAddress.map(hasAddress =>
        if (hasAddress == HasAddressOption.No)
          application.copy(
            nationality = Some(IsoNationality(
              countryIsos = List.empty,
              noNationalityReason = Some(List(
                originalNonat,
                Some(
                  "Nationality is " +
                  originalListOfUserNationalities.flatMap(_.toNiceString).getOrElse("unspecified") +
                  ". This person has no UK address so needs to be set as an 'other' elector: IER-DS."
                )
              ).flatten.mkString("\n"))
            )))
        else application
      )).getOrElse(application)
    }
  }

  implicit class ForcesApplicationHacked(application: ForcesApplication) {
    /**
     * For most cases it returns unchanged application but for users with 'last' UK address but not
     * living there, because there is currently no special flag for that in output data,
     * do (mis)use nationality excuse description and erase nationality to filter out user on ERO side
     */
    def hackNoUkAddressToNonat(
      originalListOfUserNationalities: Option[PartialNationality],
      address: Option[LastAddress]
      ): ForcesApplication = {
      val originalNonat = application.nationality.flatMap(_.noNationalityReason)
      address.flatMap(_.hasAddress.map(hasAddress =>
        if (hasAddress == HasAddressOption.No)
          application.copy(
            nationality = Some(IsoNationality(
              countryIsos = List.empty,
              noNationalityReason = Some(List(
                originalNonat,
                Some(
                  "Nationality is " +
                    originalListOfUserNationalities.flatMap(_.toNiceString).getOrElse("unspecified") +
                    ". This person has no UK address so needs to be set as an 'other' elector: IER-DS."
                )
              ).flatten.mkString("\n"))
            )))
        else application
      )).getOrElse(application)
    }
  }
}
