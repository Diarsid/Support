package diarsid.support.concurrency.async.exchange;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diarsid.support.concurrency.async.exchange.api.AsyncExchangePoint;

import static diarsid.support.tests.concurrency.CurrentThread.async;
import static diarsid.support.tests.concurrency.CurrentThread.blocking;
import static diarsid.support.concurrency.threads.ThreadsUtil.sleepSafely;

public class ExchangePointDemo {

    private static final Logger log = LoggerFactory.getLogger(ExchangePointDemo.class);

    public static void main(String[] args) {
        AsyncExchangePoint<String> exchangePoint = AsyncExchangePoint.newInstance("happened");

        exchangePoint.consumers().add(
                "first",
                (s) -> log.info("first received: " + s));

        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

        exchangePoint.sources().add(
                "source_1",
                queue);

        async()
                .loopEndless()
                .eachTimeSleep(1000)
                .eachTimeDo((count) -> {
                    queue.put("message_SOURCE_"+count);
                    log.info("fired SOURCE " + count);
                });

        async()
                .loopEndless()
                .eachTimeSleep(300)
                .eachTimeDo((count) -> {
                    exchangePoint.put("message_PUT_"+count);
                    log.info("fired PUT " + count);
                });

        blocking()
                .sleep(3200)
                .afterSleepNothing();

        exchangePoint.consumers().add(
                "second",
                (s) -> log.info("second received: " + s));

        sleepSafely(10_500);
        exchangePoint.destroy();
    }
}
