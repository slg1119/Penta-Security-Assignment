package kr.penta.assignment.common.config;

import java.util.stream.IntStream;
import kr.penta.assignment.board.entity.Board;
import kr.penta.assignment.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final BoardRepository boardRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (boardRepository.count() == 0) {
            log.info("샘플 데이터를 생성합니다...");

            IntStream.rangeClosed(1, 50)
                    .forEach(i -> {
                        Board board = Board.builder()
                                .title("샘플 게시글 " + i)
                                .content("이것은 " + i + "번째 샘플 게시글의 내용입니다. " +
                                        "전략패턴을 테스트하기 위한 데이터입니다. " +
                                        "무한스크롤과 페이징 기능을 모두 확인해보세요!")
                                .author("작성자" + (i % 5 + 1))
                                .build();
                        boardRepository.save(board);
                    });

            log.info("샘플 데이터 생성 완료: 총 50개 게시글");
        } else {
            log.info("기존 데이터가 존재합니다. 샘플 데이터 생성을 건너뜁니다.");
        }
    }
}