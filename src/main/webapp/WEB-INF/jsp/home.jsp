<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />

<div class="thin_col">
<div class="error">
	<c:out value="${error}" />
</div>
	
   <form class="login_form" action="/login" method="post">
   Log in
      <div class="login_fields">
         <input type="text" id="j_username" name="j_username"
            placeholder="Username" class="login_input"/> <br/>
         <input type="password" id="j_password" name="j_password" 
            placeholder="Password"  class="login_input"/><br/>
         <input class="button" type="submit" value="Submit" />
      </div>
   </form>
   <br/>
   or <a href="/signupform">create account</a>
   <br/><br/>

</div>
<div class="clearer"></div>

<jsp:include page="footer.jsp"/>
