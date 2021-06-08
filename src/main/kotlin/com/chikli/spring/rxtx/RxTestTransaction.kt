package com.chikli.spring.rxtx

import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.CorePublisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.test.test

@Component
class RxTestTransaction(transactionalOperator: TransactionalOperator) {

    init {
        RxTestTransaction.transactionalOperator = transactionalOperator
    }

    companion object {
        lateinit var transactionalOperator: TransactionalOperator

        fun <T> createWithRollback(publisher: Mono<T>) = publisher.`as` { setupRollback(it).next() }.test()
        fun <T> createWithRollback(publisher: Flux<T>) = publisher.`as` { setupRollback(it) }.test()

        private fun <T> setupRollback(publisher: CorePublisher<T>) =
            transactionalOperator.execute {
                it.setRollbackOnly()
                publisher
            }
    }
}

fun <T> Mono<T>.testWithTx() = RxTestTransaction.createWithRollback(this)
