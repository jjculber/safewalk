<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />

<div>
	<c:out value="${error}" />
</div>
<div class="thin_col">
   <form class="signin_form" action="/signup" method="post">
   create account
		<div class="signup_fields">
         <input type="text" id="email" name="email" placeholder="CalPoly email" class="login_input"/> <br/>
         <input type="text" id="firstName" name="firstName" placeholder="First Name" class="login_input"/> <br/>
         <input type="text" id="lastName" name="lastName" placeholder="Last Name" class="login_input"/> <br/>
         <input type="password" id="password" name="password" placeholder="Password"  class="login_input"/><br/>
         <input class="button" type="submit" value="submit" />
		</div>
   </form>
</div>

<div class="clearer"></div>

<jsp:include page="footer.jsp"/>
