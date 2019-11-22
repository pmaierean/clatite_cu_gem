<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<link href="css/decoding.css" rel="stylesheet">
		<link href="css/bootstrap.min.css" rel="stylesheet">
	</head>
	<body>
		<div class="container">
			<tiles:insertAttribute name="body" />
		</div>
	    <script src="js/jquery.min.js" type="text/javascript"></script>
	    <script src="js/bootstrap.min.js" type="text/javascript"></script
	    <script src="js/decoding.js" type="text/javascript"></script>		
	</body>
</html>