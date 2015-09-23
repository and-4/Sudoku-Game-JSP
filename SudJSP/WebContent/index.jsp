<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" session="true"%>
<!DOCTYPE html>
<%@page import="sudoku_JSP_2015.Sudoku"%>
<jsp:useBean id="counter" scope="session"  class="sudoku_JSP_2015.Sudoku" />
 <html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sudoku JSP</title>
<style> 
 .butt {border: 1px outset #afafaf;background-color:#ffcf77;height:30px;width:30px;cursor:pointer;}
 .butt:hover {background-color:#e4b96a;} 
 .buttdrk {border:1px outset #afafaf;background-color:#7accc8;height:30px;width:30px;cursor:pointer;} 
 .buttdrk:hover {background-color:#6bb4b0;} 
 .bdrk {border:1px outset #afafaf;background-color:#f2ea90;height:30px;width:30px;cursor:pointer;}
 .bdrk:hover {background-color:#c9c378;} 
 table.center {margin-left:auto;margin-right:auto;border-spacing: 0px;}body{background-color:#e1e1e1;} 
 </style>
</head>
<body><br>
 <form name="Form1" action=""><div align="right">Level: 
 <select name="level"><option>Easy</option><option>Normal</option><option>Hard</option></select>&nbsp;
 <input name="n40" type="submit" value="New game"/></div><br><br> 
 <table class="center">
<%=Sudoku.getBigHtmlForJSP(request)%>
</table>
 
 <%String menuCode = Sudoku.getMenuHtmlForJSP();
 if (menuCode.length() > 0){
 out.print("<br><br><table class=\"center\"><tr> ");
 out.print(menuCode);
 out.print("</tr></table>");}%>
  
 <%=Sudoku.getMessageForJSP()%>
</form> 
</body>   
</html>