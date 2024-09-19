package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.function.*;
import java.time.Duration;


public class FluxAndMonoGeneratorService {

    public Flux<String> flatString(String s) {
        return Flux.just(s.toUpperCase().split(""));
    }

    public Flux<String> flatStringDelay(String s) {
        @SuppressWarnings("unused")
        var delay =  new Random().nextInt(1000);
        return Flux.just(s.toUpperCase().split("")).delayElements(Duration.ofMillis(0));
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe")).log();
    }

    public Flux<String> namesFluxMap(int size) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .map(String::toUpperCase)
            .filter(x -> x.length() > size)
            .log();
    }

    public Flux<String> namesFluxFlatMap(int size) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .filter(x -> x.length() > size)
            .flatMap(s -> Flux.just(s.toUpperCase().split("")))
            .log();
    }

    public Flux<String> namesFluxConcatMap(int size) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .filter(x -> x.length() > size)
            .concatMap(s -> flatString(s))           
            .log();
    }

    public Flux<String> namesFluxTransform(int size) {
        Function<Flux<String>, Flux<String>> filterMap = s -> s.map(String::toUpperCase).filter(x -> x.length() > size);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .transform(filterMap)
            .flatMap(s -> Flux.just(s.toUpperCase().split("")))
            .log();
    }

    public Flux<String> namesFluxImmut() {
        Flux<String> fluxImmut = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        fluxImmut.map(String::toUpperCase);
        return fluxImmut;
    }

    public Flux<String> namesFluxConcat() {
        Flux<String> abcFlux = Flux.just("A","B","C");
        Flux<String> defFlux = Flux.just("D","E","F");

        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> namesFluxConcatWith() {
        Flux<String> abcFlux = Flux.just("A","B","C");
        Flux<String> defFlux = Flux.just("D","E","F");

        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> namesMonoConcatWith() {
        Mono<String> aFlux = Mono.just("A");
        Mono<String> bFlux = Mono.just("B");

        return aFlux.concatWith(bFlux).log();
    }

    public Flux<String> namesFluxMerge() {
        Flux<String> abcFlux = Flux.just("A","B","C").delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D","E","F").delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> namesFluxMergeWith() {
        Flux<String> abcFlux = Flux.just("A","B","C").delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D","E","F").delayElements(Duration.ofMillis(125));

        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> namesMonoMergeWith() {
        Mono<String> aFlux = Mono.just("A");
        Mono<String> bFlux = Mono.just("B");

        return aFlux.mergeWith(bFlux).log();
    }

    public Flux<String> namesFluxMergeSeq() {
        Flux<String> abcFlux = Flux.just("A","B","C").delayElements(Duration.ofMillis(100));
        Flux<String> defFlux = Flux.just("D","E","F").delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    public Flux<String> namesFluxZip() {
        Flux<String> abcFlux = Flux.just("A","B","C");
        Flux<String> defFlux = Flux.just("D","E","F");

        return Flux.zip(abcFlux, defFlux, (x, y) -> x + y).log();
    }

    public Flux<String> namesFluxZip4() {
        Flux<String> abcFlux = Flux.just("A","B","C");
        Flux<String> defFlux = Flux.just("D","E","F");
        Flux<String> _123Flux = Flux.just("1","2","3");
        Flux<String> _345Flux = Flux.just("4","5","6");

        return Flux.zip(abcFlux, defFlux, _123Flux, _345Flux)
            .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
            .log();
    }

    public Flux<String> namesFluxZipWith() {
        Flux<String> abcFlux = Flux.just("A","B","C");
        Flux<String> defFlux = Flux.just("D","E","F");

        return abcFlux.zipWith(defFlux, (x, y) -> x + y).log();
    }

    public Mono<String> namesMonoZipWith() {
        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");

        return aMono.zipWith(bMono)
            .map(t2 -> t2.getT1() + t2.getT2())
            .log();
    }

    public Mono<String> nameMono() {
        return Mono.just("alex").log();
    }

    public static void main(String[] args) {
        int size = 3;
        FluxAndMonoGeneratorService fluxSvc = new FluxAndMonoGeneratorService();
        fluxSvc.namesFlux().subscribe(name -> {
            System.out.println("Flux Name is: " + name);
        });

        System.out.println("*****************************************");

        fluxSvc.namesFluxMap(size).subscribe(name -> {
            System.out.println("FluxMap Name is: " + name);
        });

        System.out.println("*****************************************");

        fluxSvc.namesFluxFlatMap(size).subscribe(name -> {
            System.out.println("FluxFlatMap Name is: " + name);
        });

        System.out.println("*****************************************");

        fluxSvc.namesFluxImmut().subscribe(name -> {
            System.out.println("FluxImmut Name is: " + name);
        });

        System.out.println("*****************************************");

        fluxSvc.nameMono().subscribe(name -> {
            System.out.println("Mono Name is: " + name);
        });

        System.out.println("*****************************************");

        fluxSvc.namesFluxConcatMap(size).subscribe(name -> {
            System.out.println("FluxConcatMap Name is: " + name);
        });

        System.out.println("*****************************************");

        fluxSvc.namesFluxTransform(size).subscribe(name -> {
            System.out.println("FluxTransform Name is: " + name);
        });

        System.out.println("*****************************************");

    }
}
