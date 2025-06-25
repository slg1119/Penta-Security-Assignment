package kr.penta.assignment.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "BOARD")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "TITLE")
    @Comment("게시글 제목")
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT", name = "CONTENT")
    @Comment("게시글 내용")
    private String content;


    @NotNull
    @Size(max = 50)
    @Column(name = "AUTHOR")
    @Comment("게시글 작성자")
    private String author;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    @Comment("게시글 작성 시간")
    private LocalDateTime createdAt;
}
