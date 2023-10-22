package com.techprimers.domainservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain models are from :
 * <a href="https://api.domainsdb.info/v1/domains/search?domain=facebook&zone=com">
 * domainsdb.info
 * </a>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Domain {

    private String domain;
    private String create_date;
    private String update_date;
    private String country;
    private boolean isDead;
    private String A;
    private String NS;
    private String CNAME;
    private String MX;
    private String TXT;

}
