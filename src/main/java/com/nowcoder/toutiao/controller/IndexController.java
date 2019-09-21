package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.model.User;
import com.nowcoder.toutiao.service.WendaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class IndexController {

    @Autowired
    WendaService wendaService;
    private static final Logger logger = LoggerFactory.getLogger (IndexController.class);

    @RequestMapping ( path = { "/", "/index" }, method = { RequestMethod.GET } )
    @ResponseBody
    public String index (HttpSession httpSession) {
        logger.info ("vist");
        return wendaService.getMessage (2) + "hello nowcoder" + httpSession.getAttribute ("msg");
    }

    @RequestMapping ( path = { "/profile/{groupId}/{userId}" } )
    @ResponseBody
    public String profile (@PathVariable ( "userId" ) int userId,
                           @PathVariable ( "groupId" ) String groupId,
                           @RequestParam ( value = "type", defaultValue = "1" ) int type,
                           @RequestParam ( value = "key", defaultValue = "ss", required = false ) String key) {
        return String.format ("Profile Page of %s/%d,t:%d k:%s", groupId, userId, type, key);
    }

    @RequestMapping ( path = { "/vm" }, method = { RequestMethod.GET } )
    public String template (Model model) {
//        model.addAttribute("s","as");
        model.addAttribute ("value1", "vvvvvv1");
        List<String> colors = Arrays.asList (new String[]{ "RED", "GREEN", "BLUE" });
        model.addAttribute ("colors", colors);
        Map<String, String> map = new HashMap<> ();
        for (int i = 0; i < 4; i++) {
            map.put (String.valueOf (i), String.valueOf (i + 1));
        }
        model.addAttribute ("map", map);
        model.addAttribute ("user", new User ("zhouli"));

        return "home";
    }

    @RequestMapping ( path = { "/request" }, method = { RequestMethod.GET } )
    @ResponseBody
    public String template (Model model, HttpServletResponse response,
                            HttpServletRequest request,
                            HttpSession session,
                            @CookieValue ( "JSESSIONID" ) String sessionId) {
        StringBuilder sb = new StringBuilder ();
        sb.append ("cookie:" + sessionId + "<br>");
        Enumeration<String> headerName = request.getHeaderNames ();
        while (headerName.hasMoreElements ()) {
            String name = headerName.nextElement ();
            sb.append (name + ":" + request.getHeader (name) + "<br>");
        }
        if (request.getCookies () != null) {
            for (Cookie cookie : request.getCookies ()) {
                sb.append ("Cookie:" + cookie.getName () + "Value:" + cookie.getValue ());
            }
        }
        sb.append (request.getMethod () + "<br>");
        sb.append (request.getQueryString () + "<br>");
        sb.append (request.getPathInfo () + "<br>");
        sb.append (request.getRequestURI () + "<br>");
        response.addHeader ("sa", "sad");
        response.addCookie (new Cookie ("username", "password"));
        return sb.toString ();


    }

    @RequestMapping ( path = { "/redirect/{code}" }, method = { RequestMethod.GET } )
//    @ResponseBody
    public RedirectView redirect (@PathVariable ( "code" ) int code,
                                  HttpSession httpSession) {
        httpSession.setAttribute ("msg", "jump from redirect");
        RedirectView red = new RedirectView ("/", true);
        if (code == 301) {
            red.setStatusCode (HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }

    @RequestMapping ( path = { "/admin" }, method = { RequestMethod.GET } )
    @ResponseBody
    public String admin (@RequestParam ( "key" ) String key) {
        if ("admin".equals (key)) {
            return "hello admin";
        }
        throw new IllegalArgumentException ("参数不对");
    }

    @ExceptionHandler ()
    @ResponseBody
    public String error (Exception e) {
        return "error:" + e.getMessage ();
    }

}
