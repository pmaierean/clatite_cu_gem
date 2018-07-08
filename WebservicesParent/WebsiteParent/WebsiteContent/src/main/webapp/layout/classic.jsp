<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<title><fmt:message key="title"/></title>
		<link rel="icon" href="favicon.ico">
		<link href="css/bootstrap.min.css" rel="stylesheet">
	</head>
	<body>
		<nav class="navbar navbar-expand-sm navbar-dark bg-dark my-sm-0">
			<div class="navbar navbar-inverse">
				<div class="container">
					<a class="nav-link" href="#" id="dropdown01" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						<span class="navbar-toggler-icon"></span>
					</a>
					<div class="dropdown-menu dropdown-submenu" aria-labelledby="dropdown01">
						<c:if test="${ not empty menu }">
							<c:set var="counter" value="2"/>
							<c:forEach items="${ menu }" var="m">
								<c:choose>
									<c:when test="${ not empty m.menus }">
										<a class="dropdown-item nav-link" href="#" title="${ m.title }" id="dropdown${ counter }" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${ m.text }</a>
										<div class="dropdown-menu" aria-labelledby="dropdown${ counter }">
										<c:forEach items="${ m.menus }" var="mc">
											<a class="dropdown-item nav-link" href="${ mc.link }" title="${ mc.title }">${ mc.text }</a>
										</c:forEach>
										</div>										
									</c:when>
									<c:otherwise>
										<a class="dropdown-item nav-link" href="${ m.link }" title="${ m.title }">${ m.text }</a>
									</c:otherwise>
								</c:choose>
								<c:set var="counter" value="${ counter + 1 }"/>
							</c:forEach>
						</c:if>
					</div>
				</div>
			</div>
		</nav>
		<main role="main" class="container">
			<tiles:insertAttribute name="body" />    
		</main>

		<footer class="footer">
			<div class="container">
				<div class="row">
				    <div class="col-12 mx-auto">
				        <div class="text-center">
							<fmt:message key="copyright"/>
				        </div>
				    </div>
				</div>	
			</div>
		</footer>
		<script src="js/bootstrap.min.js"></script>
	</body>
</html>