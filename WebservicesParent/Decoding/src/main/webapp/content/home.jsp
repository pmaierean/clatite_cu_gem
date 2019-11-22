<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<h1>Decoding the text</h1>
<div class="row">
	<div class="col-12">
<c:forEach items="${ translation.original }" var="row">
		<div class="row">
			<div class="col-12">
	<c:choose>
		<c:when test="${ not empty row.cols }">
			<c:forEach items="${ row.cols }" var="cr">
				<c:choose>
					<c:when test="${ not empty cr }">
					<div class="letter">${ cr }</div>	
					</c:when>
					<c:otherwise>
					<div class="letter">&nbsp;</div>				
					</c:otherwise>
				</c:choose>
			</c:forEach>		
		</c:when>
		<c:otherwise>
			<br/>
		</c:otherwise>
	</c:choose>
			</div>
		</div>
</c:forEach>
	</div>
</div>
<h1>Decoded text</h1>
<div class="row">
	<div class="col-12">
<c:forEach items="${ translation.translated }" var="row">
		<div class="row">
			<div class="col-12">
	<c:choose>
		<c:when test="${ not empty row.cols }">
			<c:forEach items="${ row.cols }" var="cr">
				<c:choose>
					<c:when test="${ not empty cr }">
					<div class="letter">${ cr }</div>	
					</c:when>
					<c:otherwise>
					<div class="letter">&nbsp;</div>				
					</c:otherwise>
				</c:choose>
			</c:forEach>		
		</c:when>
		<c:otherwise>
			<br/>
		</c:otherwise>
	</c:choose>
			</div>
		</div>
</c:forEach>
	</div>
