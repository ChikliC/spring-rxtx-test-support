Spring Reactive Transaction Test Support Library
================================================

![Maven Central](https://img.shields.io/maven-central/v/com.chikli.spring/spring-rxtx-test-support)
[![License](https://img.shields.io/github/license/ChikliC/spring-rxtx-test-support)](https://github.com/ChikliC/spring-rxtx-test-support/blob/main/LICENSE.txt)

Background
----------
One of the great strengths of the Spring Framework is its great transaction support while running tests. That is, simply
by annotating a database test method with `@Transactional`, any statements executed during the test will be rolled back.
Thus ensuring that your DB is in the same state it was before the test.

When trying to learn & use the "new-ish" reactive stack within Spring, it seems that this is a glaring hole.

Luckily, during my searching for a solution, I stumbled upon [Jose Luis Leon's](https://github.com/JoseLion)
relevant [GitHub comment](https://github.com/spring-projects/spring-framework/issues/24226#issuecomment-853519428). This
in turn inspired me to create this little library.

Hopefully at some point this functionality will be included in the Spring Framework by default, but until then...

Installing
----------
Gradle

    testImplementation("com.chikli.spring:spring-rxtx-test-support:<version>")

Maven

    <dependency>
        <groupId>com.chikli.spring</groupId>
        <artifactId>spring-rxtx-test-support</artifactId>
        <version>version</version>
        <scope>test</scope>
    </dependency>

Using
-----
[See the tests](https://github.com/ChikliC/spring-rxtx-test-support/blob/main/src/test/kotlin/com/chikli/spring/rxtx/KotlinRepositoryTest.kt)
for complete examples, but the gist of it is:

Ensure that `RxTestTransaction` is brought into the Spring Context, and it seems like you need to
use `@EnableAutoConfiguration`

Then you can simply do:

        numberRepository.save(100)                  <-- Save something to the DB
            .flatMap { numberRepository.read() }    <-- Read it back from the DB
            .testWithTx()                           <-- Wrap it all in a transaction
            .expectNext(100)                        <-- Evaluate the first result
            .verifyComplete()                       <-- Ensure no more data is coming

Releasing a new version
-----------------------
Update the `version` variable in `gradle.properties` and then run the following:

    gw publishToSonatype closeAndReleaseSonatypeStagingRepository

Feedback
--------
I'd love to know if you're using this (star the Repo if you do!), and please feel free to submit issues & PRs.
