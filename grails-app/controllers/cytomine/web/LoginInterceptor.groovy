package cytomine.web

import cytomine.core.security.AuthWithToken
import cytomine.core.security.SecUser
import cytomine.core.security.User
import cytomine.core.utils.SecurityUtils
import grails.artefact.Interceptor
import grails.plugin.springsecurity.SpringSecurityUtils

import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse


class LoginInterceptor {

    boolean before() {
        println getClass().toString() + '001 BEFORE '

        //log with token id
        boolean token = tryAPIAUhtentificationWithToken(request, response)
        if(!token) {
            //with signature (in header)
            println getClass().toString() + '002'
            tryAPIAuthentification(request, response)
        }
        true
    }

    boolean tryAPIAuthentification(def request, def response) {
        println getClass().toString() + '001 ' + request.getHeader("date")
        println getClass().toString() + '002 ' + request.getHeader("host")
        println getClass().toString() + '003 ' + request.getHeader("authorization")

        if (request.getHeader("date") == null) {
            println getClass().toString() + '004' + 'request.getHeader("date") == null'
            return false
        }
        if (request.getHeader("host") == null) {
            println getClass().toString() + '005 ' + 'request.getHeader("host") == nulll'
            return false
        }
        String authorization = request.getHeader("authorization")
        if (authorization == null) {
            println getClass().toString() + '006 ' + 'authorization == nulll'
            return false
        }
        if (!authorization.startsWith("CYTOMINE") || !authorization.indexOf(" ") == -1 || !authorization.indexOf(":") == -1) {
            println getClass().toString() + '007 ' + '!authorization.startsWith("CYTOMINE") || !authorization.indexOf(" ") == -1 || !authorization.indexOf(":") == -1'
            return false
        }
        try {
            String content_md5 = (request.getHeader("content-MD5") != null) ? request.getHeader("content-MD5") : ""
            String content_type = (request.getHeader("content-type") != null) ? request.getHeader("content-type") : ""
            content_type = (request.getHeader("Content-Type") != null) ? request.getHeader("Content-Type") : content_type
            String date = (request.getHeader("date") != null) ? request.getHeader("date") : ""

            println getClass().toString() + '008 ' + "content_md5 = $content_md5"
            println getClass().toString() + '009 ' + "content_type = $content_type"
            println getClass().toString() + '010 ' + "date = $date"

            String queryString = (request.getQueryString() != null) ? "?" + request.getQueryString() : ""

            String path = request.forwardURI //original URI Request

            String accessKey = authorization.substring(authorization.indexOf(" ") + 1, authorization.indexOf(":"))
            String authorizationSign = authorization.substring(authorization.indexOf(":") + 1)
            SecUser user = SecUser.findByPublicKeyAndEnabled(accessKey, true)

            if (!user) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return false
            }
            println getClass().toString() + '002' + user.toString()

            String signature = SecurityUtils.generateKeys(request.getMethod(), content_md5, content_type, date, queryString, path, user)
            if (authorizationSign == signature) {
                SpringSecurityUtils.reauthenticate user.getUsername(), null
                return true
            } else {
                return false
            }


        } catch (Exception e) {
            e.printStackTrace()
            return false
        }
        return false
    }

    private boolean tryAPIAUhtentificationWithToken(ServletRequest request, ServletResponse response) {
        String tokenKey = request.getParameter("tokenKey");

        if(tokenKey!=null) {
            String username = request.getParameter("username")
            User user = User.findByUsername(username) //we are not logged, we bypass the service
            AuthWithToken authToken = AuthWithToken.findByTokenKeyAndUser(tokenKey, user)
            //check first if a entry is made for this token
            if (authToken && authToken.isValid())  {
                SpringSecurityUtils.reauthenticate user.username, null
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }

    boolean after() {true
    }

    void afterView() {
        // no-op
    }
}