</div>
<h1>Change code</h1>
<form action="/Decoding/update.html">
	<div class="row">
		<div class="col-1"><label for="s1" class="col-sm-2 col-form-label">1 (${ translation.replacements[0].frequency })</label><input type="text" size="2" maxlength="2" id="s1" name="s1" value="${ translation.replacements[0].value }"></input></div>
		<div class="col-1"><label for="s2" class="col-sm-2 col-form-label">2 (${ translation.replacements[1].frequency })</label><input type="text" size="2" maxlength="2" id="s2" name="s2" value="${ translation.replacements[1].value }"></input></div>
		<div class="col-1"><label for="s3" class="col-sm-2 col-form-label">3 (${ translation.replacements[2].frequency })</label><input type="text" size="2" maxlength="2" id="s3" name="s3" value="${ translation.replacements[2].value }"></input></div>
		<div class="col-1"><label for="s4" class="col-sm-2 col-form-label">4 (${ translation.replacements[3].frequency })</label><input type="text" size="2" maxlength="2" id="s4" name="s4" value="${ translation.replacements[3].value }"></input></div>
		<div class="col-1"><label for="s5" class="col-sm-2 col-form-label">5 (${ translation.replacements[4].frequency })</label><input type="text" size="2" maxlength="2" id="s5" name="s5" value="${ translation.replacements[4].value }"></input></div>
		<div class="col-1"><label for="s6" class="col-sm-2 col-form-label">6 (${ translation.replacements[5].frequency })</label><input type="text" size="2" maxlength="2" id="s6" name="s6" value="${ translation.replacements[5].value }"></input></div>
		<div class="col-1"><label for="s7" class="col-sm-2 col-form-label">7 (${ translation.replacements[6].frequency })</label><input type="text" size="2" maxlength="2" id="s7" name="s7" value="${ translation.replacements[6].value }"></input></div>
		<div class="col-1"><label for="s8" class="col-sm-2 col-form-label">8 (${ translation.replacements[7].frequency })</label><input type="text" size="2" maxlength="2" id="s8" name="s8" value="${ translation.replacements[7].value }"></input></div>
	</div>
	<div class="row">
		<div class="col-1"><label for="s9" class="col-sm-2 col-form-label">9 (${ translation.replacements[8].frequency })</label><input type="text" size="2" maxlength="2" id="s9" name="s9" value="${ translation.replacements[8].value }"></input></div>
		<div class="col-1"><label for="s10" class="col-sm-2 col-form-label">10 (${ translation.replacements[9].frequency })</label><input type="text" size="2" maxlength="2" id="s10" name="s10" value="${ translation.replacements[9].value }"></input></div>
		<div class="col-1"><label for="s11" class="col-sm-2 col-form-label">11 (${ translation.replacements[10].frequency })</label><input type="text" size="2" maxlength="2" id="s11" name="s11" value="${ translation.replacements[10].value }"></input></div>
		<div class="col-1"><label for="s12" class="col-sm-2 col-form-label">12 (${ translation.replacements[11].frequency })</label><input type="text" size="2" maxlength="2" id="s12" name="s12" value="${ translation.replacements[11].value }"></input></div>
		<div class="col-1"><label for="s13" class="col-sm-2 col-form-label">13 (${ translation.replacements[12].frequency })</label><input type="text" size="2" maxlength="2" id="s13" name="s13" value="${ translation.replacements[12].value }"></input></div>
		<div class="col-1"><label for="s14" class="col-sm-2 col-form-label">14 (${ translation.replacements[13].frequency })</label><input type="text" size="2" maxlength="2" id="s14" name="s14" value="${ translation.replacements[13].value }"></input></div>
		<div class="col-1"><label for="s15" class="col-sm-2 col-form-label">15 (${ translation.replacements[14].frequency })</label><input type="text" size="2" maxlength="2" id="s15" name="s15" value="${ translation.replacements[14].value }"></input></div>
		<div class="col-1"><label for="s16" class="col-sm-2 col-form-label">16 (${ translation.replacements[15].frequency })</label><input type="text" size="2" maxlength="2" id="s16" name="s16" value="${ translation.replacements[15].value }"></input></div>
	</div>
	<div class="row">
		<div class="col-1"><label for="s17" class="col-sm-2 col-form-label">17 (${ translation.replacements[16].frequency })</label><input type="text" size="2" maxlength="2" id="s17" name="s17" value="${ translation.replacements[16].value }"></input></div>
		<div class="col-1"><label for="s18" class="col-sm-2 col-form-label">18 (${ translation.replacements[17].frequency })</label><input type="text" size="2" maxlength="2" id="s18" name="s18" value="${ translation.replacements[17].value }"></input></div>
		<div class="col-1"><label for="s19" class="col-sm-2 col-form-label">19 (${ translation.replacements[18].frequency })</label><input type="text" size="2" maxlength="2" id="s19" name="s19" value="${ translation.replacements[18].value }"></input></div>
		<div class="col-1"><label for="s20" class="col-sm-2 col-form-label">20 (${ translation.replacements[19].frequency })</label><input type="text" size="2" maxlength="2" id="s20" name="s20" value="${ translation.replacements[19].value }"></input></div>
		<div class="col-1"><label for="s21" class="col-sm-2 col-form-label">21 (${ translation.replacements[20].frequency })</label><input type="text" size="2" maxlength="2" id="21" name="s21" value="${ translation.replacements[20].value }"></input></div>
		<div class="col-1"><label for="s22" class="col-sm-2 col-form-label">22 (${ translation.replacements[21].frequency })</label><input type="text" size="2" maxlength="2" id="22" name="s22" value="${ translation.replacements[21].value }"></input></div>
		<div class="col-1"><label for="s23" class="col-sm-2 col-form-label">23 (${ translation.replacements[22].frequency })</label><input type="text" size="2" maxlength="2" id="23" name="s23" value="${ translation.replacements[22].value }"></input></div>
		<div class="col-1"><label for="s24" class="col-sm-2 col-form-label">24 (${ translation.replacements[23].frequency })</label><input type="text" size="2" maxlength="2" id="24" name="s24" value="${ translation.replacements[23].value }"></input></div>
	</div>
	<div class="row">
		<div class="col-12">&nbsp;</div>
	</div>
	<div class="row">
		<div class="col-12"><button type="submit" class="btn btn-primary mb-2">Retry</button></div>
	</div>
</form>