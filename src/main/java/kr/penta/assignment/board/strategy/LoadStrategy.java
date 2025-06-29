package kr.penta.assignment.board.strategy;

import kr.penta.assignment.board.dto.BoardDto.LoadResult;
import kr.penta.assignment.board.repository.BoardRepository;

/**
 * 게시글 로딩 전략을 정의하는 인터페이스 전략패턴(Strategy Pattern)의 핵심 인터페이스
 */
public interface LoadStrategy {

    /**
     * 게시글 목록을 로드하는 전략 메서드
     *
     * @param repository 게시글 리포지토리
     * @param page       페이지 번호 (0부터 시작)
     * @param size       페이지 크기
     * @return 로드된 게시글 목록과 메타 정보
     */
    LoadResult loadBoards(BoardRepository repository, int page, int size);

    /**
     * 전략의 이름을 반환
     *
     * @return 전략 이름
     */
    default String getStrategyName() {
        return this.getClass().getSimpleName().replace("Strategy", "").toLowerCase();
    }
}