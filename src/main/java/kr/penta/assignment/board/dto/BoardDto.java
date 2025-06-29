package kr.penta.assignment.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardRequest {

        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다")
        private String title;

        @NotBlank(message = "내용은 필수입니다")
        private String content;

        @NotBlank(message = "작성자는 필수입니다")
        @Size(max = 50, message = "작성자명은 50자를 초과할 수 없습니다")
        private String author;
    }

    @Getter
    @Builder
    public static class BoardResponse {

        private Long id;
        private String title;
        private String content;
        private String author;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoadResult {

        private List<BoardResponse> boards;
        private boolean hasNext;
        private boolean hasPrevious;
        private long totalElements;
        private int totalPages;
        private int currentPage;
        private String strategy;
    }

    @Getter
    @Builder
    public static class ErrorResponse {

        private String message;
        private int status;
        private LocalDateTime timestamp;
    }
}
