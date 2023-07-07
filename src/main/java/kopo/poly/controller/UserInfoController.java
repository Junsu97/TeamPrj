package kopo.poly.controller;

import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserInfoController {
    private final IUserInfoService userInfoService;

    @GetMapping(value="/user/register")
    public String register(){
        log.info(this.getClass().getName() + ".user/register");
        return "/user/register";
    }

    // 회원가입
    @PostMapping(value = "/user/insertUserInfo")
    public String insertUserInfo(HttpServletRequest request, ModelMap model) throws Exception{
        log.info(this.getClass().getName() + ".insertUserInfo Start!");
        int res;
        String msg = "";
        String url = "";

        UserInfoDTO pDTO = null;

        try{
            String user_id = CmmUtil.nvl(request.getParameter("user_id"));
            String user_name = CmmUtil.nvl(request.getParameter("user_name")); // 이름
            String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호
            String email = CmmUtil.nvl(request.getParameter("email")); // 이메일


            log.info("user_id : " + user_id);
            log.info("user_name : " + user_name);
            log.info("password : " + password);

            // 웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUser_id(user_id);
            pDTO.setUser_name(user_name);

            // 비밀번호는 절대로 복호화되지 않도록 해서 알고리즘으로 암호화함
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            // 민감 정보인 이메일은 AES128-CBC로 암호화
            pDTO.setEmail(EncryptUtil.encAES128CBC(email));

            // 회원가입
            res = userInfoService.insertUserInfo(pDTO);

            log.info("회원가입 결과(res) : " + res);

            if(res == 1){
                msg = "회원가입되었습니다.";
                url = "/templates/user/login";
                // 추후 회원가입 입력화면에서 ajax를 활용해서 아이디 중복, 이메일 중복 체크
            }else if(res == 2){
                msg = "이미 가입된 아이디입니다.";
            }else{
                msg = "오류로 인해 회원가입이 실해하였습니다.";
            }
        }catch (Exception e){
            msg = "실패하였습니다. : " + e;
            log.info(e.toString());
            e.printStackTrace();
        }finally {
            model.addAttribute("msg",msg);
            model.addAttribute("url",url);

            log.info(this.getClass().getName() + ".insertUserInfo End!");
        }

        return "/redirect";
    }

    @PostMapping(value = "/user/loginProc")
    public String loginProc(HttpServletRequest request, ModelMap model, HttpSession session){


        log.info(this.getClass().getName() + ".loginProc Start!");
        String msg = ""; // 로그인 결과에 대한 메시지를 전달할 변수
        String url = "";
        // 웹(회원정보 입력화면)에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try{
            String user_id = CmmUtil.nvl(request.getParameter("user_id"));
            String password = CmmUtil.nvl(request.getParameter("password"));

            log.info("user_id : " + user_id);
            log.info("password : " + password);

            // 웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();
            pDTO.setUser_id(user_id);

            // 비밀번호는 절대로 복호화되지 않도록 해서 알고리즘으로 암호화
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));
            // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기 위해 userInfoService 호출하기
            UserInfoDTO rDTO = userInfoService.getLogin(pDTO);

            if(CmmUtil.nvl(rDTO.getUser_id()).length() > 0){

                session.setAttribute("SS_USER_ID", user_id);
                session.setAttribute("SS_USER_NAME", CmmUtil.nvl(rDTO.getUser_name()));

                // 로그인 성공 메세지와 이동할 경로의 url
                msg = "로그인이 성공했습니다. \n" + rDTO.getUser_name()+"님 환영합니다.";
                url = "/notice/tables";
            }else{
                msg = "로그인 실패 \n회원가입 페이지로 이동합니다.";
                url = "/templates/user/register";
            }


        }catch (Exception e){
            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "시스템 문제로 로그인이 실패했습니다.";
            log.info(e.toString());
            e.printStackTrace();
        }finally {
            model.addAttribute("msg",msg);
            model.addAttribute("url",url);

            log.info(this.getClass().getName() + ".loginProc End!");
        }
        return "/redirect";
    }

    @GetMapping(value = "/user/login")
    public String login(){
        log.info(this.getClass().getName() + ".user/login Start!");
        log.info(this.getClass().getName() + ".user/login End!");
        return "/user/login";
    }
    /**회원가입 전 아이디 중복체크*/
    @ResponseBody
    @PostMapping(value = "/user/getUserIdExists")
    public UserInfoDTO getUserExists(HttpServletRequest request) throws Exception{
        log.info(this.getClass().getName() + ".getUserExists Start!");

        String user_id = CmmUtil.nvl(request.getParameter("user_id"));

        log.info("user_id : " + user_id);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUser_id(user_id);

        // 회원아이디를 통해 중복된 아이디인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getUserExists End!");
        return rDTO;
    }

    /**
     * 유효한 이메일인지 확인하기 위해 입력된 이메일에 인증번호 포함하여 메일 발송
     * */
    @ResponseBody
    @PostMapping(value = "/user/getEmailExists")
    public UserInfoDTO getEmailExists(HttpServletRequest request) throws Exception{
        log.info(this.getClass().getName() + ".getEmailExists Start!");

        String email = CmmUtil.nvl(request.getParameter("email")); // 회원아이디

        log.info("email : " + email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        // 입력된 이메일이 중복된 이메일인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getEmailExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getEmailExists End!");

        return rDTO;
    }

}
