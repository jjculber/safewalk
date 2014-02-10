<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />

<style>
.time_left {
	float: left;
	width: 250px;
	height: 40px;
	text-decoration: none;
}
.time_right {
	float: right;
}
.row {
	width: 300px;
}
.clearer {
	clear: both;
}
.time_left {
background-color: #fff;
border: #aaa solid 1px;
}
.time_right {
border: #aaa solid 1px;
padding: 0 15px;
height: 40px;
background-color: #fff;
width: 15px;
}
</style>
<div class="thin_col">
Select a time
<c:forEach items="${routes}" var="route">
	<option value="${route.id}">${area.name}</option>
	<div class="row">
		<div>
			<a href="/selectRoute?route=${route.id}" class="time_left">
				${route.time}
			</a>
			<div class="time_right">
				${route.number}
			</div>
		</div>
	</div>
</c:forEach>

<div class="clearer"></div>
</div>
<br/>

<jsp:include page="footer.jsp"/>
