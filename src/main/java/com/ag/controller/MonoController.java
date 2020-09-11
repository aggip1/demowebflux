package com.ag.controller;

import com.ag.domain.User;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@RestController
@RequestMapping("/mono")
public class MonoController {
    @RequestMapping("/testMono")
    public Mono<String> testMono(String name){
        return Mono.just(name+"--1");
    }
    @RequestMapping("/userMono")
    public Mono<User> userMono(User user){
        return Mono.just(user);
    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> requestBodyFlux(FilePart filePart) throws IOException {
        System.out.println(filePart.filename());
        Path tempFile = Files.createTempFile("test", filePart.filename());

        //NOTE 方法一
        AsynchronousFileChannel channel =
                AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE);
        DataBufferUtils.write(filePart.content(), channel, 0)
                .doOnComplete(() -> {
                    System.out.println("finish");
                })
                .subscribe();

        //NOTE 方法二
//        filePart.transferTo(tempFile.toFile());

        System.out.println(tempFile.toString());
        return Mono.just(filePart.filename());
    }

    @GetMapping("/pc")
    public Mono<Object> mono() {
        return Mono.create(monoSink -> {
            System.out.println("创建 Mono");
            monoSink.success("hello webflux");
        })
                .doOnSubscribe(subscription -> { //当订阅者去订阅发布者的时候，该方法会调用
                    System.out.println("subscription:"+subscription);
                }).doOnNext(o -> { //当订阅者   收到数据时，改方法会调用
                    System.out.println("o = " + o);
                });
    }
    
}
