package kr.penta.assignment.board.strategy;

import java.util.List;
import java.util.stream.Collectors;
import kr.penta.assignment.board.dto.BoardDto.BoardResponse;
import kr.penta.assignment.board.dto.BoardDto.LoadResult;
import kr.penta.assignment.board.entity.Board;
import kr.penta.assignment.board.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("paginationStrategy")
public class PaginationStrategy implements LoadStrategy {

    @Override
    public LoadResult loadBoards(BoardRepository repository, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Board> boardPage = repository.findAll(pageable);

        List<BoardResponse> boards = boardPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return LoadResult.builder()
                .boards(boards)
                .hasNext(boardPage.hasNext())
                .hasPrevious(boardPage.hasPrevious())
                .totalElements(boardPage.getTotalElements())
                .totalPages(boardPage.getTotalPages())
                .currentPage(page)
                .strategy("pagination")
                .build();
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