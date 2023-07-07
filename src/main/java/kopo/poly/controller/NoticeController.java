package kopo.poly.controller;

import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.INoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class NoticeController {
    private final INoticeService noticeService;
    @GetMapping( value = "/notice/noticeList")
    public String noticeList(ModelMap model) throws Exception{
        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이)
        log.info(this.getClass().getName() + ".noticeList Start!");

        // 공지사항 리스트 조회
        List<NoticeDTO> rList = noticeService.getNoticeList();
        if(rList == null) rList = new ArrayList<>();
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception)처리
        // List<NoticeDTO> rList = Optional.ofNullable(noticeService.getNoticeList())

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        // 로그찍기
        log.info(this.getClass().getName() + ".noticeList End!");

        // 함수 처리가 끝나고 보여줄 html파일명
        return "tables";
    }
}
