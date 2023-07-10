package kopo.poly.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.oauth.NaverLoginBO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@Controller
public class NaverController {

    private final IUserInfoService userInfoService;
    /**
     * 네이버 로그인
     * */
    private NaverLoginBO naverLoginBO;
    private UserInfoController userInfoController;
    private String apiResult = null;

    private String naverAuthUrl = "/user/login";


    @Autowired
    private void setNaverLoginBO(NaverLoginBO naverLoginBO) {
        log.info(this.getClass().getName() + ".setNaverLoginBO Start!");
        this.naverLoginBO = naverLoginBO;
        log.info(this.getClass().getName() + ".setNaverLoginBO End!");
    }

    // 로그인페이지
    //로그인 첫 화면 요청 메소드

    @RequestMapping(value = "/user/login.do", method = { RequestMethod.GET, RequestMethod.POST })
    public String login(Model model, HttpSession session) {
        log.info(this.getClass().getName() + ".naverLogin Start!");
        /* 네아로 인증 URL을 생성하기 위하여 naverLoginBO클래스의 getAuthorizationUrl메소드 호출 */
        naverAuthUrl = naverLoginBO.getAuthorizationUrl(session);
        /* 인증요청문 확인 */
        log.info("네이버:" + naverAuthUrl);
        /* 객체 바인딩 */
        model.addAttribute("urlNaver","redirect:"+ naverAuthUrl);
        log.info(this.getClass().getName() + ".naverLogin End!");
        /* 생성한 인증 URL을 View로 전달 */
        return "redirect:"+naverAuthUrl;
    }


    //네이버 로그인 성공시 callback호출 메소드
    @RequestMapping(value = "/login/oauth2/code/naver", method = { RequestMethod.GET, RequestMethod.POST })
    public String callbackNaver(HttpServletRequest request, Model model, @RequestParam String code, @RequestParam String state, HttpSession session)
            throws Exception {
        log.info("로그인 성공 callbackNaver");
        UserInfoDTO pDTO = null;

        int res;

        OAuth2AccessToken oauthToken;
        oauthToken = naverLoginBO.getAccessToken(session, code, state);
        //로그인 사용자 정보를 읽어온다.
        apiResult = naverLoginBO.getUserProfile(oauthToken);

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj;

        jsonObj = (JSONObject) jsonParser.parse(apiResult);
        JSONObject response_obj = (JSONObject) jsonObj.get("response");
        // 프로필 조회
        String email = (String) response_obj.get("email");
        String name = (String) response_obj.get("name");
        String password = "1234";

        String msg = "";
        String url = "";

        try{
            pDTO = new UserInfoDTO();

            pDTO.setUser_id(email);
            pDTO.setEmail(email);
            pDTO.setUser_name(name);
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            log.info(userInfoService.getUserIdExists(pDTO).getExists_yn());
            if(userInfoService.getUserIdExists(pDTO).getExists_yn().equals("N")){
                userInfoService.insertUserInfo(pDTO);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            msg = "로그인이 성공했습니다. \n " + pDTO.getUser_name()+ "님 환영합니다!";
            url = "/main";
            log.info("msg : ", msg);
            log.info("url : ", url);
            log.info(email);
            log.info(name);
            model.addAttribute("msg",msg);
            model.addAttribute("url",url);
        }

        // 세션에 사용자 정보 등록
        // session.setAttribute("islogin_r", "Y");
        session.setAttribute("signIn", apiResult);
        session.setAttribute("SS_USER_ID", email);
        session.setAttribute("SS_USER_NAME", name);

        /* 네이버 로그인 성공 페이지 View 호출 */
        return "/redirect";
    }

    // 소셜 로그인 성공 페이지
    @RequestMapping("/user/loginSuccess.do")
    public String loginSuccess() {
        return "/user/loginResult";
    }
}
