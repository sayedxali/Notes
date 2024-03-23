package com.dev.vault.helper.payload.group;

import com.dev.vault.model.project.enums.JoinStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JoinResponse {
    private String status;
    private JoinStatus joinStatus;
}
