package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.FirstStep;
import reactor.test.StepVerifier.Step;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxSvc  = new FluxAndMonoGeneratorService();


    @Test
    void namesFlux() {

        int size = 3;
        Flux<String> testNamesFlux = fluxSvc.namesFlux();
        Flux<String> testNamesFluxMap = fluxSvc.namesFluxMap(size);
        Flux<String> testNamesFluxFlatMap = fluxSvc.namesFluxFlatMap(size);

        StepVerifier.create(testNamesFlux)
            //.expectNextCount(3)
            .expectNext("alex", "ben", "chloe")
            .verifyComplete();

        StepVerifier.create(testNamesFluxMap)
        .expectNext("alex".toUpperCase(), "chloe".toUpperCase())
        .verifyComplete();

        StepVerifier.create(testNamesFluxFlatMap)
        .expectNext("alex".toUpperCase().split(""))
        .expectNext("chloe".toUpperCase().split(""))
        .verifyComplete();

        FirstStep<String> verifier = StepVerifier.create(testNamesFlux);
        Step<String> s1 = verifier.expectNext("alex");
        Step<String> s2 = verifier.expectNext("ben");
        Step<String> s3 = verifier.expectNext("chloe");

        s1.verifyComplete();
        s2.verifyComplete();
        s3.verifyComplete();
        
    }

    @Test
    void testNamesFluxTransform() {
        int size = 3;
        Flux<String> testNamesFluxMap = fluxSvc.namesFluxTransform(size);

        StepVerifier.create(testNamesFluxMap)
        .expectNext("alex".toUpperCase().split(""))
        .expectNext("chloe".toUpperCase().split(""))
        .verifyComplete();
    }


    @Test
    void nameMono() {
        
    }

    @Test
    void testNamesFluxConcat() {
        Flux<String> testNamesFluxMap = fluxSvc.namesFluxConcat();

        StepVerifier.create(testNamesFluxMap)
        .expectNext("abcdef".toUpperCase().split(""))
        .verifyComplete();
    }

    @Test
    void testNamesFluxConcatWith() {
        Flux<String> testNamesFluxMap = fluxSvc.namesFluxConcatWith();

        StepVerifier.create(testNamesFluxMap)
        .expectNext("abcdef".toUpperCase().split(""))
        .verifyComplete();
    }

    @Test
    void testNamesMonoConcatWith() {
        Flux<String> testNamesFluxMap = fluxSvc.namesMonoConcatWith();

        StepVerifier.create(testNamesFluxMap)
        .expectNext("ab".toUpperCase().split(""))
        .verifyComplete();        
    }

    @Test
    void testNamesFluxMerge() {
        Flux<String> testNamesFluxMap = fluxSvc.namesFluxMerge();

        StepVerifier.create(testNamesFluxMap)
        .expectNext("adbecf".toUpperCase().split(""))
        .verifyComplete();
    }

    @Test
    void testNamesFluxMergeWith() {
        Flux<String> testNamesFluxMap = fluxSvc.namesFluxMergeWith();

        StepVerifier.create(testNamesFluxMap)
        .expectNext("adbecf".toUpperCase().split(""))
        .verifyComplete();
    }

    @Test
    void testNamesMonoMergeWith() {
        Flux<String> testNamesFluxMap = fluxSvc.namesMonoMergeWith();

        StepVerifier.create(testNamesFluxMap)
        .expectNext("ab".toUpperCase().split(""))
        .verifyComplete();     
    }

    @Test
    void testNamesFluxMergeSeq() {
        Flux<String> testNamesFluxMap = fluxSvc.namesFluxMergeSeq();

        StepVerifier.create(testNamesFluxMap)
        .expectNext("abcdef".toUpperCase().split(""))
        .verifyComplete();
    }

    @Test
    void testNamesFluxZip() {
        Flux<String> testNamesFluxMap = fluxSvc.namesFluxZip();

        StepVerifier.create(testNamesFluxMap)
        .expectNext("ad".toUpperCase())
        .expectNext("be".toUpperCase())
        .expectNext("cf".toUpperCase())
        .verifyComplete();   
    }

    @Test
    void testNamesFluxZipWith() {
        Flux<String> testNamesFluxMap = fluxSvc.namesFluxZipWith();

        StepVerifier.create(testNamesFluxMap)
        .expectNext("ad".toUpperCase())
        .expectNext("be".toUpperCase())
        .expectNext("cf".toUpperCase())
        .verifyComplete();   
    }

    @Test
    void testNamesFluxZip4() {
        Flux<String> testNamesFluxMap = fluxSvc.namesFluxZip4();

        StepVerifier.create(testNamesFluxMap)
        .expectNext("ad14".toUpperCase())
        .expectNext("be25".toUpperCase())
        .expectNext("cf36".toUpperCase())
        .verifyComplete();   
    }

    @Test
    void testNamesMonoZipWith() {
        Mono<String> testNamesMono = fluxSvc.namesMonoZipWith();

        StepVerifier.create(testNamesMono)
        .expectNext("ab".toUpperCase())
        .verifyComplete(); 
    }

}