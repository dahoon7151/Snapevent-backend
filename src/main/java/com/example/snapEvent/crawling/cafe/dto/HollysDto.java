package com.example.snapEvent.crawling.cafe.dto;

import com.example.snapEvent.crawling.cafe.entity.Hollys;
import com.example.snapEvent.crawling.embedded.DateRange;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HollysDto {

    private String title;
    private DateRange dateRange;
    private String image;
    private String href;

    public HollysDto(Hollys hollys) {
        this.title = hollys.getTitle();
        this.dateRange = hollys.getDateRange();
        this.image = hollys.getImage();
        this.href = hollys.getHref();
    }
}
