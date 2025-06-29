package kr.penta.assignment.board.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import kr.penta.assignment.board.dto.BoardDto.BoardRequest;
import kr.penta.assignment.board.dto.BoardDto.BoardResponse;
import kr.penta.assignment.board.dto.BoardDto.LoadResult;
import kr.penta.assignment.board.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    @Test
    @DisplayName("게시글 목록 조회 API 테스트")
    void getBoards_Success() throws Exception {
        // given
        BoardResponse boardResponse = BoardResponse.builder()
                .id(1L)
                .title("테스트 제목")
                .content("테스트 내용")
                .author("테스트 작성자")
                .createdAt(LocalDateTime.now())
                .build();

        LoadResult loadResult = LoadResult.builder()
                .boards(Arrays.asList(boardResponse))
                .hasNext(true)
                .totalElements(1L)
                .currentPage(0)
                .strategy("infinite")
                .build();

        when(boardService.getBoards(anyString(), anyInt(), anyInt())).thenReturn(loadResult);

        // when & then
        mockMvc.perform(get("/api/boards")
                        .param("strategy", "infinite")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.strategy").value("infinite"))
                .andExpect(jsonPath("$.boards[0].title").value("테스트 제목"));
    }

    @Test
    @DisplayName("게시글 생성 API 테스트")
    void createBoard_Success() throws Exception {
        // given
        BoardRequest request = BoardRequest.builder()
                .title("새 게시글")
                .content("새 내용")
                .author("새 작성자")
                .build();

        BoardResponse response = BoardResponse.builder()
                .id(1L)
                .title("새 게시글")
                .content("새 내용")
                .author("새 작성자")
                .createdAt(LocalDateTime.now())
                .build();

        when(boardService.createBoard(any(BoardRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("새 게시글"))
                .andExpect(jsonPath("$.author").value("새 작성자"));
    }

    @Test
    @DisplayName("유효하지 않은 게시글 생성 요청 시 400 에러")
    void createBoard_InvalidRequest_BadRequest() throws Exception {
        // given
        BoardRequest invalidRequest = BoardRequest.builder()
                .title("") // 빈 제목
                .content("내용")
                .author("작성자")
                .build();

        // when & then
        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}