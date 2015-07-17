package uk.gov.gds.ier.assets

import controllers.AssetsBuilder
import com.google.inject.{Singleton, Inject}

@Singleton
class Assets extends AssetsBuilder

@Singleton
class GovukToolkit extends AssetsBuilder

@Singleton
class Template extends AssetsBuilder
