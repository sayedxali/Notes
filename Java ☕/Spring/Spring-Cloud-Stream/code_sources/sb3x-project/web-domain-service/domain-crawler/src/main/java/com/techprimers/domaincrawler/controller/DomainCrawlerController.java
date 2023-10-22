package com.techprimers.domaincrawler.controller;

import com.techprimers.domaincrawler.service.DomainCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/domain")
public class DomainCrawlerController {

    private final DomainCrawlerService domainCrawlerService;

    @GetMapping("/lookup/{name}")
    public String lookupDomain(@PathVariable String name) {
        domainCrawlerService.crawl(name);
        return "Domain crawler has scrapped your data";
    }

}
