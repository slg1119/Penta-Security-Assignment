package kr.penta.assignment.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import kr.penta.assignment.board.dto.BoardDto.BoardRequest;
import kr.penta.assignment.board.dto.BoardDto.BoardResponse;
import kr.penta.assignment.board.dto.BoardDto.LoadResult;
import kr.penta.assignment.board.entity.Board;
import kr.penta.assignment.board.repository.BoardRepository;
import kr.penta.assignment.board.strategy.InfiniteScrollStrategy;
import kr.penta.assignment.board.strategy.LoadStrategy;
import kr.penta.assignment.board.strategy.PaginationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private Map<String, LoadStrategy> loadStrategies;

    @Mock
    private InfiniteScrollStrategy infiniteScrollStrategy;

    @Mock
    private PaginationStrategy paginationStrategy;

    @InjectMocks
    private BoardService boardService;

    private Board testBoard;
    private BoardRequest testRequest;

    @BeforeEach
    void setUp() {
        testBoard = Board.builder()
                .id(1L)
                .title("테스트 제목")
                .content("테스트 내용")
                .author("테스트 작성자")
                .createdAt(LocalDateTime.now())
                .build();

        testRequest = BoardRequest.builder()
                .title("새 게시글 제목")
                .content("새 게시글 내용")
                .author("새 작성자")
                .build();
    }

    @Test
    @DisplayName("무한스크롤 전략으로 게시글 목록 조회 성공")
    void getBoards_InfiniteStrategy_Success() {
        // given
        when(loadStrategies.get("infiniteStrategy")).thenReturn(infiniteScrollStrategy);
        LoadResult expectedResult = LoadResult.builder()
                .strategy("infinite")
                .hasNext(true)
                .build();
        when(infiniteScrollStrategy.loadBoards(boardRepository, 0, 10)).thenReturn(expectedResult);

        // when
        LoadResult result = boardService.getBoards("infinite", 0, 10);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStrategy()).isEqualTo("infinite");
        verify(loadStrategies).get("infiniteStrategy");
        verify(infiniteScrollStrategy).loadBoards(boardRepository, 0, 10);
    }

    @Test
    @DisplayName("페이징 전략으로 게시글 목록 조회 성공")
    void getBoards_PaginationStrategy_Success() {
        // given
        when(loadStrategies.get("paginationStrategy")).thenReturn(paginationStrategy);
        LoadResult expectedResult = LoadResult.builder()
                .strategy("pagination")
                .hasNext(true)
                .hasPrevious(false)
                .build();
        when(paginationStrategy.loadBoards(boardRepository, 0, 10)).thenReturn(expectedResult);

        // when
        LoadResult result = boardService.getBoards("pagination", 0, 10);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStrategy()).isEqualTo("pagination");
        verify(loadStrategies).get("paginationStrategy");
        verify(paginationStrategy).loadBoards(boardRepository, 0, 10);
    }

    @Test
    @DisplayName("지원하지 않는 전략으로 조회 시 예외 발생")
    void getBoards_InvalidStrategy_ThrowsException() {
        // given
        when(loadStrategies.get("invalidStrategy")).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> boardService.getBoards("invalid", 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 로딩 전략입니다");
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createBoard_Success() {
        // given
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);

        // when
        BoardResponse response = boardService.createBoard(testRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getAuthor()).isEqualTo("테스트 작성자");
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("게시글 ID로 조회 성공")
    void getBoardById_Success() {
        // given
        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));

        // when
        BoardResponse response = boardService.getBoardById(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        verify(boardRepository).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID로 조회 시 예외 발생")
    void getBoardById_NotFound_ThrowsException() {
        // given
        when(boardRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.getBoardById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");
    }
}
