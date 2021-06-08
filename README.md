Spring Reactive Transaction Test Support Library
================================================

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

    testImplementation("com.chikli:spring-rxtx-test-support:<version>")

Maven

    <dependency>
        <groupId>com.chikli</groupId>
        <artifactId>spring-rxtx-test-support</artifactId>
        <version>version</version>
        <scope>test</scope>
    </dependency>

Using
-----
See the tests for complete examples, but the gist of it is:

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
