package com.BitOracle.BitOracle.domain;

import com.BitOracle.BitOracle.domain.enums.NewsType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long newsId;
    @Column(name = "news_title")
    private String newsTitle;
    @Column(name = "news_content")
    private String  newsContent;
    @Column(name = "news_url")
    private String newsUrl;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "news_type")
    @Enumerated(EnumType.STRING)
    private NewsType newsType;
}
