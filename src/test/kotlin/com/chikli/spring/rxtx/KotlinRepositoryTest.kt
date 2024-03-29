package com.chikli.spring.rxtx

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ContextConfiguration

@DataR2dbcTest
@ContextConfiguration(classes = [NumberRepository::class, RxTestTransaction::class])
@EnableAutoConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Suppress("SqlNoDataSourceInspection", "SqlResolve")
internal class KotlinRepositoryTest(
    @Autowired private val numberRepository: NumberRepository,
    @Autowired private val client: DatabaseClient
) {

    @BeforeAll
    fun setup() {
        client.sql(
            """
            create table number_table
            (
                the_number integer not null
            );
        """.trimIndent()
        ).fetch().rowsUpdated().subscribe()
    }

    @Test
    internal fun `first save & read works - second read doesn't see the first`() {
        numberRepository.save(100)
            .flatMap { numberRepository.read() }
            .testWithTx()
            .expectNext(100)
            .verifyComplete()

        numberRepository.read()
            .flatMap { numberRepository.read() }
            .testWithTx()
            .verifyComplete()
    }
}
