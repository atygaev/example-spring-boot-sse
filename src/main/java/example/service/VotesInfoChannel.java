package example.service;

import lombok.SneakyThrows;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.function.Supplier;

public class VotesInfoChannel implements Supplier<VotesInfo> {

    private final TransferQueue<VotesInfo> queue = new LinkedTransferQueue<>();

    public void add(VotesInfo votesInfo) {
        queue.offer(votesInfo);
    }

    @SneakyThrows
    @Override
    public VotesInfo get() {
        return queue.take();
    }
}