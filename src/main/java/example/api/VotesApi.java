package example.api;

import example.service.VotesInfo;
import example.service.VotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static java.util.stream.Stream.generate;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;
import static reactor.core.publisher.Flux.fromStream;

@RequiredArgsConstructor
@RestController
public class VotesApi {

    private final VotesService votesService;

    @GetMapping(path = "/api/votes", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<VotesInfoDto> streamVotes() {
        return fromStream(generate(votesService.subscribe()).map(VotesApi::toDto));
    }

    private static VotesInfoDto toDto(VotesInfo votesInfo) {
        return VotesInfoDto.builder()
                .yesCount(votesInfo.getYesCount())
                .noCount(votesInfo.getNoCount())
                .build();
    }
}