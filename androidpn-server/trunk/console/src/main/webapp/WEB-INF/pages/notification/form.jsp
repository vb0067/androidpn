<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Admin Console</title>
	<meta name="menu" content="notification" />    
</head>

<body>

<h1>Send Notifications</h1>

<%--<div style="background:#eee; margin:20px 0px; padding:20px; width:500px; border:solid 1px #999;">--%>
<div style="margin:20px 0px;">
<form action="notification.do?action=send" method="post" style="margin: 0px;">
<table width="480" cellpadding="4" cellspacing="0" border="0">
<tr>
	<td width="20%">To:</td>
	<td width="80%">
		<input type="radio" name="broadcast" value="Y" checked="checked" />  All (Broadcast) 
        <input type="radio" name="broadcast" value="N" /> Single Device 
	</td>
</tr>
<tr id="trUsername" style="display:none;">
	<td>Username:</td>
	<td><input type="text" id="username" name="username" value="" style="width:300px;" /></td>
</tr>
<tr>
	<td>Title:</td>
	<td><input type="text" id="title" name="title" value="Demo App" style="width:300px;" /></td>
</tr>
<tr>
	<td>Message:</td>
	<td><textarea id="message" name="message" style="width:300px; height:50px;" >This is a test message.</textarea></td>
</tr>
<tr>
	<td>Ticker:</td>
	<td><input type="text" id="ticker" name="ticker" value="" style="width:300px;" /></td>
</tr>
<tr>
	<td>URL:</td>
	<td><input type="text" id="url" name="url" value="" style="width:300px;" /></td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td><input type="submit" value="Submit" /></td>
</tr>
</table> 
</form>
</div>

<script type="text/javascript"> 
//<![CDATA[
 
$(function() {
	$('input[name=broadcast]').click(function() {
		if ($('input[name=broadcast]')[0].checked) {
			$('#trUsername').hide();
		} else {
			$('#trUsername').show();
		}
	});
	
	if ($('input[name=broadcast]')[0].checked) {
		$('#trUsername').hide();
	} else {
		$('#trUsername').show();
	}	
});
 
//]]>
</script>

</body>
</html>
