<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />

<div class="thin_col">
	<h2>profile</h2>
	<div class="grey_text rockwell">
	${user.fullName}<br/>
	${user.email}
	</div>
	<h2>walks</h2>
	<c:choose>
	<c:when test="${not empty currentRoute}">
		<div class="rockwell grey_text">
			tonight at ${currentRoute.time}<br/>
			${currentRoute.fromAreaName} to<br/>
			${currentRoute.toAreaName}<br/>
			<a href="/cancel" class="button">cancel walk</a>
		</div>
	</c:when>
	<c:otherwise>
		<a href="/create" class="button">schedule new walk</a>
	</c:otherwise>
	</c:choose>
</div>
<div class="clearer"></div>

<jsp:include page="footer.jsp"/>
