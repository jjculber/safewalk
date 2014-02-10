<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />

<div class="thin_col">
	<c:choose>
	<c:when test="${not empty currentRoute}">
		<div class="rockwell">
			tonight at ${currentRoute.time}<br/>
			${currentRoute.fromAreaName}<br/>
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
