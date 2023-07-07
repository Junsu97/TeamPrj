package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDTO {
    private String userId;
    private String userName;
    private String userPassword;
    private String email;
    private String regId;
    private String regDt;
    private String chgId;
    private String chgDt;

    private int authNumber;
    private String existsYn;
}
