package kr.penta.assignment.board.controller;

import static kr.penta.assignment.board.dto.BoardDto.BoardRequest;
import static kr.penta.assignment.board.dto.BoardDto.ErrorResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import kr.penta.assignment.board.dto.BoardDto.BoardResponse;
import kr.penta.assignment.board.dto.BoardDto.LoadResult;
import kr.penta.assignment.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 목록 조회 (전략 선택 가능)
     *
     * @param strategy 로딩 전략 (infinite 또는 pagination)
     * @param page     페이지 번호 (0부터 시작)
     * @param size     페이지 크기 (1~100)
     * @return 게시글 목록과 메타 정보
     */
    @GetMapping
    public ResponseEntity<LoadResult> getBoards(
            @RequestParam(defaultValue = "infinite") String strategy,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        log.info("게시글 목록 조회 요청 - strategy: {}, page: {}, size: {}", strategy, page, size);

        try {
            LoadResult result = boardService.getBoards(strategy, page, size);
            log.info("게시글 목록 조회 성공 - 조회된 게시글 수: {}", result.getBoards().size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 새로운 게시글 생성
     *
     * @param request 게시글 생성 요청 데이터
     * @return 생성된 게시글 정보
     */
    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody BoardRequest request) {
        log.info("게시글 생성 요청 - title: {}, author: {}", request.getTitle(), request.getAuthor());

        try {
            BoardResponse response = boardService.createBoard(request);
            log.info("게시글 생성 성공 - ID: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("게시글 생성 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 특정 게시글 조회
     *
     * @param id 게시글 ID
     * @return 게시글 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable Long id) {
        log.info("게시글 상세 조회 요청 - ID: {}", id);

        try {
            BoardResponse response = boardService.getBoardById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("게시글을 찾을 수 없음 - ID: {}", id);
            throw e;
        }
    }

    /**
     * 전체 게시글 수 조회
     *
     * @return 전체 게시글 수
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCount() {
        long count = boardService.getTotalBoardCount();
        return ResponseEntity.ok(count);
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e) {
        log.error("잘못된 요청: {}", e.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.error("유효성 검증 실패: {}", message);

        ErrorResponse error = ErrorResponse.builder()
                .message("입력 값이 올바르지 않습니다: " + message)
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * 전역 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        log.error("예상치 못한 오류 발생", e);

        ErrorResponse error = ErrorResponse.builder()
                .message("서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}