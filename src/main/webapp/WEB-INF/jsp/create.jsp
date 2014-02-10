<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />

<div class="thin_col">
	<form action="/select" method="get">
	From:
	<select id="fromArea" name="fromArea">
	<c:forEach items="${fromAreas}" var="area">
		<option value="${area.id}">${area.name}</option>
	</c:forEach>
	</select>
	<br/>
	To:
	<select id="toArea" name="toArea">
	<c:forEach items="${toAreas}" var="area">
		<option value="${area.id}">${area.name}</option>
	</c:forEach>
	</select>
	<br/>
	<input type="submit" value="Next" class="button">
	</form>
</div>
<div class="clearer"></div>

<jsp:include page="footer.jsp"/>
