package com.chikli.spring.rxtx

import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Suppress("SqlNoDataSourceInspection", "SqlResolve")
@Repository
class NumberRepository(private val client: DatabaseClient) {

    fun save(number: Int): Mono<Int> {
        return client.sql("insert into number_table (the_number) values (:number) ")
            .bind("number", number)
            .fetch().rowsUpdated()
    }

    fun read(): Mono<Int> {
        return client.sql("select the_number from number_table")
            .fetch().one().map { it["the_number"] as Int }
    }

}
