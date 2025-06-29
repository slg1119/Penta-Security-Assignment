package kr.penta.assignment.board.service;

import java.util.Map;
import kr.penta.assignment.board.dto.BoardDto.BoardRequest;
import kr.penta.assignment.board.dto.BoardDto.BoardResponse;
import kr.penta.assignment.board.dto.BoardDto.LoadResult;
import kr.penta.assignment.board.entity.Board;
import kr.penta.assignment.board.repository.BoardRepository;
import kr.penta.assignment.board.strategy.LoadStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final Map<String, LoadStrategy> loadStrategies;

    /**
     * 선택된 전략에 따라 게시글 목록을 조회
     *
     * @param strategy 로딩 전략 ("infinite" 또는 "pagination")
     * @param page     페이지 번호
     * @param size     페이지 크기
     * @return 게시글 목록과 메타 정보
     */
    public LoadResult getBoards(String strategy, int page, int size) {
        String strategyKey = strategy + "Strategy";
        LoadStrategy loadStrategy = loadStrategies.get(strategyKey);

        if (loadStrategy == null) {
            throw new IllegalArgumentException("지원하지 않는 로딩 전략입니다: " + strategy +
                    ". 지원 전략: infinite, pagination");
        }

        return loadStrategy.loadBoards(boardRepository, page, size);
    }

    /**
     * 새로운 게시글을 생성
     *
     * @param request 게시글 생성 요청
     * @return 생성된 게시글 정보
     */
    @Transactional
    public BoardResponse createBoard(BoardRequest request) {
        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(request.getAuthor())
                .build();

        Board savedBoard = boardRepository.save(board);
        return convertToResponse(savedBoard);
    }

    /**
     * 게시글 ID로 특정 게시글 조회
     *
     * @param id 게시글 ID
     * @return 게시글 정보
     */
    public BoardResponse getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));

        return convertToResponse(board);
    }

    /**
     * 전체 게시글 수 조회
     *
     * @return 전체 게시글 수
     */
    public long getTotalBoardCount() {
        return boardRepository.count();
    }

    private BoardResponse convertToResponse(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .author(board.getAuthor())
                .createdAt(board.getCreatedAt())
                .build();
    }
}