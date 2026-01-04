package com.zhm.edges.plugins.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.test.StepVerifier;

class JobContextTest {

  @Test
  void publish() throws InterruptedException {

    // Schedulers.parallel();
    // This like the reactor
    Flow flow;

    Flux<String> words = Flux.just("apple", "banana", "car", "airplane", "boat");
    Flux<GroupedFlux<Integer, String>> groupedByLength = words.groupBy(String::length);

    Flux<String> flux = Flux.just("foo", "bar");

    StepVerifier.create(flux) // 为flux创建一个验证器
        .expectNext("foo") // 期望第一个元素是"foo"
        .expectNext("bar") // 期望第二个元素是"bar"
        .expectComplete() // 期望完成信号
        .verify(); // 触发验证

    // 示例：创建一个包含多个元素的 Flux
    Flux<String> sequenceOfNames =
        Flux.just("Alice", "Bob", "Charlie")
            .map(String::toUpperCase)
            .filter(name -> name.startsWith("A"))
            .doOnNext(System.out::println);

    // JDK way:

    NumberPublisher publisher = new NumberPublisher();
    NumberToStringProcessor processor = new NumberToStringProcessor();
    ConsoleSubscriber subscriber = new ConsoleSubscriber("Sub1");

    publisher.subscribe(processor);
    processor.subscribe(subscriber);
    publisher.publishNumbers(10);

    Thread.sleep(2_000);

    /*Flux<String> ids = Flux.just("NameJoe", "NameBart")

    Flux<String> combinations =
            ids.flatMap(id -> {
              Mono<String> nameTask = ifhrName(id);
              Mono<Integer> statTask = ifhrStat(id);

              return nameTask.zipWith(statTask,
                      (name, stat) -> "Name " + name + " has stats " + stat);
            });

    Mono<List<String>> result = combinations.collectList();

    List<String> results = result.block();
    assertThat(results).containsExactly(
            "Name NameJoe has stats 103",
            "Name NameBart has stats 104",
            "Name NameHenry has stats 105",
            "Name NameNicole has stats 106",
            "Name NameABSLAJNFOAJNFOANFANSF has stats 121"
    );*/
  }

  class NumberPublisher implements Flow.Publisher<Integer> {
    private final SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();

    public void publishNumbers(int count) {
      IntStream.rangeClosed(1, count).forEach(publisher::submit);
      publisher.close();
    }

    @Override
    public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
      publisher.subscribe(subscriber);
    }
  }

  class NumberToStringProcessor implements Flow.Processor<Integer, String> {
    private Flow.Subscriber<? super String> subscriber;
    private Flow.Subscription subscription;

    @Override
    public void subscribe(Flow.Subscriber<? super String> subscriber) {
      this.subscriber = subscriber;
      subscriber.onSubscribe(
          new Flow.Subscription() {
            @Override
            public void request(long n) {
              subscription.request(n);
            }

            @Override
            public void cancel() {
              subscription.cancel();
            }
          });
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
      this.subscription = subscription;
      subscription.request(1); // 请求第一个元素
    }

    @Override
    public void onNext(Integer item) {
      // 转换处理：数字转字符串
      subscriber.onNext("Number: " + item);
      subscription.request(1); // 处理完一个请求下一个
    }

    @Override
    public void onError(Throwable throwable) {
      subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
      subscriber.onComplete();
    }
  }

  class ConsoleSubscriber implements Flow.Subscriber<String> {
    private Flow.Subscription subscription;
    private final String name;

    public ConsoleSubscriber(String name) {
      this.name = name;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
      this.subscription = subscription;
      System.out.println(name + ": Subscribed");
      subscription.request(1); // 请求第一个元素
    }

    @Override
    public void onNext(String item) {
      System.out.println(name + ": " + item);
      // 模拟处理时间
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
      }
      subscription.request(1); // 请求下一个元素
    }

    @Override
    public void onError(Throwable throwable) {
      System.err.println(name + ": Error - " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
      System.out.println(name + ": Completed");
    }
  }
}
