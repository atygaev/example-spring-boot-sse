package example.service;

import example.service.provider.VotesProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


@RequiredArgsConstructor
@Service
public class VotesService {

    private final List<VotesInfoChannel> channels = new CopyOnWriteArrayList<>();

    private final ExecutorService providerSubscription = Executors.newSingleThreadExecutor();

    private final VotesProvider provider;

    @PostConstruct
    void startInternalSubscriptionOnProvider() {
        providerSubscription.submit(() -> {
            while (!providerSubscription.isShutdown() && !providerSubscription.isTerminated()) {
                VotesInfo votesInfo = provider.provide();

                for (VotesInfoChannel channel : channels) {
                    channel.add(votesInfo);
                }
            }
        });
    }

    @SneakyThrows
    @PreDestroy
    void stopInternalSubscriptionOnProvider() {
        providerSubscription.awaitTermination(1, TimeUnit.SECONDS);
    }

    public Supplier<VotesInfo> subscribe() {
        VotesInfoChannel channel = new VotesInfoChannel();
        channels.add(channel);
        return channel;
    }
}