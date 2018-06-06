<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<html>
	<head>
		<title><tiles:getAsString name="title"/></title>
		<link rel="stylesheet" href="/bootstrap-3.3.7/css/bootstrap.min.css">
	</head>
	<body>
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-9 col-md-push-3">
					<tiles:insertAttribute name="header" />				
				</div>
			</div>
			<div class="row">
				<div class="col-md-9 col-md-push-3">
					<tiles:insertAttribute name="body" />				
				</div>
			</div>
			<div class="row">
				<div class="col-md-9 col-md-push-3">
					<tiles:insertAttribute name="footer" />				
				</div>
			</div>
		</div>
	</body>
</html>