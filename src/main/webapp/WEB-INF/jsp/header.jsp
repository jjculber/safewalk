<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>${title}</title>
<script src="/resources/js/jquery-1.10.2.min.js"></script>
<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon"></link>
<link rel="icon" href="/favicon.ico" type="image/x-icon"></link>

</head>

<body>
<div class="page_width">
   <div class="nav_bar">
      <a href="/" class="tab tab_left ${home}">Home</a>
      <sec:authorize access="isAnonymous()">
         <form class="login_form" action="/login" method="post">
            <div class="login_fields">
               <input type="text" id="j_username" name="j_username"
                  placeholder="Username" class="login_input"/> 
               <input type="password" id="j_password" name="j_password" 
                  placeholder="Password"  class="login_input"/>
               <input class="login_submit" type="submit" value="Log in" />
            </div>
         </form>
      </sec:authorize>
      <sec:authorize access="isAuthenticated()">
         <a class="logout_button tab" href="/logout">Log Out</a>
      </sec:authorize>
   </div>
</div>
<div class="page_width center_unit">
