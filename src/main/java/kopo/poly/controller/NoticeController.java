package kopo.poly.controller;

import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.INoticeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
public class NoticeController {
//    // @RequiredArgsConstructor를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입
    private final INoticeService noticeService;
//
//    /*
//    * 게시판 리스트 보여주기
//    * */
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
        return "/notice/noticeList";
    }
//
//    /*
//    * <p>
//    * 이 함수는 게시판 작성 페이지로 접근하기 위해 만듬
//    * <p>
//    * */
    @GetMapping(value = "/notice/noticeReg")
    public String noticeReg(){
        log.info(this.getClass().getName() + ".noticeReg Start!");
        log.info(this.getClass().getName() + ".noticeReg End!");

        return "/notice/noticeReg";
    }
//
//
//    /*
//    * 게시판 글 등록
//    * <p>
//    * 게시판 등록은 Ajax로 호출되기 때문에 결과는 JSO 구조로 전달해야 함
//    * JSON 구조로 결과 메시지를 전송하기 위해 @ResponseBody 어노테이션 추가
//    * <p>
//    *
//    * */
    @PostMapping(value = "/notice/noticeInsert")
    public String noticeInsert(HttpServletRequest request, ModelMap model, HttpSession session){
        log.info(this.getClass().getName() + ".noticeInsert Start!");

        String msg = "";
        String url = "/notice/noticeReg";

        try{
            // 로그인된 사용자 아이디 가져오기
            // 로그인을 아직 구현하지 않았기에 공지사항 리스트에서 로그인한 것 처럼 Session 값을 저장
            String user_id = CmmUtil.nvl((String)session.getAttribute("SS_USER_ID"));
            String title = CmmUtil.nvl(request.getParameter("title"));

            String contents = CmmUtil.nvl(request.getParameter("contents"));

            /*
            * ############################################################
            * 반드시, 값을 받았으면 꼭 로그를 찍어서 값이 제대로 들어오는지 파약해야함 반드시 작성
            * ############################################################
            * */
            log.info("session user_id : " + user_id);
            log.info("title : " + title);

            log.info("contents : " + contents);

            // 데이터 저장하기 위해 DTO에 저장
            NoticeDTO pDTO = new NoticeDTO();
            pDTO.setUser_id(user_id);
            pDTO.setTitle(title);

            pDTO.setContents(contents);


            /*
            * 게시글 등록하기위한 비즈니스 로직 호출
            * */
            noticeService.insertNoticeInfo(pDTO);

            // 저장이 완료되면 사용자에게 보여줄 메시지
            msg = "등록되었습니다.";
            url = "/notice/noticeList";
        }catch (Exception e){
            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        }finally {
            model.addAttribute("msg",msg);
            model.addAttribute("url",url);
            log.info(this.getClass().getName() + ".noticeInsert End!");
        }

        return "/redirect";
    }

    /*
    * 게시판 상세보기
    * */
    @GetMapping(value = "/notice/noticeInfo")
    public String noticeInfo(HttpServletRequest request, ModelMap model) throws Exception{
        log.info(this.getClass().getName() + ".noticeInfo End!");

        String nSeq = CmmUtil.nvl(request.getParameter("nSeq")); // 공지글 번호(PK)

        /*
        * #########################################################
         * 반드시, 값을 받았으면 꼭 로그를 찍어서 값이 제대로 들어오는지 파약해야함 반드시 작성
        * #########################################################
        * */

        log.info("nSeq : " + nSeq);

        /*
        * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO객체에 넣는다.
        * */

        NoticeDTO pDTO = new NoticeDTO();
        pDTO.setNotice_seq(nSeq);

        // 공지사항 상세정보 가져오기
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리

        NoticeDTO rDTO = Optional.ofNullable(noticeService.getNoticeInfo(pDTO,true)).orElseGet(NoticeDTO::new);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO",rDTO);

        log.info(this.getClass().getName() + ".noticeInfo End!");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "/notice/noticeInfo";
    }

    /*
    * 게시판 수정을 위한 페이지
    * */
    @GetMapping(value = "/notice/noticeEditInfo")
    public String noticeEditInfo(HttpServletRequest request, ModelMap model) throws Exception{
        log.info(this.getClass().getName() + ".noticeEditInfo Start!");

        String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));

        /*
         * #########################################################
         * 반드시, 값을 받았으면 꼭 로그를 찍어서 값이 제대로 들어오는지 파약해야함 반드시 작성
         * #########################################################
         * */
        log.info("nSeq : " + nSeq);

        /*
         * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO객체에 넣는다.
         * */
        NoticeDTO pDTO = new NoticeDTO();
        pDTO.setNotice_seq(nSeq);

        NoticeDTO rDTO = noticeService.getNoticeInfo(pDTO, false);
        if(rDTO == null) rDTO = new NoticeDTO();

        //조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO",rDTO);

        log.info(this.getClass().getName() + ".noticeEditInfo End!");

        return "/notice/noticeEditInfo";
    }

    /*
    * 게시판 글 수정 실행 로직
    * */
    @PostMapping(value = "/notice/noticeUpdate")
    public String noticeUpdate(HttpServletRequest request, ModelMap model, HttpSession session){
        log.info(this.getClass().getName() + ".noticeUpdate Start!");

        String msg = "";
        String url = "";

        try{
            log.info(CmmUtil.nvl("SS_USER_ID : "+(String)session.getAttribute("SS_USER_ID")));
            String user_id = CmmUtil.nvl((String)session.getAttribute("SS_USER_ID"));
            String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));

            log.info("session user_id : " + user_id);
            log.info("nSeq : " + nSeq);
            log.info("title : " + title);
            log.info("contents : " + contents);

            // 데이터 저장하기 위해 DTO에 저장
            NoticeDTO pDTO = new NoticeDTO();
            pDTO.setUser_id(user_id);
            pDTO.setNotice_seq(nSeq);
            pDTO.setTitle(title);
            pDTO.setContents(contents);

            noticeService.updateNoticeInfo(pDTO);

            msg = "수정되었습니다.";
            url = "notice/noticeInfo?nSeq="+nSeq;
        }catch (Exception e){
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        }finally {
            model.addAttribute("msg",msg);
            model.addAttribute("url",url);
            log.info(this.getClass().getName() + ".noticeUpdate End!");
        }
        return "/redirect";
    }

    @GetMapping(value = "/notice/noticeDelete")
    public String noticeDelete(HttpSession session, HttpServletRequest request, ModelMap model){
        log.info(this.getClass().getName() + ".noticeDelete Start!");
        String msg = "";
        String url = "";

        try{
            String user_id = CmmUtil.nvl((String)session.getAttribute("SS_USER_ID"));
            String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));

            log.info("session user_id : " + user_id);
            log.info("nSeq : " + nSeq);
            log.info("title : " + title);
            log.info("contents : " + contents);

            // 데이터 저장하기 위해 DTO에 저장
            NoticeDTO pDTO = new NoticeDTO();
            pDTO.setUser_id(user_id);
            pDTO.setNotice_seq(nSeq);
            pDTO.setTitle(title);
            pDTO.setContents(contents);

            noticeService.deleteNoticeInfo(pDTO);

            msg = "삭제.";
            url = "/notice/noticeList?nSeq=" + nSeq;
        }catch (Exception e){
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        }finally {
            model.addAttribute("msg",msg);
            model.addAttribute("url",url);
            log.info(this.getClass().getName() + ".noticeUpdate End!");
        }

        return "redirect";
    }

}
