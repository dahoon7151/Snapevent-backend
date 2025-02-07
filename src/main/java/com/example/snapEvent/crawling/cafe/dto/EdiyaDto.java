package com.example.snapEvent.crawling.cafe.dto;

import com.example.snapEvent.crawling.cafe.entity.Ediya;
import com.example.snapEvent.crawling.embedded.DateRange;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EdiyaDto {

    private String title;
    private DateRange dateRange;
    private String image;
    private String href;

    public EdiyaDto(Ediya ediya) {
        this.title = ediya.getTitle();
        this.dateRange = ediya.getDateRange();
        this.image = ediya.getImage();
        this.href = ediya.getHref();
    }
}
