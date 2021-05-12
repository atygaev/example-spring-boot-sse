package example.service.provider;

import example.service.VotesInfo;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ConditionalOnProperty(name = "app.votes.provider", havingValue = "fake")
public class FakeVotesProvider implements VotesProvider {

    private final ScheduledExecutorService fakeVotesProviderService = Executors.newSingleThreadScheduledExecutor();

    private final AtomicInteger yesVotesCounter = new AtomicInteger(0);

    private final AtomicInteger noVotesCounter = new AtomicInteger(0);

    private final TransferQueue<VotesInfo> queue = new LinkedTransferQueue<>();

    private final Random random = new Random();

    @SneakyThrows
    @Override
    public VotesInfo provide() {
        return queue.take();
    }

    @PostConstruct
    private void onConstruct() {
        fakeVotesProviderService.scheduleWithFixedDelay(this::addFakeVotes, 1, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    private void onDestroy() {
        try {
            fakeVotesProviderService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addFakeVotes() {
        final int newYesVotes;
        final int newNoVotes;

        if (random.nextBoolean()) {
            newYesVotes = random.nextInt(100) + 1;
            newNoVotes = 0;
        } else {
            newYesVotes = 0;
            newNoVotes = random.nextInt(100) + 1;
        }

        yesVotesCounter.addAndGet(newYesVotes);
        noVotesCounter.addAndGet(newNoVotes);

        queue.offer(VotesInfo.builder()
                .yesCount(yesVotesCounter.get())
                .noCount(noVotesCounter.get())
                .build());
    }
}