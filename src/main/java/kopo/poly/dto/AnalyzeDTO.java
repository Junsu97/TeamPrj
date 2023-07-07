package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalyzeDTO {
    private String analyze_seq;
    private String contents;
    private String title;
    private String url;
    private String mention;
    private String definition;
    private String user_id;
    private String read_cnt;
    private String reg_id;
    private String reg_dt;
}
