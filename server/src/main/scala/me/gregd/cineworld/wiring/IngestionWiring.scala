package me.gregd.cineworld.wiring

import com.softwaremill.macwire.wire
import me.gregd.cineworld.ingestion.IngestionService

class IngestionWiring(integrationWiring: IntegrationWiring, domainRepositoryWiring: DomainRepositoryWiring, domainServiceWiring: DomainServiceWiring) {

  val ingestionService: IngestionService = wire[IngestionService]
}
