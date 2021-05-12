package example.service;

import example.service.provider.VotesProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VotesServiceTest {

    private static final Random random = new Random();

    private final TransferQueue<VotesInfo> votesProviderQueue = new LinkedTransferQueue<>();

    @Mock
    private VotesProvider votesProvider;

    @InjectMocks
    private VotesService votesService;


    @BeforeEach
    public void onBeforeEach() {
        when(votesProvider.provide()).then(args -> votesProviderQueue.take());

        votesService.startInternalSubscriptionOnProvider();
    }

    @AfterEach
    public void onAfterEach() {
        votesService.stopInternalSubscriptionOnProvider();
    }

    @Test
    public void singleOneTimeSubscription() {
        Supplier<VotesInfo> subscription = votesService.subscribe();

        VotesInfo votesInfo = createRandomVotesInfo();

        votesProviderQueue.offer(votesInfo);

        assertThat(subscription.get(), is(sameInstance(votesInfo)));
    }

    @Test
    public void multipleOneTimeSubscription() {
        Supplier<VotesInfo> subscription1 = votesService.subscribe();
        Supplier<VotesInfo> subscription2 = votesService.subscribe();

        VotesInfo votesInfo = createRandomVotesInfo();

        votesProviderQueue.offer(votesInfo);

        assertThat(subscription1.get(), is(sameInstance(votesInfo)));
        assertThat(subscription2.get(), is(sameInstance(votesInfo)));
    }

    @Test
    public void singleSubscription() {
        Supplier<VotesInfo> subscription = votesService.subscribe();

        List<VotesInfo> votesInfoItems = Arrays.asList(
                createRandomVotesInfo(),
                createRandomVotesInfo(),
                createRandomVotesInfo()
        );

        votesInfoItems.forEach(votesProviderQueue::offer);

        assertThat(subscribeToList(subscription, votesInfoItems.size()), is(votesInfoItems));
    }

    @Test
    public void multipleSubscriptions() {
        Supplier<VotesInfo> subscription1 = votesService.subscribe();
        Supplier<VotesInfo> subscription2 = votesService.subscribe();

        List<VotesInfo> votesInfoItems = Arrays.asList(
                createRandomVotesInfo(),
                createRandomVotesInfo(),
                createRandomVotesInfo()
        );

        votesInfoItems.forEach(votesProviderQueue::offer);

        assertThat(subscribeToList(subscription1, votesInfoItems.size()), is(votesInfoItems));
        assertThat(subscribeToList(subscription2, votesInfoItems.size()), is(votesInfoItems));
    }

    private static VotesInfo createRandomVotesInfo() {
        return VotesInfo.builder()
                .yesCount(random.nextInt(100) + 1)
                .noCount(random.nextInt(100) + 1)
                .build();
    }

    private static List<VotesInfo> subscribeToList(Supplier<VotesInfo> subscription, int count) {
        return Stream.generate(subscription).limit(count).collect(Collectors.toList());
    }
}