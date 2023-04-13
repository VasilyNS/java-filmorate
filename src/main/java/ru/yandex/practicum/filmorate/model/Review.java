package ru.yandex.practicum.filmorate.model;

import lombok.*;

/**
 * целочисленный идентификатор — id;
 * текст отзыва — content;
 * оценка — isPositive;
 * рейтинг (соотношение лайков и дизлайков) — useful;
 * id пользователя, оставившего отзыв - userId;
 * id фильма, на который оставлен отзыв - filmId;
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Review {

    private Integer reviewId;
    private String content;
    private Boolean isPositive;
    private Integer useful = 0;
    @NonNull
    private Integer userId;
    @NonNull
    private Integer filmId;
}
