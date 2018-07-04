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
							<p><bean:message key="copyright"/></p>
							<p><bean:message key="application.version"/></p>
				        </div>
				    </div>
				</div>	
			</div>
		</footer>
	</body>
</html>