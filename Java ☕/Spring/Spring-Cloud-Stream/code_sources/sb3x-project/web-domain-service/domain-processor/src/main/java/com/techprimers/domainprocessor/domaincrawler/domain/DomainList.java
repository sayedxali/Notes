package com.techprimers.domainprocessor.domaincrawler.domain;

import com.techprimers.domainprocessor.config.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomainList {

    List<Domain> domains;

}
