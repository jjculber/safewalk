<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<sec:authorize access="isAuthenticated()">
<div class="footer_bar">
   <a class="logout_button" href="/logout" style="float:right;">Log Out</a>
</div>
</sec:authorize>

</div>
</body>
</html>
